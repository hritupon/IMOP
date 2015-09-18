package com.opinion.repository.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.opinion.core.managed.Redis;
import com.opinion.models.*;
import com.opinion.models.enums.PartitionType;
import com.opinion.models.enums.RedisNamespace;
import com.opinion.models.enums.TopicCategory;
import com.opinion.repository.RedisSourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.xml.ws.WebServiceException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Rituparna Saikia
 * Date: 20/11/14
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */

public class RedisSourceRepositoryImpl implements RedisSourceRepository {

    private final Redis redis;
    private static final Logger logger = LoggerFactory.getLogger(RedisSourceRepositoryImpl.class);

    @Inject
    public RedisSourceRepositoryImpl(Redis redis){
        this.redis = redis;
    }


    private void handleAndLogJedisException(Jedis jedis, JedisConnectionException e, PartitionType partitionType,
                                            RedisNamespace namespace) {
        logger.error("Jedis connection exception: ", e);
        if(jedis != null) {
            if(namespace.equals(RedisNamespace.MASTER)){
                redis.returnBrokenMasterResource(jedis, partitionType);
            }else if(namespace.equals(RedisNamespace.SLAVE)){
                redis.returnBrokenSlaveResource(jedis, partitionType);
            }else{
                redis.returnBrokenConfigResource(jedis);
            }
        }
        throw e;
    }

    @Override
    public void insertTopic(List<TopicRequest>topicRequests)throws Exception{
        insertTopicId(topicRequests);
        insertTopicCount(topicRequests);
        insertTopicData(topicRequests);

    }

