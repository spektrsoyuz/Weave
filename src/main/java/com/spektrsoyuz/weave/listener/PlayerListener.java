/*
 * Weave
 * Created by SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.weave.listener;

import com.spektrsoyuz.weave.WeavePlugin;
import com.spektrsoyuz.weave.player.WeavePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerListener implements Listener {

    private final WeavePlugin plugin;

    public PlayerListener(final WeavePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final WeavePlayer weavePlayer = plugin.getPlayerManager().loadPlayer(player);

        if (!weavePlayer.isVanished()) {
            event.joinMessage(null);
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final WeavePlayer weavePlayer = plugin.getPlayerManager().getPlayer(player);

        if (!weavePlayer.isVanished()) {
            event.quitMessage(null);
        }

        plugin.getDatabaseManager().saveWeavePlayer(weavePlayer);
    }
}
