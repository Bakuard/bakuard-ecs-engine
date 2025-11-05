package com.bakuard.ecsEngine.entity;

import com.bakuard.collections.DynamicArray;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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

	@DisplayName("""
			hasAliveEntityWith(index):
			 index < 0
			 => return false
			""")
	@Test
	void hasAliveEntityWith1() {
		EntityManager manager = new EntityManager();

		boolean actual = manager.hasAliveEntityWith(-1);

		Assertions.assertThat(actual).isFalse();
	}

	@DisplayName("""
			hasAliveEntityWith(index):
			 EntityManager is empty,
			 index = 0
			 => return false
			""")
	@Test
	void hasAliveEntityWith2() {
		EntityManager manager = new EntityManager();

		boolean actual = manager.hasAliveEntityWith(0);

		Assertions.assertThat(actual).isFalse();
	}

	@DisplayName("""
			hasAliveEntityWith(index):
			 EntityManager is not empty,
			 index = EntityManager.entityIndexUpperBound()
			 => return false
			""")
	@Test
	void hasAliveEntityWith3() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; i++)
			manager.create();

		boolean actual = manager.hasAliveEntityWith(1000);

		Assertions.assertThat(actual).isFalse();
	}

	@DisplayName("""
			hasAliveEntityWith(index):
			 EntityManager is not empty,
			 index > EntityManager.entityIndexUpperBound()
			 => return false
			""")
	@Test
	void hasAliveEntityWith4() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; i++)
			manager.create();

		boolean actual = manager.hasAliveEntityWith(1001);

		Assertions.assertThat(actual).isFalse();
	}

	@DisplayName("""
			hasAliveEntityWith(index):
			 EntityManager is not empty,
			 there is not alive entity with index
			 => return false
			""")
	@Test
	void hasAliveEntityWith5() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; i++)
			manager.create();
		manager.remove(new Entity(550, 0));

		boolean actual = manager.hasAliveEntityWith(550);

		Assertions.assertThat(actual).isFalse();
	}

	@DisplayName("""
			hasAliveEntityWith(index):
			 EntityManager is not empty,
			 there is alive entity with index
			 => return false
			""")
	@Test
	void hasAliveEntityWith6() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; i++)
			manager.create();

		boolean actual = manager.hasAliveEntityWith(550);

		Assertions.assertThat(actual).isTrue();
	}

	@DisplayName("""
			revive(entity):
			 entity is alive
			 => throw IllegalSTateException
			""")
	@Test
	void revive1() {
		EntityManager manager = new EntityManager();
		Entity entity = manager.create();

		Assertions.assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> manager.revive(entity));
	}

	@DisplayName("""
			revive(entity):
			 entity is dead,
			 there is alive entity with same index
			 => throw IllegalSTateException
			""")
	@Test
	void revive2() {
		EntityManager manager = new EntityManager();
		Entity entity = manager.create();
		manager.remove(entity);
		for(int i = 0; i < 1000; ++i)
			manager.remove(manager.create());
		manager.create();

		Assertions.assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> manager.revive(entity));
	}

	@DisplayName("""
			revive(entity):
			 entity is dead,
			 there is not alive entity with same index,
			 there are not dead entities,
			 manager.entityIndexHighWaterMark() + 1 = entity.index()
			 => revive entity
			""")
	@Test
	void revive3() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();

		Entity entity = new Entity(1000, 512);
		manager.revive(entity);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.isAlive(entity)).isTrue();
		assertions.assertThat(manager.create()).isEqualTo(new Entity(1001, 0));
		assertions.assertAll();
	}

	@DisplayName("""
			revive(entity):
			 entity is dead,
			 there is not alive entity with same index,
			 there are not dead entities,
			 manager.entityIndexHighWaterMark() + 1000 = entity.index()
			 => revive entity, dead entities from manager.entityIndexHighWaterMark() to entity.index()
			""")
	@Test
	void revive4() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();

		Entity entity = new Entity(2000, 512);
		manager.revive(entity);
		List<Entity> actual = IntStream.range(1000, 2000).mapToObj(i -> manager.create()).toList();

		List<Entity> expected = IntStream.range(1000, 2000).mapToObj(i -> new Entity(i, 0)).toList();
		Assertions.assertThat(actual).isEqualTo(expected);
	}

	@DisplayName("""
			revive(entity):
			 entity is dead,
			 there is not alive entity with same index,
			 there are dead entities,
			 there is not reserved dead entity with same index,
			 manager.entityIndexHighWaterMark() + 1 = entity.index()
			 => revive entity, dead entities from manager.entityIndexHighWaterMark() to entity.index()
			""")
	@Test
	void revive5() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		manager.remove(new Entity(0, 0));
		manager.remove(new Entity(100, 0));
		manager.remove(new Entity(101, 0));
		manager.remove(new Entity(512, 0));
		manager.remove(new Entity(999, 0));

		Entity entity = new Entity(1000, 512);
		manager.revive(entity);
		List<Entity> actual = IntStream.range(0, 6).mapToObj(i -> manager.create()).toList();

		List<Entity> expected = new ArrayList<>();
		expected.add(new Entity(0, 1));
		expected.add(new Entity(100, 1));
		expected.add(new Entity(101, 1));
		expected.add(new Entity(512, 1));
		expected.add(new Entity(999, 1));
		expected.add(new Entity(1001, 0));
		Assertions.assertThat(actual).isEqualTo(expected);
	}

	@DisplayName("""
			revive(entity):
			 entity is dead,
			 there is not alive entity with same index,
			 there are dead entities,
			 there is not reserved dead entity with same index,
			 manager.entityIndexHighWaterMark() + 1000 = entity.index()
			 => revive entity, dead entities from manager.entityIndexHighWaterMark() to entity.index()
			""")
	@Test
	void revive6() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		manager.remove(new Entity(0, 0));
		manager.remove(new Entity(100, 0));
		manager.remove(new Entity(101, 0));
		manager.remove(new Entity(512, 0));
		manager.remove(new Entity(999, 0));

		Entity entity = new Entity(2000, 512);
		manager.revive(entity);
		List<Entity> actual = IntStream.range(0, 2005).mapToObj(i -> manager.create()).toList();

		List<Entity> expected = new ArrayList<>();
		expected.add(new Entity(0, 1));
		expected.add(new Entity(100, 1));
		expected.add(new Entity(101, 1));
		expected.add(new Entity(512, 1));
		expected.add(new Entity(999, 1));
		IntStream.range(1000, 2000).mapToObj(i -> new Entity(i, 0)).forEach(expected::add);
		IntStream.range(2001, 3001).mapToObj(i -> new Entity(i, 0)).forEach(expected::add);
		Assertions.assertThat(actual).isEqualTo(expected);
	}

	@DisplayName("""
			revive(entity):
			 entity is dead,
			 there is not alive entity with same index,
			 there are dead entities,
			 there is reserved dead entity with same index in the begin of implicit list
			 => revive entity
			""")
	@Test
	void revive7() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		manager.remove(new Entity(0, 0));
		manager.remove(new Entity(100, 0));
		manager.remove(new Entity(101, 0));
		manager.remove(new Entity(512, 0));
		manager.remove(new Entity(999, 0));

		Entity entity = new Entity(0, 512);
		manager.revive(entity);
		List<Entity> actual = IntStream.range(0, 1004).mapToObj(i -> manager.create()).toList();

		List<Entity> expected = new ArrayList<>();
		expected.add(new Entity(100, 1));
		expected.add(new Entity(101, 1));
		expected.add(new Entity(512, 1));
		expected.add(new Entity(999, 1));
		IntStream.range(1000, 2000).mapToObj(i -> new Entity(i, 0)).forEach(expected::add);
		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.isAlive(entity)).isTrue();
		assertions.assertThat(manager.getEntityByIndex(0)).isEqualTo(entity);
		assertions.assertThat(actual).isEqualTo(expected);
		assertions.assertAll();
	}

	@DisplayName("""
			revive(entity):
			 entity is dead,
			 there is not alive entity with same index,
			 there are dead entities,
			 there is reserved dead entity with same index in the middle of implicit list
			 => revive entity
			""")
	@Test
	void revive8() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		manager.remove(new Entity(0, 0));
		manager.remove(new Entity(100, 0));
		manager.remove(new Entity(101, 0));
		manager.remove(new Entity(512, 0));
		manager.remove(new Entity(999, 0));

		Entity entity = new Entity(101, 512);
		manager.revive(entity);
		List<Entity> actual = IntStream.range(0, 1004).mapToObj(i -> manager.create()).toList();

		List<Entity> expected = new ArrayList<>();
		expected.add(new Entity(0, 1));
		expected.add(new Entity(100, 1));
		expected.add(new Entity(512, 1));
		expected.add(new Entity(999, 1));
		IntStream.range(1000, 2000).mapToObj(i -> new Entity(i, 0)).forEach(expected::add);
		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.isAlive(entity)).isTrue();
		assertions.assertThat(manager.getEntityByIndex(101)).isEqualTo(entity);
		assertions.assertThat(actual).isEqualTo(expected);
		assertions.assertAll();
	}

	@DisplayName("""
			revive(entity):
			 entity is dead,
			 there is not alive entity with same index,
			 there are dead entities,
			 there is reserved dead entity with same index in the end of implicit list
			 => revive entity
			""")
	@Test
	void revive9() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		manager.remove(new Entity(0, 0));
		manager.remove(new Entity(100, 0));
		manager.remove(new Entity(101, 0));
		manager.remove(new Entity(512, 0));
		manager.remove(new Entity(999, 0));

		Entity entity = new Entity(999, 512);
		manager.revive(entity);
		List<Entity> actual = IntStream.range(0, 1004).mapToObj(i -> manager.create()).toList();

		List<Entity> expected = new ArrayList<>();
		expected.add(new Entity(0, 1));
		expected.add(new Entity(100, 1));
		expected.add(new Entity(101, 1));
		expected.add(new Entity(512, 1));
		IntStream.range(1000, 2000).mapToObj(i -> new Entity(i, 0)).forEach(expected::add);
		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.isAlive(entity)).isTrue();
		assertions.assertThat(manager.getEntityByIndex(999)).isEqualTo(entity);
		assertions.assertThat(actual).isEqualTo(expected);
		assertions.assertAll();
	}

	@DisplayName("""
			revive(entity):
			 entity is dead,
			 there is not alive entity with same index,
			 there is only one dead entity,
			 there is reserved dead entity with same index
			 => revive entity
			""")
	@Test
	void revive10() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		manager.remove(new Entity(101, 0));

		Entity entity = new Entity(101, 512);
		manager.revive(entity);
		List<Entity> actual = IntStream.range(0, 1000).mapToObj(i -> manager.create()).toList();

		List<Entity> expected = IntStream.range(1000, 2000).mapToObj(i -> new Entity(i, 0)).toList();
		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.isAlive(entity)).isTrue();
		assertions.assertThat(manager.getEntityByIndex(101)).isEqualTo(entity);
		assertions.assertThat(actual).isEqualTo(expected);
		assertions.assertAll();
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