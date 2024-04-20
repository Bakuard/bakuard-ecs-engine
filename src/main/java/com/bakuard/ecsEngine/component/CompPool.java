package com.bakuard.ecsEngine.component;

import com.bakuard.ecsEngine.entity.Entity;

public interface CompPool extends ReadableCompPool {

    public void attachComp(Entity entity, Object component);

    public void detachComp(Entity entity);

}
