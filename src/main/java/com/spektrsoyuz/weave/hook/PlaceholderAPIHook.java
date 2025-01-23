package com.spektrsoyuz.weave.hook;

import com.spektrsoyuz.weave.WeavePlugin;
import com.spektrsoyuz.weave.player.WeavePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PlaceholderAPIHook extends PlaceholderExpansion {

    private final WeavePlugin plugin;

    public PlaceholderAPIHook(final WeavePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "weave";
    }

    @Override
    public @NotNull String getAuthor() {
        return "SpektrSoyuz";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        WeavePlayer weavePlayer = plugin.getPlayerManager().getPlayer(player);

        if (weavePlayer != null) {
            switch (params) {
                case "displayname" -> {
                    return weavePlayer.getDisplayName();
                }
                case "nickname" -> {
                    return weavePlayer.getNickname();
                }
                case "nickname_full" -> {
                    return "<yellow>(" + weavePlayer.getNickname() + "<reset><yellow>)</yellow> ";
                }
            }
        }
        return null;
    }
}
