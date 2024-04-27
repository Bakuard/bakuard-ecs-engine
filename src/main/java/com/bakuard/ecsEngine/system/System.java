package com.bakuard.ecsEngine.system;

import com.bakuard.ecsEngine.Game;

@FunctionalInterface
public interface System {

    public void update(SystemMeta systemMeta, Game game);

}
