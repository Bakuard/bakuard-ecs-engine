package com.bakuard.ecsEngine.entity;

import com.bakuard.collections.Bits;
import com.bakuard.ecsEngine.exception.DeadEntityException;
import com.bakuard.ecsEngine.exception.IllegalEntityStateException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class EntityManagerTest {

	@DisplayName("""
			create():
			 all entity indexes have never been used
			 => generation for each entity must be 0
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
		createEntities(manager, 1000);

		SoftAssertions assertions = new SoftAssertions();
		for(int i = 0; i < 10; ++i) {
			removeEntities(manager, i, 0,906,512,112,704,705,55,54,53,12,400);
			assertions.assertThat(createEntities(manager, 11))
					.containsExactlyInAnyOrderElementsOf(createEntities(i + 1, 0,906,512,112,704,705,55,54,53,12,400));
		}
		assertions.assertAll();
	}

	@DisplayName("""
			create(), remove(entity):
			 entity has already been removed,
			 there is entity with same index
			 => doesn't remove entity with same index, throw exception
			""")
	@Test
	void createAndRemove2() {
		EntityManager manager = new EntityManager();
		createEntities(manager, 1000);
		Entity removedEntity = new Entity(512, 0);

		manager.remove(removedEntity);
		Entity newEntity = manager.create();

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThatThrownBy(() -> manager.remove(removedEntity)).isInstanceOf(DeadEntityException.class);
		assertions.assertThat(newEntity.index()).isEqualTo(removedEntity.index());
		assertions.assertThat(manager.isAlive(newEntity)).isTrue();
		assertions.assertThat(newEntity.generation()).isEqualTo(1);
		assertions.assertAll();
	}

	@DisplayName("""
			create(), remove(entity):
			 entity has already been removed,
			 create new several entities
			 => throw exception when remove entity again
			""")
	@Test
	void createAndRemove3() {
		EntityManager manager = new EntityManager();
		createEntities(manager, 1000);
		Entity removedEntity = new Entity(512, 0);

		manager.remove(removedEntity);
		Entity entity1 = manager.create();
		Entity entity2 = manager.create();
		Entity entity3 = manager.create();

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThatThrownBy(() -> manager.remove(removedEntity)).isInstanceOf(DeadEntityException.class);
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
		createEntities(manager, 1000);

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
		createEntities(manager, 1000);
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
		createEntities(manager, 1000);
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
		createEntities(manager, 1000);
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
		createEntities(manager, 1000);
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
			isAlive(entity):
			 entity with such generation has never been created
			 => return false
			""")
	@Test
	void isAlive6() {
		EntityManager manager = new EntityManager();
		manager.remove(manager.create());
		Entity deadEntity = new Entity(0, 1);

		boolean actual = manager.isAlive(deadEntity);

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
		createEntities(manager, 1000);

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
		createEntities(manager, 1000);

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
		createEntities(manager, 1000);
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
		createEntities(manager, 1000);

		boolean actual = manager.hasAliveEntityWith(550);

		Assertions.assertThat(actual).isTrue();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 entity.index() < 0
			 => throw exception
			""")
	@Test
	void unsafeSet1() {
		EntityManager manager = new EntityManager();
		Entity entity = new Entity(-1, 0);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThatThrownBy(() -> manager.unsafeSet(entity, true)).isInstanceOf(IllegalEntityStateException.class);
		assertions.assertThatThrownBy(() -> manager.unsafeSet(entity, false)).isInstanceOf(IllegalEntityStateException.class);
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 entity.generation() < 0
			 => throw exception
			""")
	@Test
	void unsafeSet2() {
		EntityManager manager = new EntityManager();
		Entity entity = new Entity(0, -1);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThatThrownBy(() -> manager.unsafeSet(entity, true)).isInstanceOf(IllegalEntityStateException.class);
		assertions.assertThatThrownBy(() -> manager.unsafeSet(entity, false)).isInstanceOf(IllegalEntityStateException.class);
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager contains this entity,
			 entity is dead,
			 isAlive == false
			 => do nothing
			""")
	@Test
	void unsafeSet3() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		for(int i = 0; i < 10; ++i) manager.remove(manager.create());
		Entity deadEntity = new Entity(1000, 10);

		manager.unsafeSet(deadEntity, false);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1000))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(1001);
		assertions.assertThat(manager.getEntityByIndex(deadEntity.index())).isEqualTo(deadEntity);
		assertions.assertThat(manager.hasAliveEntityWith(deadEntity.index())).isFalse();
		assertions.assertThat(manager.isAlive(deadEntity)).isFalse();
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager contains this entity,
			 entity is dead,
			 isAlive == true
			 => make this entity alive
			""")
	@Test
	void unsafeSet4() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		for(int i = 0; i < 10; ++i) manager.remove(manager.create());
		Entity entity = new Entity(1000, 10);

		manager.unsafeSet(entity, true);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1001))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(1001);
		assertions.assertThat(manager.getEntityByIndex(entity.index())).isEqualTo(entity);
		assertions.assertThat(manager.hasAliveEntityWith(entity.index())).isTrue();
		assertions.assertThat(manager.isAlive(entity)).isTrue();
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager contains this entity,
			 entity is alive,
			 isAlive == false
			 => make this entity dead
			""")
	@Test
	void unsafeSet5() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		for(int i = 0; i < 10; ++i) manager.remove(manager.create());
		Entity entity = manager.create();

		manager.unsafeSet(entity, false);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1000))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(1001);
		assertions.assertThat(manager.getEntityByIndex(entity.index())).isEqualTo(entity);
		assertions.assertThat(manager.hasAliveEntityWith(entity.index())).isFalse();
		assertions.assertThat(manager.isAlive(entity)).isFalse();
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager contains this entity,
			 entity is alive,
			 isAlive == true
			 => do nothing
			""")
	@Test
	void unsafeSet6() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		for(int i = 0; i < 10; ++i) manager.remove(manager.create());
		Entity aliveEntity = manager.create();

		manager.unsafeSet(aliveEntity, true);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1001))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(1001);
		assertions.assertThat(manager.getEntityByIndex(aliveEntity.index())).isEqualTo(aliveEntity);
		assertions.assertThat(manager.hasAliveEntityWith(aliveEntity.index())).isTrue();
		assertions.assertThat(manager.isAlive(aliveEntity)).isTrue();
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager doesn't contain this entity,
			 EntityManager contains dead entity with same index,
			 dead entity with same index in begin of reserved dead entities list
			 isAlive == false
			 => replace entity in EntityManager with this entity as dead
			""")
	@Test
	void unsafeSet7() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		for(int i = 512; i < 600; ++i) manager.remove(new Entity(i, 0));
		Entity entity = new Entity(512, 512);

		manager.unsafeSet(entity, false);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1000).clearRange(512, 600))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(1000);
		assertions.assertThat(manager.getEntityByIndex(entity.index())).isEqualTo(entity);
		assertions.assertThat(manager.hasAliveEntityWith(entity.index())).isFalse();
		assertions.assertThat(manager.isAlive(entity)).isFalse();
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager doesn't contain this entity,
			 EntityManager contains dead entity with same index,
			 dead entity with same index in end of reserved dead entities list
			 isAlive == false
			 => replace entity in EntityManager with this entity as dead
			""")
	@Test
	void unsafeSet8() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		for(int i = 512; i < 600; ++i) manager.remove(new Entity(i, 0));
		Entity entity = new Entity(599, 512);

		manager.unsafeSet(entity, false);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1000).clearRange(512, 600))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(1000);
		assertions.assertThat(manager.getEntityByIndex(entity.index())).isEqualTo(entity);
		assertions.assertThat(manager.hasAliveEntityWith(entity.index())).isFalse();
		assertions.assertThat(manager.isAlive(entity)).isFalse();
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager doesn't contain this entity,
			 EntityManager contains dead entity with same index,
			 reserved dead entities list has single entity
			 isAlive == false
			 => replace entity in EntityManager with this entity as dead
			""")
	@Test
	void unsafeSet9() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		manager.remove(new Entity(512, 0));
		Entity entity = new Entity(512, 512);

		manager.unsafeSet(entity, false);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1000).clearAll(512))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(1000);
		assertions.assertThat(manager.getEntityByIndex(entity.index())).isEqualTo(entity);
		assertions.assertThat(manager.hasAliveEntityWith(entity.index())).isFalse();
		assertions.assertThat(manager.isAlive(entity)).isFalse();
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager doesn't contain this entity,
			 EntityManager contains dead entity with same index,
			 dead entity with same index in begin of reserved dead entities list
			 isAlive == true
			 => replace entity in EntityManager with this entity as alive
			""")
	@Test
	void unsafeSet10() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		for(int i = 512; i < 600; ++i) manager.remove(new Entity(i, 0));
		Entity entity = new Entity(512, 512);

		manager.unsafeSet(entity, true);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1000).clearRange(513, 600))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(1000);
		assertions.assertThat(manager.getEntityByIndex(entity.index())).isEqualTo(entity);
		assertions.assertThat(manager.hasAliveEntityWith(entity.index())).isTrue();
		assertions.assertThat(manager.isAlive(entity)).isTrue();
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager doesn't contain this entity,
			 EntityManager contains dead entity with same index,
			 dead entity with same index in end of reserved dead entities list
			 isAlive == true
			 => replace entity in EntityManager with this entity as alive
			""")
	@Test
	void unsafeSet11() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		for(int i = 512; i < 600; ++i) manager.remove(new Entity(i, 0));
		Entity entity = new Entity(599, 512);

		manager.unsafeSet(entity, true);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1000).clearRange(512, 599))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(1000);
		assertions.assertThat(manager.getEntityByIndex(entity.index())).isEqualTo(entity);
		assertions.assertThat(manager.hasAliveEntityWith(entity.index())).isTrue();
		assertions.assertThat(manager.isAlive(entity)).isTrue();
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager doesn't contain this entity,
			 EntityManager contains dead entity with same index,
			 reserved dead entities list has single entity
			 isAlive == true
			 => replace entity in EntityManager with this entity as alive
			""")
	@Test
	void unsafeSet12() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		manager.remove(new Entity(512, 0));
		Entity entity = new Entity(512, 512);

		manager.unsafeSet(entity, true);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1000))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(1000);
		assertions.assertThat(manager.getEntityByIndex(entity.index())).isEqualTo(entity);
		assertions.assertThat(manager.hasAliveEntityWith(entity.index())).isTrue();
		assertions.assertThat(manager.isAlive(entity)).isTrue();
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager doesn't contain this entity,
			 EntityManager contains alive entity with same index,
			 isAlive == false
			 => replace entity in EntityManager with this entity as dead
			""")
	@Test
	void unsafeSet13() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		Entity entity = new Entity(217, 512);

		manager.unsafeSet(entity, false);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1000).clearAll(217))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(1000);
		assertions.assertThat(manager.getEntityByIndex(entity.index())).isEqualTo(entity);
		assertions.assertThat(manager.hasAliveEntityWith(entity.index())).isFalse();
		assertions.assertThat(manager.isAlive(entity)).isFalse();
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager doesn't contain this entity,
			 EntityManager contains alive entity with same index,
			 isAlive == true
			 => replace entity in EntityManager with this entity as alive
			""")
	@Test
	void unsafeSet14() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		Entity entity = new Entity(217, 512);

		manager.unsafeSet(entity, true);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1000))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(1000);
		assertions.assertThat(manager.getEntityByIndex(entity.index())).isEqualTo(entity);
		assertions.assertThat(manager.hasAliveEntityWith(entity.index())).isTrue();
		assertions.assertThat(manager.isAlive(entity)).isTrue();
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager doesn't contain this entity,
			 EntityManager doesn't contain entity with same index,
			 EntityManager.countReservedIndexes() = entity.index()
			 isAlive == true
			 => add this entity in EntityManager as alive
			""")
	@Test
	void unsafeSet15() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		Entity entity = new Entity(1000, 512);

		manager.unsafeSet(entity, true);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1001))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(1001);
		assertions.assertThat(manager.getEntityByIndex(entity.index())).isEqualTo(entity);
		assertions.assertThat(manager.hasAliveEntityWith(entity.index())).isTrue();
		assertions.assertThat(manager.isAlive(entity)).isTrue();
		assertions.assertThat(manager.create()).isEqualTo(new Entity(1001, 0));
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager doesn't contain this entity,
			 EntityManager doesn't contain entity with same index,
			 EntityManager.countReservedIndexes() = entity.index()
			 isAlive == false
			 => add this entity in EntityManager as dead
			""")
	@Test
	void unsafeSet16() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		Entity entity = new Entity(1000, 512);

		manager.unsafeSet(entity, false);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1000))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(1001);
		assertions.assertThat(manager.getEntityByIndex(entity.index())).isEqualTo(entity);
		assertions.assertThat(manager.hasAliveEntityWith(entity.index())).isFalse();
		assertions.assertThat(manager.isAlive(entity)).isFalse();
		assertions.assertThat(manager.create()).isEqualTo(entity);
		assertions.assertThat(manager.create()).isEqualTo(new Entity(1001, 0));
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager doesn't contain this entity,
			 EntityManager doesn't contain entity with same index,
			 EntityManager.countReservedIndexes() + 1000 = entity.index()
			 isAlive == true
			 => add this entity in EntityManager as alive,
			    add dead entities in EntityManager with index > countReservedIndexes() and index < entity.index()
			""")
	@Test
	void unsafeSet17() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		Entity entity = new Entity(1999, 512);

		manager.unsafeSet(entity, true);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.of(2000).setRange(0, 1000).setAll(1999))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(2000);
		assertions.assertThat(manager.getEntityByIndex(entity.index())).isEqualTo(entity);
		assertions.assertThat(manager.hasAliveEntityWith(entity.index())).isTrue();
		assertions.assertThat(manager.isAlive(entity)).isTrue();
		for(int i = 1000; i < 1999; ++i) assertions.assertThat(manager.create()).isEqualTo(new Entity(i, 0));
		assertions.assertThat(manager.create()).isEqualTo(entity);
		assertions.assertThat(manager.create()).isEqualTo(new Entity(2000, 0));
		assertions.assertAll();
	}

	@DisplayName("""
			unsafeSet(entity, isAlive):
			 EntityManager doesn't contain this entity,
			 EntityManager doesn't contain entity with same index,
			 EntityManager.countReservedIndexes() + 1000 = entity.index()
			 isAlive == false
			 => add this entity in EntityManager as dead,
			    add dead entities in EntityManager with index > countReservedIndexes() and index < entity.index()
			""")
	@Test
	void unsafeSet18() {
		EntityManager manager = new EntityManager();
		for(int i = 0; i < 1000; ++i) manager.create();
		Entity entity = new Entity(1999, 512);

		manager.unsafeSet(entity, false);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(manager.getAliveEntitiesMask().equalsIgnoreSize(Bits.filled(1000))).isTrue();
		assertions.assertThat(manager.countReservedIndexes()).isEqualTo(2000);
		assertions.assertThat(manager.getEntityByIndex(entity.index())).isEqualTo(entity);
		assertions.assertThat(manager.hasAliveEntityWith(entity.index())).isFalse();
		assertions.assertThat(manager.isAlive(entity)).isFalse();
		for(int i = 1000; i < 1999; ++i) assertions.assertThat(manager.create()).isEqualTo(new Entity(i, 0));
		assertions.assertThat(manager.create()).isEqualTo(entity);
		assertions.assertThat(manager.create()).isEqualTo(new Entity(2000, 0));
		assertions.assertAll();
	}


	private static List<Entity> createEntities(int generation, int... indexes) {
		List<Entity> result = new ArrayList<>();
		for(int index : indexes) result.add(new Entity(index, generation));
		return result;
	}

	private static List<Entity> createEntities(EntityManager manager, int count) {
		List<Entity> result = new ArrayList<>();
		for(int i = 0; i < count; ++i) result.add(manager.create());
		return result;
	}

	private static List<Entity> createEntities(int generation, int startIndexInclusive, int endIndexExclusive) {
		List<Entity> result = new ArrayList<>();
		for(int i = startIndexInclusive; i < endIndexExclusive; ++i)  result.add(new Entity(i, generation));
		return result;
	}

	private static void removeEntities(EntityManager manager, int generation, int... indexes) {
		for(int index : indexes) manager.remove(new Entity(index, generation));
	}
}