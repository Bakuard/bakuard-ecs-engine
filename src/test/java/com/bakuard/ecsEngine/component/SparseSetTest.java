package com.bakuard.ecsEngine.component;

import com.bakuard.ecsEngine.entity.Entity;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

	@DisplayName("""
            iterator():
             sparse set is empty
             => next return false, recentXXX methods return null
            """)
	@Test
	public void iterator1() {
		SparseSet sparseSet = new SparseSet();

		CompPool.EntryIterator<Object> iterator = sparseSet.iterator();

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(iterator.next()).isFalse();
		assertions.assertThat(iterator.recentEntity()).isNull();
		assertions.assertThat(iterator.recentComp()).isNull();
		assertions.assertAll();
	}

	@DisplayName("""
            iterator():
             sparse set contains one comp
             => next return true for item then false, recentXXX methods return entry then null
            """)
	@Test
	public void iterator2() {
		SparseSet sparseSet = new SparseSet();
		Entity entity = new Entity(1, 0);
		sparseSet.attachComp(entity, "some comp");

		CompPool.EntryIterator<String> iterator = sparseSet.iterator();
		List<String> actualComps = new ArrayList<>();
		List<Entity> actualEntities = new ArrayList<>();
		while(iterator.next()) {
			actualEntities.add(iterator.recentEntity());
			actualComps.add(iterator.recentComp());
		}
		actualEntities.add(iterator.recentEntity());
		actualComps.add(iterator.recentComp());

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(actualComps).containsExactly("some comp", null);
		assertions.assertThat(actualEntities).containsExactly(entity, null);
		assertions.assertAll();
	}

	@DisplayName("""
            iterator():
             sparse set contains several comps
             => next return true for each item then false, recentXXX methods return each entry then null
            """)
	@Test
	public void iterator3() {
		SparseSet sparseSet = new SparseSet();
		Entity entity1 = new Entity(1, 0);
		Entity entity2 = new Entity(2, 0);
		Entity entity3 = new Entity(3, 0);
		Entity entity4 = new Entity(4, 0);
		Entity entity5 = new Entity(5, 0);
		sparseSet.attachComp(entity1, "comp1");
		sparseSet.attachComp(entity2, "comp2");
		sparseSet.attachComp(entity3, "comp3");
		sparseSet.attachComp(entity4, "comp4");
		sparseSet.attachComp(entity5, "comp5");

		CompPool.EntryIterator<String> iterator = sparseSet.iterator();
		List<String> actualComps = new ArrayList<>();
		List<Entity> actualEntities = new ArrayList<>();
		while(iterator.next()) {
			actualEntities.add(iterator.recentEntity());
			actualComps.add(iterator.recentComp());
		}
		actualEntities.add(iterator.recentEntity());
		actualComps.add(iterator.recentComp());

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(actualComps).containsExactly("comp1", "comp2", "comp3", "comp4", "comp5", null);
		assertions.assertThat(actualEntities).containsExactly(entity1, entity2, entity3, entity4, entity5, null);
		assertions.assertAll();
	}

	@DisplayName("""
            iterator():
             sparse set contains several comps,
             try get entry without calling next()
             => recentXXX must return null
            """)
	@Test
	public void iterator4() {
		SparseSet sparseSet = new SparseSet();
		Entity entity1 = new Entity(1, 0);
		Entity entity2 = new Entity(2, 0);
		Entity entity3 = new Entity(3, 0);
		Entity entity4 = new Entity(4, 0);
		Entity entity5 = new Entity(5, 0);
		sparseSet.attachComp(entity1, "comp1");
		sparseSet.attachComp(entity2, "comp2");
		sparseSet.attachComp(entity3, "comp3");
		sparseSet.attachComp(entity4, "comp4");
		sparseSet.attachComp(entity5, "comp5");

		CompPool.EntryIterator<Object> iterator = sparseSet.iterator();

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(iterator.recentEntity()).isNull();
		assertions.assertThat(iterator.recentComp()).isNull();
		assertions.assertAll();
	}
}