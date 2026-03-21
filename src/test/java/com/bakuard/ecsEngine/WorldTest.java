package com.bakuard.ecsEngine;

import com.bakuard.collections.Bits;
import com.bakuard.ecsEngine.component.EntityFilter;
import com.bakuard.ecsEngine.entity.Entity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

class WorldTest {

	private static final Comparator<Bits> BITS_COMPARATOR = (maskA, maskB) -> maskA.equalsIgnoreSize(maskB) ? 0 : -1;

	private record A(){}
	private record B(){}
	private record C(){}
	private record D(){}
	private record E(){}
	private record F(){}
	private record UnexistedComp(){}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is empty,
			 filter.allTags is empty,
			 filter.noneTags is empty,
			 filter.allComps is empty,
			 filter.noneComps is empty,
			 filter.withoutComps is false,
			 filter.withoutTags is false
			 => return empty mask
			""")
	@Test
	public void selectEntityIndexes1() {
		World world = new World();
		EntityFilter filter = new EntityFilter();

		Bits selection = world.selectEntityIndexes(filter);

		Assertions.assertThat(selection.isClear()).isTrue();
	}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is not empty,
			 filter.allTags is empty,
			 filter.noneTags is empty,
			 filter.allComps is empty,
			 filter.noneComps is empty,
			 filter.withoutComps is false,
			 filter.withoutTags is false
			 => return mask for all existed entities
			""")
	@Test
	public void selectEntityIndexes2() {
		World world = new World();
		Entity emptyEntity1 = world.create();
		Entity emptyEntity2 = world.create();
		Entity emptyEntity3 = world.create();
		Entity entityWithComps1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithComps2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithComps3 = world.create(new C(), new D(), new E(), new F());
		Entity entityWithTags1 = world.create();
		Entity entityWithTags2 = world.create();
		Entity entityWithTags3 = world.create();
		world.attachTags(entityWithTags1, "A", "B", "C", "D");
		world.attachTags(entityWithTags2, "A", "B", "E", "F");
		world.attachTags(entityWithTags3, "C", "D", "E", "F");
		Entity entityWithCompsAndTags1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithCompsAndTags2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithCompsAndTags3 = world.create(new C(), new D(), new E(), new F());
		world.attachTags(entityWithCompsAndTags1, "A", "B", "C", "D");
		world.attachTags(entityWithCompsAndTags2, "A", "B", "E", "F");
		world.attachTags(entityWithCompsAndTags3, "C", "D", "E", "F");
		EntityFilter filter = new EntityFilter();

		Bits selection = world.selectEntityIndexes(filter);

		Bits actual = Bits.filled(12);
		Assertions.assertThat(selection).usingComparator(BITS_COMPARATOR).isEqualTo(actual);
	}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is not empty,
			 filter.allTags is empty,
			 filter.noneTags is empty,
			 filter.allComps is empty,
			 filter.noneComps is empty,
			 filter.withoutComps is true,
			 filter.withoutTags is false
			 => return mask for entities without comps and empty entities
			""")
	@Test
	public void selectEntityIndexes3() {
		World world = new World();
		Entity emptyEntity1 = world.create();
		Entity emptyEntity2 = world.create();
		Entity emptyEntity3 = world.create();
		Entity entityWithComps1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithComps2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithComps3 = world.create(new C(), new D(), new E(), new F());
		Entity entityWithTags1 = world.create();
		Entity entityWithTags2 = world.create();
		Entity entityWithTags3 = world.create();
		world.attachTags(entityWithTags1, "A", "B", "C", "D");
		world.attachTags(entityWithTags2, "A", "B", "E", "F");
		world.attachTags(entityWithTags3, "C", "D", "E", "F");
		Entity entityWithCompsAndTags1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithCompsAndTags2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithCompsAndTags3 = world.create(new C(), new D(), new E(), new F());
		world.attachTags(entityWithCompsAndTags1, "A", "B", "C", "D");
		world.attachTags(entityWithCompsAndTags2, "A", "B", "E", "F");
		world.attachTags(entityWithCompsAndTags3, "C", "D", "E", "F");
		EntityFilter filter = new EntityFilter().withoutComps(true);

		Bits selection = world.selectEntityIndexes(filter);

		Bits actual = Bits.of(9, 0,1,2, 6,7,8);
		Assertions.assertThat(selection).usingComparator(BITS_COMPARATOR).isEqualTo(actual);
	}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is not empty,
			 filter.allTags is empty,
			 filter.noneTags is empty,
			 filter.allComps is empty,
			 filter.noneComps is empty,
			 filter.withoutComps is false,
			 filter.withoutTags is true
			 => return mask for entities without tags and empty entities
			""")
	@Test
	public void selectEntityIndexes4() {
		World world = new World();
		Entity emptyEntity1 = world.create();
		Entity emptyEntity2 = world.create();
		Entity emptyEntity3 = world.create();
		Entity entityWithComps1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithComps2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithComps3 = world.create(new C(), new D(), new E(), new F());
		Entity entityWithTags1 = world.create();
		Entity entityWithTags2 = world.create();
		Entity entityWithTags3 = world.create();
		world.attachTags(entityWithTags1, "A", "B", "C", "D");
		world.attachTags(entityWithTags2, "A", "B", "E", "F");
		world.attachTags(entityWithTags3, "C", "D", "E", "F");
		Entity entityWithCompsAndTags1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithCompsAndTags2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithCompsAndTags3 = world.create(new C(), new D(), new E(), new F());
		world.attachTags(entityWithCompsAndTags1, "A", "B", "C", "D");
		world.attachTags(entityWithCompsAndTags2, "A", "B", "E", "F");
		world.attachTags(entityWithCompsAndTags3, "C", "D", "E", "F");
		EntityFilter filter = new EntityFilter().withoutTags(true);

		Bits selection = world.selectEntityIndexes(filter);

		Bits actual = Bits.of(6, 0,1,2, 3,4,5);
		Assertions.assertThat(selection).usingComparator(BITS_COMPARATOR).isEqualTo(actual);
	}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is not empty,
			 filter.allTags is empty,
			 filter.noneTags is empty,
			 filter.allComps is empty,
			 filter.noneComps is empty,
			 filter.withoutComps is true,
			 filter.withoutTags is true
			 => return mask for empty entities
			""")
	@Test
	public void selectEntityIndexes5() {
		World world = new World();
		Entity emptyEntity1 = world.create();
		Entity emptyEntity2 = world.create();
		Entity emptyEntity3 = world.create();
		Entity entityWithComps1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithComps2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithComps3 = world.create(new C(), new D(), new E(), new F());
		Entity entityWithTags1 = world.create();
		Entity entityWithTags2 = world.create();
		Entity entityWithTags3 = world.create();
		world.attachTags(entityWithTags1, "A", "B", "C", "D");
		world.attachTags(entityWithTags2, "A", "B", "E", "F");
		world.attachTags(entityWithTags3, "C", "D", "E", "F");
		Entity entityWithCompsAndTags1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithCompsAndTags2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithCompsAndTags3 = world.create(new C(), new D(), new E(), new F());
		world.attachTags(entityWithCompsAndTags1, "A", "B", "C", "D");
		world.attachTags(entityWithCompsAndTags2, "A", "B", "E", "F");
		world.attachTags(entityWithCompsAndTags3, "C", "D", "E", "F");
		EntityFilter filter = new EntityFilter().withoutTags(true).withoutComps(true);

		Bits selection = world.selectEntityIndexes(filter);

		Bits actual = Bits.of(6, 0,1,2);
		Assertions.assertThat(selection).usingComparator(BITS_COMPARATOR).isEqualTo(actual);
	}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is not empty,
			 filter.allTags is not empty,
			 filter.noneTags is empty,
			 filter.allComps is empty,
			 filter.noneComps is empty,
			 filter.withoutComps is false,
			 filter.withoutTags is false
			 => return mask for empty with specified tags
			""")
	@Test
	public void selectEntityIndexes6() {
		World world = new World();
		Entity emptyEntity1 = world.create();
		Entity emptyEntity2 = world.create();
		Entity emptyEntity3 = world.create();
		Entity entityWithComps1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithComps2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithComps3 = world.create(new C(), new D(), new E(), new F());
		Entity entityWithTags1 = world.create();
		Entity entityWithTags2 = world.create();
		Entity entityWithTags3 = world.create();
		world.attachTags(entityWithTags1, "A", "B", "C", "D");
		world.attachTags(entityWithTags2, "A", "B", "E", "F");
		world.attachTags(entityWithTags3, "C", "D", "E", "F");
		Entity entityWithCompsAndTags1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithCompsAndTags2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithCompsAndTags3 = world.create(new C(), new D(), new E(), new F());
		world.attachTags(entityWithCompsAndTags1, "A", "B", "C", "D");
		world.attachTags(entityWithCompsAndTags2, "A", "B", "E", "F");
		world.attachTags(entityWithCompsAndTags3, "C", "D", "E", "F");
		EntityFilter filter = new EntityFilter().allTags("A", "B");

		Bits selection = world.selectEntityIndexes(filter);

		Bits actual = Bits.of(12, 6,7,9,10);
		Assertions.assertThat(selection).usingComparator(BITS_COMPARATOR).isEqualTo(actual);
	}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is not empty,
			 filter.allTags is empty,
			 filter.noneTags is not empty,
			 filter.allComps is empty,
			 filter.noneComps is empty,
			 filter.withoutComps is false,
			 filter.withoutTags is false
			 => return mask for empty without specified tags
			""")
	@Test
	public void selectEntityIndexes7() {
		World world = new World();
		Entity emptyEntity1 = world.create();
		Entity emptyEntity2 = world.create();
		Entity emptyEntity3 = world.create();
		Entity entityWithComps1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithComps2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithComps3 = world.create(new C(), new D(), new E(), new F());
		Entity entityWithTags1 = world.create();
		Entity entityWithTags2 = world.create();
		Entity entityWithTags3 = world.create();
		world.attachTags(entityWithTags1, "A", "B", "C", "D");
		world.attachTags(entityWithTags2, "A", "B", "E", "F");
		world.attachTags(entityWithTags3, "C", "D", "E", "F");
		Entity entityWithCompsAndTags1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithCompsAndTags2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithCompsAndTags3 = world.create(new C(), new D(), new E(), new F());
		world.attachTags(entityWithCompsAndTags1, "A", "B", "C", "D");
		world.attachTags(entityWithCompsAndTags2, "A", "B", "E", "F");
		world.attachTags(entityWithCompsAndTags3, "C", "D", "E", "F");
		EntityFilter filter = new EntityFilter().noneTags("A", "B");

		Bits selection = world.selectEntityIndexes(filter);

		Bits actual = Bits.of(12, 0,1,2, 3,4,5, 8,11);
		Assertions.assertThat(selection).usingComparator(BITS_COMPARATOR).isEqualTo(actual);
	}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is not empty,
			 filter.allTags is not empty,
			 filter.noneTags is not empty,
			 filter.allComps is empty,
			 filter.noneComps is empty,
			 filter.withoutComps is false,
			 filter.withoutTags is false
			 => return mask for empty with specified tags and without other specified tags
			""")
	@Test
	public void selectEntityIndexes8() {
		World world = new World();
		Entity emptyEntity1 = world.create();
		Entity emptyEntity2 = world.create();
		Entity emptyEntity3 = world.create();
		Entity entityWithComps1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithComps2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithComps3 = world.create(new C(), new D(), new E(), new F());
		Entity entityWithTags1 = world.create();
		Entity entityWithTags2 = world.create();
		Entity entityWithTags3 = world.create();
		world.attachTags(entityWithTags1, "A", "B", "C", "D");
		world.attachTags(entityWithTags2, "A", "B", "E", "F");
		world.attachTags(entityWithTags3, "C", "D", "E", "F");
		Entity entityWithCompsAndTags1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithCompsAndTags2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithCompsAndTags3 = world.create(new C(), new D(), new E(), new F());
		world.attachTags(entityWithCompsAndTags1, "A", "B", "C", "D");
		world.attachTags(entityWithCompsAndTags2, "A", "B", "E", "F");
		world.attachTags(entityWithCompsAndTags3, "C", "D", "E", "F");
		EntityFilter filter = new EntityFilter().allTags("A", "B").noneTags("E", "F");

		Bits selection = world.selectEntityIndexes(filter);

		Bits actual = Bits.of(12, 6,9);
		Assertions.assertThat(selection).usingComparator(BITS_COMPARATOR).isEqualTo(actual);
	}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is not empty,
			 filter.allTags is not empty,
			 filter.noneTags is not empty,
			 filter.allComps is empty,
			 filter.noneComps is empty,
			 filter.withoutComps is false,
			 filter.withoutTags is false,
			 there are not entities by this filter
			 => return empty mask
			""")
	@Test
	public void selectEntityIndexes9() {
		World world = new World();
		Entity emptyEntity1 = world.create();
		Entity emptyEntity2 = world.create();
		Entity emptyEntity3 = world.create();
		Entity entityWithComps1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithComps2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithComps3 = world.create(new C(), new D(), new E(), new F());
		Entity entityWithTags1 = world.create();
		Entity entityWithTags2 = world.create();
		Entity entityWithTags3 = world.create();
		world.attachTags(entityWithTags1, "A", "B", "C", "D");
		world.attachTags(entityWithTags2, "A", "B", "E", "F");
		world.attachTags(entityWithTags3, "C", "D", "E", "F");
		Entity entityWithCompsAndTags1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithCompsAndTags2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithCompsAndTags3 = world.create(new C(), new D(), new E(), new F());
		world.attachTags(entityWithCompsAndTags1, "A", "B", "C", "D");
		world.attachTags(entityWithCompsAndTags2, "A", "B", "E", "F");
		world.attachTags(entityWithCompsAndTags3, "C", "D", "E", "F");
		EntityFilter filter = new EntityFilter().allTags("A", "B").noneTags("B", "E");

		Bits selection = world.selectEntityIndexes(filter);

		Bits actual = new Bits();
		Assertions.assertThat(selection).usingComparator(BITS_COMPARATOR).isEqualTo(actual);
	}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is not empty,
			 filter.allTags is empty,
			 filter.noneTags is empty,
			 filter.allComps is not empty,
			 filter.noneComps is empty,
			 filter.withoutComps is false,
			 filter.withoutTags is false
			 => return mask for with specified comps
			""")
	@Test
	public void selectEntityIndexes10() {
		World world = new World();
		Entity emptyEntity1 = world.create();
		Entity emptyEntity2 = world.create();
		Entity emptyEntity3 = world.create();
		Entity entityWithComps1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithComps2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithComps3 = world.create(new C(), new D(), new E(), new F());
		Entity entityWithTags1 = world.create();
		Entity entityWithTags2 = world.create();
		Entity entityWithTags3 = world.create();
		world.attachTags(entityWithTags1, "A", "B", "C", "D");
		world.attachTags(entityWithTags2, "A", "B", "E", "F");
		world.attachTags(entityWithTags3, "C", "D", "E", "F");
		Entity entityWithCompsAndTags1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithCompsAndTags2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithCompsAndTags3 = world.create(new C(), new D(), new E(), new F());
		world.attachTags(entityWithCompsAndTags1, "A", "B", "C", "D");
		world.attachTags(entityWithCompsAndTags2, "A", "B", "E", "F");
		world.attachTags(entityWithCompsAndTags3, "C", "D", "E", "F");
		EntityFilter filter = new EntityFilter().allComps(A.class, B.class);

		Bits selection = world.selectEntityIndexes(filter);

		Bits actual = Bits.of(12, 3,4,9,10);
		Assertions.assertThat(selection).usingComparator(BITS_COMPARATOR).isEqualTo(actual);
	}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is not empty,
			 filter.allTags is empty,
			 filter.noneTags is empty,
			 filter.allComps is empty,
			 filter.noneComps is not empty,
			 filter.withoutComps is false,
			 filter.withoutTags is false
			 => return mask for empty without specified comps
			""")
	@Test
	public void selectEntityIndexes11() {
		World world = new World();
		Entity emptyEntity1 = world.create();
		Entity emptyEntity2 = world.create();
		Entity emptyEntity3 = world.create();
		Entity entityWithComps1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithComps2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithComps3 = world.create(new C(), new D(), new E(), new F());
		Entity entityWithTags1 = world.create();
		Entity entityWithTags2 = world.create();
		Entity entityWithTags3 = world.create();
		world.attachTags(entityWithTags1, "A", "B", "C", "D");
		world.attachTags(entityWithTags2, "A", "B", "E", "F");
		world.attachTags(entityWithTags3, "C", "D", "E", "F");
		Entity entityWithCompsAndTags1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithCompsAndTags2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithCompsAndTags3 = world.create(new C(), new D(), new E(), new F());
		world.attachTags(entityWithCompsAndTags1, "A", "B", "C", "D");
		world.attachTags(entityWithCompsAndTags2, "A", "B", "E", "F");
		world.attachTags(entityWithCompsAndTags3, "C", "D", "E", "F");
		EntityFilter filter = new EntityFilter().noneComps(A.class, B.class);

		Bits selection = world.selectEntityIndexes(filter);

		Bits actual = Bits.of(12, 0,1,2, 5, 6,7,8, 11);
		Assertions.assertThat(selection).usingComparator(BITS_COMPARATOR).isEqualTo(actual);
	}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is not empty,
			 filter.allTags is empty,
			 filter.noneTags is empty,
			 filter.allComps is not empty,
			 filter.noneComps is not empty,
			 filter.withoutComps is false,
			 filter.withoutTags is false
			 => return mask for empty with specified comps and without other specified comps
			""")
	@Test
	public void selectEntityIndexes12() {
		World world = new World();
		Entity emptyEntity1 = world.create();
		Entity emptyEntity2 = world.create();
		Entity emptyEntity3 = world.create();
		Entity entityWithComps1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithComps2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithComps3 = world.create(new C(), new D(), new E(), new F());
		Entity entityWithTags1 = world.create();
		Entity entityWithTags2 = world.create();
		Entity entityWithTags3 = world.create();
		world.attachTags(entityWithTags1, "A", "B", "C", "D");
		world.attachTags(entityWithTags2, "A", "B", "E", "F");
		world.attachTags(entityWithTags3, "C", "D", "E", "F");
		Entity entityWithCompsAndTags1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithCompsAndTags2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithCompsAndTags3 = world.create(new C(), new D(), new E(), new F());
		world.attachTags(entityWithCompsAndTags1, "A", "B", "C", "D");
		world.attachTags(entityWithCompsAndTags2, "A", "B", "E", "F");
		world.attachTags(entityWithCompsAndTags3, "C", "D", "E", "F");
		EntityFilter filter = new EntityFilter().allComps(A.class, B.class).noneComps(E.class, F.class);

		Bits selection = world.selectEntityIndexes(filter);

		Bits actual = Bits.of(12, 3,9);
		Assertions.assertThat(selection).usingComparator(BITS_COMPARATOR).isEqualTo(actual);
	}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is not empty,
			 filter.allTags is empty,
			 filter.noneTags is empty,
			 filter.allComps is not empty,
			 filter.noneComps is not empty,
			 filter.withoutComps is false,
			 filter.withoutTags is false,
			 there are not entities by this filter
			 => return empty mask
			""")
	@Test
	public void selectEntityIndexes13() {
		World world = new World();
		Entity emptyEntity1 = world.create();
		Entity emptyEntity2 = world.create();
		Entity emptyEntity3 = world.create();
		Entity entityWithComps1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithComps2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithComps3 = world.create(new C(), new D(), new E(), new F());
		Entity entityWithTags1 = world.create();
		Entity entityWithTags2 = world.create();
		Entity entityWithTags3 = world.create();
		world.attachTags(entityWithTags1, "A", "B", "C", "D");
		world.attachTags(entityWithTags2, "A", "B", "E", "F");
		world.attachTags(entityWithTags3, "C", "D", "E", "F");
		Entity entityWithCompsAndTags1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithCompsAndTags2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithCompsAndTags3 = world.create(new C(), new D(), new E(), new F());
		world.attachTags(entityWithCompsAndTags1, "A", "B", "C", "D");
		world.attachTags(entityWithCompsAndTags2, "A", "B", "E", "F");
		world.attachTags(entityWithCompsAndTags3, "C", "D", "E", "F");
		EntityFilter filter = new EntityFilter().allComps(A.class, B.class).noneComps(B.class, E.class);

		Bits selection = world.selectEntityIndexes(filter);

		Bits actual = new Bits();
		Assertions.assertThat(selection).usingComparator(BITS_COMPARATOR).isEqualTo(actual);
	}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is not empty,
			 filter.allTags is empty,
			 filter.noneTags is empty,
			 filter.allComps is not empty,
			 filter.noneComps is empty,
			 filter.withoutComps is false,
			 filter.withoutTags is false,
			 there are not entities with this comps
			 => return empty mask
			""")
	@Test
	public void selectEntityIndexes14() {
		World world = new World();
		Entity emptyEntity1 = world.create();
		Entity emptyEntity2 = world.create();
		Entity emptyEntity3 = world.create();
		Entity entityWithComps1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithComps2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithComps3 = world.create(new C(), new D(), new E(), new F());
		Entity entityWithTags1 = world.create();
		Entity entityWithTags2 = world.create();
		Entity entityWithTags3 = world.create();
		world.attachTags(entityWithTags1, "A", "B", "C", "D");
		world.attachTags(entityWithTags2, "A", "B", "E", "F");
		world.attachTags(entityWithTags3, "C", "D", "E", "F");
		Entity entityWithCompsAndTags1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithCompsAndTags2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithCompsAndTags3 = world.create(new C(), new D(), new E(), new F());
		world.attachTags(entityWithCompsAndTags1, "A", "B", "C", "D");
		world.attachTags(entityWithCompsAndTags2, "A", "B", "E", "F");
		world.attachTags(entityWithCompsAndTags3, "C", "D", "E", "F");
		EntityFilter filter = new EntityFilter().allComps(UnexistedComp.class);

		Bits selection = world.selectEntityIndexes(filter);

		Bits actual = new Bits();
		Assertions.assertThat(selection).usingComparator(BITS_COMPARATOR).isEqualTo(actual);
	}

	@DisplayName("""
			selectEntityIndexes(filter):
			 World is not empty,
			 filter.allTags is empty,
			 filter.noneTags is empty,
			 filter.allComps is not empty,
			 filter.noneComps is empty,
			 filter.withoutComps is false,
			 filter.withoutTags is false,
			 there are not entities with this tags
			 => return empty mask
			""")
	@Test
	public void selectEntityIndexes15() {
		World world = new World();
		Entity emptyEntity1 = world.create();
		Entity emptyEntity2 = world.create();
		Entity emptyEntity3 = world.create();
		Entity entityWithComps1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithComps2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithComps3 = world.create(new C(), new D(), new E(), new F());
		Entity entityWithTags1 = world.create();
		Entity entityWithTags2 = world.create();
		Entity entityWithTags3 = world.create();
		world.attachTags(entityWithTags1, "A", "B", "C", "D");
		world.attachTags(entityWithTags2, "A", "B", "E", "F");
		world.attachTags(entityWithTags3, "C", "D", "E", "F");
		Entity entityWithCompsAndTags1 = world.create(new A(), new B(), new C(), new D());
		Entity entityWithCompsAndTags2 = world.create(new A(), new B(), new E(), new F());
		Entity entityWithCompsAndTags3 = world.create(new C(), new D(), new E(), new F());
		world.attachTags(entityWithCompsAndTags1, "A", "B", "C", "D");
		world.attachTags(entityWithCompsAndTags2, "A", "B", "E", "F");
		world.attachTags(entityWithCompsAndTags3, "C", "D", "E", "F");
		EntityFilter filter = new EntityFilter().allTags("UnexistedTag");

		Bits selection = world.selectEntityIndexes(filter);

		Bits actual = new Bits();
		Assertions.assertThat(selection).usingComparator(BITS_COMPARATOR).isEqualTo(actual);
	}
}