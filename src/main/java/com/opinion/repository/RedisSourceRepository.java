package com.opinion.repository;

import com.opinion.models.*;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ramachandra.as
 * Date: 20/11/14
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RedisSourceRepository {

    void insertTopicData(List<TopicRequest> topicRequests) throws Exception;
    Map<String, List<String>> getTopicIds() throws Exception;
    Map<String, List<TopicResponse>> getTopicTitles() throws Exception;
    void insertTopic(List<TopicRequest>topicRequests)throws Exception;
    void insertTopicId(List<TopicRequest> topicRequests) throws Exception;
    Map<String, List<String>> getTopicDetails(String topidId)throws Exception;
    void insertOpinion(Post post) throws Exception;
    Map<String,List<Post>> getTopicPosts(String topicId);
    String getNextUserID();
    String addUser(String userID,UserAddRequest userAddRequest);
    Long addUserIDMap(String userID,UserAddRequest userAddRequest);
    boolean checkValidUser(String userName);
    String getUserId(String userName);
    void addFollower(AddFollowerRequest addFollowerRequest);
    Map<String,String>  login(LoginRequest loginRequest);
    boolean validateRequest(String userName,String authId);
}
