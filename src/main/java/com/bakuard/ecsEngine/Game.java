package com.bakuard.ecsEngine;

import com.bakuard.ecsEngine.event.EventManager;
import com.bakuard.ecsEngine.gameLoop.GameLoop;
import com.bakuard.ecsEngine.system.SystemManager;

public final class Game {

    private final World world;
    private final SystemManager systemManager;
    private final GameLoop gameLoop;
    private final EventManager eventManager;

    public Game() {
        this(25, 5, 250);
    }

    public Game(int numberUpdatePerSecond, int maxFrameSkip, int maxEventBufferSize) {
        world = new World();
        systemManager = new SystemManager(this);
        gameLoop = new GameLoop(numberUpdatePerSecond, maxFrameSkip, this);
        eventManager = new EventManager(maxEventBufferSize);
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

    public EventManager getEventManager() {
        return eventManager;
    }
}
