package com.bakuard.ecsEngine.component;

import com.bakuard.collections.Bits;
import com.bakuard.ecsEngine.entity.Entity;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

class SparseSetTest {

	public record Comp(String name){}

	@DisplayName("""
			attachComp(entity, comp):
			 entity hasn't comp
			 => getComp(entity) must return comp,
			    size must be increased,
			    bits mask must be changed
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

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(sparseSet.<Comp>getComp(entityA)).isSameAs(expectedA);
		assertions.assertThat(sparseSet.<Comp>getComp(entityB)).isSameAs(expectedB);
		assertions.assertThat(sparseSet.<Comp>getComp(entityC)).isSameAs(expectedC);
		assertions.assertThat(sparseSet.<Comp>getComp(entityD)).isSameAs(expectedD);
		assertions.assertThat(sparseSet.size()).isEqualTo(4);
		assertions.assertThat(sparseSet.getEntityIndexesMask()).isEqualTo(Bits.of(sparseSet.getEntityIndexesMask().size(), 0,10,100,1000));
		assertions.assertAll();
	}

	@DisplayName("""
			attachComp(entity, comp):
			 entity has comp
			 => getComp(entity) must return new comp,
			    size can't be changed,
			    bits mask can't be changed
			""")
	@Test
	public void attachComp2() {
		SparseSet sparseSet = new SparseSet();
		Entity entityA = new Entity(0, 0);
		Entity entityB = new Entity(10, 0);
		Entity entityC = new Entity(100, 0);
		Entity entityD = new Entity(1000, 0);
		sparseSet.attachComp(entityA, new Comp("A"));
		sparseSet.attachComp(entityB, new Comp("B"));
		sparseSet.attachComp(entityC, new Comp("C"));
		sparseSet.attachComp(entityD, new Comp("D"));
		Comp expectedA = new Comp("A2");
		Comp expectedB = new Comp("B2");
		Comp expectedC = new Comp("C2");
		Comp expectedD = new Comp("D2");

		sparseSet.attachComp(entityA, expectedA);
		sparseSet.attachComp(entityB, expectedB);
		sparseSet.attachComp(entityC, expectedC);
		sparseSet.attachComp(entityD, expectedD);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(sparseSet.<Comp>getComp(entityA)).isSameAs(expectedA);
		assertions.assertThat(sparseSet.<Comp>getComp(entityB)).isSameAs(expectedB);
		assertions.assertThat(sparseSet.<Comp>getComp(entityC)).isSameAs(expectedC);
		assertions.assertThat(sparseSet.<Comp>getComp(entityD)).isSameAs(expectedD);
		assertions.assertThat(sparseSet.size()).isEqualTo(4);
		assertions.assertThat(sparseSet.getEntityIndexesMask()).isEqualTo(Bits.of(sparseSet.getEntityIndexesMask().size(), 0,10,100,1000));
		assertions.assertAll();
	}

	@DisplayName("""
			detachComp(entity):
			 entity has comp
			 => getComp(entity) must return null,
			    size must be decreased,
			    bits mask must be changed,
			    not changed another entities
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

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(sparseSet.<Comp>getComp(entityA)).isNull();
		assertions.assertThat(sparseSet.<Comp>getComp(entityB)).isNull();
		assertions.assertThat(sparseSet.<Comp>getComp(entityC)).isEqualTo(new Comp("C"));
		assertions.assertThat(sparseSet.<Comp>getComp(entityD)).isEqualTo(new Comp("D"));
		assertions.assertThat(sparseSet.size()).isEqualTo(2);
		assertions.assertThat(sparseSet.getEntityIndexesMask()).isEqualTo(Bits.of(sparseSet.getEntityIndexesMask().size(), 100,1000));
		assertions.assertAll();
	}

