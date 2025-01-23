/*
 * Weave
 * Created by SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.weave.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.spektrsoyuz.weave.WeavePlugin;
import com.spektrsoyuz.weave.player.WeavePlayer;
import com.spektrsoyuz.weave.util.WeaveUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

@SuppressWarnings({"UnstableApiUsage"})

public final class VanishCommand {

    private final WeavePlugin plugin;
    private final MiniMessage mm;

    public VanishCommand(final WeavePlugin plugin, Commands commands) {
        this.plugin = plugin;
        this.mm = MiniMessage.miniMessage();

        LiteralCommandNode<CommandSourceStack> node = Commands.literal("vanish")
                .requires(stack -> stack.getSender().hasPermission(WeaveUtil.PERMISSION_COMMAND_VANISH))
                .executes(this::execute0)
                .build();

        commands.register(node, "Toggle vanish", List.of("v"));
    }

    private int execute0(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();

        if (sender instanceof Player player) {
            WeavePlayer weavePlayer = plugin.getPlayerManager().getPlayer(player);
            if (weavePlayer != null) {
                boolean vanished = weavePlayer.isVanished();
                if (vanished) {
                    player.sendMessage(mm.deserialize(WeaveUtil.MESSAGE_COMMAND_VANISH_DISABLE));
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                } else {
                    player.sendMessage(mm.deserialize(WeaveUtil.MESSAGE_COMMAND_VANISH_ENABLE));
                }
                weavePlayer.setVanished(!vanished);
                plugin.getPlayerManager().updatePlayer(weavePlayer);
                return Command.SINGLE_SUCCESS;
            }
        } else {
            sender.sendMessage(mm.deserialize(WeaveUtil.MESSAGE_SENDER_NOT_PLAYER));
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }
}
