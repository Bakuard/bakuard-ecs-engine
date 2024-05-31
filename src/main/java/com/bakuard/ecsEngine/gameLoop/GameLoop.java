package com.bakuard.ecsEngine.gameLoop;

import com.bakuard.ecsEngine.Game;
import com.bakuard.ecsEngine.event.Event;
import com.bakuard.ecsEngine.system.SystemManager;

public final class GameLoop {

    public enum State {
        RUN,
        SHUTDOWN,
        STOP
    }

    public enum Group {
        INIT,
        INPUT,
        WORK,
        OUTPUT,
        DESTROY,
        CRASH
    }

    public enum SingletonEvent {
        UNHANDLED_EXCEPTION
    }

    private final GameSession gameSession;

    public GameLoop(int numberUpdatePerSecond,
                    int maxFrameSkip,
                    Game game) {
        if(numberUpdatePerSecond <= 0 || numberUpdatePerSecond > 1000) {
            throw new IllegalArgumentException(
                    "Expected: numberUpdatePerSecond > 0 || numberUpdatePerSecond <= 1000. " +
                    "Actual: " + numberUpdatePerSecond);
        } else if(maxFrameSkip <= 0) {
            throw new IllegalArgumentException("Expected: maxFrameSkip can't be less then zero. Actual: " + maxFrameSkip);
        }

        gameSession = new GameSession(numberUpdatePerSecond, maxFrameSkip, game);
    }

    public void start() {
        if(gameSession.prepareForStart()) {
            Thread thread = new Thread(gameSession);
            thread.start();
        }
    }

    public void stop() {
        gameSession.stop();
    }

    public State getCurrentState() {
        return gameSession.getCurrentState();
    }

    public String toString() {
        return gameSession.toString();
    }


    private static final class GameTimeImpl implements GameTime {

        private final long updateIntervalInMillis;
        private long elapsedFrameInMillis;

        public GameTimeImpl(long updateIntervalInMillis, long elapsedFrameInMillis) {
            this.updateIntervalInMillis = updateIntervalInMillis;
            this.elapsedFrameInMillis = elapsedFrameInMillis;
        }

        GameTimeImpl setElapsedFrameInMillis(long elapsedFrameInMillis) {
            this.elapsedFrameInMillis = elapsedFrameInMillis;
            return this;
        }

        @Override
        public long getElapsedFrameInMillis() {
            return elapsedFrameInMillis;
        }

        @Override
        public long getUpdateIntervalInMillis() {
            return updateIntervalInMillis;
        }

        @Override
        public String toString() {
            return "GameTimeImpl{"
                    + "updateIntervalInMillis: \"" + getUpdateIntervalInMillis() + "\""
                    + ", elapsedFrameInMillis: \"" + getElapsedFrameInMillis() + "\""
                    + "}";
        }
    }


    private static final class GameSession implements Runnable {

        private final int numberUpdatePerSecond;
        private final int maxFrameSkip;
        private final Game game;
        private volatile State currentState = State.STOP;
        private final GameTimeImpl gameTime;

        public GameSession(int numberUpdatePerSecond,
                           int maxFrameSkip,
                           Game game) {
            this.numberUpdatePerSecond = numberUpdatePerSecond;
            this.maxFrameSkip = maxFrameSkip;
            this.game = game;

            long updateIntervalInMillis = 1000L / numberUpdatePerSecond;
            gameTime = new GameTimeImpl(updateIntervalInMillis, updateIntervalInMillis);
        }

        boolean prepareForStart() {
            boolean canBeStarted = currentState == State.STOP;
            if(canBeStarted) currentState = State.RUN;
            return canBeStarted;
        }

        void stop() {
            if(currentState != State.STOP) currentState = State.SHUTDOWN;
        }

        State getCurrentState() {
            return currentState;
        }

        @Override
        public void run() {
            try {
                final SystemManager systemManager = game.getSystemManager();
                systemManager.updateGroup(Group.INIT.name(), gameTime);

                final long updateInterval = gameTime.getUpdateIntervalInMillis();
                long delta = gameTime.getUpdateIntervalInMillis(); //кол-во миллисекунд прошедшее с прошлого обновления
                while(currentState == State.RUN) {
                    final long lastTime = java.lang.System.currentTimeMillis();
                    systemManager.updateGroup(Group.INPUT.name(), gameTime);
                    for(int i = 0; delta >= updateInterval && i < maxFrameSkip; ++i) {
                        systemManager.updateGroup(Group.WORK.name(), gameTime);
                        delta -= updateInterval;
                    }
                    systemManager.updateGroup(Group.OUTPUT.name(), gameTime);
                    final long elapsedTime = java.lang.System.currentTimeMillis() - lastTime;
                    delta += elapsedTime;

                    gameTime.setElapsedFrameInMillis(elapsedTime);
                }

                systemManager.updateGroup(Group.DESTROY.name(), gameTime);
            } catch(Exception e) {
                game.getEventManager().pushSingletonEvent(new Event(SingletonEvent.UNHANDLED_EXCEPTION.name(), e));
                game.getSystemManager().updateGroup(Group.CRASH.name(), gameTime);
            } finally {
                currentState = State.STOP;
            }
        }

        @Override
        public String toString() {
            return "GameSession{"
                    + "numberUpdatePerSecond: \"" + numberUpdatePerSecond + "\""
                    + ", maxFrameSkip: \"" + maxFrameSkip + "\""
                    + ", currentState: \"" + getCurrentState() + "\""
                    + ", gameTime: " + gameTime
                    + "}";
        }
    }

}
