package com.bakuard.ecsEngine.entity;

import com.bakuard.ecsEngine.exception.DeadEntityException;
import com.bakuard.ecsEngine.exception.DuplicateEntityNameException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntityNamesManagerTest {

	@DisplayName("""
			assignName(entity, name):
			 entity is alive,
			 there is no entity with this name
			 => getNameByEntity(entity) return this name,
			    getEntityByName(name) return this entity by this name
			""")
	@Test
	public void assignName1() {
		EntityManager entityManager = new EntityManager();
		EntityNamesManager entityNamesManager = new EntityNamesManager(entityManager);
		Entity entityA = entityManager.create();

		entityNamesManager.assignName(entityA, "A");

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(entityNamesManager.getNameByEntity(entityA)).isEqualTo("A");
		assertions.assertThat(entityNamesManager.getEntityByName("A")).isEqualTo(entityA);
		assertions.assertAll();
	}

	@DisplayName("""
			assignName(entity, name):
			 entity is not alive,
			 there is no entity with this name
			 => throw exception,
			    getNameByEntity(entity) return null,
			    getEntityByName(name) return null,
			    hasName(entity, name) return false
			""")
	@Test
	public void assignName2() {
		EntityManager entityManager = new EntityManager();
		EntityNamesManager entityNamesManager = new EntityNamesManager(entityManager);
		Entity deadEntity = entityManager.create();
		entityManager.remove(deadEntity);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThatThrownBy(() -> entityNamesManager.assignName(deadEntity, "A")).isInstanceOf(DeadEntityException.class);
		assertions.assertThat(entityNamesManager.getNameByEntity(deadEntity)).isNull();
		assertions.assertThat(entityNamesManager.getEntityByName("A")).isNull();
		assertions.assertThat(entityNamesManager.hasName(deadEntity, "A")).isFalse();
		assertions.assertAll();
	}

	@DisplayName("""
			assignName(entity, name):
			 entity is not alive,
			 there is entity with this name
			 => throw exception,
			    getNameByEntity(entity) return null,
			    getEntityByName(name) return alive entity by this name,
			    hasName(entity, name) return false
			""")
	@Test
	public void assignName3() {
		EntityManager entityManager = new EntityManager();
		EntityNamesManager entityNamesManager = new EntityNamesManager(entityManager);
		Entity deadEntity = entityManager.create();
		Entity aliveEntity = entityManager.create();
		entityManager.remove(deadEntity);

		entityNamesManager.assignName(aliveEntity, "A");

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThatThrownBy(() -> entityNamesManager.assignName(deadEntity, "A")).isInstanceOf(DeadEntityException.class);
		assertions.assertThat(entityNamesManager.getNameByEntity(deadEntity)).isNull();
		assertions.assertThat(entityNamesManager.getEntityByName("A")).isEqualTo(aliveEntity);
		assertions.assertThat(entityNamesManager.hasName(deadEntity, "A")).isFalse();
		assertions.assertAll();
	}

	@DisplayName("""
			assignName(entity, name):
			 entity is alive,
			 there is entity with this name
			 => throw exception
			""")
	@Test
	public void assignName4() {
		EntityManager entityManager = new EntityManager();
		EntityNamesManager entityNamesManager = new EntityNamesManager(entityManager);
		Entity entityA = entityManager.create();
		Entity entityB = entityManager.create();

		entityNamesManager.assignName(entityB, "A");

		Assertions.assertThatThrownBy(() -> entityNamesManager.assignName(entityA, "A")).isInstanceOf(DuplicateEntityNameException.class);
	}

	@DisplayName("""
			assignName(entity, name):
			 entity is alive,
			 this entity has already had another name
			 => getNameByEntity(entity) return new assigned name,
				getEntityByName(name) return null by old name,
				getEntityByName(name) return this entity by new name
			""")
	@Test
	public void assignName5() {
		EntityManager entityManager = new EntityManager();
		EntityNamesManager entityNamesManager = new EntityNamesManager(entityManager);
		Entity entityA = entityManager.create();

		entityNamesManager.assignName(entityA, "A");
		entityNamesManager.assignName(entityA, "B");

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(entityNamesManager.getNameByEntity(entityA)).isEqualTo("B");
		assertions.assertThat(entityNamesManager.getEntityByName("A")).isNull();
		assertions.assertThat(entityNamesManager.getEntityByName("B")).isEqualTo(entityA);
		assertions.assertAll();
	}
}