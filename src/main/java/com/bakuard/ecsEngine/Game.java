package com.bakuard.ecsEngine;

import com.bakuard.ecsEngine.event.EventManager;
import com.bakuard.ecsEngine.gameLoop.GameLoop;
import com.bakuard.ecsEngine.gameLoop.GameTime;
import com.bakuard.ecsEngine.gameLoop.UncaughtExceptionHandler;
import com.bakuard.ecsEngine.system.SystemManager;

public final class Game {

    public static Builder builder() {
        return new Builder();
    }


    private final World world;
    private final SystemManager systemManager;
    private final GameLoop gameLoop;
    private final EventManager eventManager;

    private Game(int numberUpdatePerSecond,
                 int maxFrameSkip,
                 int maxEventBufferSize,
                 UncaughtExceptionHandler handler) {
        world = new World();
        systemManager = new SystemManager(this);
        gameLoop = new GameLoop(numberUpdatePerSecond, maxFrameSkip, this, handler);
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


    public static final class Builder {

        private int numberUpdatePerSecond = 25;
        private int maxFrameSkip = 5;
        private int maxEventBufferSize = 250;
        private UncaughtExceptionHandler handler = (GameTime gameTime, Game game, Throwable exception) -> {};

        private Builder() {}

        public Builder setNumberUpdatePerSecond(int numberUpdatePerSecond) {
            this.numberUpdatePerSecond = numberUpdatePerSecond;
            return this;
        }

        public Builder setMaxFrameSkip(int maxFrameSkip) {
            this.maxFrameSkip = maxFrameSkip;
            return this;
        }

        public Builder setMaxEventBufferSize(int maxEventBufferSize) {
            this.maxEventBufferSize = maxEventBufferSize;
            return this;
        }

        public Builder setHandler(UncaughtExceptionHandler handler) {
            this.handler = handler;
            return this;
        }

        public Game build() {
            return new Game(numberUpdatePerSecond, maxFrameSkip, maxEventBufferSize, handler);
        }
    }
}
