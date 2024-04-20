package com.bakuard.ecsEngine;

import com.bakuard.collections.DynamicArray;
import com.bakuard.ecsEngine.entity.Entity;
import com.bakuard.ecsEngine.entity.EntityManager;
import com.bakuard.ecsEngine.entity.EntityManagerSnapshot;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class EntityManagerTest {

    @DisplayName("""
            create():
             all entity indexes have never been used
             => generation for each index must be 0
            """)
    @Test
    void create1() {
        EntityManager manager = new EntityManager();

        SoftAssertions assertions = new SoftAssertions();
        for(int i = 0; i < 1000; ++i) {
            Entity entity = manager.create();
            assertions.assertThat(entity).isEqualTo(new Entity(i, 0));
        }
        assertions.assertAll();
    }

    @DisplayName("""
            create(), remove(entity):
             some entity indexes have been used and subsequently removed several times
             => return entity with reused index and increased generation
            """)
    @Test
    void createAndRemove1() {
        EntityManager manager = new EntityManager();
        for(int i = 0; i < 1000; ++i) manager.create();

        SoftAssertions assertions = new SoftAssertions();
        for(int i = 0; i < 10; ++i) {
            for(Entity entity : createEntities(i, 0,906,512,112,704,705,55,54,53,12,400))
                manager.remove(entity);
            for(Entity entity : createEntities(i + 1, 0,12,53,54,55,112,400,512,704,705,906))
                assertions.assertThat(manager.create()).isEqualTo(entity);
        }
        assertions.assertAll();
    }

    @DisplayName("""
            create(), remove(entity):
             entity has already been removed,
             there is entity with same index
             => doesn't remove entity with same index
            """)
    @Test
    void createAndRemove2() {
        EntityManager manager = new EntityManager();
        for(int i = 0; i < 1000; ++i) manager.create();
        Entity removedEntity = new Entity(512, 0);

        manager.remove(removedEntity);
        Entity newEntity = manager.create();
        for(int i = 0; i < 10; ++i) manager.remove(removedEntity);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(newEntity.index()).isEqualTo(removedEntity.index());
        assertions.assertThat(manager.isAlive(newEntity)).isTrue();
        assertions.assertThat(newEntity.generation()).isEqualTo(1);
        assertions.assertAll();
    }

    @DisplayName("""
            create(), remove(entity):
             remove the same entity several time,
             create new several entities
            """)
    @Test
    void createAndRemove3() {
        EntityManager manager = new EntityManager();
        for(int i = 0; i < 1000; ++i) manager.create();
        Entity removedEntity = new Entity(512, 0);

        for(int i = 0; i < 10; ++i) manager.remove(removedEntity);
        Entity entity1 = manager.create();
        Entity entity2 = manager.create();
        Entity entity3 = manager.create();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(entity1).isEqualTo(new Entity(512, 1));
        assertions.assertThat(entity2).isEqualTo(new Entity(1000, 0));
        assertions.assertThat(entity3).isEqualTo(new Entity(1001, 0));
        assertions.assertAll();
    }

    @DisplayName("""
            isAlive(entity):
             entity with such index has been removed,
             there is not new entity with same index
             => return false for removed entity
            """)
    @Test
    void isAlive1() {
        EntityManager manager = new EntityManager();
        for(int i = 0; i < 1000; ++i) manager.create();
        Entity entity = new Entity(512, 0);

        manager.remove(entity);
        boolean actual = manager.isAlive(entity);

        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("""
            isAlive(entity):
             entity hasn't yet been removed
             => return true
            """)
    @Test
    void isAlive2() {
        EntityManager manager = new EntityManager();
        for(int i = 0; i < 1000; ++i) manager.create();
        Entity entity = new Entity(512, 0);

        boolean actual = manager.isAlive(entity);

        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("""
            isAlive(entity):
             entity with such index has been removed,
             there is new entity with same index and different generation
             => return false for removed entity
            """)
    @Test
    void isAlive3() {
        EntityManager manager = new EntityManager();
        for(int i = 0; i < 1000; ++i) manager.create();
        Entity removedEntity = new Entity(512, 0);

        manager.remove(removedEntity);
        manager.create();
        boolean actual = manager.isAlive(removedEntity);

        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("""
            isAlive(entity):
             entity with such index has been removed,
             there is new entity with same index and different generation
             => return true for new entity
            """)
    @Test
    void isAlive4() {
        EntityManager manager = new EntityManager();
        for(int i = 0; i < 1000; ++i) manager.create();
        Entity removedEntity = new Entity(512, 0);

        manager.remove(removedEntity);
        Entity newEntity = manager.create();
        boolean actual = manager.isAlive(newEntity);

        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("snapshot():")
    @ParameterizedTest(name = """
            note: {2},
            manager is {0}
            => expected snapshot is {1}
            """)
    @MethodSource("provideForSnapshot")
    void snapshot(EntityManager manager, EntityManagerSnapshot expected, String note) {
        EntityManagerSnapshot actual = manager.snapshot();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("restore(snapshot):")
    @ParameterizedTest(name = """
            note: {3},
            manager origin state is {0},
            snapshot is {1}
            => expected manager state is {2}
            """)
    @MethodSource("provideForRestore")
    void restore(EntityManager origin,
                 EntityManagerSnapshot snapshot,
                 EntityManager expected,
                 String note) {
        origin.restore(snapshot);

        Assertions.assertThat(origin).isEqualTo(expected);
    }


    private static DynamicArray<Entity> createEntities(int generation, int... indexes) {
        DynamicArray<Entity> result = new DynamicArray<>();
        for(int index : indexes) result.addLast(new Entity(index, generation));
        return result;
    }

    private static EntityManager createManager(int totalEntitiesNumber, DynamicArray<Entity> removedEntities) {
        EntityManager manager = new EntityManager();
        for(int i = 0; i < totalEntitiesNumber; ++i) manager.create();
        for(Entity entity : removedEntities) manager.remove(entity);
        return manager;
    }

    private static Stream<Arguments> provideForSnapshot() {
        return Stream.of(
                Arguments.of(
                        new EntityManager(),
                        new EntityManagerSnapshot(new DynamicArray<>(), new DynamicArray<>()),
                        "EntityManger is empty"
                ),
                Arguments.of(
                        createManager(
                                10,
                                new DynamicArray<>()
                        ),
                        new EntityManagerSnapshot(
                                createEntities(0, 0,1,2,3,4,5,6,7,8,9),
                                new DynamicArray<>()
                        ),
                        "EntityManager has living entities and hasn't reusable entities"
                ),
                Arguments.of(
                        createManager(
                                10,
                                createEntities(0, 0,1,2,3,4,5,6,7,8,9)
                        ),
                        new EntityManagerSnapshot(
                                new DynamicArray<>(),
                                createEntities(1, 0,1,2,3,4,5,6,7,8,9)
                        ),
                        "EntityManager hasn't living entities and has reusable entities"
                ),
                Arguments.of(
                        createManager(
                                10,
                                createEntities(0, 3,4,5,9)
                        ),
                        new EntityManagerSnapshot(
                                createEntities(0, 0,1,2,6,7,8),
                                createEntities(1, 3,4,5,9)
                        ),
                        "EntityManager has living entities and has reusable entities"
                )
        );
    }

    private static Stream<Arguments> provideForRestore() {
        return Stream.of(
                Arguments.of(
                        new EntityManager(),
                        new EntityManagerSnapshot(new DynamicArray<>(), new DynamicArray<>()),
                        new EntityManager(),
                        "EntityManager is empty, snapshot is empty"
                ),
                Arguments.of(
                        createManager(
                                10,
                                createEntities(0, 3,4,5,9)
                        ),
                        new EntityManagerSnapshot(new DynamicArray<>(), new DynamicArray<>()),
                        new EntityManager(),
                        "EntityManager is not empty, snapshot is empty"
                ),
                Arguments.of(
                        new EntityManager(),
                        new EntityManagerSnapshot(
                                createEntities(0, 0,1,2,3,4,5,6,7,8,9),
                                new DynamicArray<>()
                        ),
                        createManager(
                                10,
                                new DynamicArray<>()
                        ),
                        "snapshot has living entities ans hasn't reusable entities"
                ),
                Arguments.of(
                        new EntityManager(),
                        new EntityManagerSnapshot(
                                createEntities(0, 0,1,2,6,7,8),
                                createEntities(1, 3,4,5,9)
                        ),
                        createManager(
                                10,
                                createEntities(0, 3,4,5,9)
                        ),
                        "snapshot has living entities ans has reusable entities"
                )
        );
    }
}