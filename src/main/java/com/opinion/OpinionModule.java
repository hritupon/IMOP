package com.opinion;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.opinion.core.configuration.RedisConfiguration;
import com.opinion.core.jedis.RedisPartitionPool;
import com.opinion.core.managed.Redis;
import com.opinion.repository.RedisSourceRepository;
import com.opinion.repository.impl.RedisSourceRepositoryImpl;
import com.opinion.services.AuthenticationService;
import com.opinion.services.IngestionService;
import com.opinion.services.RetrievalService;
import com.opinion.services.UserService;
import com.opinion.services.impl.AuthenticationServiceImpl;
import com.opinion.services.impl.IngestionServiceImpl;
import com.opinion.services.impl.RetrievalServiceImpl;
import com.opinion.services.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.*;

/**
 * Created by w7 on 5/7/2015.
 */
public class OpinionModule implements Module {
    private static final Logger logger = LoggerFactory.getLogger(OpinionModule.class);

    @Override
    public void configure(Binder binder) {

    }

    @Provides @Singleton
    public UserService provideUserService(RedisSourceRepository redisSourceRepository){
        return new UserServiceImpl(redisSourceRepository);
    }

    @Provides @Singleton
    public ExecutorService provideExecutorService(OpinionConfiguration opinionConfiguration){
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("athena-aggregator")
                .build();
        return new ThreadPoolExecutor(opinionConfiguration.getThreadPoolSize(), opinionConfiguration.getThreadPoolSize(), 1000,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
    }

    @Provides @Singleton
    public RedisPartitionPool provideRedisPartitionPool(OpinionConfiguration configuration){
        return new RedisPartitionPool(configuration.getRedis());
    }

    @Provides @Singleton
    public JedisPool provideConfigPool(OpinionConfiguration configuration) {
        RedisConfiguration redisConfiguration = configuration.getRedis();
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisConfiguration.getMasterMaxThreads());
        logger.info("Created jedis master pool with {} max active threads", redisConfiguration.getMasterMaxThreads());
        return new JedisPool(poolConfig, redisConfiguration.getConfig().getHost(), redisConfiguration.getConfig().getPort(), redisConfiguration.getTimeout());
    }

    @Provides @Singleton
    public RedisSourceRepository providesRedisSourceRepository(Redis redis){
        return new RedisSourceRepositoryImpl(redis);
    }

    @Provides @Singleton
    public IngestionService provideIngestionService(RedisSourceRepository redisSourceRepository){
        return new IngestionServiceImpl(redisSourceRepository);
    }

    @Provides @Singleton
    public RetrievalService provideRetrievalService(RedisSourceRepository redisSourceRepository){
        return new RetrievalServiceImpl(redisSourceRepository);
    }

    @Provides @Singleton
    public AuthenticationService provideAuthenticationService(RedisSourceRepository redisSourceRepository){
        return new AuthenticationServiceImpl(redisSourceRepository);
    }
}
