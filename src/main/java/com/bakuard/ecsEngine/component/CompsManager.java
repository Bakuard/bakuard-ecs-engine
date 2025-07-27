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
	private final HashMap<String, CompPool> compPools;

	public CompsManager(EntityManager entityManager) {
		this.entityManager = entityManager;
		this.compPools = new HashMap<>();
	}

	public void attachComp(Entity entity, Object comp) {
		attachComp(entity, comp.getClass().getName(), comp);
	}

	public void attachComp(Entity entity, String poolName, Object comp) {
		if(entityManager.isAlive(entity)) attachCompIgnoringEntityState(entity, poolName, comp);
	}

	public void attachComps(Entity entity, Object... comps) {
		if(entityManager.isAlive(entity)) {
			for(Object comp : comps) attachCompIgnoringEntityState(entity, comp.getClass().getName(), comp);
		}
	}

	public <T> void detachComp(Entity entity, Class<T> compType) {
		detachComp(entity, compType.getName());
	}

	public void detachComp(Entity entity, String poolName) {
		if(entityManager.isAlive(entity)) detachCompIgnoringEntityState(entity, poolName);
	}

	public void detachComps(Entity entity, Class<?>... compTypes) {
		detachComps(entity, map(compTypes));
	}

	public void detachComps(Entity entity, String... poolNames) {
		if(entityManager.isAlive(entity)) {
			for(String poolName : poolNames) detachCompIgnoringEntityState(entity, poolName);
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


	public <T> T getComp(Entity entity, Class<T> compType) {
		return getComp(entity, compType.getName());
	}

	public <T> T getComp(Entity entity, String poolName) {
		T result = null;
		if(entityManager.isAlive(entity)) {
			CompPool pool = compPools.get(poolName);
			if(pool != null) result = pool.getComp(entity);
		}
		return result;
	}

	public <T> boolean hasComp(Entity entity, Class<T> compType) {
		return hasComp(entity, compType.getName());
	}

	public boolean hasComp(Entity entity, String poolName) {
		return entityManager.isAlive(entity) && hasComponentIgnoringEntityState(entity, poolName);
	}

	public boolean hasAllComps(Entity entity, Class<?>... compTypes) {
		return hasAllComps(entity, map(compTypes));
	}

	public boolean hasAllComps(Entity entity, String... poolNames) {
		boolean result = entityManager.isAlive(entity);
		for(int i = 0; i < poolNames.length && result; i++)
			result = hasComponentIgnoringEntityState(entity, poolNames[i]);
		return result;
	}

	public boolean hasNoneOfComps(Entity entity, Class<?>... compTypes) {
		return hasNoneOfComps(entity, map(compTypes));
	}

	public boolean hasNoneOfComps(Entity entity, String... poolNames) {
		boolean result = entityManager.isAlive(entity);
		for(int i = 0; i < poolNames.length && result; i++)
			result = !hasComponentIgnoringEntityState(entity, poolNames[i]);
		return result;
	}

	public boolean haveEqualComps(Entity firstEntity, Entity secondEntity) {
		boolean isFirstAlive = entityManager.isAlive(firstEntity);
		boolean isSecondAlive = entityManager.isAlive(secondEntity);
		boolean result = isFirstAlive && isSecondAlive;

		if(result) {
			Iterator<CompPool> storeIterator = compPools.values().iterator();
			while(result && storeIterator.hasNext()) {
				CompPool store = storeIterator.next();
				result = Objects.equals(store.getComp(firstEntity), store.getComp(secondEntity));
			}
		}

		return result || (!isFirstAlive && !isSecondAlive);
	}


	public void excludeEntityIndexesWithout(Bits entityIndexes, ReadableLinearStructure<String> poolNames) {
		for(int i = 0; i < poolNames.size(); ++i) {
			CompPool pool = compPools.get(poolNames.get(i));
			if(pool != null) {
				entityIndexes.and(pool.getEntityIndexesMask());
			} else {
				entityIndexes.clearAll();
				break;
			}
		}
	}

	public void excludeEntityIndexesWith(Bits entityIndexes, ReadableLinearStructure<String> poolNames) {
		for(int i = 0; i < poolNames.size(); ++i) {
			CompPool pool = compPools.get(poolNames.get(i));
			if(pool != null) entityIndexes.andNot(pool.getEntityIndexesMask());
		}
	}


	public void registerCompPool(CompPool pool, String poolName) {
		compPools.put(poolName, pool);
	}

	public <S extends CompPool> S getCompPool(String poolName) {
		return (S) compPools.get(poolName);
	}


	private void attachCompIgnoringEntityState(Entity entity, String poolName, Object comp) {
		compPools.computeIfAbsent(poolName, compType -> new SparseSet()).attachComp(entity, comp);
	}

	private void detachCompIgnoringEntityState(Entity entity, String poolName) {
		CompPool store = compPools.get(poolName);
		if(store != null) store.detachComp(entity);
	}

	private boolean hasComponentIgnoringEntityState(Entity entity, String poolName) {
		CompPool compPool = compPools.get(poolName);
		return compPool != null && compPool.hasComp(entity);
	}

	private String[] map(Class<?>... compTypes) {
		String[] poolNames = new String[compTypes.length];
		for(int i = 0; i < compTypes.length; i++)
			poolNames[i] = compTypes[i].getName();
		return poolNames;
	}
}
