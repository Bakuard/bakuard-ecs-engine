package com.bakuard.ecsEngine;

import com.bakuard.collections.ReadableLinearStructure;

public class EntityManager {

    public EntityManager() {}

    public Entity create() {
        return null;
    }

    public void remove(Entity entity) {

    }

    public boolean isAlive(Entity entity) {
        return false;
    }

    public ReadableLinearStructure<Entity> snapshotLiveEntities() {
        return null;
    }

    public ReadableLinearStructure<Entity> snapshotDeadEntities() {
        return null;
    }

    public void restore(ReadableLinearStructure<Entity> liveEntities,
                        ReadableLinearStructure<Entity> deadEntities) {

    }
}
