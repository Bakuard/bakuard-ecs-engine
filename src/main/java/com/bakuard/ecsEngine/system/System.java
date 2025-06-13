package com.bakuard.ecsEngine.system;

import com.bakuard.ecsEngine.World;
import com.bakuard.ecsEngine.event.EventManager;
import com.bakuard.ecsEngine.gameLoop.GameLoop;
import com.bakuard.ecsEngine.gameLoop.GameTime;

@FunctionalInterface
public interface System {

	public void update(SystemMeta systemMeta,
					   SystemManager systemManager,
					   GameTime gameTime,
					   GameLoop gameLoop,
					   EventManager eventManager,
					   World world);

}
