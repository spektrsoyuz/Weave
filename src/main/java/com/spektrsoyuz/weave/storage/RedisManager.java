package com.spektrsoyuz.weave.storage;

import com.spektrsoyuz.weave.WeavePlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class RedisManager {

    private final WeavePlugin plugin;
    private JedisPool pool;

    public RedisManager(final WeavePlugin plugin) {
        this.plugin = plugin;

        init();
    }

    private void init() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(2);
        poolConfig.setTestOnBorrow(true);

        pool = new JedisPool(poolConfig, "localhost", 6379, 10, "test");
    }

    private void set(final String key, final String value) {
        try (Jedis jedis = pool.getResource()) {
            jedis.set(key, value);
        }
    }

    private String get(final String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.get(key);
        }
    }
}
