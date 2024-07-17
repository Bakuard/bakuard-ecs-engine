package com.bakuard.ecsEngine.gameLoop;

import com.bakuard.ecsEngine.Game;

public interface UncaughtExceptionHandler {

    public void handle(GameTime gameTime, Game game, Throwable exception);
    
}
