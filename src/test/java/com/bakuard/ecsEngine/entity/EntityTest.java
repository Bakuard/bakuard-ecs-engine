package com.bakuard.ecsEngine.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntityTest {

    @DisplayName("""
            Entity(entityAsLong):
             entityAsLong = 0
             => index = 0, generation = 0
            """)
    @Test
    void constructorWithLong1() {
        long entityAsLong = 0;

        Entity actual = new Entity(entityAsLong);

        Assertions.assertThat(actual).isEqualTo(new Entity(0, 0));
    }

    @DisplayName("""
            Entity(entityAsLong):
             entityAsLong = 0000_0000_0000_0000_0000_0000_0000_0001_0000_0000_0000_0000_0000_0000_0000_0000
             => index = 1, generation = 0
            """)
    @Test
    void constructorWithLong2() {
        long entityAsLong = 0b0000_0000_0000_0000_0000_0000_0000_0001_0000_0000_0000_0000_0000_0000_0000_0000L;

        Entity actual = new Entity(entityAsLong);

        Assertions.assertThat(actual).isEqualTo(new Entity(1, 0));
    }

    @DisplayName("""
            Entity(entityAsLong):
             entityAsLong = 0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0001
             => index = 0, generation = 1
            """)
    @Test
    void constructorWithLong3() {
        long entityAsLong = 0b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0001L;

        Entity actual = new Entity(entityAsLong);

        Assertions.assertThat(actual).isEqualTo(new Entity(0, 1));
    }

    @DisplayName("""
            Entity(entityAsLong):
             entityAsLong = 0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0001
             => index = 1, generation = 1
            """)
    @Test
    void constructorWithLong4() {
        long entityAsLong = 0b0000_0000_0000_0000_0000_0000_0000_0001_0000_0000_0000_0000_0000_0000_0000_0001L;

        Entity actual = new Entity(entityAsLong);

        Assertions.assertThat(actual).isEqualTo(new Entity(1, 1));
    }

    @DisplayName("""
            asLong(), Entity(entityAsLong):
             => new Entity(originEntity.asLong()) must be equal originEntity
            """)
    @Test
    void asLong1() {
        Entity origin = new Entity(112, 56);

        Entity actual = new Entity(origin.asLong());

        Assertions.assertThat(actual).isEqualTo(origin);
    }
}