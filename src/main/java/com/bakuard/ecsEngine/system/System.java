package com.bakuard.ecsEngine.system;

import com.bakuard.ecsEngine.Game;
import com.bakuard.ecsEngine.gameLoop.GameTime;

@FunctionalInterface
public interface System {

    public void update(SystemMeta systemMeta, GameTime gameTime, Game game);

}
