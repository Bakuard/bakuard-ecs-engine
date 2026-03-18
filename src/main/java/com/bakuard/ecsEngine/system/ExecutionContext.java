package com.bakuard.ecsEngine.system;

import com.bakuard.ecsEngine.World;
import com.bakuard.ecsEngine.event.EventManager;
import com.bakuard.ecsEngine.gameLoop.GameLoop;
import com.bakuard.ecsEngine.gameLoop.GameTime;

public record ExecutionContext(World world,
							   SystemManager systemManager,
							   EventManager eventManager,
							   GameLoop gameLoop,
							   GameTime gameTime) {}

