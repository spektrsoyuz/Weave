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

        updatePlayer(weavePlayer);
        return getPlayer(player);
    }

    public WeavePlayer getPlayer(final Player player) {
        return getPlayer(player.getUniqueId());
    }

    public WeavePlayer getPlayer(final UUID mojangId) {
        return redisManager.getPlayerData("players:" + mojangId.toString());
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
        return redisManager.getAllPlayerData();
    }

    public void updatePlayer(final WeavePlayer weavePlayer) {
        redisManager.sendPlayerData(weavePlayer);
    }
}
