package com.bakuard.ecsEngine;

public interface ComponentStore {

    public void attach(Entity entity, Object component);

    public void detach(Entity entity);

    public Object get(Entity entity);
}
