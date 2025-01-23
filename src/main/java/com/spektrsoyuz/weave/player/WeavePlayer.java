/*
 * Weave
 * Created by SpektrSoyuz
 * All Rights Reserved
 */
package com.spektrsoyuz.weave.player;

import java.util.UUID;

public class WeavePlayer {

    private final UUID mojangId;
    private String username;
    private String displayName;
    private String nickname;
    private boolean vanished;

    public WeavePlayer(
            final UUID mojangId,
            final String username,
            final String displayName,
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
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