    @Override
    public void insertTopicId(List<TopicRequest> topicRequests) throws Exception{
        PartitionType partitionType = PartitionType.getTopicPartition();
        Jedis jedis = null;
        try{
            jedis = redis.getMasterResource(partitionType);
            Pipeline p = jedis.pipelined();

            for(TopicRequest topicRequest : topicRequests){
                if(!validateRequest(topicRequest.getUserName(),topicRequest.getAuth())){
                    throw new WebServiceException(ResponseCode.AUTHENTICATION_FAILED);
                }
                p.lpush(topicRequest.getTopicCategory(), topicRequest.getTopicId());
            }
            p.sync();
            redis.returnMasterResource(jedis, partitionType);
        }catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.MASTER);
        }
    }

    @Override
    public void insertTopicData(List<TopicRequest> topicRequests) throws Exception{
        PartitionType partitionType = PartitionType.getTopicPartition();
        Jedis jedis = null;
        try{
            jedis = redis.getMasterResource(partitionType);
            Pipeline p = jedis.pipelined();

            for(TopicRequest topicRequest : topicRequests){
                String key="topic:"+topicRequest.getTopicId();
                p.hmset(key, topicRequest.getTopicData());
            }
            p.sync();
            redis.returnMasterResource(jedis, partitionType);
        }catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.MASTER);
        }
    }

    public Map<String,String> getTopicData(String topicId){
        PartitionType partitionType = PartitionType.getTopicPartition();
        Jedis jedis = null;
        Response<Map<String,String>> responseMap=null;
        try{
            jedis = redis.getSlaveResource(partitionType);
            Pipeline p = jedis.pipelined();
            String key="topic:"+topicId;
            responseMap=p.hgetAll(key);
            p.sync();
            redis.returnSlaveResource(jedis, partitionType);
        }catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.MASTER);
        }
        return responseMap.get();
    }
    private void insertTopicCount(List<TopicRequest> topicRequests){
        PartitionType partitionType = PartitionType.getTopicPartition();
        Jedis jedis = null;
        try{
            jedis = redis.getMasterResource(partitionType);
            Pipeline p = jedis.pipelined();

            for(TopicRequest topicRequest : topicRequests){
                p.hset("TOPIC_RESPONSE_COUNT", topicRequest.getTopicId(), "0");
            }
            p.sync();
            redis.returnMasterResource(jedis, partitionType);
        }catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.MASTER);
        }
    }
    private String getResponseCount(String topicId){
        PartitionType partitionType = PartitionType.getTopicPartition();
        Jedis jedis = null;
        Response<String> response=null;
        try{
            jedis = redis.getSlaveResource(partitionType);
            Pipeline p = jedis.pipelined();
            response=p.hget("TOPIC_RESPONSE_COUNT", topicId);
            p.sync();
            redis.returnSlaveResource(jedis, partitionType);
        }catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.MASTER);
        }
        return response.get();
    }
    private void incrementTopicResponseCount(String topicId){
        PartitionType partitionType = PartitionType.getTopicPartition();
        Jedis jedis = null;
        try{
            jedis = redis.getMasterResource(partitionType);
            Pipeline p = jedis.pipelined();
            p.hincrBy("TOPIC_RESPONSE_COUNT", topicId, 1);
            p.sync();
            redis.returnMasterResource(jedis, partitionType);
        }catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.MASTER);
        }
    }
    @Override
    public Map<String, List<String>> getTopicIds() throws Exception {
        PartitionType partitionType = PartitionType.getTopicPartition();
        Map<String, Response<List<String>>> responses = Maps.newHashMap();
        Jedis jedis = null;
        try{
            jedis = redis.getSlaveResource(partitionType);
            Pipeline p = jedis.pipelined();
            for(TopicCategory topicCategory : TopicCategory.values()){
                if(topicExists(topicCategory.name())) {
                    responses.put(topicCategory.name(), p.lrange(topicCategory.name(), 0, 1000));
                }
            }
            p.sync();
            redis.returnSlaveResource(jedis, partitionType);
        }catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.SLAVE);
        }
        return populateTopicIds(responses);
    }

    private Map<String,List<String>> populateTopicIds(Map<String, Response<List<String>>> responses){
        Map<String, List<String>> topics = Maps.newHashMap();
        for(String topicCategory : responses.keySet()){
            Response<List<String>> topicIdsResponse = responses.get(topicCategory);
            if(topicIdsResponse!=null) {
                List<String> topicIds=topicIdsResponse.get();
                topics.put(topicCategory, topicIds);
            }
        }
        return topics;
    }
    @Override
    public Map<String, List<TopicResponse>> getTopicTitles() throws Exception {
        PartitionType partitionType = PartitionType.getTopicPartition();
        Map<String, Response<List<String>>> responses = Maps.newHashMap();
        Jedis jedis = null;
        try{
            jedis = redis.getSlaveResource(partitionType);
            Pipeline p = jedis.pipelined();
            for(TopicCategory topicCategory : TopicCategory.values()){
                if(topicExists(topicCategory.name())) {
                    responses.put(topicCategory.name(), p.lrange(topicCategory.name(), 0, 1000));
                }
            }
            p.sync();
            redis.returnSlaveResource(jedis, partitionType);
        }catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.SLAVE);
        }
        return populateTopics(responses);
    }

    private Map<String,List<TopicResponse>> populateTopics(Map<String, Response<List<String>>> responses){
        Map<String, List<TopicResponse>> topics = Maps.newHashMap();
        for(String topicCategory : responses.keySet()){
            Response<List<String>> topicIdsResponse = responses.get(topicCategory);
            if(topicIdsResponse!=null) {
                List<String> topicIds=topicIdsResponse.get();
                List<TopicResponse> topicResponse=getTopicTitles(topicIds);
                topics.put(topicCategory, topicResponse);
            }
        }
        return topics;
    }

    private List<TopicResponse> getTopicTitles(List<String>topicIds){
        PartitionType partitionType = PartitionType.getTopicPartition();
        Map<String, Response<String>> responses = Maps.newHashMap();
        Jedis jedis = null;
        try {
            jedis = redis.getSlaveResource(partitionType);
            Pipeline p = jedis.pipelined();
            for (String topicId : topicIds) {
                responses.put(topicId,p.hget("TOPIC_DATA",topicId));
            }
            p.sync();
            redis.returnSlaveResource(jedis, partitionType);
        }
        catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.SLAVE);
        }
        return populateTopicNames(responses);
    }

    private List<TopicResponse> populateTopicNames(Map<String, Response<String>> responses){
        List<TopicResponse> topicResponses=Lists.newArrayList();
        for(String topicId:responses.keySet()){
            Response<String>response=responses.get(topicId);
            String topicName=response.get();
            TopicResponse topicResponse=new TopicResponse();
            topicResponse.setTopicId(topicId);
            topicResponse.setTopicName(topicName);
            String responseCountString=getResponseCount(topicId);
            if(responseCountString==null || getResponseCount(topicId).length()<=0) {
                responseCountString="0";
            }
            topicResponse.setResponseCount(Integer.parseInt(responseCountString));
            topicResponses.add(topicResponse);
        }
        return topicResponses;
    }
    private boolean topicExists(String keyName){
        PartitionType partitionType = PartitionType.getTopicPartition();
        Jedis jedis = null;
        Response<Boolean> exists=null;
        try{
            jedis = redis.getSlaveResource(partitionType);
            Pipeline p = jedis.pipelined();
            exists=p.exists(keyName);
            p.sync();
            redis.returnSlaveResource(jedis, partitionType);
        }catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.MASTER);
        }
        return exists.get();
    }

    public Map<String, List<String>> getTopicDetails(String topicId)throws Exception{
        PartitionType partitionType = PartitionType.getTopicPartition();
        Map<Response<String>, List<String>> responses = Maps.newHashMap();
        Jedis jedis = null;
        try {
            jedis = redis.getSlaveResource(partitionType);
            Pipeline p = jedis.pipelined();
            responses.put(p.hget("TOPIC_DATA", topicId), new ArrayList<String>());
            p.sync();
            redis.returnSlaveResource(jedis, partitionType);
        }
        catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.SLAVE);
        }
        return populateTopicDetails(responses);
    }

    @Override
    public void insertOpinion(Post post) throws Exception{
        try {
            if(!validateRequest(post.getUserName(),post.getAuth())){
                throw new WebServiceException(ResponseCode.AUTHENTICATION_FAILED);
            }
            insertTopicToPostMap(post);
            insertPostData(post);
            incrementTopicResponseCount(post.getTopicId());
            insertPostToTimeline(post);
            insertPostToFollowers(post);
            insertPostToCategories(post);
        }catch (Exception e){
            logger.error("Post insertion exception: ", e);
        }
    }

    private void insertPostToCategories(Post post){
        Map<String,String> map= getTopicData(post.getTopicId());
        String key="category:"+map.get("category");
        PartitionType partitionType=PartitionType.getTopicPartition();
        Jedis jedis = null;
        try {
            jedis = redis.getMasterResource(partitionType);
            Pipeline p = jedis.pipelined();
            p.lpush(key,post.getPostId());
            p.sync();
            redis.returnMasterResource(jedis, partitionType);
        }
        catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.SLAVE);
        }
    }

    public Set<String> getPostIdsInCategory(String category){
        Response<Set<String>> response=null;
        String key="category:"+category;
        PartitionType partitionType=PartitionType.getTopicPartition();
        Jedis jedis = null;
        try {
            jedis = redis.getSlaveResource(partitionType);
            Pipeline p = jedis.pipelined();
            response=p.zrange(key,0,-1);
            p.sync();
            redis.returnSlaveResource(jedis, partitionType);
        }
        catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.SLAVE);
        }
        return response.get();
    }
    private void insertPostToFollowers(Post post){
        Set<String> followers=getFollowers(post.getUserName());
        PartitionType partitionType=PartitionType.getPostsPartition();
        Jedis jedis = null;
        try {
            jedis = redis.getMasterResource(partitionType);
            Pipeline p = jedis.pipelined();
            for(String follower:followers){
                p.lpush("posts:"+follower,post.getPostId());
            }
            p.sync();
            redis.returnMasterResource(jedis, partitionType);
        }
        catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.SLAVE);
        }

    }
    private void insertPostToTimeline(Post post){
        String userId=getUserId(post.getUserName());
        String key="posts:"+userId;
        PartitionType partitionType=PartitionType.getPostsPartition();
        Jedis jedis = null;
        try {
            jedis = redis.getMasterResource(partitionType);
            Pipeline p = jedis.pipelined();
            p.lpush(key, post.getPostId());
            p.sync();
            redis.returnMasterResource(jedis, partitionType);
        }
        catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.SLAVE);
        }
    }

    public Set<String> getTimelinePosts(String userName){
        String userId=getUserId(userName);
        String key="posts:"+userId;
        PartitionType partitionType=PartitionType.getPostsPartition();
        Response<Set<String>>response=null;
        Jedis jedis = null;
        try {
            jedis = redis.getSlaveResource(partitionType);
            Pipeline p = jedis.pipelined();
            response=p.zrange(key,0,-1);
            p.sync();
            redis.returnSlaveResource(jedis, partitionType);
        }
        catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.SLAVE);
        }
        return response.get();
    }

    @Override
    public Map<String, List<Post>> getTopicPosts(String topicId) {
        PartitionType partitionType = PartitionType.getPartitionType(topicId);
        Jedis jedis = null;
        HashMap<String, Response<List<String>>> responses = Maps.newHashMap();
        try {
            jedis = redis.getSlaveResource(partitionType);
            Pipeline p = jedis.pipelined();
            responses.put(topicId, p.lrange(topicId, 0, -1));
            p.sync();
            redis.returnSlaveResource(jedis, partitionType);
        }
        catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.SLAVE);
        }
        return populateTopicPosts(responses);
    }

    private Map<String,List<Post>> populateTopicPosts(HashMap<String,Response<List<String>>> responses){
        HashMap<String,List<Post>> resposePosts=Maps.newHashMap();
        for(String topicId:responses.keySet()){
            Response<List<String>> response=responses.get(topicId);
            List<String>postIds=response.get();
            for(String postId:postIds){
                populatePost(resposePosts,postId);
            }

        }
        return resposePosts;
    }

    private void populatePost(HashMap<String,List<Post>> resposePosts,String postId){
        PartitionType partitionType = PartitionType.getPartitionType(postId);
        Jedis jedis = null;
        Map<String,String> postMap = Maps.newHashMap();

        try {
            jedis = redis.getSlaveResource(partitionType);
            Pipeline p = jedis.pipelined();
            Response<Map<String,String>> response=p.hgetAll(postId);
            p.sync();
            redis.returnSlaveResource(jedis, partitionType);
            postMap=response.get();
        }
        catch (JedisConnectionException ex){
            handleAndLogJedisException(jedis, ex, partitionType, RedisNamespace.SLAVE);
        }
        Post post=new Post();
        post.setUserName(postMap.get("user_name"));
        post.setPostData(postMap.get("data"));
        List<Post>posts=Lists.newArrayList();
        if(resposePosts.containsKey(postId)){
            posts=resposePosts.get(postId);

        }
        posts.add(post);
        resposePosts.put(postId, posts);
    }
    private void insertTopicToPostMap(Post post) throws Exception{
        PartitionType partitionType = PartitionType.getPartitionType(post.getTopicId());
        Jedis jedis = redis.getMasterResource(partitionType);
        Pipeline p = jedis.pipelined();
        p.lpush(post.getTopicId(), post.getPostId());
        p.sync();
        redis.returnMasterResource(jedis, partitionType);
    }

    private void insertPostData(Post post) throws Exception{
        PartitionType partitionType = PartitionType.getPartitionType(post.getPostId());
        Jedis jedis = redis.getMasterResource(partitionType);
        Pipeline p = jedis.pipelined();
        String key="post:"+post.getPostId();
        p.hmset(key, post.getPostParamsData());
        p.sync();
        redis.returnMasterResource(jedis, partitionType);
    }

    private Map<String, List<String>> populateTopicDetails( Map<Response<String>, List<String>> responses){
        Map<String, List<String>> returnResponses = Maps.newHashMap();

        for(Response<String>response:responses.keySet()){
            String topic_title=response.get();
            returnResponses.put(topic_title,new ArrayList<String>());
        }
        return returnResponses;
    }

    public String getNextUserID(){
        PartitionType partitionType = PartitionType.getUserPartition();
        Jedis jedis = redis.getMasterResource(partitionType);
        Pipeline p = jedis.pipelined();
        Response<Long> response=p.incr("next_user_id");
        p.sync();
        redis.returnMasterResource(jedis, partitionType);
        return response.get().toString();
    }

    public String addUser(String userID,UserAddRequest userAddRequest){
        String key="user:"+userID;
        PartitionType partitionType = PartitionType.getUserPartition();
        Jedis jedis = redis.getMasterResource(partitionType);
        Pipeline p = jedis.pipelined();
        Map<String,String> userMap=Maps.newHashMap();
        userMap.put("user_name",userAddRequest.getUserName());
        userMap.put("password",userAddRequest.getPassword());
        Response<String> response=p.hmset(key, userMap);
        p.sync();
        redis.returnMasterResource(jedis, partitionType);
        return response.get();
    }

    public Long addUserIDMap(String userID,UserAddRequest userAddRequest){
        String key="users";
        PartitionType partitionType = PartitionType.getUserPartition();
        Jedis jedis = redis.getMasterResource(partitionType);
        Pipeline p = jedis.pipelined();
        Response<Long> response=p.hset(key, userAddRequest.getUserName(), userID);
        p.sync();
        redis.returnMasterResource(jedis, partitionType);
        return response.get();
    }

    @Override
    public boolean checkValidUser(String userName) {
        String key="users";
        PartitionType partitionType = PartitionType.getUserPartition();
        Jedis jedis = redis.getSlaveResource(partitionType);
        Pipeline p = jedis.pipelined();
        Response<Boolean>response=p.hexists(key, userName);
        p.sync();
        redis.returnSlaveResource(jedis, partitionType);
        return response.get();
    }

    @Override
    public String getUserId(String userName) {
        String key="users";
        PartitionType partitionType = PartitionType.getUserPartition();
        Jedis jedis = redis.getSlaveResource(partitionType);
        Pipeline p = jedis.pipelined();
        Response<String>response=p.hget(key, userName);
        p.sync();
        redis.returnSlaveResource(jedis, partitionType);
        return response.get();
    }

    @Override
    public void addFollower(AddFollowerRequest addFollowerRequest) {
        PartitionType partitionType = PartitionType.getUserPartition();
        Jedis jedis = redis.getMasterResource(partitionType);
        Pipeline p = jedis.pipelined();

        String followerId=addFollowerRequest.getFollowerId();
        String userId=addFollowerRequest.getUserId();

        String key="followers:"+followerId;

        java.util.Date date= new java.util.Date();
        Timestamp timestamp= new Timestamp(date.getTime());
        p.zadd(key,timestamp.getTime(),userId);
        p.sync();
        redis.returnMasterResource(jedis, partitionType);
    }

    public Set<String> getFollowers(String userName){
        String userId=getUserId(userName);
        PartitionType partitionType = PartitionType.getUserPartition();
        Jedis jedis = redis.getSlaveResource(partitionType);
        Pipeline p = jedis.pipelined();

        String key="followers:"+userId;

        Response<Set<String>>followersResp=p.zrange(key, 0, -1);
        p.sync();
        redis.returnSlaveResource(jedis, partitionType);
        return followersResp.get();
    }

    @Override
    public Map<String,String> login(LoginRequest loginRequest) {
        String userId=getUserId(loginRequest.getUserName());
        Map<String,String>response=Maps.newHashMap();
        if(userId==null){
            response.put("status",ResponseCode.USR_DOES_NOT_EXIST);
            return response;
        }
        response.put("user_id",userId);
        if(!checkCredentials(loginRequest)){
            response.put("status",ResponseCode.AUTHENTICATION_FAILED);
            return response;
        }
        String authId= UUID.randomUUID().toString();
        addUserToAuth(loginRequest.getUserName(), authId);
        addAuthToUser(authId, loginRequest.getUserName());
        response.put("status",ResponseCode.SUCCESS);
        response.put("auth",authId);
        return response;
    }
    private boolean checkCredentials(LoginRequest loginRequest){
        String userId=getUserId(loginRequest.getUserName());
        String key="user:"+userId;
        PartitionType partitionType = PartitionType.getUserPartition();
        Jedis jedis = redis.getSlaveResource(partitionType);
        Pipeline p = jedis.pipelined();
        Response<List<String>> response=p.hmget(key, "user_name","password");
        p.sync();
        redis.returnSlaveResource(jedis, partitionType);
        List<String>credentials=response.get();
        if(credentials==null || credentials.size()<2)return false;
        String userName=credentials.get(0);
        String password=credentials.get(1);
        if(userName.equals(loginRequest.getUserName()) && password.equals(loginRequest.getPassword())) {
            return true;
        }
        return false;
    }
    private void addUserToAuth(String userName,String auth){
        PartitionType partitionType = PartitionType.getUserPartition();
        Jedis jedis = redis.getMasterResource(partitionType);
        Pipeline p = jedis.pipelined();
        String key="user:"+getUserId(userName);
        p.hset(key,"auth",auth);
        p.sync();
        redis.returnMasterResource(jedis, partitionType);
    }

    private void addAuthToUser(String auth,String userName){
        PartitionType partitionType = PartitionType.getUserPartition();
        Jedis jedis = redis.getMasterResource(partitionType);
        Pipeline p = jedis.pipelined();
        p.hset("auths", auth, getUserId(userName));
        p.sync();
        redis.returnMasterResource(jedis, partitionType);
    }


    @Override
    public boolean validateRequest(String userName,String authId){
        String userId=getUserId(userName);
        String auth=getAuthFromUserId(userName);
        String user=getUserIdFromAuth(authId);
        return auth.equals(authId) && user.equals(userId);
    }

    private String getAuthFromUserId(String userName){
        PartitionType partitionType = PartitionType.getUserPartition();
        Jedis jedis = redis.getSlaveResource(partitionType);
        Pipeline p = jedis.pipelined();
        String userId=getUserId(userName);
        String key="user:"+userId;
        Response<String>auth=p.hget(key, "auth");
        p.sync();
        redis.returnSlaveResource(jedis, partitionType);
        return auth.get();
    }

    private String getUserIdFromAuth(String auth){
        PartitionType partitionType = PartitionType.getUserPartition();
        Jedis jedis = redis.getSlaveResource(partitionType);
        Pipeline p = jedis.pipelined();
        Response<String> userId=p.hget("auths", auth);
        p.sync();
        redis.returnSlaveResource(jedis, partitionType);
        return userId.get();
    }
}
