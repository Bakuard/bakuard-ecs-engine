package com.bakuard.ecsEngine.component;

import com.bakuard.collections.Bits;
import com.bakuard.collections.ReadableLinearStructure;
import com.bakuard.ecsEngine.entity.Entity;
import com.bakuard.ecsEngine.entity.EntityManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public final class CompsManager {

    private final EntityManager entityManager;
    private final HashMap<Class<?>, BitwiseProxyPool> compPools;

    public CompsManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.compPools = new HashMap<>();
    }

    public void attachComp(Entity entity, Object comp) {
        if(entityManager.isAlive(entity)) attachCompIgnoringEntityState(entity, comp);
    }

    public void attachComps(Entity entity, Object... comps) {
        if(entityManager.isAlive(entity)) {
            for(Object comp : comps) attachCompIgnoringEntityState(entity, comp);
        }
    }

    public <T> void detachComp(Entity entity, Class<T> compType) {
        if(entityManager.isAlive(entity)) detachCompIgnoringEntityState(entity, compType);
    }

    public void detachComps(Entity entity, Class<?>... compTypes) {
        if(entityManager.isAlive(entity)) {
            for(Class<?> compType : compTypes) detachCompIgnoringEntityState(entity, compType);
        }
    }

    public void detachAllComps(Entity entity) {
        if(entityManager.isAlive(entity)) {
            compPools.forEach((compType, compPool) -> compPool.detachComp(entity));
        }
    }

    public void replaceAllComps(Entity entity, Object... comps) {
        detachAllComps(entity);
        attachComps(entity, comps);
    }


    public <T> T getComponent(Entity entity, Class<T> compType) {
        T result = null;
        if(entityManager.isAlive(entity)) {
            CompPool pool = compPools.get(compType);
            if(pool != null) result = pool.getComp(entity);
        }
        return result;
    }

    public <T> boolean hasComponent(Entity entity, Class<T> compType) {
        return entityManager.isAlive(entity) && hasComponentIgnoringEntityState(entity, compType);
    }

    public boolean hasAllComponents(Entity entity, Class<?>... compTypes) {
        boolean result = entityManager.isAlive(entity);
        for(int i = 0; i < compTypes.length && result; i++) {
            Class<?> compType = compTypes[i];
            result = hasComponentIgnoringEntityState(entity, compType);
        }
        return result;
    }

    public boolean hasNoneOfComponents(Entity entity, Class<?>... compTypes) {
        boolean result = entityManager.isAlive(entity);
        for(int i = 0; i < compTypes.length && result; i++) {
            Class<?> compType = compTypes[i];
            result = !hasComponentIgnoringEntityState(entity, compType);
        }
        return result;
    }

    public boolean haveEqualComponents(Entity firstEntity, Entity secondEntity) {
        boolean isFirstAlive = entityManager.isAlive(firstEntity);
        boolean isSecondAlive = entityManager.isAlive(secondEntity);
        boolean result = isFirstAlive && isSecondAlive;

        if(result) {
            Iterator<BitwiseProxyPool> storeIterator = compPools.values().iterator();
            while(result && storeIterator.hasNext()) {
                CompPool store = storeIterator.next();
                result = Objects.equals(store.getComp(firstEntity), store.getComp(secondEntity));
            }
        }

        return result || (!isFirstAlive && !isSecondAlive);
    }


    public void excludeEntityIndexesWithout(Bits entityIndexes, ReadableLinearStructure<Class<?>> compTypes) {
        for(int i = 0; i < compTypes.size(); ++i) {
            Class<?> compType = compTypes.get(i);
            BitwiseProxyPool pool = compPools.get(compType);
            if(pool != null) {
                entityIndexes.and(pool.getEntityIndexes());
            } else {
                entityIndexes.clearAll();
                break;
            }
        }
    }

    public void excludeEntityIndexesWith(Bits entityIndexes, ReadableLinearStructure<Class<?>> compTypes) {
        for(int i = 0; i < compTypes.size(); ++i) {
            Class<?> compType = compTypes.get(i);
            BitwiseProxyPool pool = compPools.get(compType);
            if(pool != null) entityIndexes.andNot(pool.getEntityIndexes());
        }
    }


    public <T> CompsManager registerComponentPool(CompPool pool, Class<T> compType) {
        compPools.put(compType, new BitwiseProxyPool(pool));
        return this;
    }

    public <T> CompPool getComponentPool(Class<T> compType) {
        return compPools.get(compType);
    }


    private void attachCompIgnoringEntityState(Entity entity, Object comp) {
        compPools.computeIfAbsent(
                        comp.getClass(),
                        compType -> new BitwiseProxyPool(new SparseSet())
                )
                .attachComp(entity, comp);
    }

    private <T> void detachCompIgnoringEntityState(Entity entity, Class<T> compType) {
        CompPool store = compPools.get(compType);
        if(store != null) store.detachComp(entity);
    }

    private <T> boolean hasComponentIgnoringEntityState(Entity entity, Class<T> compType) {
        ReadableCompPool compPool = compPools.get(compType);
        return compPool != null && compPool.hasComp(entity);
    }
}
