package com.bakuard.ecsEngine.component;

import com.bakuard.collections.Bits;
import com.bakuard.collections.DynamicArray;
import com.bakuard.ecsEngine.entity.Entity;
import com.bakuard.ecsEngine.entity.EntityManager;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TagsManagerTest {

    @DisplayName("""
            attachTag(entity, tag):
             entities is alive,
             some entities have the same tags
             => hasTag(entity, tag) return true for attached tags and false for not attached tags
            """)
    @Test
    public void attachTag1() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        Entity entityC = entityManager.create();

        tagsManager.attachTag(entityA, "A");
        tagsManager.attachTag(entityA, "C");
        tagsManager.attachTag(entityB, "A");
        tagsManager.attachTag(entityB, "B");
        tagsManager.attachTag(entityC, "B");
        tagsManager.attachTag(entityC, "C");

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(tagsManager.hasTag(entityA, "A")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityA, "B")).isFalse();
        assertions.assertThat(tagsManager.hasTag(entityA, "C")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityB, "A")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityB, "B")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityB, "C")).isFalse();
        assertions.assertThat(tagsManager.hasTag(entityC, "A")).isFalse();
        assertions.assertThat(tagsManager.hasTag(entityC, "B")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityC, "C")).isTrue();
        assertions.assertAll();
    }

    @DisplayName("""
            attachTag(entity, tag):
             some entities is not alive,
             deadEntity.index() == aliveEntity.index()
             => doesn't change any entity
            """)
    @Test
    public void attachTag2() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity deadEntity = entityManager.create();
        entityManager.remove(deadEntity);
        Entity aliveEntity = entityManager.create();

        tagsManager.attachTag(deadEntity, "A");
        tagsManager.attachTag(deadEntity, "B");
        tagsManager.attachTag(deadEntity, "C");
        tagsManager.attachTag(aliveEntity, "A");
        tagsManager.attachTag(aliveEntity, "B");
        tagsManager.attachTag(aliveEntity, "C");

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(tagsManager.hasTag(deadEntity, "A")).isFalse();
        assertions.assertThat(tagsManager.hasTag(deadEntity, "B")).isFalse();
        assertions.assertThat(tagsManager.hasTag(deadEntity, "C")).isFalse();
        assertions.assertThat(tagsManager.hasTag(aliveEntity, "A")).isTrue();
        assertions.assertThat(tagsManager.hasTag(aliveEntity, "B")).isTrue();
        assertions.assertThat(tagsManager.hasTag(aliveEntity, "C")).isTrue();
        assertions.assertThat(deadEntity.index()).isEqualTo(aliveEntity.index());
        assertions.assertAll();
    }

    @DisplayName("""
            detachTag(entity, tag):
             entities is alive,
             some entities have the same tags
             => hasTag(entity, tag) return true for not detached tags and false for detached tags
            """)
    @Test
    public void detachTag1() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        Entity entityC = entityManager.create();
        tagsManager.attachTags(entityA, "A", "B", "C");
        tagsManager.attachTags(entityB, "A", "B", "C");
        tagsManager.attachTags(entityC, "A", "B", "C");

        tagsManager.detachTag(entityA, "A");
        tagsManager.detachTag(entityB, "B");
        tagsManager.detachTag(entityC, "C");

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(tagsManager.hasTag(entityA, "A")).isFalse();
        assertions.assertThat(tagsManager.hasTag(entityA, "B")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityA, "C")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityB, "A")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityB, "B")).isFalse();
        assertions.assertThat(tagsManager.hasTag(entityB, "C")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityC, "A")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityC, "B")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityC, "C")).isFalse();
        assertions.assertAll();
    }

    @DisplayName("""
            detachTag(entity, tag):
             some entities is not alive,
             deadEntity.index() == aliveEntity.index()
             => doesn't change any entity
            """)
    @Test
    public void detachTag2() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity deadEntity = entityManager.create();
        entityManager.remove(deadEntity);
        Entity aliveEntity = entityManager.create();
        tagsManager.attachTags(aliveEntity, "A", "B", "C");

        tagsManager.detachTag(deadEntity, "A");
        tagsManager.detachTag(deadEntity, "B");
        tagsManager.detachTag(deadEntity, "C");

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(tagsManager.hasTag(deadEntity, "A")).isFalse();
        assertions.assertThat(tagsManager.hasTag(deadEntity, "B")).isFalse();
        assertions.assertThat(tagsManager.hasTag(deadEntity, "C")).isFalse();
        assertions.assertThat(tagsManager.hasTag(aliveEntity, "A")).isTrue();
        assertions.assertThat(tagsManager.hasTag(aliveEntity, "B")).isTrue();
        assertions.assertThat(tagsManager.hasTag(aliveEntity, "C")).isTrue();
        assertions.assertThat(deadEntity.index()).isEqualTo(aliveEntity.index());
        assertions.assertAll();
    }

    @DisplayName("""
            detachTag(entity, tag):
             entities is alive,
             entity hasn't tags that will be detached
             => doesn't change entity set tags
            """)
    @Test
    public void detachTag3() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        tagsManager.attachTags(entityA, "A", "B", "C");
        tagsManager.attachTags(entityB, "D", "E", "F");

        tagsManager.detachTag(entityA, "D");
        tagsManager.detachTag(entityA, "E");
        tagsManager.detachTag(entityA, "F");

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(tagsManager.hasTag(entityA, "A")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityA, "B")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityA, "C")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityB, "D")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityB, "E")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entityB, "F")).isTrue();
        assertions.assertAll();
    }

    @DisplayName("""
            detachAllTags(entity):
             entity is alive
             => hasTag(entity, tag) return false for all entity tags
            """)
    @Test
    public void detachAllTags1() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity entity = entityManager.create();
        tagsManager.attachTags(entity, "A", "B", "C", "D", "E", "F");

        tagsManager.detachAllTags(entity);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(tagsManager.hasTag(entity, "A")).isFalse();
        assertions.assertThat(tagsManager.hasTag(entity, "B")).isFalse();
        assertions.assertThat(tagsManager.hasTag(entity, "C")).isFalse();
        assertions.assertThat(tagsManager.hasTag(entity, "D")).isFalse();
        assertions.assertThat(tagsManager.hasTag(entity, "E")).isFalse();
        assertions.assertThat(tagsManager.hasTag(entity, "F")).isFalse();
        assertions.assertAll();
    }

    @DisplayName("""
            detachAllTags(entity):
             entity is not alive,
             deadEntity.index() == aliveEntity.index()
             => doesn't change any entity
            """)
    @Test
    public void detachAllTags2() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity deadEntity = entityManager.create();
        entityManager.remove(deadEntity);
        Entity aliveEntity = entityManager.create();
        tagsManager.attachTags(aliveEntity, "A", "B", "C", "D", "E", "F");

        tagsManager.detachAllTags(deadEntity);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(tagsManager.hasTag(aliveEntity, "A")).isTrue();
        assertions.assertThat(tagsManager.hasTag(aliveEntity, "B")).isTrue();
        assertions.assertThat(tagsManager.hasTag(aliveEntity, "C")).isTrue();
        assertions.assertThat(tagsManager.hasTag(aliveEntity, "D")).isTrue();
        assertions.assertThat(tagsManager.hasTag(aliveEntity, "E")).isTrue();
        assertions.assertThat(tagsManager.hasTag(aliveEntity, "F")).isTrue();
        assertions.assertThat(deadEntity.index()).isEqualTo(aliveEntity.index());
        assertions.assertAll();
    }

    @DisplayName("""
            replaceAllTags(entity, tags):
             entity is alive,
             entity have some tags
             => hasTag(entity, tag) return false for old tags and true for new tags
            """)
    @Test
    public void replaceAllTags1() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity entity = entityManager.create();
        tagsManager.attachTags(entity, "A", "B", "C");

        tagsManager.replaceAllTags(entity, "C", "D", "E");

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(tagsManager.hasTag(entity, "A")).isFalse();
        assertions.assertThat(tagsManager.hasTag(entity, "B")).isFalse();
        assertions.assertThat(tagsManager.hasTag(entity, "C")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entity, "D")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entity, "E")).isTrue();
        assertions.assertAll();
    }

    @DisplayName("""
            replaceAllTags(entity, tags):
             entity is alive,
             entity haven't any tags
             => hasTag(entity, tag) return true for new tags
            """)
    @Test
    public void replaceAllTags2() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity entity = entityManager.create();

        tagsManager.replaceAllTags(entity, "C", "D", "E");

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(tagsManager.hasTag(entity, "C")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entity, "D")).isTrue();
        assertions.assertThat(tagsManager.hasTag(entity, "E")).isTrue();
        assertions.assertAll();
    }

    @DisplayName("""
            haveEqualTags(firstEntity, secondEntity):
             firstEntity is not alive,
             secondEntity is not alive,
             firstEntity and secondEntity had different tags set
             => true
            """)
    @Test
    public void haveEqualTags1() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity firstEntity = entityManager.create();
        tagsManager.attachTags(firstEntity, "A", "B", "C", "D");
        Entity secondEntity = entityManager.create();
        tagsManager.attachTags(secondEntity, "C", "D", "E", "F");

        entityManager.remove(firstEntity);
        entityManager.remove(secondEntity);
        boolean actual = tagsManager.haveEqualTags(firstEntity, secondEntity);

        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("""
            haveEqualTags(firstEntity, secondEntity):
             firstEntity is alive,
             secondEntity is not alive,
             firstEntity and secondEntity had same tags set
             => false
            """)
    @Test
    public void haveEqualTags2() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity firstEntity = entityManager.create();
        tagsManager.attachTags(firstEntity, "A", "B", "C", "D");
        Entity secondEntity = entityManager.create();
        tagsManager.attachTags(secondEntity, "A", "B", "C", "D");

        entityManager.remove(secondEntity);
        boolean actual = tagsManager.haveEqualTags(firstEntity, secondEntity);

        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("""
            haveEqualTags(firstEntity, secondEntity):
             firstEntity is alive,
             secondEntity is alive,
             firstEntity and secondEntity has different tags set
             => false
            """)
    @Test
    public void haveEqualTags3() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity firstEntity = entityManager.create();
        tagsManager.attachTags(firstEntity, "A", "B", "C", "D");
        Entity secondEntity = entityManager.create();
        tagsManager.attachTags(secondEntity, "C", "D", "E", "F");

        boolean actual = tagsManager.haveEqualTags(firstEntity, secondEntity);

        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("""
            haveEqualTags(firstEntity, secondEntity):
             firstEntity is alive,
             secondEntity is alive,
             firstEntity and secondEntity has the same tags set
             => true
            """)
    @Test
    public void haveEqualTags4() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity firstEntity = entityManager.create();
        tagsManager.attachTags(firstEntity, "A", "B", "C");
        Entity secondEntity = entityManager.create();
        tagsManager.attachTags(secondEntity, "A", "B", "C");

        boolean actual = tagsManager.haveEqualTags(firstEntity, secondEntity);

        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("""
            haveEqualTags(firstEntity, secondEntity):
             firstEntity is alive,
             secondEntity is alive,
             firstEntity and secondEntity hasn't any tags
             => true
            """)
    @Test
    public void haveEqualTags5() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity firstEntity = entityManager.create();
        Entity secondEntity = entityManager.create();

        boolean actual = tagsManager.haveEqualTags(firstEntity, secondEntity);

        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("""
            excludeEntityIndexesWithout(entityIndexes, tagNames):
             tagsManager contains all tags in tagNames,
             there are entities with all tags in tagNames
             => remove correct entity indexes from entityIndexes
            """)
    @Test
    public void excludeEntityIndexesWithout1() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        Entity entityC = entityManager.create();
        Entity entityD = entityManager.create();
        tagsManager.attachTags(entityA, "A", "B");
        tagsManager.attachTags(entityB, "A", "C");
        tagsManager.attachTags(entityC, "B", "C");

        Bits actual = Bits.filled(100);
        tagsManager.excludeEntityIndexesWithout(actual, DynamicArray.of("B"));

        Assertions.assertThat(actual).isEqualTo(Bits.of(100, 0, 2));
    }

    @DisplayName("""
            excludeEntityIndexesWithout(entityIndexes, tagNames):
             tagsManager contains all tags in tagNames,
             there are not entities with all tags in tagNames
             => clear entityIndexes
            """)
    @Test
    public void excludeEntityIndexesWithout2() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        Entity entityC = entityManager.create();
        Entity entityD = entityManager.create();
        tagsManager.attachTags(entityA, "A", "B");
        tagsManager.attachTags(entityB, "A", "C");
        tagsManager.attachTags(entityC, "B", "C");
        tagsManager.attachTags(entityD, "D");

        tagsManager.detachTag(entityD, "D");
        Bits actual = Bits.filled(100);
        tagsManager.excludeEntityIndexesWithout(actual, DynamicArray.of("D"));

        Assertions.assertThat(actual).isEqualTo(new Bits(100));
    }

    @DisplayName("""
            excludeEntityIndexesWithout(entityIndexes, tagNames):
             tagsManager doesn't contain all tags in tagNames,
             there are not entities with all tags in tagNames
             => clear entityIndexes
            """)
    @Test
    public void excludeEntityIndexesWithout3() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        Entity entityC = entityManager.create();
        Entity entityD = entityManager.create();
        tagsManager.attachTags(entityA, "A", "B");
        tagsManager.attachTags(entityB, "A", "C");
        tagsManager.attachTags(entityC, "B", "C");

        Bits actual = Bits.filled(100);
        tagsManager.excludeEntityIndexesWithout(actual, DynamicArray.of("Z"));

        Assertions.assertThat(actual).isEqualTo(new Bits(100));
    }

    @DisplayName("""
            excludeEntityIndexesWith(entityIndexes, tagNames):
             tagsManager contains all tags in tagNames,
             there are entities without all tags in tagNames
             => remove correct entity index from entityIndexes
            """)
    @Test
    public void excludeEntityIndexesWith1() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        Entity entityC = entityManager.create();
        Entity entityD = entityManager.create();
        tagsManager.attachTags(entityA, "A", "B");
        tagsManager.attachTags(entityB, "A", "C");
        tagsManager.attachTags(entityC, "B", "C");

        Bits actual = Bits.filled(4);
        tagsManager.excludeEntityIndexesWith(actual, DynamicArray.of("B"));

        Assertions.assertThat(actual).isEqualTo(Bits.of(4, 1,3));
    }

    @DisplayName("""
            excludeEntityIndexesWith(entityIndexes, tagNames):
             tagsManager contains all tags in tagNames,
             there are not entities without all tags in tagNames
             => clear entityIndexes
            """)
    @Test
    public void excludeEntityIndexesWith2() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        Entity entityC = entityManager.create();
        tagsManager.attachTags(entityA, "A", "B");
        tagsManager.attachTags(entityB, "A", "C");
        tagsManager.attachTags(entityC, "B", "C");

        Bits actual = Bits.filled(3);
        tagsManager.excludeEntityIndexesWith(actual, DynamicArray.of("A", "C"));

        Assertions.assertThat(actual).isEqualTo(new Bits(3));
    }

    @DisplayName("""
            excludeEntityIndexesWith(entityIndexes, tagNames):
             tagsManager doesn't contain all tags in tagNames
             => doesn't change entityIndexes
            """)
    @Test
    public void excludeEntityIndexesWith3() {
        EntityManager entityManager = new EntityManager();
        TagsManager tagsManager = new TagsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        Entity entityC = entityManager.create();
        Entity entityD = entityManager.create();
        tagsManager.attachTags(entityA, "A", "B");
        tagsManager.attachTags(entityB, "A", "C");
        tagsManager.attachTags(entityC, "B", "C");

        Bits actual = Bits.filled(100);
        tagsManager.excludeEntityIndexesWith(actual, DynamicArray.of("Z"));

        Assertions.assertThat(actual).isEqualTo(Bits.filled(100));
    }
}