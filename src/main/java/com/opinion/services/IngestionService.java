package com.opinion.services;


import com.opinion.models.Post;
import com.opinion.models.TopicRequest;

import java.util.List;

/**
 * Created by sutirtha on 05/09/14.
 */
public interface IngestionService {

    public void insertTopic(List<TopicRequest> topicRequest) throws Exception;

    public void insertOpinion(Post post) throws Exception;
}
