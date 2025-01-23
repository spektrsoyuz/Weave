package com.spektrsoyuz.weave.player;

import com.spektrsoyuz.weave.WeavePlugin;
import com.spektrsoyuz.weave.storage.RedisManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class PlayerManager {

    private final WeavePlugin plugin;
    private final RedisManager redisManager;

    public PlayerManager(final WeavePlugin plugin) {
        this.plugin = plugin;
        this.redisManager = new RedisManager(plugin);
    }

    public WeavePlayer loadPlayer(final Player player) {
        final WeavePlayer existing = getPlayer(player);
        if (existing != null) {
            existing.setUsername(player.getName());
            updatePlayer(existing);
            return existing;
        }

        WeavePlayer weavePlayer = new WeavePlayer(player.getUniqueId(), player.getName(), Component.empty(), "", false);

        // TODO query from database

        updatePlayer(weavePlayer);
        return weavePlayer;
    }

    public WeavePlayer getPlayer(final Player player) {
        return getPlayer(player.getUniqueId());
    }

    public WeavePlayer getPlayer(final UUID mojangId) {
        return redisManager.getPlayerData(mojangId.toString());
    }

    public void updatePlayer(final WeavePlayer weavePlayer) {
        redisManager.sendPlayerData(weavePlayer);
    }
}
