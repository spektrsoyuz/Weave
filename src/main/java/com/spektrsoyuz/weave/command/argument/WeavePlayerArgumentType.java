package com.spektrsoyuz.weave.command.argument;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.spektrsoyuz.weave.WeavePlugin;
import com.spektrsoyuz.weave.player.WeavePlayer;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings({"UnstableApiUsage"})

public class WeavePlayerArgumentType implements CustomArgumentType<WeavePlayer, String> {

    private final WeavePlugin plugin;

    public WeavePlayerArgumentType(final WeavePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull WeavePlayer parse(@NotNull StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        while (reader.canRead() && !Character.isWhitespace(reader.peek())) {
            reader.skip();
        }

        final String text = reader.getString().substring(start, reader.getCursor());
        final WeavePlayer weavePlayer = plugin.getPlayerManager().getPlayer(text);

        if (weavePlayer != null) {
            return weavePlayer;
        } else {
            Message message = MessageComponentSerializer.message().serialize(Component.text("No player was found", NamedTextColor.RED));
            throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
        }
    }

    @Override
    public <S> @NotNull WeavePlayer parse(@NotNull StringReader reader, @NotNull S source) throws CommandSyntaxException {
        return CustomArgumentType.super.parse(reader, source);
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        return CustomArgumentType.super.listSuggestions(context, builder);
    }
}
