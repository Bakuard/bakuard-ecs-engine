package com.bakuard.ecsEngine.gameLoop;

import com.bakuard.ecsEngine.World;
import com.bakuard.ecsEngine.event.EventManager;

public interface UncaughtExceptionHandler {

	public void handle(GameTime gameTime,
					   EventManager eventManager,
					   World world,
					   Throwable exception);

}
