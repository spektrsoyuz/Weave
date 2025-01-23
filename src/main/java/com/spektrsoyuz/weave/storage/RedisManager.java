/*
 * Weave
 * Created by SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.weave.storage;

import com.spektrsoyuz.weave.WeavePlugin;
import com.spektrsoyuz.weave.player.WeavePlayer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.UUID;

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

        String host = plugin.getConfig().getString("redis.host");
        int port = plugin.getConfig().getInt("redis.port");
        String password = plugin.getConfig().getString("redis.password");

        pool = new JedisPool(poolConfig, host, port, 10, password);
    }

    public void sendPlayerData(final WeavePlayer weavePlayer) {
        final String key = "players:" + weavePlayer.getMojangId();
        final String username = "username:" + weavePlayer.getUsername();
        final String displayName = ",displayName:" + weavePlayer.getDisplayName();
        final String nickname = ",nickname:" + weavePlayer.getNickname();
        final String vanished = ",vanished:" + weavePlayer.isVanished();

        set(key, username + displayName + nickname + vanished);
    }

    public WeavePlayer getPlayerData(final String key) {
        final String value = get(key);
        if (value != null) {
            final String[] values = value.split(",");
            final UUID mojangId = UUID.fromString(key.split(":")[1]);
            final String username = values[0].split(":")[1];
            final String displayName = values[1].split(":", -1)[1];
            final String nickname = values[2].split(":", -1)[1];
            final boolean vanished = Boolean.parseBoolean(values[3].split(":")[1]);

            return new WeavePlayer(mojangId, username, displayName, nickname, vanished);
        }
        return null;
    }

    private void set(final String key, final String value) {
        try (Jedis jedis = pool.getResource()) {
            jedis.set(key, value);
            jedis.expire(key, 5);
        }
    }

    private String get(final String key) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.get(key);
        }
    }
}
