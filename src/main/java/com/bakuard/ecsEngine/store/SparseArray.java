package com.bakuard.ecsEngine.store;

import com.bakuard.collections.DynamicArray;
import com.bakuard.ecsEngine.ComponentStore;
import com.bakuard.ecsEngine.Entity;

import java.util.Objects;

public final class SparseArray implements ComponentStore {

    private final DynamicArray<Object> array;

    public SparseArray() {
        array = new DynamicArray<>();
    }

    @Override
    public void attach(Entity entity, Object component) {
        array.replaceWithGrow(entity.index(), component);
    }

    @Override
    public void detach(Entity entity) {
        if(array.inBound(entity.index())) array.replace(entity.index(), null);
    }

    @Override
    public Object get(Entity entity) {
        Object component = null;
        if(array.inBound(entity.index())) component = array.get(entity.index());
        return component;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SparseArray that = (SparseArray) o;
        return Objects.equals(array, that.array);
    }

    @Override
    public int hashCode() {
        return Objects.hash(array);
    }

    @Override
    public String toString() {
        return "SparseArray{" + array + '}';
    }
}
