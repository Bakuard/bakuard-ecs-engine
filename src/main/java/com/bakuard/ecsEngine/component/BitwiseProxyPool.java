package com.bakuard.ecsEngine.component;

import com.bakuard.collections.Bits;
import com.bakuard.ecsEngine.entity.Entity;

import java.util.Objects;
import java.util.function.BiConsumer;

public final class BitwiseProxyPool implements CompPool {

    private final Bits entityIndexes;
    private final CompPool inner;

    public BitwiseProxyPool(CompPool inner) {
        entityIndexes = new Bits(inner.size());
        this.inner = inner;
    }

    @Override
    public void attachComp(Entity entity, Object component) {
        inner.attachComp(entity, component);
        entityIndexes.growToIndex(entity.index()).set(entity.index());
    }

    @Override
    public void detachComp(Entity entity) {
        inner.detachComp(entity);
        if(entityIndexes.inBound(entity.index())) entityIndexes.clear(entity.index());
    }

    @Override
    public <T> T getComp(Entity entity) {
        return inner.getComp(entity);
    }

    @Override
    public boolean hasComp(Entity entity) {
        return inner.hasComp(entity);
    }

    @Override
    public int size() {
        return inner.size();
    }

    @Override
    public <T> void forEach(BiConsumer<Entity, T> consumer) {
        inner.forEach(consumer);
    }

    public Bits getEntityIndexes() {
        return entityIndexes;
    }

    public CompPool getInner() {
        return inner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BitwiseProxyPool that = (BitwiseProxyPool) o;
        return Objects.equals(entityIndexes, that.entityIndexes)
                && Objects.equals(inner, that.inner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityIndexes, inner);
    }

    @Override
    public String toString() {
        return "BitwiseProxyPool{"
                + "entityIndexes: " + entityIndexes
                + ", inner: " + inner
                + "}";
    }
}
