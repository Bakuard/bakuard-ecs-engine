package com.bakuard.ecsEngine.component;

import com.bakuard.ecsEngine.entity.Entity;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SparseSetTest {

    public record Comp(String name){}

    @DisplayName("""
            attachComp(entity, comp):
             entity hasn't such comp
             => getComp(entity) must return comp
            """)
    @Test
    public void attachComp1() {
        SparseSet sparseSet = new SparseSet();
        Entity entityA = new Entity(0, 0);
        Entity entityB = new Entity(10, 0);
        Entity entityC = new Entity(100, 0);
        Entity entityD = new Entity(1000, 0);
        Comp expectedA = new Comp("A");
        Comp expectedB = new Comp("B");
        Comp expectedC = new Comp("C");
        Comp expectedD = new Comp("D");

        sparseSet.attachComp(entityA, expectedA);
        sparseSet.attachComp(entityB, expectedB);
        sparseSet.attachComp(entityC, expectedC);
        sparseSet.attachComp(entityD, expectedD);
        Comp actualA = sparseSet.getComp(entityA);
        Comp actualB = sparseSet.getComp(entityB);
        Comp actualC = sparseSet.getComp(entityC);
        Comp actualD = sparseSet.getComp(entityD);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualA).isSameAs(expectedA);
        assertions.assertThat(actualB).isSameAs(expectedB);
        assertions.assertThat(actualC).isSameAs(expectedC);
        assertions.assertThat(actualD).isSameAs(expectedD);
        assertions.assertAll();
    }

    @DisplayName("""
            attachComp(entity, comp):
             entity has such comp
             => replace comp, getComp(entity) must return new comp
            """)
    @Test
    public void attachComp2() {
        SparseSet sparseSet = new SparseSet();
        Entity entityA = new Entity(0, 0);
        Entity entityB = new Entity(10, 0);
        Entity entityC = new Entity(100, 0);
        Entity entityD = new Entity(1000, 0);
        Comp expectedA = new Comp("A");
        Comp expectedB = new Comp("B");
        Comp expectedC = new Comp("C");
        Comp expectedD = new Comp("D");

        sparseSet.attachComp(entityA, new Comp("A"));
        sparseSet.attachComp(entityB, new Comp("B"));
        sparseSet.attachComp(entityC, new Comp("C"));
        sparseSet.attachComp(entityD, new Comp("D"));
        sparseSet.attachComp(entityA, expectedA);
        sparseSet.attachComp(entityB, expectedB);
        sparseSet.attachComp(entityC, expectedC);
        sparseSet.attachComp(entityD, expectedD);
        Comp actualA = sparseSet.getComp(entityA);
        Comp actualB = sparseSet.getComp(entityB);
        Comp actualC = sparseSet.getComp(entityC);
        Comp actualD = sparseSet.getComp(entityD);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualA).isSameAs(expectedA);
        assertions.assertThat(actualB).isSameAs(expectedB);
        assertions.assertThat(actualC).isSameAs(expectedC);
        assertions.assertThat(actualD).isSameAs(expectedD);
        assertions.assertAll();
    }

    @DisplayName("""
            detachComp(entity, comp):
             entity has such comp
             => getComp(entity) must return null
            """)
    @Test
    public void detachComp1() {
        SparseSet sparseSet = new SparseSet();
        Entity entityA = new Entity(0, 0);
        Entity entityB = new Entity(10, 0);
        Entity entityC = new Entity(100, 0);
        Entity entityD = new Entity(1000, 0);
        sparseSet.attachComp(entityA, new Comp("A"));
        sparseSet.attachComp(entityB, new Comp("B"));
        sparseSet.attachComp(entityC, new Comp("C"));
        sparseSet.attachComp(entityD, new Comp("D"));

        sparseSet.detachComp(entityA);
        sparseSet.detachComp(entityB);
        sparseSet.detachComp(entityC);
        sparseSet.detachComp(entityD);
        Comp actualA = sparseSet.getComp(entityA);
        Comp actualB = sparseSet.getComp(entityB);
        Comp actualC = sparseSet.getComp(entityC);
        Comp actualD = sparseSet.getComp(entityD);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualA).isNull();
        assertions.assertThat(actualB).isNull();
        assertions.assertThat(actualC).isNull();
        assertions.assertThat(actualD).isNull();
        assertions.assertAll();
    }

    @DisplayName("""
            detachComp(entity, comp):
             entity hasn't such comp
             => doesn't change sparseSet, getComp(entity) must return null
            """)
    @Test
    public void detachComp2() {
        SparseSet sparseSet = new SparseSet();
        Entity entityA = new Entity(0, 0);
        Entity entityB = new Entity(10, 0);
        Entity entityC = new Entity(100, 0);
        Entity entityD = new Entity(1000, 0);

        sparseSet.detachComp(entityA);
        sparseSet.detachComp(entityB);
        sparseSet.detachComp(entityC);
        sparseSet.detachComp(entityD);
        Comp actualA = sparseSet.getComp(entityA);
        Comp actualB = sparseSet.getComp(entityB);
        Comp actualC = sparseSet.getComp(entityC);
        Comp actualD = sparseSet.getComp(entityD);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualA).isNull();
        assertions.assertThat(actualB).isNull();
        assertions.assertThat(actualC).isNull();
        assertions.assertThat(actualD).isNull();
        assertions.assertAll();
    }

    @DisplayName("""
            swap(first, second):
             sparseSet doesn't contain first,
             sparseSet contains second
             => do nothing
            """)
    @Test
    public void swap1() {
        SparseSet sparseSet = new SparseSet();
        Entity first = new Entity(100, 0);
        Entity second = new Entity(0, 0);
        Comp expected = new Comp("A");
        sparseSet.attachComp(second, expected);

        sparseSet.swap(first, second);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat((Comp)sparseSet.getComp(first)).isNull();
        assertions.assertThat((Comp)sparseSet.getComp(second)).isSameAs(expected);
        assertions.assertAll();
    }

    @DisplayName("""
            swap(first, second):
             sparseSet doesn't contain first,
             sparseSet doesn't contains second
             => do nothing
            """)
    @Test
    public void swap2() {
        SparseSet sparseSet = new SparseSet();
        Entity first = new Entity(100, 0);
        Entity second = new Entity(0, 0);

        sparseSet.swap(first, second);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat((Comp)sparseSet.getComp(first)).isNull();
        assertions.assertThat((Comp)sparseSet.getComp(second)).isNull();
        assertions.assertAll();
    }

    @DisplayName("""
            swap(first, second):
             sparseSet contains first,
             sparseSet contains second
             => swap comps
            """)
    @Test
    public void swap3() {
        SparseSet sparseSet = new SparseSet();
        Entity first = new Entity(100, 0);
        Entity second = new Entity(0, 0);
        Comp firstComp = new Comp("first");
        Comp secondComp = new Comp("second");
        sparseSet.attachComp(first, firstComp);
        sparseSet.attachComp(second, secondComp);

        sparseSet.swap(first, second);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat((Comp)sparseSet.getComp(first)).isSameAs(secondComp);
        assertions.assertThat((Comp)sparseSet.getComp(second)).isSameAs(firstComp);
        assertions.assertAll();
    }

    @DisplayName("""
            hasComp(entity):
             entity has such comp
             => return true
            """)
    @Test
    public void hasComp1() {
        SparseSet sparseSet = new SparseSet();
        Entity entityA = new Entity(0, 0);
        Entity entityB = new Entity(10, 0);
        Entity entityC = new Entity(100, 0);
        Entity entityD = new Entity(1000, 0);
        sparseSet.attachComp(entityA, new Comp("A"));
        sparseSet.attachComp(entityB, new Comp("B"));
        sparseSet.attachComp(entityC, new Comp("C"));
        sparseSet.attachComp(entityD, new Comp("D"));

        boolean actualA = sparseSet.hasComp(entityA);
        boolean actualB = sparseSet.hasComp(entityB);
        boolean actualC = sparseSet.hasComp(entityC);
        boolean actualD = sparseSet.hasComp(entityD);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualA).isTrue();
        assertions.assertThat(actualB).isTrue();
        assertions.assertThat(actualC).isTrue();
        assertions.assertThat(actualD).isTrue();
        assertions.assertAll();
    }

    @DisplayName("""
            hasComp(entity):
             entity hasn't such comp
             => return false
            """)
    @Test
    public void hasComp2() {
        SparseSet sparseSet = new SparseSet();
        Entity entityA = new Entity(0, 0);
        Entity entityB = new Entity(10, 0);
        Entity entityC = new Entity(100, 0);
        Entity entityD = new Entity(1000, 0);

        boolean actualA = sparseSet.hasComp(entityA);
        boolean actualB = sparseSet.hasComp(entityB);
        boolean actualC = sparseSet.hasComp(entityC);
        boolean actualD = sparseSet.hasComp(entityD);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualA).isFalse();
        assertions.assertThat(actualB).isFalse();
        assertions.assertThat(actualC).isFalse();
        assertions.assertThat(actualD).isFalse();
        assertions.assertAll();
    }

    @DisplayName("""
            size():
             sparse set is empty
             => return 0
            """)
    @Test
    public void size1() {
        SparseSet sparseSet = new SparseSet();

        int actual = sparseSet.size();

        Assertions.assertThat(actual).isZero();
    }

    @DisplayName("""
            size():
             all entities and comps was removed from sparse set
             => return 0
            """)
    @Test
    public void size2() {
        SparseSet sparseSet = new SparseSet();
        Entity entityA = new Entity(0, 0);
        Entity entityB = new Entity(10, 0);
        Entity entityC = new Entity(100, 0);
        Entity entityD = new Entity(1000, 0);
        sparseSet.attachComp(entityA, new Comp("A"));
        sparseSet.attachComp(entityB, new Comp("B"));
        sparseSet.attachComp(entityC, new Comp("C"));
        sparseSet.attachComp(entityD, new Comp("D"));

        sparseSet.detachComp(entityA);
        sparseSet.detachComp(entityB);
        sparseSet.detachComp(entityC);
        sparseSet.detachComp(entityD);
        int actual = sparseSet.size();

        Assertions.assertThat(actual).isZero();
    }

    @DisplayName("""
            size():
             sparse set contains 100 entities
             => return 100
            """)
    @Test
    public void size3() {
        SparseSet sparseSet = new SparseSet();

        for(int i = 0; i < 100; ++i) {
            sparseSet.attachComp(new Entity(i, 0), new Comp(Integer.toString(i)));
        }
        int actual = sparseSet.size();

        Assertions.assertThat(actual).isEqualTo(100);
    }
}