/*
 * Weave
 * Created by SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.weave.player;

import com.spektrsoyuz.weave.WeavePlugin;
import com.spektrsoyuz.weave.storage.RedisManager;
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
        final UUID mojangId = player.getUniqueId();
        final WeavePlayer existing = getPlayer(player);
        if (existing != null) {
            existing.setUsername(player.getName());
            updatePlayer(existing);
            return existing;
        }

        final String displayName = "<gray>" + player.getName() + "</gray>";
        final WeavePlayer weavePlayer = new WeavePlayer(mojangId, player.getName(), displayName, "", false);
        plugin.getDatabaseManager().queryWeavePlayer(mojangId).thenAccept(weavePlayerQuery -> {
            if (weavePlayerQuery.hasResults()) {
                final WeavePlayer found = weavePlayerQuery.getFirst();
                weavePlayer.setUsername(found.getUsername());
                weavePlayer.setDisplayName(found.getDisplayName());
                weavePlayer.setNickname(found.getNickname());
                weavePlayer.setVanished(found.isVanished());
            }
        });

        updatePlayer(weavePlayer);
        plugin.getDatabaseManager().saveWeavePlayer(weavePlayer);
        return weavePlayer;
    }

    public WeavePlayer getPlayer(final Player player) {
        return getPlayer(player.getUniqueId());
    }

    public WeavePlayer getPlayer(final UUID mojangId) {
        return redisManager.getPlayerData("players:" + mojangId.toString());
    }

    public void updatePlayer(final WeavePlayer weavePlayer) {
        redisManager.sendPlayerData(weavePlayer);
    }
}
