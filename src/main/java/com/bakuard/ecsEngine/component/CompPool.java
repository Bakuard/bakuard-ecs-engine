package com.bakuard.ecsEngine.component;

import com.bakuard.collections.Bits;
import com.bakuard.ecsEngine.entity.Entity;

import java.util.function.BiConsumer;

public interface CompPool {

    public void attachComp(Entity entity, Object component);

    public void detachComp(Entity entity);

    public <T> T getComp(Entity entity);

    public boolean hasComp(Entity entity);

    public int size();

    public <T> void forEach(BiConsumer<Entity, T> consumer);

    public Bits getEntityIndexes();
}