	@DisplayName("""
			detachComp(entity):
			 entity hasn't comp
			 => getComp(entity) must return null,
			    size can't be changed,
			    bits mask can't be changed,
			    not changed another entities
			""")
	@Test
	public void detachComp2() {
		SparseSet sparseSet = new SparseSet();
		Entity entityA = new Entity(0, 0);
		Entity entityB = new Entity(10, 0);
		Entity entityC = new Entity(100, 0);
		Entity entityD = new Entity(1000, 0);
		sparseSet.attachComp(entityC, new Comp("C"));
		sparseSet.attachComp(entityD, new Comp("D"));

		sparseSet.detachComp(entityA);
		sparseSet.detachComp(entityB);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(sparseSet.<Comp>getComp(entityA)).isNull();
		assertions.assertThat(sparseSet.<Comp>getComp(entityB)).isNull();
		assertions.assertThat(sparseSet.<Comp>getComp(entityC)).isEqualTo(new Comp("C"));
		assertions.assertThat(sparseSet.<Comp>getComp(entityD)).isEqualTo(new Comp("D"));
		assertions.assertThat(sparseSet.size()).isEqualTo(2);
		assertions.assertThat(sparseSet.getEntityIndexesMask()).isEqualTo(Bits.of(sparseSet.getEntityIndexesMask().size(), 100,1000));
		assertions.assertAll();
	}

	@DisplayName("""
			detachComp(entity):
			 entity has comp,
			 sparseSet has single comp
			 => getComp(entity) must return null,
			    size must be zero,
			    bits mask must be zero
			""")
	@Test
	public void detachComp3() {
		SparseSet sparseSet = new SparseSet();
		Entity entity = new Entity(0, 0);
		sparseSet.attachComp(entity, new Comp("C"));

		sparseSet.detachComp(entity);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(sparseSet.<Comp>getComp(entity)).isNull();
		assertions.assertThat(sparseSet.size()).isZero();
		assertions.assertThat(sparseSet.getEntityIndexesMask()).isEqualTo(new Bits(sparseSet.getEntityIndexesMask().size()));
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
		assertions.assertThat(sparseSet.getEntityIndexesMask()).isEqualTo(Bits.of(sparseSet.getEntityIndexesMask().size(), 0));
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
		assertions.assertThat(sparseSet.getEntityIndexesMask()).isEqualTo(new Bits(sparseSet.getEntityIndexesMask().size()));
		assertions.assertAll();
	}

