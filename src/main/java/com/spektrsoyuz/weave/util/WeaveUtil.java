/*
 * Weave
 * Created by SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.weave.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WeaveUtil {
    /* Permission Nodes */
    public static final String PERMISSION_COMMAND_NAME = "weave.name";
    public static final String PERMISSION_COMMAND_NICKNAME = "weave.nickname";
    public static final String PERMISSION_COMMAND_VANISH = "weave.vanish";

    /* Messages */
    public static final String MESSAGE_SENDER_NOT_PLAYER = "<red>Only players may use this command.</red>";
    public static final String MESSAGE_COMMAND_VANISH_ENABLE = "<gold>Vanish has been</gold> <green>enabled</green><gold>.</gold>";
    public static final String MESSAGE_COMMAND_VANISH_DISABLE = "<gold>Vanish has been</gold> <red>disabled</red><gold>.</gold>";
    public static final String MESSAGE_COMMAND_NICKNAME_VIEW = "<gold>Your current nickname is:</gold> <nickname><gold>.\nSet your nickname with /nickname <nickname>.</gold>";
    public static final String MESSAGE_COMMAND_NICKNAME_RESET = "<gold>Nickname has been reset.</gold>";
    public static final String MESSAGE_COMMAND_NICKNAME_SET = "<gold>Nickname has been set to</gold> <nickname><gold>.</gold>";
    public static final String MESSAGE_COMMAND_NAME_VIEW = "<gold>Your display name is:</gold> <name><gold>.\nSet your nickname with /name <displayname>.</gold>";
    public static final String MESSAGE_COMMAND_NAME_RESET = "<gold>Display name has been reset.</gold>";
    public static final String MESSAGE_COMMAND_NAME_SET = "<gold>Display name has been set to</gold> <name><gold>.</gold>";
    public static final String MESSAGE_COMMAND_NAME_INVALID = "<red>Display name does not match your player name</red>";

    /* MiniMessage Resolvers */
    public static @NotNull TagResolver papiTag(final @NotNull Player player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            String placeholder = argumentQueue.popOr("papi tag requires an argument").value();
            String parsed = PlaceholderAPI.setPlaceholders(player, '%' + placeholder + '%');
            return Tag.selfClosingInserting(Component.text(parsed));
        });
    }
}
