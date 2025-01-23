package com.spektrsoyuz.weave.player;

import net.kyori.adventure.text.Component;

import java.util.UUID;

public class WeavePlayer {

    private final UUID mojangId;
    private String username;
    private Component displayName;
    private String nickname;
    private boolean vanished;

    public WeavePlayer(
            final UUID mojangId,
            final String username,
            final Component displayName,
            final String nickname,
            final boolean vanished
    ) {
        this.mojangId = mojangId;
        this.username = username;
        this.displayName = displayName;
        this.nickname = nickname;
        this.vanished = vanished;
    }

    public UUID getMojangId() {
        return mojangId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isVanished() {
        return vanished;
    }

    public void setVanished(boolean vanished) {
        this.vanished = vanished;
    }
}
