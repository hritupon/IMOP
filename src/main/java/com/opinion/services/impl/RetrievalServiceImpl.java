package com.opinion.services.impl;

import com.google.inject.Inject;
import com.opinion.models.Post;
import com.opinion.models.TopicResponse;
import com.opinion.repository.RedisSourceRepository;
import com.opinion.services.RetrievalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by hritupon on 10/5/15.
 */
public class RetrievalServiceImpl implements RetrievalService{

    private final RedisSourceRepository redisSourceRepository;

    private static final Logger logger = LoggerFactory.getLogger(IngestionServiceImpl.class);

    @Inject
    public RetrievalServiceImpl(RedisSourceRepository redisSourceRepository) {
        this.redisSourceRepository = redisSourceRepository;
    }

    @Override
    public Map<String, List<String>> getTopicIds()throws Exception{
        return redisSourceRepository.getTopicIds();
    }

    @Override
    public Map<String, List<TopicResponse>> getTopicTitles()throws Exception{
        return redisSourceRepository.getTopicTitles();
    }

    @Override
    public Map<String, List<String>> getTopicDetails(String topidId)throws Exception{
        return redisSourceRepository.getTopicDetails(topidId);
    }

    @Override
    public Map<String, List<Post>> getTopicPosts(String topicId)throws Exception{
        return redisSourceRepository.getTopicPosts(topicId);
    }
}
