package com.bakuard.ecsEngine.component;

import com.bakuard.collections.Bits;
import com.bakuard.collections.ReadableLinearStructure;
import com.bakuard.ecsEngine.entity.Entity;
import com.bakuard.ecsEngine.entity.EntityManager;

import java.util.*;

public final class CompsManager {

	private final EntityManager entityManager;
	private final HashMap<String, CompPool> compPools;

	public CompsManager(EntityManager entityManager) {
		this.entityManager = entityManager;
		this.compPools = new HashMap<>();
	}

	private CompsManager(CompsManager compsManager, EntityManager entityManager) {
		this.entityManager = Objects.requireNonNull(entityManager);
		this.compPools = new HashMap<>(compsManager.compPools);
	}

	public CompsManager copyWith(EntityManager entityManager) {
		return new CompsManager(this, entityManager);
	}


	public void attachComp(Entity entity, Object comp) {
		attachComp(entity, comp, comp.getClass().getName());
	}

	public void attachComps(Entity entity, Object... comps) {
		entityManager.assertIsAlive(entity);
		for(Object comp : comps) attachCompIgnoringEntityState(entity, comp, comp.getClass().getName());
	}

	public <T> void detachComp(Entity entity, Class<T> compType) {
		detachComp(entity, compType.getName());
	}

	public void detachComps(Entity entity, Class<?>... compTypes) {
		detachComps(entity, map(compTypes));
	}

	public void detachAllComps(Entity entity) {
		entityManager.assertIsAlive(entity);
		compPools.forEach((poolName, compPool) -> compPool.detachComp(entity));
	}

	public void replaceAllComps(Entity entity, Object... comps) {
		detachAllComps(entity);
		attachComps(entity, comps);
	}


	public void attachComp(Entity entity, Object comp, String poolName) {
		entityManager.assertIsAlive(entity);
		attachCompIgnoringEntityState(entity, comp, poolName);
	}

	public void detachComp(Entity entity, String poolName) {
		entityManager.assertIsAlive(entity);
		detachCompIgnoringEntityState(entity, poolName);
	}

	public void detachComps(Entity entity, String... poolNames) {
		entityManager.assertIsAlive(entity);
		for(String poolName : poolNames) detachCompIgnoringEntityState(entity, poolName);
	}


	public <T> T getComp(Entity entity, Class<T> compType) {
		return getComp(entity, compType.getName());
	}

	public <T> boolean hasComp(Entity entity, Class<T> compType) {
		return hasComp(entity, compType.getName());
	}

	public boolean hasAllComps(Entity entity, Class<?>... compTypes) {
		return hasAllComps(entity, map(compTypes));
	}

	public boolean hasNoneOfComps(Entity entity, Class<?>... compTypes) {
		return hasNoneOfComps(entity, map(compTypes));
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


	public <T> T getComp(Entity entity, String poolName) {
		if(!entityManager.isAlive(entity)) return null;
		CompPool pool = compPools.get(poolName);
		return pool != null ? pool.getComp(entity) : null;
	}

	public boolean hasComp(Entity entity, String poolName) {
		return entityManager.isAlive(entity) && hasComponentIgnoringEntityState(entity, poolName);
	}

	public boolean hasAllComps(Entity entity, String... poolNames) {
		boolean result = entityManager.isAlive(entity);
		for(int i = 0; i < poolNames.length && result; i++)
			result = hasComponentIgnoringEntityState(entity, poolNames[i]);
		return result;
	}

	public boolean hasNoneOfComps(Entity entity, String... poolNames) {
		boolean result = entityManager.isAlive(entity);
		for(int i = 0; i < poolNames.length && result; i++)
			result = !hasComponentIgnoringEntityState(entity, poolNames[i]);
		return result;
	}


	public void maskAnd(Bits entityIndexesMask, ReadableLinearStructure<String> poolNames) {
		for(int i = 0; i < poolNames.size(); ++i) {
			CompPool pool = compPools.get(poolNames.get(i));
			if(pool != null) {
				entityIndexesMask.and(pool.getEntityIndexesMask());
			} else {
				entityIndexesMask.clearAll();
				break;
			}
		}
	}

	public void maskAndNot(Bits entityIndexesMask, ReadableLinearStructure<String> poolNames) {
		for(int i = 0; i < poolNames.size(); ++i) {
			CompPool pool = compPools.get(poolNames.get(i));
			if(pool != null) entityIndexesMask.andNot(pool.getEntityIndexesMask());
		}
	}

	public void maskAndNotAll(Bits entityIndexesMask) {
		compPools.forEach((poolName, pool) -> entityIndexesMask.andNot(pool.getEntityIndexesMask()));
	}


	public void registerCompPool(CompPool pool, String poolName) {
		compPools.put(poolName, pool);
	}

	public <S extends CompPool> S getCompPool(String poolName) {
		return (S) compPools.get(poolName);
	}

	public boolean isCompPoolPopulated(String poolName) {
		CompPool pool = compPools.get(poolName);
		return pool != null && !pool.isEmpty();
	}

	public Set<String> getAllCompPoolNames() {
		return new HashSet<>(compPools.keySet());
	}


	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		CompsManager that = (CompsManager) o;
		return entityManager.equals(that.entityManager) && compPools.equals(that.compPools);
	}

	@Override
	public int hashCode() {
		return Objects.hash(entityManager, compPools);
	}


	private void attachCompIgnoringEntityState(Entity entity, Object comp, String poolName) {
		getOrCreateCompPool(poolName).attachComp(entity, comp);
	}

	private void detachCompIgnoringEntityState(Entity entity, String poolName) {
		CompPool store = compPools.get(poolName);
		if(store != null) store.detachComp(entity);
	}

	private boolean hasComponentIgnoringEntityState(Entity entity, String poolName) {
		CompPool compPool = compPools.get(poolName);
		return compPool != null && compPool.hasComp(entity);
	}

	private CompPool getOrCreateCompPool(String poolName) {
		return compPools.computeIfAbsent(poolName, pn -> new SparseSet());
	}

	private String[] map(Class<?>... compTypes) {
		String[] poolNames = new String[compTypes.length];
		for(int i = 0; i < compTypes.length; i++)
			poolNames[i] = compTypes[i].getName();
		return poolNames;
	}
}
