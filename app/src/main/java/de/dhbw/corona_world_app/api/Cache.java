package de.dhbw.corona_world_app.api;

import java.time.LocalDateTime;

public class Cache {

    private LocalDateTime lastTimeAccessedLifeDataWorld;

    public LocalDateTime getLastTimeAccessedLifeDataWorld() {
        return lastTimeAccessedLifeDataWorld;
    }

    public void setLastTimeAccessedLifeDataWorldToNow() {
        this.lastTimeAccessedLifeDataWorld = LocalDateTime.now();
    }
}
