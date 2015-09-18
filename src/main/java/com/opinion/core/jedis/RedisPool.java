package com.opinion.core.jedis;

import com.opinion.models.enums.PartitionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Created with IntelliJ IDEA.
 * User: ramachandra.as
 * Date: 24/11/14
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
@Getter
@AllArgsConstructor
public class RedisPool {

    private final JedisPool jedisPool;

    private final SlavePool slavePool;

    private static final Logger logger = LoggerFactory.getLogger(RedisPool.class);

    public Jedis getMasterResource() {
        return jedisPool.getResource();
    }

    public Jedis getSlaveResource() {
        return slavePool.getResource();
    }

    public void returnMasterResource(Jedis jedis) {
        jedisPool.returnResource(jedis);
    }

    public void returnSlaveResource(Jedis jedis) {
        try {
            slavePool.returnResource(jedis);
        } catch (JedisException e) {
            e.printStackTrace();
            logger.info("Slave resource:" + e.getMessage());
        }
    }

    public void returnBrokenMasterResource(Jedis jedis) {
        jedisPool.returnBrokenResource(jedis);
    }

    public void returnBrokenSlaveResource(Jedis jedis) {
        try {
            slavePool.returnBrokenResource(jedis);
        } catch (JedisException e) {
            e.printStackTrace();
            logger.info("Broken slave resource:" + e.getMessage());
        }
    }

    public void destory(){
        logger.info("Disconnecting from Redis Master");
        jedisPool.destroy();
        logger.info("Disconnecting from Redis Slave Cluster");
        slavePool.destroy();
    }

    public void pingMaster() throws Exception {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.ping();
        } catch (Exception e) {
            jedisPool.returnBrokenResource(jedis);
            throw new Exception();
        } finally {
            jedisPool.returnResource(jedis);
        }

    }

    public void pingSlaves() throws Exception {
        Jedis jedis = null;
        try {
            jedis = slavePool.getResource();
            jedis.ping();
        } catch (Exception e) {
            slavePool.returnBrokenResource(jedis);
            throw new Exception();
        }finally {
            slavePool.returnResource(jedis);
        }
    }

}
