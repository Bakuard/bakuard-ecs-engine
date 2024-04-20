package com.bakuard.ecsEngine.component;

import com.bakuard.ecsEngine.entity.Entity;

import java.util.function.BiConsumer;

public interface ReadableCompPool {

    public <T> T getComp(Entity entity);

    public boolean hasComp(Entity entity);

    public int size();

    public <T> void forEach(BiConsumer<Entity, T> consumer);

}