	@DisplayName("""
			swap(first, second):
			 sparseSet contains first,
			 sparseSet contains second
			 => swap comps,
			    not change bits mask
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
		assertions.assertThat(sparseSet.getEntityIndexesMask()).isEqualTo(Bits.of(sparseSet.getEntityIndexesMask().size(), 0,100));
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

		Assertions.assertThat(sparseSet.size()).isZero();
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

		Assertions.assertThat(sparseSet.size()).isEqualTo(100);
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

	@DisplayName("""
			merge(src, strategy):
			 current pool is empty,
			 src pool is empty
			 => strategy never call
			""")
	@Test
	public void merge1() {
		SparseSet dest = new SparseSet();
		SparseSet src = new SparseSet();
		MergeCompPoolStrategy<Integer> strategy = Mockito.mock(MergeCompPoolStrategy.class);

		dest.merge(src, strategy);

		Mockito.verify(strategy, Mockito.never()).merge(Mockito.any(Entity.class), Mockito.any(Integer.class), Mockito.any(Integer.class));
	}

	@DisplayName("""
			merge(src, strategy):
			 current pool is empty,
			 src pool is not empty
			 => strategy.merge(entity, currentComp, srcComp) must be called src pool size times,
			    currentComp always must be null
			""")
	@Test
	public void merge2() {
		SparseSet dest = new SparseSet();
		SparseSet src = new SparseSet();
		for(int i = 0; i < 10; ++i) src.attachComp(new Entity(i, 0), i);
		MergeCompPoolStrategy<Integer> strategy = Mockito.mock(MergeCompPoolStrategy.class);

		dest.merge(src, strategy);

		Mockito.verify(strategy, Mockito.times(10)).merge(Mockito.isNotNull(Entity.class), Mockito.isNull(), Mockito.any(Integer.class));
	}

	@DisplayName("""
			merge(src, strategy):
			 current pool is not empty,
			 src pool is empty
			 => strategy.merge(entity, currentComp, srcComp) must be called current pool size times,
			    srcComp always must be null
			""")
	@Test
	public void merge3() {
		SparseSet dest = new SparseSet();
		SparseSet src = new SparseSet();
		for(int i = 0; i < 10; ++i) dest.attachComp(new Entity(i, 0), i);
		MergeCompPoolStrategy<Integer> strategy = Mockito.mock(MergeCompPoolStrategy.class);

		dest.merge(src, strategy);

		Mockito.verify(strategy, Mockito.times(10)).merge(Mockito.isNotNull(Entity.class), Mockito.isNotNull(Integer.class), Mockito.isNull());
	}

	@DisplayName("""
			merge(src, strategy):
			 current pool is not empty,
			 src pool is not empty
			 => strategy.merge(entity, currentComp, srcComp) must be called unique(entities of current and src pool) times,
			    srcComp always must be null
			""")
	@Test
	public void merge4() {
		SparseSet dest = new SparseSet();
		SparseSet src = new SparseSet();
		for(int i = 0; i < 10; ++i) dest.attachComp(new Entity(i, 0), i);
		for(int i = 5; i < 15; ++i) src.attachComp(new Entity(i, 0), i);
		MergeCompPoolStrategy<Integer> strategy = Mockito.mock(MergeCompPoolStrategy.class);
		Mockito.when(strategy.merge(Mockito.any(Entity.class), Mockito.any(Integer.class), Mockito.any(Integer.class))).thenReturn(1000);

		dest.merge(src, strategy);

		Mockito.verify(strategy, Mockito.times(15))
				.merge(Mockito.isNotNull(Entity.class), Mockito.nullable(Integer.class), Mockito.nullable(Integer.class));
	}

	@DisplayName("""
			merge(src, strategy):
			 current pool is not empty,
			 src pool is empty,
			 discard half items from current pool
			 => current pool must contains half items
			""")
	@Test
	public void merge5() {
		SparseSet dest = new SparseSet();
		SparseSet src = new SparseSet();
		for(int i = 0; i < 10; ++i) dest.attachComp(new Entity(i, 0), i);
		MergeCompPoolStrategy<Integer> strategy = (entity, currentComp, srcComp) -> currentComp < 5 ? currentComp : null;

		dest.merge(src, strategy);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(dest.size()).isEqualTo(5);
		assertions.assertThat(dest.<Integer>getComp(new Entity(0, 0))).isEqualTo(0);
		assertions.assertThat(dest.<Integer>getComp(new Entity(1, 0))).isEqualTo(1);
		assertions.assertThat(dest.<Integer>getComp(new Entity(2, 0))).isEqualTo(2);
		assertions.assertThat(dest.<Integer>getComp(new Entity(3, 0))).isEqualTo(3);
		assertions.assertThat(dest.<Integer>getComp(new Entity(4, 0))).isEqualTo(4);
		assertions.assertThat(dest.getEntityIndexesMask()).isEqualTo(Bits.of(dest.getEntityIndexesMask().size(), 0,1,2,3,4));
		assertions.assertAll();
	}

	@DisplayName("""
			merge(src, strategy):
			 current pool is not empty,
			 src pool is empty,
			 replace all items from current pool
			 => current pool must contains new items
			""")
	@Test
	public void merge6() {
		SparseSet dest = new SparseSet();
		SparseSet src = new SparseSet();
		for(int i = 0; i < 10; ++i) dest.attachComp(new Entity(i, 0), i);
		MergeCompPoolStrategy<Integer> strategy = (entity, currentComp, srcComp) -> currentComp * 2;

		dest.merge(src, strategy);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(dest.size()).isEqualTo(10);
		assertions.assertThat(dest.<Integer>getComp(new Entity(0, 0))).isEqualTo(0);
		assertions.assertThat(dest.<Integer>getComp(new Entity(1, 0))).isEqualTo(2);
		assertions.assertThat(dest.<Integer>getComp(new Entity(2, 0))).isEqualTo(4);
		assertions.assertThat(dest.<Integer>getComp(new Entity(3, 0))).isEqualTo(6);
		assertions.assertThat(dest.<Integer>getComp(new Entity(4, 0))).isEqualTo(8);
		assertions.assertThat(dest.<Integer>getComp(new Entity(5, 0))).isEqualTo(10);
		assertions.assertThat(dest.<Integer>getComp(new Entity(6, 0))).isEqualTo(12);
		assertions.assertThat(dest.<Integer>getComp(new Entity(7, 0))).isEqualTo(14);
		assertions.assertThat(dest.<Integer>getComp(new Entity(8, 0))).isEqualTo(16);
		assertions.assertThat(dest.<Integer>getComp(new Entity(9, 0))).isEqualTo(18);
		assertions.assertThat(dest.getEntityIndexesMask()).isEqualTo(Bits.of(dest.getEntityIndexesMask().size(), 0,1,2,3,4,5,6,7,8,9));
		assertions.assertAll();
	}

	@DisplayName("""
			merge(src, strategy):
			 current pool is empty,
			 src pool is empty,
			 discard half items from src pool
			 => current pool must contains half items from src pool
			""")
	@Test
	public void merge7() {
		SparseSet dest = new SparseSet();
		SparseSet src = new SparseSet();
		for(int i = 0; i < 10; ++i) src.attachComp(new Entity(i, 0), i);
		MergeCompPoolStrategy<Integer> strategy = (entity, currentComp, srcComp) -> srcComp < 5 ? srcComp : null;

		dest.merge(src, strategy);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(dest.size()).isEqualTo(5);
		assertions.assertThat(dest.<Integer>getComp(new Entity(0, 0))).isEqualTo(0);
		assertions.assertThat(dest.<Integer>getComp(new Entity(1, 0))).isEqualTo(1);
		assertions.assertThat(dest.<Integer>getComp(new Entity(2, 0))).isEqualTo(2);
		assertions.assertThat(dest.<Integer>getComp(new Entity(3, 0))).isEqualTo(3);
		assertions.assertThat(dest.<Integer>getComp(new Entity(4, 0))).isEqualTo(4);
		assertions.assertThat(dest.getEntityIndexesMask()).isEqualTo(Bits.of(dest.getEntityIndexesMask().size(), 0,1,2,3,4));
		assertions.assertAll();
	}

	@DisplayName("""
			merge(src, strategy):
			 current pool is empty,
			 src pool is not empty,
			 replace all items from src pool
			 => current pool must contains new items
			""")
	@Test
	public void merge8() {
		SparseSet dest = new SparseSet();
		SparseSet src = new SparseSet();
		for(int i = 0; i < 10; ++i) src.attachComp(new Entity(i, 0), i);
		MergeCompPoolStrategy<Integer> strategy = (entity, currentComp, srcComp) -> srcComp * 2;

		dest.merge(src, strategy);

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(dest.size()).isEqualTo(10);
		assertions.assertThat(dest.<Integer>getComp(new Entity(0, 0))).isEqualTo(0);
		assertions.assertThat(dest.<Integer>getComp(new Entity(1, 0))).isEqualTo(2);
		assertions.assertThat(dest.<Integer>getComp(new Entity(2, 0))).isEqualTo(4);
		assertions.assertThat(dest.<Integer>getComp(new Entity(3, 0))).isEqualTo(6);
		assertions.assertThat(dest.<Integer>getComp(new Entity(4, 0))).isEqualTo(8);
		assertions.assertThat(dest.<Integer>getComp(new Entity(5, 0))).isEqualTo(10);
		assertions.assertThat(dest.<Integer>getComp(new Entity(6, 0))).isEqualTo(12);
		assertions.assertThat(dest.<Integer>getComp(new Entity(7, 0))).isEqualTo(14);
		assertions.assertThat(dest.<Integer>getComp(new Entity(8, 0))).isEqualTo(16);
		assertions.assertThat(dest.<Integer>getComp(new Entity(9, 0))).isEqualTo(18);
		assertions.assertThat(dest.getEntityIndexesMask()).isEqualTo(Bits.of(dest.getEntityIndexesMask().size(), 0,1,2,3,4,5,6,7,8,9));
		assertions.assertAll();
	}

	@DisplayName("""
			equals(obj) and hashCode():
			 sparseSetA is empty,
			 sparseSetB is empty,
			 inner arrays has same size
			 => equals return true,
			    hashCodes must be equal
			""")
	@Test
	public void equalsAndHashCode1() {
		SparseSet sparseSetA = new SparseSet();
		SparseSet sparseSetB = new SparseSet();

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(sparseSetA.equals(sparseSetB)).isTrue();
		assertions.assertThat(sparseSetA.hashCode()).isEqualTo(sparseSetB.hashCode());
		assertions.assertAll();
	}

	@DisplayName("""
			equals(obj) and hashCode():
			 sparseSetA is empty,
			 sparseSetB is empty,
			 sparseSetA inner arrays size < sparseSetB inner arrays size,
			 methods is called on sparseSetA
			 => equals return true,
			    hashCodes must be equal
			""")
	@Test
	public void equalsAndHashCode2() {
		SparseSet sparseSetA = new SparseSet();
		SparseSet sparseSetB = new SparseSet();
		sparseSetB.attachComp(new Entity(1000, 0), new Comp("some value"));
		sparseSetB.detachComp(new Entity(1000, 0));

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(sparseSetA.equals(sparseSetB)).isTrue();
		assertions.assertThat(sparseSetA.hashCode()).isEqualTo(sparseSetB.hashCode());
		assertions.assertAll();
	}

	@DisplayName("""
			equals(obj) and hashCode():
			 sparseSetA has single item,
			 sparseSetB has single item,
			 inner arrays has same size,
			 sparseSetA equal to sparseSetB
			 => equals return true,
			    hashCodes must be equal
			""")
	@Test
	public void equalsAndHashCode3() {
		SparseSet sparseSetA = new SparseSet();
		sparseSetA.attachComp(new Entity(1000, 0), new Comp("some value"));
		SparseSet sparseSetB = new SparseSet();
		sparseSetB.attachComp(new Entity(1000, 0), new Comp("some value"));

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(sparseSetA.equals(sparseSetB)).isTrue();
		assertions.assertThat(sparseSetA.hashCode()).isEqualTo(sparseSetB.hashCode());
		assertions.assertAll();
	}

	@DisplayName("""
			equals(obj) and hashCode():
			 sparseSetA has single item,
			 sparseSetB has single item,
			 sparseSetA inner arrays size > sparseSetB inner arrays size,
			 sparseSetA equal to sparseSetB
			 => equals return true,
			    hashCodes must be equal
			""")
	@Test
	public void equalsAndHashCode4() {
		SparseSet sparseSetA = new SparseSet();
		sparseSetA.attachComp(new Entity(1000, 0), new Comp("some value"));
		sparseSetA.attachComp(new Entity(2000, 0), new Comp("other value"));
		sparseSetA.detachComp(new Entity(2000, 0));
		SparseSet sparseSetB = new SparseSet();
		sparseSetB.attachComp(new Entity(1000, 0), new Comp("some value"));

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(sparseSetA.equals(sparseSetB)).isTrue();
		assertions.assertThat(sparseSetA.hashCode()).isEqualTo(sparseSetB.hashCode());
		assertions.assertAll();
	}

	@DisplayName("""
			equals(obj) and hashCode():
			 sparseSetA has several items,
			 sparseSetB has several items,
			 inner arrays has same size,
			 sparseSetA equal to sparseSetB
			 => equals return true,
			    hashCodes must be equal
			""")
	@Test
	public void equalsAndHashCode5() {
		SparseSet sparseSetA = new SparseSet();
		sparseSetA.attachComp(new Entity(10, 0), new Comp("value 10"));
		sparseSetA.attachComp(new Entity(1000, 0), new Comp("value 1000"));
		sparseSetA.attachComp(new Entity(100, 0), new Comp("value 100"));
		sparseSetA.attachComp(new Entity(0, 0), new Comp("value 0"));
		SparseSet sparseSetB = new SparseSet();
		sparseSetB.attachComp(new Entity(0, 0), new Comp("value 0"));
		sparseSetB.attachComp(new Entity(100, 0), new Comp("value 100"));
		sparseSetB.attachComp(new Entity(10, 0), new Comp("value 10"));
		sparseSetB.attachComp(new Entity(1000, 0), new Comp("value 1000"));

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(sparseSetA.equals(sparseSetB)).isTrue();
		assertions.assertThat(sparseSetA.hashCode()).isEqualTo(sparseSetB.hashCode());
		assertions.assertAll();
	}

	@DisplayName("""
			equals(obj) and hashCode():
			 sparseSetA has several items,
			 sparseSetB has several items,
			 sparseSetA inner arrays size < sparseSetB inner arrays size,
			 sparseSetA equal to sparseSetB
			 => equals return true,
			    hashCodes must be equal
			""")
	@Test
	public void equalsAndHashCode6() {
		SparseSet sparseSetA = new SparseSet();
		sparseSetA.attachComp(new Entity(10, 0), new Comp("value 10"));
		sparseSetA.attachComp(new Entity(1000, 0), new Comp("value 1000"));
		sparseSetA.attachComp(new Entity(100, 0), new Comp("value 100"));
		sparseSetA.attachComp(new Entity(0, 0), new Comp("value 0"));
		SparseSet sparseSetB = new SparseSet();
		sparseSetB.attachComp(new Entity(0, 0), new Comp("value 0"));
		sparseSetB.attachComp(new Entity(100, 0), new Comp("value 100"));
		sparseSetB.attachComp(new Entity(10, 0), new Comp("value 10"));
		sparseSetB.attachComp(new Entity(1000, 0), new Comp("value 1000"));
		sparseSetB.attachComp(new Entity(2000, 0), new Comp("removed"));
		sparseSetB.detachComp(new Entity(2000, 0));

		SoftAssertions assertions = new SoftAssertions();
		assertions.assertThat(sparseSetA.equals(sparseSetB)).isTrue();
		assertions.assertThat(sparseSetA.hashCode()).isEqualTo(sparseSetB.hashCode());
		assertions.assertAll();
	}

	@DisplayName("""
			equals(obj) and hashCode():
			 sparseSetA has several items,
			 sparseSetB has several items,
			 sparseSetA.size() < sparseSetB.size(),
			 sparseSetA inner arrays size > sparseSetB inner arrays size,
			 sparseSetA not equal sparseSetB
			 => equals return false
			""")
	@Test
	public void equalsAndHashCode7() {
		SparseSet sparseSetA = new SparseSet();
		sparseSetA.attachComp(new Entity(10, 0), new Comp("value 10"));
		sparseSetA.attachComp(new Entity(1000, 0), new Comp("value 1000"));
		sparseSetA.attachComp(new Entity(100, 0), new Comp("value 100"));
		sparseSetA.attachComp(new Entity(0, 0), new Comp("value 0"));
		sparseSetA.attachComp(new Entity(2000, 0), new Comp("removed"));
		sparseSetA.detachComp(new Entity(2000, 0));
		SparseSet sparseSetB = new SparseSet();
		sparseSetB.attachComp(new Entity(0, 0), new Comp("value 0"));
		sparseSetB.attachComp(new Entity(100, 0), new Comp("value 100"));
		sparseSetB.attachComp(new Entity(10, 0), new Comp("value 10"));
		sparseSetB.attachComp(new Entity(1000, 0), new Comp("value 1000"));
		sparseSetB.attachComp(new Entity(800, 0), new Comp("value 800"));

		Assertions.assertThat(sparseSetA.equals(sparseSetB)).isFalse();
	}

	@DisplayName("""
			equals(obj) and hashCode():
			 sparseSetA has several items,
			 sparseSetB has several items,
			 sparseSetA.size() = sparseSetB.size(),
			 sparseSetA inner arrays size > sparseSetB inner arrays size,
			 sparseSetA not equal sparseSetB
			 => equals return false
			""")
	@Test
	public void equalsAndHashCode8() {
		SparseSet sparseSetA = new SparseSet();
		sparseSetA.attachComp(new Entity(10, 0), new Comp("value 10"));
		sparseSetA.attachComp(new Entity(1000, 0), new Comp("value 1000"));
		sparseSetA.attachComp(new Entity(100, 0), new Comp("value 100"));
		sparseSetA.attachComp(new Entity(0, 0), new Comp("value 0"));
		sparseSetA.attachComp(new Entity(2000, 0), new Comp("removed"));
		sparseSetA.detachComp(new Entity(2000, 0));
		SparseSet sparseSetB = new SparseSet();
		sparseSetB.attachComp(new Entity(0, 0), new Comp("value 0"));
		sparseSetB.attachComp(new Entity(100, 0), new Comp("value 100"));
		sparseSetB.attachComp(new Entity(20, 0), new Comp("value 20"));
		sparseSetB.attachComp(new Entity(1000, 0), new Comp("value 1000"));

		Assertions.assertThat(sparseSetA.equals(sparseSetB)).isFalse();
	}
}