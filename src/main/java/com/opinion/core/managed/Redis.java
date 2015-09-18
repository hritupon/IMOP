package com.opinion.core.managed;

/**
 * Created by hritupon on 10/5/15.
 */

        import com.opinion.core.jedis.RedisPartitionPool;
        import com.opinion.models.enums.PartitionType;
        import com.google.inject.Inject;
        import io.dropwizard.lifecycle.Managed;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import redis.clients.jedis.*;
        import redis.clients.jedis.exceptions.JedisException;

/**
 * Created by sutirtha on 5/9/14.
 */
public class Redis implements Managed {
    private static final Logger logger = LoggerFactory.getLogger(Redis.class);

    private final RedisPartitionPool partitionPool;
    private final JedisPool configPool;

    @Inject
    public Redis(RedisPartitionPool partitionPool, JedisPool configPool) {
        this.partitionPool = partitionPool;
        this.configPool = configPool;
    }

    @Override
    public void start() throws Exception {
        logger.info("Connecting to Redis Master");
        logger.info("Connecting to Redis Slave Cluster");
        logger.info("Connecting to Redis Config Cluster");
    }

    @Override
    public void stop() throws Exception {
        this.partitionPool.destory();
        this.configPool.destroy();
    }

    public Jedis getConfigResource(){
        return configPool.getResource();
    }

    public void returnConfigResource(Jedis jedis){
        configPool.returnResource(jedis);
    }

    public void returnBrokenConfigResource(Jedis jedis){
        configPool.returnBrokenResource(jedis);
    }

    /*
     * Move the following methods to use namespace instead
     */
    public Jedis getMasterResource(PartitionType partitionType) {
        return partitionPool.getMasterResource(partitionType);
    }

    public Jedis getSlaveResource(PartitionType partitionType) {
        return partitionPool.getSlaveResource(partitionType);
    }

    public void returnMasterResource(Jedis jedis, PartitionType partitionType) {
        partitionPool.returnMasterResource(jedis, partitionType);
    }

    public void returnSlaveResource(Jedis jedis, PartitionType partitionType) {
        try {
            partitionPool.returnSlaveResource(jedis, partitionType);
        } catch (JedisException e) {
            e.printStackTrace();
            logger.warn("Slave resource:" + e.getMessage());
        }
    }

    public void returnBrokenMasterResource(Jedis jedis, PartitionType partitionType) {
        partitionPool.returnBrokenMasterResource(jedis, partitionType);
    }

    public void returnBrokenSlaveResource(Jedis jedis, PartitionType partitionType) {
        try {
            partitionPool.returnBrokenSlaveResource(jedis, partitionType);
        } catch (JedisException e) {
            e.printStackTrace();
            logger.warn("Broken slave resource:" + e.getMessage());
        }
    }

    public void pingMaster() throws Exception {
        partitionPool.pingMaster();
    }

    public void pingConfig() throws Exception{
        Jedis jedis = null;
        try{
            jedis = configPool.getResource();
            jedis.ping();
        }catch (Exception e){
            configPool.returnBrokenResource(jedis);
            throw new Exception();
        }finally {
            configPool.returnResource(jedis);
        }
    }

    public void pingSlaves() throws Exception {
        partitionPool.pingSlaves();
    }
}
