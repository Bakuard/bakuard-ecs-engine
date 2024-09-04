package com.bakuard.ecsEngine.gameLoop;

public interface GameTime {

	public long getElapsedFrameInMillis();

	public long getUpdateIntervalInMillis();

	public long getTotalElapsedFramesInMillis();

	public long getTotalElapsedFrames();
}
