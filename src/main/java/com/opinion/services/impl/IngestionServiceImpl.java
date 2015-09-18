package com.opinion.services.impl;

import com.opinion.models.Post;
import com.opinion.repository.RedisSourceRepository;
import com.google.inject.Inject;
import com.opinion.models.TopicRequest;
import com.opinion.services.IngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class IngestionServiceImpl implements IngestionService {

    private final RedisSourceRepository redisSourceRepository;

    private static final Logger logger = LoggerFactory.getLogger(IngestionServiceImpl.class);

    @Inject
    public IngestionServiceImpl(RedisSourceRepository redisSourceRepository) {
        this.redisSourceRepository = redisSourceRepository;
    }

    @Override
    public void insertTopic(List<TopicRequest> topicRequest) throws Exception {
        logger.info("Insert topic {}", topicRequest);
        redisSourceRepository.insertTopic(topicRequest);
    }

    @Override
    public void insertOpinion(Post post) throws Exception{
        logger.info("Insert Opinion {}{}",post);
        redisSourceRepository.insertOpinion(post);
    }
}
