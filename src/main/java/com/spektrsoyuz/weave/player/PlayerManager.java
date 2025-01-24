/*
 * Weave
 * Created by SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.weave.player;

import com.spektrsoyuz.weave.WeavePlugin;
import com.spektrsoyuz.weave.storage.RedisManager;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerManager {

    private final WeavePlugin plugin;
    private final RedisManager redisManager;
    private final Map<UUID, WeavePlayer> cache;
    private final boolean useRedis;

    public PlayerManager(final WeavePlugin plugin) {
        this.plugin = plugin;
        this.redisManager = new RedisManager(plugin);
        this.cache = new ConcurrentHashMap<>();
        this.useRedis = plugin.getConfig().getBoolean("redis.enabled", false);
    }

    public WeavePlayer loadPlayer(final Player player) {
        final UUID mojangId = player.getUniqueId();
        final WeavePlayer existing = getPlayer(player);

        if (existing != null) {
            existing.setUsername(player.getName());
            if (useRedis) {
                updatePlayer(existing);
            } else {
                cache.put(mojangId, existing);
            }
            return existing;
        }

        final WeavePlayer weavePlayer = new WeavePlayer(mojangId, player.getName(), "<gray>" + player.getName() + "</gray>", "", false);
        final WeavePlayer dbPlayer = plugin.getDatabaseManager().queryWeavePlayer(mojangId);

        if (dbPlayer != null) {
            weavePlayer.setUsername(player.getName());
            weavePlayer.setDisplayName(dbPlayer.getDisplayName());
            weavePlayer.setNickname(dbPlayer.getNickname());
            weavePlayer.setVanished(dbPlayer.isVanished());
        } else {
            plugin.getDatabaseManager().saveWeavePlayer(weavePlayer);
        }

        if (useRedis) {
            updatePlayer(weavePlayer);
        } else {
            cache.put(mojangId, weavePlayer);
        }
        return getPlayer(player);
    }

    public WeavePlayer getPlayer(final Player player) {
        return getPlayer(player.getUniqueId());
    }

    public WeavePlayer getPlayer(final UUID mojangId) {
        if (useRedis) {
            return redisManager.getPlayerData("weave_players:" + mojangId.toString());
        } else {
            return cache.get(mojangId);
        }
    }

    public WeavePlayer getPlayer(final String playerName) {
        for (WeavePlayer weavePlayer : getPlayers()) {
            if (weavePlayer.getUsername().equals(playerName)) {
                return weavePlayer;
            }
        }
        return null;
    }

    public Collection<WeavePlayer> getPlayers() {
        if (useRedis) {
            return redisManager.getAllPlayerData();
        } else {
            return cache.values();
        }
    }

    public void updatePlayer(final WeavePlayer weavePlayer) {
        if (useRedis) {
            redisManager.sendPlayerData(weavePlayer);
        } else {
            cache.put(weavePlayer.getMojangId(), weavePlayer);
        }
    }
}
