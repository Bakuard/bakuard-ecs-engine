package com.bakuard.ecsEngine;

import com.bakuard.collections.Bits;
import com.bakuard.collections.DynamicArray;

import java.util.Arrays;
import java.util.Objects;

/**
 * Отвечает за создание и удаление сущностей ({@link Entity}). Также позволяет проверить, была ли
 * сущность удалена.
 */
public final class EntityManager {

    private static final int MIN_BITS_SIZE = 256;


    private long[] entities;
    private int size;
    private Bits aliveEntitiesMask;

    public EntityManager() {
        entities = new long[10];
        aliveEntitiesMask = new Bits(calculateBitsCapacity(10) + 1);
    }

    /**
     * Создает и возвращает новую сущность.
     * <br/><br/>
     * Менеджер сущностей переиспользует индексы недавно удаленных сущностей в порядке возрастания
     * их (индексов) значений.
     */
    public Entity create() {
        int nextReusableEntityIndex = aliveEntitiesMask.nextClearBit(0);

        if(nextReusableEntityIndex >= size) {
            growToIndex(nextReusableEntityIndex);
            aliveEntitiesMask.growToIndex(calculateBitsCapacity(nextReusableEntityIndex));
            aliveEntitiesMask.set(nextReusableEntityIndex);
            entities[nextReusableEntityIndex] = pack(nextReusableEntityIndex, 0);
            return new Entity(nextReusableEntityIndex, 0);
        } else {
            aliveEntitiesMask.set(nextReusableEntityIndex);
            return unpack(entities[nextReusableEntityIndex]);
        }
    }

    /**
     * Удаляет сущность. Если переданная сущность уже ранее удалялась, то ничего не делает.
     */
    public void remove(Entity entity) {
        if(isAlive(entity)) {
            int index = entity.index();
            entities[index] = pack(index, entity.generation() + 1);
            aliveEntitiesMask.clear(index);
        }
    }

    /**
     * Если указанная сущность не удалялась, то возвращает true, иначе - false.
     */
    public boolean isAlive(Entity entity) {
        final long packedEntity = entities[entity.index()];
        return extractGeneration(packedEntity) == entity.generation();
    }

    /**
     * Возвращает общее кол-во всех живых и зарезервированных для переиспользования сущностей.
     */
    public int totalEntities() {
        return size;
    }

    /**
     * Создает снимок текущего состояния данного менеджера сущностей. Снимок представляет собой
     * все созданные (включая удаленные) сущности через данный менеджер сущностей.
     */
    public EntityManagerSnapshot snapshot() {
        DynamicArray<Entity> alive = new DynamicArray<>();
        DynamicArray<Entity> notAlive = new DynamicArray<>();
        for(int i = 0; i < size; ++i) {
            long packedEntity = entities[i];
            if(aliveEntitiesMask.get(i)) alive.append(unpack(packedEntity));
            else notAlive.append(unpack(packedEntity));
        }

        return new EntityManagerSnapshot(alive, notAlive);
    }

    /**
     * Заменяет текущее состояние менеджера сущностей на состояние сохраненное в snapshot.
     */
    public void restore(EntityManagerSnapshot snapshot) {
        size = snapshot.alive().size() + snapshot.notAlive().size();
        entities = new long[size];
        aliveEntitiesMask = new Bits(calculateBitsCapacity(size) + 1);

        for(Entity entity : snapshot.alive()) {
            entities[entity.index()] = pack(entity);
            aliveEntitiesMask.set(entity.index());
        }
        for(Entity entity : snapshot.notAlive()) {
            entities[entity.index()] = pack(entity);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityManager other = (EntityManager) o;

        boolean result = size == other.size && aliveEntitiesMask.equals(other.aliveEntitiesMask);
        for(int i = 0; i < size && result; ++i) result = entities[i] == other.entities[i];
        return result;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size, aliveEntitiesMask);
        result = 31 * result + Arrays.hashCode(entities);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("EntityManager{ totalEntities: ")
                .append(size)
                .append(", alive: [");

        for(int i = 0; i < size; ++i) {
            long packedEntity = entities[i];
            if(aliveEntitiesMask.get(i)) {
                result.append("{index: ")
                        .append(i)
                        .append(", generation: ")
                        .append(extractGeneration(packedEntity))
                        .append("},");
            }
        }
        result.append("], notAlive: [");

        for(int i = 0; i < size; ++i) {
            long packedEntity = entities[i];
            if(!aliveEntitiesMask.get(i)) {
                result.append("{index: ")
                        .append(i)
                        .append(", generation: ")
                        .append(extractGeneration(packedEntity))
                        .append("},");
            }
        }
        result.append("], aliveEntitiesMask: ")
                .append(aliveEntitiesMask)
                .append('}');

        return result.toString();
    }


    private long pack(Entity entity) {
        return pack(entity.index(), entity.generation());
    }

    private long pack(int index, int generation) {
        return (long)index << 32 | (long)generation;
    }

    private Entity unpack(long entity) {
        return new Entity(extractIndex(entity), extractGeneration(entity));
    }

    private int extractIndex(long entity) {
        return (int) (entity >>> 32);
    }

    private int extractGeneration(long entity) {
        return (int) entity;
    }

    private void growToIndex(int index) {
        final int newSize = index + 1;
        if(newSize > size) {
            size = newSize;
            if(newSize > entities.length) {
                entities = Arrays.copyOf(entities, calculateCapacity(newSize));
            }
        }
    }

    private int calculateCapacity(int size) {
        return size + (size >>> 1);
    }

    private int calculateBitsCapacity(int index) {
        int pageNumber = index / MIN_BITS_SIZE;
        return pageNumber * MIN_BITS_SIZE + MIN_BITS_SIZE;
    }
}
