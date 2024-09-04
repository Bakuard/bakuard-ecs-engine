package com.bakuard.ecsEngine.gameLoop;

import com.bakuard.ecsEngine.Game;
import com.bakuard.ecsEngine.event.EventManager;
import com.bakuard.ecsEngine.system.SystemManager;

public final class GameLoop {

	public static final String INIT_GROUP = "INIT_GROUP";
	public static final String INPUT_GROUP = "INPUT_GROUP";
	public static final String WORK_GROUP = "WORK_GROUP";
	public static final String OUTPUT_GROUP = "OUTPUT_GROUP";
	public static final String SHUTDOWN_GROUP = "SHUTDOWN_GROUP";

	public enum State {
		RUN,
		SHUTDOWN,
		STOP
	}

	private final GameSession gameSession;

	public GameLoop(int numberUpdatePerSecond,
					int maxFrameSkip,
					Game game,
					UncaughtExceptionHandler handler) {
		if(numberUpdatePerSecond <= 0 || numberUpdatePerSecond > 1000) {
			throw new IllegalArgumentException(
					"Expected: numberUpdatePerSecond > 0 || numberUpdatePerSecond <= 1000. " +
					"Actual: " + numberUpdatePerSecond);
		} else if(maxFrameSkip <= 0) {
			throw new IllegalArgumentException("Expected: maxFrameSkip can't be less then zero. Actual: " + maxFrameSkip);
		}

		gameSession = new GameSession(numberUpdatePerSecond, maxFrameSkip, game, handler);
	}

	public void start() {
		gameSession.start();
	}

	public void stop() {
		gameSession.stop();
	}

	public State getCurrentState() {
		return gameSession.getCurrentState();
	}

	public String getGameSessionDescription() {
		return gameSession.toString();
	}


	private static final class GameTimeImpl implements GameTime {

		private final long updateIntervalInMillis;
		private long totalElapsedFramesMillis;
		private long elapsedFrameInMillis;
		private long totalElapsedFrames;

		public GameTimeImpl(long updateIntervalInMillis) {
			this.updateIntervalInMillis = updateIntervalInMillis;
			this.totalElapsedFramesMillis = updateIntervalInMillis;
			this.elapsedFrameInMillis = updateIntervalInMillis;
			this.totalElapsedFrames = 1;
		}

		void increaseTime(long elapsedFrameInMillis) {
			this.elapsedFrameInMillis = elapsedFrameInMillis;
			totalElapsedFramesMillis += elapsedFrameInMillis;
			++totalElapsedFrames;
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
		public long getTotalElapsedFramesInMillis() {
			return totalElapsedFramesMillis;
		}

		@Override
		public long getTotalElapsedFrames() {
			return totalElapsedFrames;
		}
	}


	private static final class GameSession implements Runnable {

		private final int numberUpdatePerSecond;
		private final int maxFrameSkip;
		private final Game game;
		private final UncaughtExceptionHandler handler;
		private volatile State currentState = State.STOP;

		public GameSession(int numberUpdatePerSecond,
						   int maxFrameSkip,
						   Game game,
						   UncaughtExceptionHandler handler) {
			this.numberUpdatePerSecond = numberUpdatePerSecond;
			this.maxFrameSkip = maxFrameSkip;
			this.game = game;
			this.handler = handler;
		}

		void start() {
			if(currentState == State.STOP) {
				currentState = State.RUN;
				Thread thread = new Thread(this);
				thread.start();
			}
		}

		void stop() {
			if(currentState == State.RUN) currentState = State.SHUTDOWN;
		}

		State getCurrentState() {
			return currentState;
		}

		@Override
		public void run() {
			final SystemManager systemManager = game.getSystemManager();
			final EventManager eventManager = game.getEventManager();
			final GameTimeImpl gameTime = new GameTimeImpl(1000L / numberUpdatePerSecond);

			try {
				eventManager.flushBufferOfAsyncEvents();
				systemManager.updateGroup(INIT_GROUP, gameTime);

				final long updateInterval = gameTime.getUpdateIntervalInMillis();
				long delta = gameTime.getUpdateIntervalInMillis(); //кол-во миллисекунд прошедшее с прошлого обновления
				while(currentState == State.RUN) {
					final long lastTime = java.lang.System.currentTimeMillis();
					eventManager.flushBufferOfAsyncEvents();
					systemManager.updateGroup(INPUT_GROUP, gameTime);
					for(int i = 0; delta >= updateInterval && i < maxFrameSkip; ++i) {
						systemManager.updateGroup(WORK_GROUP, gameTime);
						delta -= updateInterval;
					}
					systemManager.updateGroup(OUTPUT_GROUP, gameTime);
					final long elapsedTime = java.lang.System.currentTimeMillis() - lastTime;
					delta += elapsedTime;

					gameTime.increaseTime(elapsedTime);
				}

				systemManager.updateGroup(SHUTDOWN_GROUP, gameTime);
			} catch(Exception e) {
				currentState = State.SHUTDOWN;
				handler.handle(gameTime, game, e);
			} finally {
				currentState = State.STOP;
			}
		}

		@Override
		public String toString() {
			return "GameSession{"
					+ "numberUpdatePerSecond: " + numberUpdatePerSecond
					+ ", maxFrameSkip: " + maxFrameSkip
					+ "}";
		}
	}

}
