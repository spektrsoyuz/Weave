/*
 * Weave
 * Created by SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.weave.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.spektrsoyuz.weave.WeavePlugin;
import com.spektrsoyuz.weave.player.WeavePlayer;
import com.spektrsoyuz.weave.util.WeaveUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings({"UnstableApiUsage"})

public final class NicknameCommand {

    private final WeavePlugin plugin;
    private final MiniMessage mm;

    public NicknameCommand(final WeavePlugin plugin, Commands commands) {
        this.plugin = plugin;
        this.mm = MiniMessage.miniMessage();

        LiteralCommandNode<CommandSourceStack> node = Commands.literal("nickname")
                .requires(s -> s.getSender().hasPermission(WeaveUtil.PERMISSION_COMMAND_NICKNAME) && s.getSender() instanceof Player)
                .executes(this::view)
                .then(Commands.literal("reset")
                        .executes(this::reset))
                .then(Commands.argument("nickname", StringArgumentType.greedyString())
                        .executes(this::set))
                .build();

        commands.register(node, "Set your nickname", List.of("rpname"));
    }

    private int view(final CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        WeavePlayer weavePlayer = plugin.getPlayerManager().getPlayer(player);
        if (weavePlayer != null) {
            String nickname = weavePlayer.getNickname();
            player.sendMessage(mm.deserialize(WeaveUtil.MESSAGE_COMMAND_NICKNAME_VIEW, Placeholder.parsed("nickname", nickname)));
        }
        return Command.SINGLE_SUCCESS;
    }

    private int set(final CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        WeavePlayer weavePlayer = plugin.getPlayerManager().getPlayer(player);
        if (weavePlayer != null) {
            String nickname = ctx.getArgument("nickname", String.class);
            weavePlayer.setNickname(nickname);
            player.sendMessage(mm.deserialize(WeaveUtil.MESSAGE_COMMAND_NICKNAME_SET, Placeholder.parsed("nickname", nickname)));
            plugin.getPlayerManager().updatePlayer(weavePlayer);
        }
        return Command.SINGLE_SUCCESS;
    }

    private int reset(final CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        WeavePlayer weavePlayer = plugin.getPlayerManager().getPlayer(player);
        if (weavePlayer != null) {
            weavePlayer.setNickname("");
            player.sendMessage(mm.deserialize(WeaveUtil.MESSAGE_COMMAND_NICKNAME_RESET));
            plugin.getPlayerManager().updatePlayer(weavePlayer);
        }
        return Command.SINGLE_SUCCESS;
    }
}
