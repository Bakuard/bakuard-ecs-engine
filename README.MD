# Bakuard ECS Engine

---

## What is it?
*Bakuard ECS Engine* is a lightweight game development framework with the following features:
1. It follows the Entity Component System (ECS) architectural pattern.
2. All components of the same type are stored in dedicated pools.
3. The default pool implementation is a `SparseSet`, but you can provide your own implementation and use any type-specific methods it offers.
4. Component lookup is implemented using bit vectors: each component pool maintains a bit vector used in bitwise expressions to find entities that possess specific combinations of component types.
5. Entities are represented as simple, immutable IDs composed of an index and a generation value. Unlike in many other Java ECS frameworks, entities are not game-object containers.
6. An event manager for cross-thread communication.
7. A system manager for grouping and storing systems, which allows modifying the execution order of systems on the fly. 
8. A default game loop implementation.

---

## Getting started
Minimal Java version: 25

### Maven:
```
<dependency>
    <groupId>com.bakuard</groupId>
    <artifactId>ecsEngine</artifactId>
    <version>0.7.0</version>
</dependency>
```

## License
This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.