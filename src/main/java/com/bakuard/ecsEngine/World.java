package com.bakuard.ecsEngine;

import com.bakuard.collections.Bits;
import com.bakuard.collections.function.IndexBiConsumer;
import com.bakuard.ecsEngine.store.SparseArray;

import java.util.HashMap;
import java.util.Iterator;

public final class World {

    private final HashMap<String, Bits> tags;
    private final HashMap<Class<?>, ComponentStore> components;
    private final EntityManager entityManager;

    public World(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.tags = new HashMap<>();
        this.components = new HashMap<>();
    }

    public Entity create() {
        return entityManager.create();
    }

    public Entity create(Object... components) {
        Entity entity = entityManager.create();
        attach(entity, components);
        return entity;
    }

    public void remove(Entity entity) {
        if(entityManager.isAlive(entity)) {
            detachAll(entity);
            detachAllTags(entity);
            entityManager.remove(entity);
        }
    }

    public boolean isAlive(Entity entity) {
        return entityManager.isAlive(entity);
    }

    public void attach(Entity entity, Object component) {
        components.computeIfAbsent(component.getClass(), compType -> new SparseArray())
                .attach(entity, component);
    }

    public void attach(Entity entity, Object... components) {
        for(Object component : components) attach(entity, component);
    }

    public <T> void detach(Entity entity, Class<T> componentType) {
        ComponentStore store = components.get(componentType);
        if(store != null) store.detach(entity);
    }

    public void detach(Entity entity, Class<?>... componentTypes) {
        for(Class<?> componentType : componentTypes) detach(entity, componentType);
    }

    public void detachAll(Entity entity) {
        components.forEach((componentType, componentStore) -> componentStore.detach(entity));
    }

    public void replaceAll(Entity entity, Object... components) {
        detachAll(entity);
        attach(entity, components);
    }

    public void attachTag(Entity entity, String tag) {
        tags.computeIfAbsent(tag, key -> new Bits(entityManager.totalEntities()))
                .growToIndex(entity.index())
                .set(entity.index());
    }

    public void attachTags(Entity entity, String... tags) {
        for(String tag : tags) attachTag(entity, tag);
    }

    public void detachTag(Entity entity, String tag) {
        Bits bits = tags.get(tag);
        if(bits != null && bits.inBound(entity.index())) bits.clear(entity.index());
    }

    public void detachTags(Entity entity, String... tags) {
        for(String tag : tags) detachTag(entity, tag);
    }

    public void detachAllTags(Entity entity) {
        tags.forEach((key, bits) -> {
            if(bits.inBound(entity.index())) bits.clear(entity.index());
        });
    }

    public void replaceAllTags(Entity entity, String... tags) {
        detachAllTags(entity);
        attachTags(entity, tags);
    }

    public <T> T getComponent(Entity entity, Class<T> componentType) {
        T result = null;
        ComponentStore store = components.get(componentType);
        if(store != null) result = (T)store.get(entity);
        return result;
    }

    public boolean hasTag(Entity entity, String tag) {
        Bits bits = tags.get(tag);
        return  bits != null && bits.inBound(entity.index()) && bits.get(entity.index());
    }

    public boolean match(Entity entity, EntityFilter filter) {
        return false;
    }

    public boolean haveEqualComponentsAndTags(Entity firstEntity, Entity secondEntity) {
        return false;
    }

    public Iterator<Entity> select(EntityFilter filter) {
        return null;
    }

    public void forEach(EntityFilter filter, IndexBiConsumer<Entity> consumer) {

    }


    public <T> World registerComponentStore(ComponentStore store, Class<T> componentType) {
        components.put(componentType, store);
        return this;
    }

    public <T> ComponentStore getComponentStore(Class<T> componentType) {
        return components.get(componentType);
    }

    public EntityManager getEntityManager() {
        return null;
    }

    public void putSingletonComponent(String name, Object component) {

    }

    public <T> T getSingletonComponent(String name, Object component) {
        return null;
    }
}
