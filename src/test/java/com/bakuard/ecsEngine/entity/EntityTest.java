package com.bakuard.ecsEngine.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntityTest {

	@DisplayName("""
			fromLong(entityAsLong):
			 entityAsLong = 0
			 => index = 0, generation = 0
			""")
	@Test
	void fromLong1() {
		long entityAsLong = 0;

		Entity actual = Entity.fromLong(entityAsLong);

		Assertions.assertThat(actual).isEqualTo(new Entity(0, 0));
	}

	@DisplayName("""
			fromLong(entityAsLong):
			 entityAsLong = 0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0001
			 => index = 1, generation = 0
			""")
	@Test
	void fromLong2() {
		long entityAsLong = 0b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0001L;

		Entity actual = Entity.fromLong(entityAsLong);

		Assertions.assertThat(actual).isEqualTo(new Entity(1, 0));
	}

	@DisplayName("""
			fromLong(entityAsLong):
			 entityAsLong = 0000_0000_0000_0000_0000_0000_0000_0001_0000_0000_0000_0000_0000_0000_0000_0000
			 => index = 0, generation = 1
			""")
	@Test
	void fromLong3() {
		long entityAsLong = 0b0000_0000_0000_0000_0000_0000_0000_0001_0000_0000_0000_0000_0000_0000_0000_0000L;

		Entity actual = Entity.fromLong(entityAsLong);

		Assertions.assertThat(actual).isEqualTo(new Entity(0, 1));
	}

	@DisplayName("""
			fromLong(entityAsLong):
			 entityAsLong = 0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0001
			 => index = 1, generation = 1
			""")
	@Test
	void fromLong4() {
		long entityAsLong = 0b0000_0000_0000_0000_0000_0000_0000_0001_0000_0000_0000_0000_0000_0000_0000_0001L;

		Entity actual = Entity.fromLong(entityAsLong);

		Assertions.assertThat(actual).isEqualTo(new Entity(1, 1));
	}

	@DisplayName("""
			asLong(), Entity(entityAsLong):
			 => new Entity(originEntity.asLong()) must be equal originEntity
			""")
	@Test
	void toLong1() {
		Entity origin = new Entity(112, 56);

		Entity actual = Entity.fromLong(Entity.toLong(origin));

		Assertions.assertThat(actual).isEqualTo(origin);
	}
}