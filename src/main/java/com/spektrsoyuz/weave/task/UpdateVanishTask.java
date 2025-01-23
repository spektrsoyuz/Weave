/*
 * Weave
 * Created by SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.weave.task;

import com.spektrsoyuz.weave.WeavePlugin;
import com.spektrsoyuz.weave.player.WeavePlayer;
import com.spektrsoyuz.weave.util.WeaveUtil;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class UpdateVanishTask implements Runnable {

    private final WeavePlugin plugin;

    public UpdateVanishTask(final WeavePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!player.isOnline()) continue;

            WeavePlayer weavePlayer = plugin.getPlayerManager().getPlayer(player);
            if (weavePlayer == null) {
                plugin.getLogger().warning("UpdateVanishTask failed for " + player.getName());
                continue;
            }

            if (!player.hasPermission(WeaveUtil.PERMISSION_COMMAND_VANISH)) {
                weavePlayer.setVanished(false);
            }

            if (weavePlayer.isVanished()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
            }

            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                if (weavePlayer.isVanished()) {
                    onlinePlayer.hidePlayer(plugin, player);
                } else {
                    onlinePlayer.showPlayer(plugin, player);
                }
            }
        }
    }
}
