package com.bakuard.ecsEngine.gameLoop;

public interface GameTime {

	public long getLastFrameInMillis();

	public long getUpdateIntervalInMillis();

	public long getTotalElapsedTimeInMillis();

	public long getTotalElapsedTimeInFrames();
}
