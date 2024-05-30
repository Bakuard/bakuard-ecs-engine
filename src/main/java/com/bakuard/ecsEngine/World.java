package com.bakuard.ecsEngine;

import com.bakuard.collections.Bits;
import com.bakuard.ecsEngine.component.CompPool;
import com.bakuard.ecsEngine.component.CompsManager;
import com.bakuard.ecsEngine.component.Filter;
import com.bakuard.ecsEngine.component.TagsManager;
import com.bakuard.ecsEngine.entity.Entity;
import com.bakuard.ecsEngine.entity.EntityManager;

public final class World {

    private final EntityManager entityManager;
    private final CompsManager compsManager;
    private final TagsManager tagsManager;

    public World() {
        this.entityManager = new EntityManager();
        this.compsManager = new CompsManager(entityManager);
        this.tagsManager = new TagsManager(entityManager);
    }


    public Entity create() {
        return entityManager.create();
    }

    public Entity create(Object... comps) {
        Entity entity = entityManager.create();
        compsManager.attachComps(entity, comps);
        return entity;
    }

    public Entity getEntityByIndex(int index) {
        return entityManager.getEntityByIndex(index);
    }

    public void remove(Entity entity) {
        compsManager.detachAllComps(entity);
        tagsManager.detachAllTags(entity);
        entityManager.remove(entity);
    }

    public boolean isAlive(Entity entity) {
        return entityManager.isAlive(entity);
    }


    public void attachComp(Entity entity, Object comp) {
        compsManager.attachComp(entity, comp);
    }

    public void attachComps(Entity entity, Object... comps) {
       compsManager.attachComps(entity, comps);
    }

    public <T> void detachComp(Entity entity, Class<T> compType) {
        compsManager.detachComp(entity, compType);
    }

    public void detachComps(Entity entity, Class<?>... compTypes) {
        compsManager.detachComps(entity, compTypes);
    }

    public void detachAllComps(Entity entity) {
        compsManager.detachAllComps(entity);
    }

    public void replaceAllComps(Entity entity, Object... comps) {
        compsManager.replaceAllComps(entity, comps);
    }


    public <T> T getComp(Entity entity, Class<T> compType) {
        return compsManager.getComp(entity, compType);
    }

    public <T> boolean hasComp(Entity entity, Class<T> compType) {
        return compsManager.hasComp(entity, compType);
    }

    public boolean hasAllComps(Entity entity, Class<?>... compTypes) {
        return compsManager.hasAllComps(entity, compTypes);
    }

    public boolean hasNoneOfComps(Entity entity, Class<?>... compTypes) {
        return compsManager.hasNoneOfComps(entity, compTypes);
    }

    public boolean haveEqualComps(Entity firstEntity, Entity secondEntity) {
        return compsManager.haveEqualComps(firstEntity, secondEntity);
    }


    public void attachTag(Entity entity, String tag) {
        tagsManager.attachTag(entity, tag);
    }

    public void attachTags(Entity entity, String... tags) {
        tagsManager.attachTags(entity, tags);
    }

    public void detachTag(Entity entity, String tag) {
        tagsManager.detachTag(entity, tag);
    }

    public void detachTags(Entity entity, String... tags) {
        tagsManager.detachTags(entity, tags);
    }

    public void detachAllTags(Entity entity) {
        tagsManager.detachAllTags(entity);
    }

    public void replaceAllTags(Entity entity, String... tags) {
        tagsManager.replaceAllTags(entity, tags);
    }


    public boolean hasTag(Entity entity, String tag) {
        return tagsManager.hasTag(entity, tag);
    }

    public boolean hasAllTags(Entity entity, String... tags) {
        return tagsManager.hasAllTags(entity, tags);
    }

    public boolean hasNoneOfTags(Entity entity, String... tags) {
        return tagsManager.hasNoneOfTags(entity, tags);
    }

    public boolean haveEqualTags(Entity firstEntity, Entity secondEntity) {
        return tagsManager.haveEqualTags(firstEntity, secondEntity);
    }


    public void attachSingletonTag(Entity entity, String singletonTag) {
        tagsManager.attachSingletonTag(entity, singletonTag);
    }

    public void detachSingletonTag(String singletonTag) {
        tagsManager.detachSingletonTag(singletonTag);
    }

    public Entity getEntityBySingletonTag(String singletonTag) {
        return tagsManager.getEntityBySingletonTag(singletonTag);
    }

    public boolean hasSingletonTag(Entity entity, String singletonTag) {
        return tagsManager.hasSingletonTag(entity, singletonTag);
    }


    public boolean haveEqualCompsAndTags(Entity firstEntity, Entity secondEntity) {
        return haveEqualTags(firstEntity, secondEntity) && haveEqualComps(firstEntity, secondEntity);
    }

    public Bits selectEntityIndexes(Filter filter) {
        Bits entityIndexes = entityManager.createAliveEntitiesMask();

        compsManager.excludeEntityIndexesWithout(entityIndexes, filter.getAllComps());
        tagsManager.excludeEntityIndexesWithout(entityIndexes, filter.getAllTags());
        compsManager.excludeEntityIndexesWith(entityIndexes, filter.getNoneComps());
        tagsManager.excludeEntityIndexesWith(entityIndexes, filter.getNoneTags());

        return entityIndexes;
    }


    public <T> World registerCompPool(CompPool pool, Class<T> compType) {
        compsManager.registerCompPool(pool, compType);
        return this;
    }

    public <T, S extends CompPool> S getCompPool(Class<T> compType) {
        return compsManager.getCompPool(compType);
    }
}
