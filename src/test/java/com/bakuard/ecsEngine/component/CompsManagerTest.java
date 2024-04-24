package com.bakuard.ecsEngine.component;

import com.bakuard.collections.Bits;
import com.bakuard.collections.DynamicArray;
import com.bakuard.ecsEngine.entity.Entity;
import com.bakuard.ecsEngine.entity.EntityManager;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CompsManagerTest {

    public record A() {}
    public record B() {}
    public record C() {}
    public record D() {}
    public record E() {}
    public record F() {}

    @DisplayName("""
            detachAllComps(entity):
             entity is alive
             => hasComp(entity, compType) return false for all entity comps
            """)
    @Test
    public void detachAllComps1() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity entity = entityManager.create();
        compsManager.attachComps(entity, new A(), new B(), new C(), new D(), new E(), new F());

        compsManager.detachAllComps(entity);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(compsManager.hasComp(entity, A.class)).isFalse();
        assertions.assertThat(compsManager.hasComp(entity, B.class)).isFalse();
        assertions.assertThat(compsManager.hasComp(entity, C.class)).isFalse();
        assertions.assertThat(compsManager.hasComp(entity, D.class)).isFalse();
        assertions.assertThat(compsManager.hasComp(entity, E.class)).isFalse();
        assertions.assertThat(compsManager.hasComp(entity, F.class)).isFalse();
        assertions.assertAll();
    }

    @DisplayName("""
            detachAllComps(entity):
             entity is alive,
             deadEntity.index() == aliveEntity.index()
             => doesn't change any entity
            """)
    @Test
    public void detachAllComps2() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity deadEntity = entityManager.create();
        entityManager.remove(deadEntity);
        Entity aliveEntity = entityManager.create();
        compsManager.attachComps(aliveEntity, new A(), new B(), new C(), new D(), new E(), new F());

        compsManager.detachAllComps(deadEntity);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(compsManager.hasComp(aliveEntity, A.class)).isTrue();
        assertions.assertThat(compsManager.hasComp(aliveEntity, B.class)).isTrue();
        assertions.assertThat(compsManager.hasComp(aliveEntity, C.class)).isTrue();
        assertions.assertThat(compsManager.hasComp(aliveEntity, D.class)).isTrue();
        assertions.assertThat(compsManager.hasComp(aliveEntity, E.class)).isTrue();
        assertions.assertThat(compsManager.hasComp(aliveEntity, F.class)).isTrue();
        assertions.assertThat(deadEntity.index()).isEqualTo(aliveEntity.index());
        assertions.assertAll();
    }

    @DisplayName("""
            replaceAllComps(entity, comps):
             entity is alive,
             entity has some comps
             => hasComp(entity, compType) return false for old comps and true for new comps
            """)
    @Test
    public void replaceAllComps1() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity entity = entityManager.create();
        compsManager.attachComps(entity, new A(), new B(), new C());

        compsManager.replaceAllComps(entity, new C(), new D(), new E());

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(compsManager.hasComp(entity, A.class)).isFalse();
        assertions.assertThat(compsManager.hasComp(entity, B.class)).isFalse();
        assertions.assertThat(compsManager.hasComp(entity, C.class)).isTrue();
        assertions.assertThat(compsManager.hasComp(entity, D.class)).isTrue();
        assertions.assertThat(compsManager.hasComp(entity, E.class)).isTrue();
        assertions.assertAll();
    }

    @DisplayName("""
            replaceAllComps(entity, comps):
             entity is alive,
             entity hasn't any comps
             => hasComp(entity, compType) return true for new comps
            """)
    @Test
    public void replaceAllComps2() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity entity = entityManager.create();

        compsManager.replaceAllComps(entity, new C(), new D(), new E());

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(compsManager.hasComp(entity, C.class)).isTrue();
        assertions.assertThat(compsManager.hasComp(entity, D.class)).isTrue();
        assertions.assertThat(compsManager.hasComp(entity, E.class)).isTrue();
        assertions.assertAll();
    }

    @DisplayName("""
            haveEqualComps(firstEntity, secondEntity):
             firstEntity is not alive,
             secondEntity is not alive,
             firstEntity and secondEntity had different comps
             => true
            """)
    @Test
    public void haveEqualComps1() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity firstEntity = entityManager.create();
        compsManager.attachComps(firstEntity, new A(), new B(), new C(), new D());
        Entity secondEntity = entityManager.create();
        compsManager.attachComps(secondEntity, new C(), new D(), new E(), new F());

        entityManager.remove(firstEntity);
        entityManager.remove(secondEntity);
        boolean actual = compsManager.haveEqualComps(firstEntity, secondEntity);

        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("""
            haveEqualComps(firstEntity, secondEntity):
             firstEntity is alive,
             secondEntity is not alive,
             firstEntity and secondEntity had same comps
             => false
            """)
    @Test
    public void haveEqualComps2() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity firstEntity = entityManager.create();
        compsManager.attachComps(firstEntity, new A(), new B(), new C(), new D());
        Entity secondEntity = entityManager.create();
        compsManager.attachComps(secondEntity, new A(), new B(), new C(), new D());

        entityManager.remove(secondEntity);
        boolean actual = compsManager.haveEqualComps(firstEntity, secondEntity);

        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("""
            haveEqualComps(firstEntity, secondEntity):
             firstEntity is alive,
             secondEntity is alive,
             firstEntity and secondEntity has different comps
             => false
            """)
    @Test
    public void haveEqualComps3() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity firstEntity = entityManager.create();
        compsManager.attachComps(firstEntity, new A(), new B(), new C(), new D());
        Entity secondEntity = entityManager.create();
        compsManager.attachComps(secondEntity, new C(), new D(), new E(), new F());

        boolean actual = compsManager.haveEqualComps(firstEntity, secondEntity);

        Assertions.assertThat(actual).isFalse();
    }

    @DisplayName("""
            haveEqualComps(firstEntity, secondEntity):
             firstEntity is alive,
             secondEntity is alive,
             firstEntity and secondEntity has the same comps
             => true
            """)
    @Test
    public void haveEqualComps4() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity firstEntity = entityManager.create();
        compsManager.attachComps(firstEntity, new A(), new B(), new C());
        Entity secondEntity = entityManager.create();
        compsManager.attachComps(secondEntity, new A(), new B(), new C());

        boolean actual = compsManager.haveEqualComps(firstEntity, secondEntity);

        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("""
            haveEqualComps(firstEntity, secondEntity):
             firstEntity is alive,
             secondEntity is alive,
             firstEntity and secondEntity hasn't any comps
             => true
            """)
    @Test
    public void haveEqualComps5() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity firstEntity = entityManager.create();
        Entity secondEntity = entityManager.create();

        boolean actual = compsManager.haveEqualComps(firstEntity, secondEntity);

        Assertions.assertThat(actual).isTrue();
    }

    @DisplayName("""
            excludeEntityIndexesWithout(entityIndexes, compTypes):
             compsManager contains all comps in compTypes,
             there are entities with all comps in compTypes
             => remove correct entity indexes from entityIndexes
            """)
    @Test
    public void excludeEntityIndexesWithout1() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        Entity entityC = entityManager.create();
        Entity entityD = entityManager.create();
        compsManager.attachComps(entityA, new A(), new B());
        compsManager.attachComps(entityB, new A(), new C());
        compsManager.attachComps(entityC, new B(), new C());

        Bits actual = Bits.filled(100);
        compsManager.excludeEntityIndexesWithout(actual, DynamicArray.of(B.class));

        Assertions.assertThat(actual).isEqualTo(Bits.of(100, 0, 2));
    }

    @DisplayName("""
            excludeEntityIndexesWithout(entityIndexes, compTypes):
             compsManager contains all comps in compTypes,
             there are not entities with all comps in compTypes
             => clear entityIndexes
            """)
    @Test
    public void excludeEntityIndexesWithout2() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        Entity entityC = entityManager.create();
        Entity entityD = entityManager.create();
        compsManager.attachComps(entityA, new A(), new B());
        compsManager.attachComps(entityB, new A(), new C());
        compsManager.attachComps(entityC, new B(), new C());
        compsManager.attachComps(entityD, new D());

        compsManager.detachComp(entityD, D.class);
        Bits actual = Bits.filled(100);
        compsManager.excludeEntityIndexesWithout(actual, DynamicArray.of(D.class));

        Assertions.assertThat(actual).isEqualTo(new Bits(100));
    }

    @DisplayName("""
            excludeEntityIndexesWithout(entityIndexes, compTypes):
             compsManager doesn't contain all comps in compTypes,
             there are not entities with all comps in compTypes
             => clear entityIndexes
            """)
    @Test
    public void excludeEntityIndexesWithout3() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        Entity entityC = entityManager.create();
        Entity entityD = entityManager.create();
        compsManager.attachComps(entityA, new A(), new B());
        compsManager.attachComps(entityB, new A(), new C());
        compsManager.attachComps(entityC, new B(), new C());

        Bits actual = Bits.filled(100);
        compsManager.excludeEntityIndexesWithout(actual, DynamicArray.of(F.class));

        Assertions.assertThat(actual).isEqualTo(new Bits(100));
    }

    @DisplayName("""
            excludeEntityIndexesWith(entityIndexes, compTypes):
             compsManager contains all comps in compTypes,
             there are entities without all comps in compTypes
             => remove correct entity index from entityIndexes
            """)
    @Test
    public void excludeEntityIndexesWith1() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        Entity entityC = entityManager.create();
        Entity entityD = entityManager.create();
        compsManager.attachComps(entityA, new A(), new B());
        compsManager.attachComps(entityB, new A(), new C());
        compsManager.attachComps(entityC, new B(), new C());

        Bits actual = Bits.filled(4);
        compsManager.excludeEntityIndexesWith(actual, DynamicArray.of(B.class));

        Assertions.assertThat(actual).isEqualTo(Bits.of(4, 1,3));
    }

    @DisplayName("""
            excludeEntityIndexesWith(entityIndexes, compTypes):
             compsManager contains all comps in compTypes,
             there are not entities without all comps in compTypes
             => clear entityIndexes
            """)
    @Test
    public void excludeEntityIndexesWith2() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        Entity entityC = entityManager.create();
        compsManager.attachComps(entityA, new A(), new B());
        compsManager.attachComps(entityB, new A(), new C());
        compsManager.attachComps(entityC, new B(), new C());

        Bits actual = Bits.filled(3);
        compsManager.excludeEntityIndexesWith(actual, DynamicArray.of(A.class, C.class));

        Assertions.assertThat(actual).isEqualTo(new Bits(3));
    }

    @DisplayName("""
            excludeEntityIndexesWith(entityIndexes, compTypes):
             compsManager doesn't contain all comps in compTypes
             => doesn't change entityIndexes
            """)
    @Test
    public void excludeEntityIndexesWith3() {
        EntityManager entityManager = new EntityManager();
        CompsManager compsManager = new CompsManager(entityManager);
        Entity entityA = entityManager.create();
        Entity entityB = entityManager.create();
        Entity entityC = entityManager.create();
        Entity entityD = entityManager.create();
        compsManager.attachComps(entityA, new A(), new B());
        compsManager.attachComps(entityB, new A(), new C());
        compsManager.attachComps(entityC, new B(), new C());

        Bits actual = Bits.filled(100);
        compsManager.excludeEntityIndexesWith(actual, DynamicArray.of(F.class));

        Assertions.assertThat(actual).isEqualTo(Bits.filled(100));
    }
}