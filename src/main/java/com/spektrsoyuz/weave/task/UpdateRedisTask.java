/*
 * Weave
 * Created by SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.weave.task;

import com.spektrsoyuz.weave.WeavePlugin;
import com.spektrsoyuz.weave.player.WeavePlayer;
import org.bukkit.entity.Player;

public class UpdateRedisTask implements Runnable {

    private final WeavePlugin plugin;

    public UpdateRedisTask(final WeavePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            WeavePlayer weavePlayer = plugin.getPlayerManager().getPlayer(player);
            if (weavePlayer != null) {
                plugin.getPlayerManager().updatePlayer(weavePlayer);
            }
        }
    }
}
