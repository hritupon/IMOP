package com.opinion.core.jedis;

import com.opinion.core.configuration.PartitionedRedis;
import com.opinion.core.configuration.RedisConfiguration;
import com.opinion.core.configuration.RedisHost;
import com.opinion.models.enums.PartitionType;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ramachandra.as
 * Date: 23/11/14
 * Time: 11:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class RedisPartitionPool {

    private final Map<PartitionType, RedisPool> redisPool;

    private static final Logger logger = LoggerFactory.getLogger(RedisPartitionPool.class);

    public RedisPartitionPool(RedisConfiguration redisConfiguration){
       this.redisPool = provideRedisPool(redisConfiguration);
    }

    private Map<PartitionType, RedisPool> provideRedisPool(RedisConfiguration redisConfiguration){
        Map<PartitionType, RedisPool> paritionPool = Maps.newHashMap();
        for(PartitionType partitionType : PartitionType.values()){
            PartitionedRedis partitionedRedis = redisConfiguration.getPartitions().get(partitionType);
            Preconditions.checkNotNull(partitionedRedis, "Inadequate hosts for the partition type "+partitionType);
            paritionPool.put(partitionType, new RedisPool(getJedisPool(redisConfiguration, partitionedRedis),
                    getSlavePool(redisConfiguration, partitionedRedis)));
        }
        return paritionPool;
    }

    private SlavePool getSlavePool(RedisConfiguration redisConfiguration, PartitionedRedis partitionedRedis){
        List<JedisShardInfo> shards = Lists.newArrayList();
        for(RedisHost host : partitionedRedis.getSlaveHosts()) {
            shards.add(new JedisShardInfo(host.getHost(), host.getPort(), redisConfiguration.getTimeout()));
        }
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisConfiguration.getSlaveMaxThreads());
        poolConfig.setLifo(false);
        poolConfig.setMaxWaitMillis(100);
        logger.info("Created round robin pool with {} max active threads", redisConfiguration.getSlaveMaxThreads());
        return new SlavePool(poolConfig, shards);
    }

    private JedisPool getJedisPool(RedisConfiguration redisConfiguration, PartitionedRedis partitionedRedis){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisConfiguration.getMasterMaxThreads());
        return new JedisPool(poolConfig, partitionedRedis.getMaster().getHost(),
                partitionedRedis.getMaster().getPort(), redisConfiguration.getTimeout());
    }

    public Jedis getMasterResource(PartitionType partitionType) {
        return redisPool.get(partitionType).getMasterResource();
    }

    public Jedis getSlaveResource(PartitionType partitionType) {
        return redisPool.get(partitionType).getSlaveResource();
    }

    public void returnMasterResource(Jedis jedis, PartitionType partitionType) {
        redisPool.get(partitionType).returnMasterResource(jedis);
    }

    public void returnSlaveResource(Jedis jedis, PartitionType partitionType) {
        redisPool.get(partitionType).returnSlaveResource(jedis);
    }

    public void returnBrokenMasterResource(Jedis jedis, PartitionType partitionType) {
        redisPool.get(partitionType).returnBrokenMasterResource(jedis);
    }

    public void returnBrokenSlaveResource(Jedis jedis, PartitionType partitionType) {
        redisPool.get(partitionType).returnBrokenSlaveResource(jedis);
    }

    public void destory(){
        for(RedisPool rPool1 : redisPool.values()){
            rPool1.destory();
        }
    }

    public void pingMaster() throws Exception {
        for(RedisPool rPool : redisPool.values()){
            rPool.pingMaster();
        }
    }

    public void pingSlaves() throws Exception {
        for(RedisPool redisPool1 : redisPool.values()){
            redisPool1.pingSlaves();
        }
    }
}
