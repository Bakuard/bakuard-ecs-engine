package com.bakuard.ecsEngine;

import com.bakuard.collections.DynamicArray;

import java.util.Arrays;
import java.util.Objects;

public final class EntityManager {

    private long[] entities;
    private int size;
    private int nextReusableEntity;

    public EntityManager() {
        entities = new long[10];
        nextReusableEntity = -1;
    }

    public Entity create() {
        if(nextReusableEntity == -1) {
            final int index = size;
            growToIndex(index);
            entities[index] = pack(index, 0);
            return new Entity(index, 0);
        } else {
            final int index = nextReusableEntity;
            final long packedEntity = entities[index];
            final int generation = extractGeneration(packedEntity);
            entities[index] = pack(index, generation);
            nextReusableEntity = extractIndex(packedEntity);
            return new Entity(index, generation);
        }
    }

    public void remove(Entity entity) {
        entities[entity.index()] = pack(nextReusableEntity, entity.generation() + 1);
        nextReusableEntity = entity.index();
    }

    public boolean isAlive(Entity entity) {
        final long packedEntity = entities[entity.index()];
        return extractGeneration(packedEntity) == entity.generation();
    }

    public EntityManagerSnapshot snapshot() {
        DynamicArray<Entity> alive = new DynamicArray<>();
        for(int i = 0; i < size; ++i) {
            long packedEntity = entities[i];
            if(extractIndex(packedEntity) == i) alive.append(unpack(packedEntity));
        }

        DynamicArray<Entity> notAlive = new DynamicArray<>();
        int index = nextReusableEntity;
        while(index != -1) {
            long packedEntity = entities[index];
            notAlive.append(new Entity(index, extractGeneration(packedEntity)));
            index = extractIndex(packedEntity);
        }

        return new EntityManagerSnapshot(alive, notAlive);
    }

    public void restore(EntityManagerSnapshot snapshot) {
        size = snapshot.alive().size() + snapshot.notAlive().size();
        entities = new long[size];
        nextReusableEntity = -1;

        for(Entity entity : snapshot.alive()) entities[entity.index()] = pack(entity);
        for(Entity entity : snapshot.notAlive()) {
            entities[entity.index()] = pack(nextReusableEntity, entity.generation());
            nextReusableEntity = entity.index();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityManager other = (EntityManager) o;

        boolean result = size == other.size && nextReusableEntity == other.nextReusableEntity;
        for(int i = 0; i < size && result; ++i) result = entities[i] == other.entities[i];
        return result;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size, nextReusableEntity);
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
            if(extractIndex(packedEntity) == i) {
                result.append("{index: ")
                        .append(i)
                        .append(", generation: ")
                        .append(extractGeneration(packedEntity))
                        .append("},");
            }
        }
        result.append("], notAlive: [");

        int index = nextReusableEntity;
        while(index != -1) {
            long packedEntity = entities[index];
            result.append("{index: ")
                    .append(index)
                    .append(", generation: ")
                    .append(extractGeneration(packedEntity))
                    .append("},");
            index = extractIndex(packedEntity);
        }
        result.append("] }");

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
}
