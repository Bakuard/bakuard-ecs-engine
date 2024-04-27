package com.bakuard.ecsEngine;

import com.bakuard.ecsEngine.system.SystemManager;

public class Game {

    private final World world;
    private final SystemManager systemManager;

    public Game() {
        world = new World();
        systemManager = new SystemManager(this);
    }

    public World getWorld() {
        return world;
    }

    public SystemManager getSystemManager() {
        return systemManager;
    }
}
