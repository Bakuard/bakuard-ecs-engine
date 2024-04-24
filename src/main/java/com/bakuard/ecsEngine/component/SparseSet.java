package com.bakuard.ecsEngine.component;

import com.bakuard.collections.Bits;
import com.bakuard.ecsEngine.entity.Entity;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class SparseSet implements CompPool {

    private static final int INIT_CAPACITY = 10;


    private int[] entityIndexToComp;
    private Object[] comps;
    private Entity[] entities;
    private int size;
    private final Bits entityIndexes;

    public SparseSet() {
        entityIndexToComp = new int[INIT_CAPACITY];
        comps = new Object[INIT_CAPACITY];
        entities = new Entity[INIT_CAPACITY];
        entityIndexes = new Bits(128);

        Arrays.fill(entityIndexToComp, -1);
    }

    @Override
    public void attachComp(Entity entity, Object component) {
        int oldSize = size;
        growSparseArray(entity.index() + 1);
        growDensityArrays(size + 1);
        entityIndexToComp[entity.index()] = oldSize;
        comps[oldSize] = component;
        entities[oldSize] = entity;

        entityIndexes.growToIndex(entity.index()).set(entity.index());
    }

    @Override
    public void detachComp(Entity entity) {
        final int compIndex = entity.index() < entityIndexToComp.length ? entityIndexToComp[entity.index()] : -1;
        if(compIndex > -1) {
            final int lastCompsIndex = --size;

            entityIndexToComp[entities[lastCompsIndex].index()] = compIndex;
            entityIndexToComp[entity.index()] = -1;

            comps[compIndex] = comps[lastCompsIndex];
            comps[lastCompsIndex] = null;
            entities[compIndex] = entities[lastCompsIndex];
            entities[lastCompsIndex] = null;

            entityIndexes.clear(entity.index());
        }
    }

    public void swap(Entity first, Entity second) {
        if(first.index() < entityIndexToComp.length && second.index() < entityIndexToComp.length) {
            final int firstIndex = entityIndexToComp[first.index()];
            final int secondIndex = entityIndexToComp[second.index()];
            if(firstIndex > -1 && secondIndex > -1) {
                swapEntityIndexToComp(first.index(), second.index());
                swapEntities(firstIndex, secondIndex);
            }
        }
    }

    @Override
    public <T> T getComp(Entity entity) {
        final int compIndex = entity.index() < entityIndexToComp.length ?
                    entityIndexToComp[entity.index()] : -1;
        return compIndex > -1 ? (T)comps[compIndex] : null;
    }

    @Override
    public boolean hasComp(Entity entity) {
        return entity.index() < entityIndexToComp.length && entityIndexToComp[entity.index()] != -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public <T> void forEach(BiConsumer<Entity, T> consumer) {
        for(int i = size - 1; i >= 0; --i) {
            consumer.accept(entities[i], (T) comps[i]);
        }
    }

    @Override
    public Bits getEntityIndexes() {
        return entityIndexes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && getClass() == o.getClass()) {
            SparseSet sparseSet = (SparseSet)o;

            boolean result = size == sparseSet.size;
            for(int i = 0; i < size && result; ++i) {
                result = Objects.equals(sparseSet.comps[i], comps[i]);
            }
            for(int i = 0; i < size && result; ++i) {
                result = sparseSet.entities[i].equals(entities[i]);
            }
            return result;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = this.size;

        for(int i = 0; i < this.size; ++i) {
            result = result * 31 + Objects.hashCode(comps[i]);
        }
        for(int i = 0; i < this.size; ++i) {
            result = result * 31 + entities[i].hashCode();
        }

        return result;
    }

    @Override
    public String toString() {
        return "SparseSet{" +
                "size: " + size +
                ", comps: " + toString(comps, size) +
                ", entities: " + toString(entities, size) +
                '}';
    }


    private int calculateCapacity(int size) {
        return size + (size >>> 1);
    }

    private void growSparseArray(int newSize) {
        if(newSize > entityIndexToComp.length) {
            int oldSize = entityIndexToComp.length;
            entityIndexToComp = Arrays.copyOf(entityIndexToComp, calculateCapacity(newSize));
            Arrays.fill(entityIndexToComp, oldSize, entityIndexToComp.length, -1);
        }
    }

    private void growDensityArrays(int newSize) {
        if(newSize > size) {
            size = newSize;
            if(newSize > comps.length) {
                comps = Arrays.copyOf(comps, calculateCapacity(newSize));
                entities = Arrays.copyOf(entities, calculateCapacity(newSize));
            }
        }
    }


    private void swapEntityIndexToComp(int firstIndex, int secondIndex) {
        int firstValue = entityIndexToComp[firstIndex];
        entityIndexToComp[firstIndex] = entityIndexToComp[secondIndex];
        entityIndexToComp[secondIndex] = firstValue;
    }

    private void swapEntities(int firstIndex, int secondIndex) {
        Entity firstComp = entities[firstIndex];
        entities[firstIndex] = entities[secondIndex];
        entities[secondIndex] = firstComp;
    }


    private String toString(Object[] array, int arraySize) {
        StringBuilder sb = new StringBuilder("[");
        if(arraySize > 0) {
            sb.append(array[0]);
            for(int i = 1; i < arraySize; ++i) sb.append(',').append(array[i]);
        }
        sb.append(']');
        return sb.toString();
    }
}
