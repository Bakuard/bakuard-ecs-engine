package com.bakuard.ecsEngine.gameLoop;

import com.bakuard.ecsEngine.World;
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

	private final int numberUpdatePerSecond;
	private final int maxFrameSkip;
	private final UncaughtExceptionHandler handler;
	private final SystemManager systemManager;
	private final EventManager eventManager;
	private final World world;
	private volatile State currentState = State.STOP;

	public GameLoop(int numberUpdatePerSecond,
					int maxFrameSkip,
					UncaughtExceptionHandler handler,
					SystemManager systemManager,
					EventManager eventManager,
					World world) {
		if(numberUpdatePerSecond <= 0 || numberUpdatePerSecond > 1000) {
			throw new IllegalArgumentException(
					"Expected: numberUpdatePerSecond > 0 || numberUpdatePerSecond <= 1000. " +
					"Actual: " + numberUpdatePerSecond);
		} else if(maxFrameSkip <= 0) {
			throw new IllegalArgumentException("Expected: maxFrameSkip can't be less then zero. Actual: " + maxFrameSkip);
		}

		this.numberUpdatePerSecond = numberUpdatePerSecond;
		this.maxFrameSkip = maxFrameSkip;
		this.handler = handler;
		this.systemManager = systemManager;
		this.eventManager = eventManager;
		this.world = world;
	}

	public void start() {
		if(currentState == State.STOP) {
			currentState = State.RUN;
			Thread thread = new Thread(this::run);
			thread.start();
		}
	}

	public void stop() {
		if(currentState == State.RUN) currentState = State.SHUTDOWN;
	}

	public State getCurrentState() {
		return currentState;
	}

	@Override
	public String toString() {
		return "GameLoop{"
					   + "numberUpdatePerSecond: " + numberUpdatePerSecond
					   + ", maxFrameSkip: " + maxFrameSkip
					   + "}";
	}


	private void run() {
		final SystemManager systemManager = this.systemManager;
		final EventManager eventManager = this.eventManager;
		final World world = this.world;
		final GameTimeImpl gameTime = new GameTimeImpl(1000L / numberUpdatePerSecond);

		try {
			eventManager.flushBufferOfAsyncEvents();
			systemManager.updateGroup(INIT_GROUP, gameTime, this, eventManager, world);

			final long updateInterval = gameTime.getUpdateIntervalInMillis();
			long delta = updateInterval; //кол-во миллисекунд прошедшее с прошлого обновления
			while(currentState == State.RUN) {
				final long lastTime = java.lang.System.currentTimeMillis();
				eventManager.flushBufferOfAsyncEvents();
				systemManager.updateGroup(INPUT_GROUP, gameTime, this, eventManager, world);
				for(int i = 0; delta >= updateInterval && i < maxFrameSkip; ++i) {
					systemManager.updateGroup(WORK_GROUP, gameTime, this, eventManager, world);
					delta -= updateInterval;
				}
				systemManager.updateGroup(OUTPUT_GROUP, gameTime, this, eventManager, world);
				final long elapsedTime = java.lang.System.currentTimeMillis() - lastTime;
				delta += elapsedTime;

				gameTime.increaseTime(elapsedTime);
			}

			systemManager.updateGroup(SHUTDOWN_GROUP, gameTime, this, eventManager, world);
		} catch(Exception e) {
			currentState = State.SHUTDOWN;
			handler.handle(gameTime, eventManager, world, e);
		} finally {
			currentState = State.STOP;
		}
	}


	private static final class GameTimeImpl implements GameTime {

		private long lastFrameInMillis;
		private final long updateIntervalInMillis;
		private long totalElapsedTimeInMillis;
		private long totalElapsedTimeInFrames;

		public GameTimeImpl(long updateIntervalInMillis) {
			this.lastFrameInMillis = updateIntervalInMillis;
			this.updateIntervalInMillis = updateIntervalInMillis;
			this.totalElapsedTimeInMillis = updateIntervalInMillis;
			this.totalElapsedTimeInFrames = 1;
		}

		void increaseTime(long lastFrameInMillis) {
			this.lastFrameInMillis = lastFrameInMillis;
			totalElapsedTimeInMillis += lastFrameInMillis;
			++totalElapsedTimeInFrames;
		}

		@Override
		public long getLastFrameInMillis() {
			return lastFrameInMillis;
		}

		@Override
		public long getUpdateIntervalInMillis() {
			return updateIntervalInMillis;
		}

		@Override
		public long getTotalElapsedTimeInMillis() {
			return totalElapsedTimeInMillis;
		}

		@Override
		public long getTotalElapsedTimeInFrames() {
			return totalElapsedTimeInFrames;
		}

		@Override
		public String toString() {
			return "GameTimeImpl{"
						   + "lastFrameInMillis: " + lastFrameInMillis
						   + ", updateIntervalInMillis: " + updateIntervalInMillis
						   + ", totalElapsedTimeInMillis: " + totalElapsedTimeInMillis
						   + ", totalElapsedTimeInFrames: " + totalElapsedTimeInFrames
						   + "}";
		}
	}
}
