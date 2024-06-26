package com.bakuard.ecsEngine.component;

import com.bakuard.collections.Bits;
import com.bakuard.collections.ReadableLinearStructure;
import com.bakuard.ecsEngine.entity.Entity;
import com.bakuard.ecsEngine.entity.EntityManager;

import java.util.HashMap;
import java.util.Iterator;

public final class TagsManager {

    private final HashMap<String, Bits> tagMasks;
    private final HashMap<String, Entity> singletonTags;
    private final EntityManager entityManager;

    public TagsManager(EntityManager entityManager) {
        this.tagMasks = new HashMap<>();
        this.singletonTags = new HashMap<>();
        this.entityManager = entityManager;
    }

    public void attachTag(Entity entity, String tag) {
        if(entityManager.isAlive(entity)) attachTagIgnoringEntityState(entity, tag);
    }

    public void attachTags(Entity entity, String... tags) {
        if(entityManager.isAlive(entity)) {
            for(String tag : tags) attachTagIgnoringEntityState(entity, tag);
        }
    }

    public void detachTag(Entity entity, String tag) {
        if(entityManager.isAlive(entity)) detachTagIgnoringEntityState(entity, tag);
    }

    public void detachTags(Entity entity, String... tags) {
        if(entityManager.isAlive(entity)) {
            for(String tag : tags) detachTagIgnoringEntityState(entity, tag);
        }
    }

    public void detachAllTags(Entity entity) {
        if(entityManager.isAlive(entity)) {
            tagMasks.forEach((key, bits) -> {
                if(bits.inBound(entity.index())) bits.clear(entity.index());
            });
            singletonTags.values().removeIf(entity::equals);
        }
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


    public void excludeEntityIndexesWithout(Bits entityIndexes, ReadableLinearStructure<String> tagNames) {
        for(int i = 0; i < tagNames.size(); ++i) {
            String tagName = tagNames.get(i);
            Bits mask = tagMasks.get(tagName);
            if(mask != null) {
                entityIndexes.and(mask);
            } else {
                entityIndexes.clearAll();
                break;
            }
        }
    }

    public void excludeEntityIndexesWith(Bits entityIndexes, ReadableLinearStructure<String> tagNames) {
        for(int i = 0; i < tagNames.size(); ++i) {
            String tagName = tagNames.get(i);
            Bits mask = tagMasks.get(tagName);
            if(mask != null) entityIndexes.andNot(mask);
        }
    }


    public void attachSingletonTag(Entity entity, String singletonTag) {
        if(entityManager.isAlive(entity)) {
            singletonTags.put(singletonTag, entity);
        }
    }

    public void detachSingletonTag(String singletonTag) {
        singletonTags.remove(singletonTag);
    }

    public Entity getEntityBySingletonTag(String singletonTag) {
        return singletonTags.get(singletonTag);
    }

    public boolean hasSingletonTag(Entity entity, String singletonTag) {
        return entityManager.isAlive(entity) && entity.equals(singletonTags.get(singletonTag));
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
