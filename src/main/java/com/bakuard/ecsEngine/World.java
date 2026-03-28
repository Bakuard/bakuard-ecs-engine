package com.bakuard.ecsEngine;

import com.bakuard.collections.Bits;
import com.bakuard.collections.ReadableBits;
import com.bakuard.ecsEngine.component.CompPool;
import com.bakuard.ecsEngine.component.CompsManager;
import com.bakuard.ecsEngine.component.EntityFilter;
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

	public void unsafeSet(Entity entity, boolean isAlive) {
		entityManager.unsafeSet(entity, isAlive);
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

	public int countReservedIndexes() {
		return entityManager.countReservedIndexes();
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

	public boolean isTagAttachedToAnyEntity(String tag) {
		return tagsManager.isTagAttachedToAnyEntity(tag);
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

	public boolean isUniqueTagClaimed(String uniqueTag) {
		return tagsManager.isUniqueTagClaimed(uniqueTag);
	}


	public boolean haveEqualCompsAndTags(Entity firstEntity, Entity secondEntity) {
		return haveEqualTags(firstEntity, secondEntity) && haveEqualComps(firstEntity, secondEntity);
	}


	public Bits selectEntityIndexesMask(EntityFilter entityFilter) {
		Bits entityIndexesMask = new Bits(entityManager.getAliveEntitiesMask());

		if(entityFilter.isWithoutComps()) {
			compsManager.maskAndNotAll(entityIndexesMask);
		} else {
			compsManager.maskAnd(entityIndexesMask, entityFilter.getAllComps());
			compsManager.maskAndNot(entityIndexesMask, entityFilter.getNoneComps());
		}

		if(entityFilter.isWithoutTags()) {
			tagsManager.maskAndNotAll(entityIndexesMask);
		} else {
			tagsManager.maskAnd(entityIndexesMask, entityFilter.getAllTags());
			tagsManager.maskAndNot(entityIndexesMask, entityFilter.getNoneTags());
		}

		return entityIndexesMask;
	}


	public World registerCompPool(CompPool pool, String poolName) {
		compsManager.registerCompPool(pool, poolName);
		return this;
	}

	public <S extends CompPool> S getCompPool(String poolName) {
		return compsManager.getCompPool(poolName);
	}

	public boolean isCompPoolPopulated(String poolName) {
		return compsManager.isCompPoolPopulated(poolName);
	}

	public Set<String> getAllCompPoolNames() {
		return compsManager.getAllCompPoolNames();
	}


	public ReadableBits getEntityIndexesMaskByTag(String tag) {
		return tagsManager.getEntityIndexesMaskByTag(tag);
	}

	public void setEntityIndexesMaskForTag(String tag, ReadableBits entityIndexes) {
		tagsManager.setEntityIndexesMaskForTag(tag, entityIndexes);
	}
}
