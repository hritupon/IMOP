package com.opinion.services;

import com.opinion.models.Post;
import com.opinion.models.TopicResponse;

import java.util.List;
import java.util.Map;

/**
 * Created by hritupon on 10/5/15.
 */
public interface RetrievalService {
    Map<String, List<String>> getTopicIds()throws Exception;
    Map<String,List<TopicResponse>> getTopicTitles()throws Exception;
    Map<String, List<String>> getTopicDetails(String topicId)throws Exception;
    Map<String, List<Post>> getTopicPosts(String topicId)throws Exception;
}
