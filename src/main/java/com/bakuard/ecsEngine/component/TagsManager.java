package com.bakuard.ecsEngine.component;

import com.bakuard.collections.Bits;
import com.bakuard.collections.ReadableBits;
import com.bakuard.collections.ReadableLinearStructure;
import com.bakuard.ecsEngine.entity.Entity;
import com.bakuard.ecsEngine.entity.EntityManager;

import java.util.*;

public final class TagsManager {

	private final EntityManager entityManager;
	private final HashMap<String, Bits> tagMasks;

	public TagsManager(EntityManager entityManager) {
		this.entityManager = entityManager;
		this.tagMasks = new HashMap<>();
	}

	private TagsManager(TagsManager tagsManager, EntityManager entityManager) {
		this.entityManager = Objects.requireNonNull(entityManager);
		this.tagMasks = new HashMap<>(tagsManager.tagMasks);
	}

	public TagsManager copyWith(EntityManager entityManager) {
		return new TagsManager(this, entityManager);
	}


	public void attachTag(Entity entity, String tag) {
		entityManager.assertIsAlive(entity);
		attachTagIgnoringEntityState(entity, tag);
	}

	public void attachTags(Entity entity, String... tags) {
		entityManager.assertIsAlive(entity);
		for(String tag : tags) attachTagIgnoringEntityState(entity, tag);
	}

	public void detachTag(Entity entity, String tag) {
		entityManager.assertIsAlive(entity);
		detachTagIgnoringEntityState(entity, tag);
	}

	public void detachTags(Entity entity, String... tags) {
		entityManager.assertIsAlive(entity);
		for(String tag : tags) detachTagIgnoringEntityState(entity, tag);
	}

	public void detachAllTags(Entity entity) {
		entityManager.assertIsAlive(entity);
		tagMasks.forEach((key, bits) -> {
			if(bits.inBound(entity.index())) bits.clear(entity.index());
		});
	}

	public void detachTagFromAllEntities(String tag) {
		tagMasks.remove(tag);
	}

	public void replaceAllTags(Entity entity, String... tags) {
		detachAllTags(entity);
		attachTags(entity, tags);
	}


	public boolean hasTag(Entity entity, String tag) {
		return entityManager.isAlive(entity) && hasTagIgnoringEntityState(entity, tag);
	}

	public boolean hasAllTags(Entity entity, String... tags) {
		boolean result = entityManager.isAlive(entity);
		for(int i = 0; i < tags.length && result; ++i) {
			result = hasTagIgnoringEntityState(entity, tags[i]);
		}
		return result;
	}

	public boolean hasNoneOfTags(Entity entity, String... tags) {
		boolean result = entityManager.isAlive(entity);
		for(int i = 0; i < tags.length && result; ++i) {
			result = !hasTagIgnoringEntityState(entity, tags[i]);
		}
		return result;
	}

	public boolean haveEqualTags(Entity firstEntity, Entity secondEntity) {
		boolean isFirstAlive = entityManager.isAlive(firstEntity);
		boolean isSecondAlive = entityManager.isAlive(secondEntity);
		boolean result = isFirstAlive && isSecondAlive;

		if(result) {
			Iterator<Bits> tagsIterator = tagMasks.values().iterator();
			while(result && tagsIterator.hasNext()) {
				Bits mask = tagsIterator.next();
				result = ( mask.inBound(firstEntity.index()) && mask.get(firstEntity.index()) ) ==
						( mask.inBound(secondEntity.index()) && mask.get(secondEntity.index()) );
			}
		}

		return result || (!isFirstAlive && !isSecondAlive);
	}

	public boolean isTagAttachedToAnyEntity(String tag) {
		Bits tagMask = tagMasks.get(tag);
		return tagMask != null && !tagMask.isClear();
	}


	public Set<String> getAllTags() {
		return new HashSet<>(tagMasks.keySet());
	}


	public void maskAnd(Bits entityIndexesMask, ReadableLinearStructure<String> tagNames) {
		for(int i = 0; i < tagNames.size(); ++i) {
			String tagName = tagNames.get(i);
			Bits mask = tagMasks.get(tagName);
			if(mask != null) {
				entityIndexesMask.and(mask);
			} else {
				entityIndexesMask.clearAll();
				break;
			}
		}
	}

	public void maskAndNot(Bits entityIndexesMask, ReadableLinearStructure<String> tagNames) {
		for(int i = 0; i < tagNames.size(); ++i) {
			String tagName = tagNames.get(i);
			Bits mask = tagMasks.get(tagName);
			if(mask != null) entityIndexesMask.andNot(mask);
		}
	}

	public void maskAndNotAll(Bits entityIndexesMask) {
		tagMasks.forEach((tagName, mask) -> entityIndexesMask.andNot(mask));
	}


	public ReadableBits getEntityIndexesMaskByTag(String tag) {
		return tagMasks.get(tag);
	}

	public void setEntityIndexesMaskForTag(String tag, ReadableBits entityIndexes) {
		tagMasks.put(tag, new Bits(entityIndexes));
	}


	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		TagsManager that = (TagsManager) o;
		return entityManager.equals(that.entityManager) && tagMasks.equals(that.tagMasks);
	}

	@Override
	public int hashCode() {
		return Objects.hash(entityManager, tagMasks);
	}


	private void attachTagIgnoringEntityState(Entity entity, String tag) {
		tagMasks.computeIfAbsent(tag, key -> new Bits(entity.index() + 1))
				.growToIndex(entity.index())
				.set(entity.index());
	}

	private void detachTagIgnoringEntityState(Entity entity, String tag) {
		Bits bits = tagMasks.get(tag);
		if(bits != null && bits.inBound(entity.index())) bits.clear(entity.index());
	}

	private boolean hasTagIgnoringEntityState(Entity entity, String tag) {
		Bits bits = tagMasks.get(tag);
		return bits != null && bits.inBound(entity.index()) && bits.get(entity.index());
	}
}
