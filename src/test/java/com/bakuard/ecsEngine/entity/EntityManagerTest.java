package com.bakuard.ecsEngine.entity;

import com.bakuard.collections.DynamicArray;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
			for(Entity entity : createEntities(i + 1, 0,906,512,112,704,705,55,54,53,12,400))
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
			getEntityByIndex(index), isAlive(entity):
			 get alive entity
			 => isAlive(returnedEntity) == true
			""")
	@Test
	void getEntityByIndex1() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();

		Entity actual = manager.getEntityByIndex(512);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(actual).isEqualTo(new Entity(512, 0));
		assertions.assertThat(manager.isAlive(actual)).isTrue();
		assertions.assertAll();
	}

	@DisplayName("""
			getEntityByIndex(index), isAlive(entity):
			 get dead entity
			 => return entity with generation + 1, isAlive(returnedEntity) == false
			""")
	@Test
	void getEntityByIndex2() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		manager.remove(new Entity(512, 0));

		Entity actual = manager.getEntityByIndex(512);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(actual).isEqualTo(new Entity(512, 1));
		assertions.assertThat(manager.isAlive(actual)).isFalse();
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

	@DisplayName("""
			isAlive(entity):
			 entity is null
			 => return false
			""")
	@Test
	void isAlive5() {
		EntityManager manager = new EntityManager();

		boolean actual = manager.isAlive(null);

		Assertions.assertThat(actual).isFalse();
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
}