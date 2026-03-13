package com.bakuard.ecsEngine;

import com.bakuard.collections.Bits;
import com.bakuard.ecsEngine.component.CompPool;
import com.bakuard.ecsEngine.component.CompsManager;
import com.bakuard.ecsEngine.component.EntityFilter;
import com.bakuard.ecsEngine.component.MergeStrategy;
import com.bakuard.ecsEngine.component.TagsManager;
import com.bakuard.ecsEngine.entity.Entity;
import com.bakuard.ecsEngine.entity.EntityManager;

import java.util.Set;

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

	public void remove(Entity entity) {
		compsManager.detachAllComps(entity);
		tagsManager.detachAllTags(entity);
		tagsManager.detachUniqueTag(entity);
		entityManager.remove(entity);
	}

	public void unsafeRevive(Entity entity) {
		entityManager.unsafeRevive(entity);
	}

	public boolean isAlive(Entity entity) {
		return entityManager.isAlive(entity);
	}

	public boolean hasAliveEntityWith(int index) {
		return entityManager.hasAliveEntityWith(index);
	}

	public Entity getEntityByIndex(int index) {
		return entityManager.getEntityByIndex(index);
	}

	public int entityIndexHighWaterMark() {
		return entityManager.entityIndexHighWaterMark();
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


	public void attachComp(Entity entity, Object comp, String poolName) {
		compsManager.attachComp(entity, comp, poolName);
	}

	public void detachComp(Entity entity, String poolName) {
		compsManager.detachComp(entity, poolName);
	}

	public void detachComps(Entity entity, String... poolNames) {
		compsManager.detachComps(entity, poolNames);
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


	public <T> T getComp(Entity entity, String poolName) {
		return compsManager.getComp(entity, poolName);
	}

	public boolean hasComp(Entity entity, String poolName) {
		return compsManager.hasComp(entity, poolName);
	}

	public boolean hasAllComps(Entity entity, String... poolNames) {
		return compsManager.hasAllComps(entity, poolNames);
	}

	public boolean hasNoneOfComps(Entity entity, String... poolNames) {
		return compsManager.hasNoneOfComps(entity, poolNames);
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

	public void detachTagFromAllEntities(String tag) {
		tagsManager.detachTagFromAllEntities(tag);
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


	public Set<String> getAllTags() {
		return tagsManager.getAllTags();
	}

	public Set<String> getAllUniqueTags() {
		return tagsManager.getAllUniqueTags();
	}


	public void attachUniqueTag(Entity entity, String uniqueTag) {
		tagsManager.attachUniqueTag(entity, uniqueTag);
	}

	public void detachUniqueTag(String uniqueTag) {
		tagsManager.detachUniqueTag(uniqueTag);
	}

	public void detachUniqueTag(Entity entity) {
		tagsManager.detachUniqueTag(entity);
	}

	public Entity getEntityByUniqueTag(String uniqueTag) {
		return tagsManager.getEntityByUniqueTag(uniqueTag);
	}

	public String getUniqueTagByEntity(Entity entity) {
		return tagsManager.getUniqueTagByEntity(entity);
	}

	public boolean hasUniqueTag(Entity entity, String uniqueTag) {
		return tagsManager.hasUniqueTag(entity, uniqueTag);
	}

	public boolean existsUniqueTag(String uniqueTag) {
		return tagsManager.existsUniqueTag(uniqueTag);
	}


	public boolean haveEqualCompsAndTags(Entity firstEntity, Entity secondEntity) {
		return haveEqualTags(firstEntity, secondEntity) && haveEqualComps(firstEntity, secondEntity);
	}


	public Bits selectEntityIndexes(EntityFilter entityFilter) {
		Bits entityIndexes = new Bits(entityManager.getAliveEntitiesMask());

		if(entityFilter.isWithoutComps()) {
			compsManager.excludeEntityIndexesWithAny(entityIndexes);
		} else {
			compsManager.excludeEntityIndexesWithout(entityIndexes, entityFilter.getAllComps());
			compsManager.excludeEntityIndexesWith(entityIndexes, entityFilter.getNoneComps());
		}

		if(entityFilter.isWithoutTags()) {
			tagsManager.excludeEntityIndexesWithAny(entityIndexes);
		} else {
			tagsManager.excludeEntityIndexesWithout(entityIndexes, entityFilter.getAllTags());
			tagsManager.excludeEntityIndexesWith(entityIndexes, entityFilter.getNoneTags());
		}

		return entityIndexes;
	}


	public void merge(World src,
					  MergeStrategy<String, Bits> tagsMergeStrategy,
					  MergeStrategy<Entity, String> uniqueTagsMergeStrategy,
					  Iterable<String> mergedPools,
					  MergeStrategy<Entity, ?> compsMergeStrategy) {
		entityManager.copyFullStateFrom(src.entityManager);
		tagsManager.mergeUniqueTags(src.tagsManager, uniqueTagsMergeStrategy);
		tagsManager.mergeTags(src.tagsManager, tagsMergeStrategy);
		compsManager.merge(src.compsManager, mergedPools, compsMergeStrategy);
	}


	public World registerCompPool(CompPool pool) {
		compsManager.registerCompPool(pool);
		return this;
	}

	public <T, S extends CompPool> S getCompPool(Class<T> poolType) {
		return compsManager.getCompPool(poolType);
	}


	public World registerCompPool(CompPool pool, String poolName) {
		compsManager.registerCompPool(pool, poolName);
		return this;
	}

	public <S extends CompPool> S getCompPool(String poolName) {
		return compsManager.getCompPool(poolName);
	}

	public Set<String> getAllCompPoolNames() {
		return compsManager.getAllCompPoolNames();
	}
}
