package com.opinion.core.jedis;

import com.google.common.collect.Iterators;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

import java.util.Iterator;
import java.util.List;

public class SlavePool extends Pool<Jedis> {

    private static final Logger logger = LoggerFactory.getLogger(SlavePool.class);

    public SlavePool(final GenericObjectPoolConfig poolConfig, List<JedisShardInfo> shards) {
        super(poolConfig, new RoundRobinFactory(shards));
    }

    public Jedis getResource() {
        try {
            return super.getResource();
        } catch (Exception e) {
            throw new JedisConnectionException(
                    "Could not get a resource from the pool", e);
        }
    }


    public void returnResource(final Jedis resource) {
        if (resource != null) {
            resource.resetState();
            returnResourceObject(resource);
        }
    }

    public void returnBrokenResource(final Jedis resource) {
        if (resource != null) {
            returnBrokenResourceObject(resource);
        }
    }

    public void destroy() {
        try {
            internalPool.close();
        } catch (Exception e) {
            throw new JedisException("Could not destroy the pool", e);
        }
    }

    private static class RoundRobinFactory implements PooledObjectFactory<Jedis> {
        private final List<JedisShardInfo> shards;
        private Iterator<JedisShardInfo> shardIterator;

        public RoundRobinFactory(List<JedisShardInfo> shards) {
            this.shards = shards;
            this.shardIterator = Iterators.cycle(this.shards);
        }

        public PooledObject<Jedis> makeObject() throws Exception {
            JedisShardInfo jsi;
            synchronized (shardIterator) {
                jsi = Iterators.getNext(shardIterator, shards.get(0));
            }

            Jedis jedis = new Jedis(jsi.getHost(), jsi.getPort());
            return new DefaultPooledObject<>(jedis);
        }

        public void destroyObject(PooledObject<Jedis> jedis) {
            try {
                try {
                    jedis.getObject().quit();
                } catch (Exception e) {
                }
                jedis.getObject().disconnect();
            } catch (Exception e) {
            }
        }

        public boolean validateObject(PooledObject<Jedis> jedis) {
            try {
                return jedis.getObject().ping().equals("PONG");
            } catch (Exception ex) {
                logger.error("Could not validate jedis: {}", ex.getMessage());
                return false;
            }
        }

        @Override
        public void activateObject(PooledObject<Jedis> p) throws Exception {

        }

        @Override
        public void passivateObject(PooledObject<Jedis> p) throws Exception {

        }
    }
}
