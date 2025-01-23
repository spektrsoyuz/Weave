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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings({"UnstableApiUsage"})

public final class DisplayNameCommand {

    private final WeavePlugin plugin;
    private final MiniMessage mm;

    public DisplayNameCommand(final WeavePlugin plugin, Commands commands) {
        this.plugin = plugin;
        this.mm = MiniMessage.miniMessage();

        LiteralCommandNode<CommandSourceStack> node = Commands.literal("displayname")
                .requires(s -> s.getSender().hasPermission(WeaveUtil.PERMISSION_COMMAND_NAME) && s.getSender() instanceof Player)
                .executes(this::view)
                .then(Commands.literal("reset")
                        .executes(this::reset))
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(this::set))
                .build();

        commands.register(node, "Set your display name", List.of("name"));
    }

    private int view(final CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        WeavePlayer weavePlayer = plugin.getPlayerManager().getPlayer(player);
        if (weavePlayer != null) {
            String displayName = weavePlayer.getDisplayName();
            player.sendMessage(mm.deserialize(WeaveUtil.MESSAGE_COMMAND_NAME_VIEW, Placeholder.parsed("name", displayName)));
        }
        return Command.SINGLE_SUCCESS;
    }

    private int set(final CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        WeavePlayer weavePlayer = plugin.getPlayerManager().getPlayer(player);
        if (weavePlayer != null) {
            String displayName = ctx.getArgument("name", String.class);
            Component displayNameComponent = mm.deserialize(displayName);
            String rawDisplayName = PlainTextComponentSerializer.plainText().serialize(displayNameComponent);
            if (player.getName().equalsIgnoreCase(rawDisplayName)) {
                weavePlayer.setDisplayName(displayName);
                player.sendMessage(mm.deserialize(WeaveUtil.MESSAGE_COMMAND_NAME_SET, Placeholder.parsed("name", displayName)));
                plugin.getPlayerManager().updatePlayer(weavePlayer);
            } else {
                player.sendMessage(mm.deserialize(WeaveUtil.MESSAGE_COMMAND_NAME_INVALID));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private int reset(final CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        WeavePlayer weavePlayer = plugin.getPlayerManager().getPlayer(player);
        if (weavePlayer != null) {
            final String displayName = "<gray>" + player.getName() + "</gray>";
            weavePlayer.setDisplayName(displayName);
            player.sendMessage(mm.deserialize(WeaveUtil.MESSAGE_COMMAND_NAME_RESET));
            plugin.getPlayerManager().updatePlayer(weavePlayer);
        }
        return Command.SINGLE_SUCCESS;
    }
}
