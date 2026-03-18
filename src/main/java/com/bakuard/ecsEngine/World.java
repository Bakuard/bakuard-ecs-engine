package com.bakuard.ecsEngine;

import com.bakuard.collections.Bits;
import com.bakuard.collections.ReadableBits;
import com.bakuard.ecsEngine.component.CompPool;
import com.bakuard.ecsEngine.component.EntityFilter;
import com.bakuard.ecsEngine.entity.Entity;

import java.util.Set;

public interface World {

	public Entity create();

	public Entity create(Object... comps);

	public void remove(Entity entity);

	public void unsafeSet(Entity entity, boolean isAlive);

	public boolean isAlive(Entity entity);

	public boolean hasAliveEntityWith(int index);

	public Entity getEntityByIndex(int index);

	public int countReservedIndexes();


	public void attachComp(Entity entity, Object comp);

	public void attachComps(Entity entity, Object... comps);

	public <T> void detachComp(Entity entity, Class<T> compType);

	public void detachComps(Entity entity, Class<?>... compTypes);

	public void detachAllComps(Entity entity);

	public void replaceAllComps(Entity entity, Object... comps);


	public void attachComp(Entity entity, Object comp, String poolName);

	public void detachComp(Entity entity, String poolName);

	public void detachComps(Entity entity, String... poolNames);


	public <T> T getComp(Entity entity, Class<T> compType);

	public <T> boolean hasComp(Entity entity, Class<T> compType);

	public boolean hasAllComps(Entity entity, Class<?>... compTypes);

	public boolean hasNoneOfComps(Entity entity, Class<?>... compTypes);

	public boolean haveEqualComps(Entity firstEntity, Entity secondEntity);


	public <T> T getComp(Entity entity, String poolName);

	public boolean hasComp(Entity entity, String poolName);

	public boolean hasAllComps(Entity entity, String... poolNames);

	public boolean hasNoneOfComps(Entity entity, String... poolNames);


	public void attachTag(Entity entity, String tag);

	public void attachTags(Entity entity, String... tags);

	public void detachTag(Entity entity, String tag);

	public void detachTags(Entity entity, String... tags);

	public void detachAllTags(Entity entity);

	public void detachTagFromAllEntities(String tag);

	public void replaceAllTags(Entity entity, String... tags);


	public boolean hasTag(Entity entity, String tag);

	public boolean hasAllTags(Entity entity, String... tags);

	public boolean hasNoneOfTags(Entity entity, String... tags);

	public boolean haveEqualTags(Entity firstEntity, Entity secondEntity);

	public boolean existsTag(String tag);


	public Set<String> getAllTags();

	public Set<String> getAllUniqueTags();


	public void attachUniqueTag(Entity entity, String uniqueTag);

	public void detachUniqueTag(String uniqueTag);

	public void detachUniqueTag(Entity entity);

	public Entity getEntityByUniqueTag(String uniqueTag);

	public String getUniqueTagByEntity(Entity entity);

	public boolean hasUniqueTag(Entity entity, String uniqueTag);

	public boolean existsUniqueTag(String uniqueTag);


	public boolean haveEqualCompsAndTags(Entity firstEntity, Entity secondEntity);


	public Bits selectEntityIndexes(EntityFilter entityFilter);


	public World registerCompPool(CompPool pool);

	public <T, S extends CompPool> S getCompPool(Class<T> poolType);


	public World registerCompPool(CompPool pool, String poolName);

	public <S extends CompPool> S getCompPool(String poolName);

	public Set<String> getAllCompPoolNames();


	public ReadableBits getEntityIndexesByTag(String tag);

	public void setEntityIndexesForTag(String tag, Bits entityIndexes);
}
