package com.bakuard.ecsEngine;

import com.bakuard.ecsEngine.gameLoop.GameLoop;
import com.bakuard.ecsEngine.system.SystemManager;

public final class Game {

    private final World world;
    private final SystemManager systemManager;
    private final GameLoop gameLoop;

    public Game() {
        this(25, 5);
    }

    public Game(int numberUpdatePerSecond, int maxFrameSkip) {
        world = new World();
        systemManager = new SystemManager(this);
        gameLoop = new GameLoop(numberUpdatePerSecond, maxFrameSkip, systemManager);
    }

    public World getWorld() {
        return world;
    }

    public SystemManager getSystemManager() {
        return systemManager;
    }

    public GameLoop getGameLoop() {
        return gameLoop;
    }
}
