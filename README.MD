# Bakuard ECS Engine

---

## What is it?
**Bakuard ECS Engine** is a lightweight, server-side Java game development framework that follows the pure Entity Component System (ECS) architecture.

---

## Features
1. All components of the same type are stored in dedicated pools.
2. The default pool implementation is a `SparseSet`, but you can provide your own implementation and use any type-specific methods it offers.
3. Component lookup is implemented using bit vectors: each component pool maintains a bit vector used in bitwise expressions to find entities that possess specific combinations of component types.
4. Apply structural changes (adding/removing components/tags or entities) on the fly while iterating. No deferred buffers, no synchronization delays — changes take effect immediately.
5. Unlike in many other Java ECS frameworks, it doesn't require you to extend a class or interface for your components. Any Plain Old Java Object (POJO) can be a component.
6. Entities are represented as simple, immutable IDs composed of an index and a generation value. Unlike in many other Java ECS frameworks, entities are not game-object containers.
7. Optionally, you can assign a unique string name to any entity for easier debugging and lookup.
8. Built-in event manager for cross-thread communication.
9. A system manager for grouping and storing systems, which allows modifying the execution order of systems on the fly. 
10. A ready-to-use default game loop implementation.

---

## Getting started

### System Requirements:
- Java: Version 25 or higher
- Maven: Version 3.6 or higher

### Maven:
```
<dependency>
    <groupId>com.bakuard</groupId>
    <artifactId>ecsEngine</artifactId>
    <version>0.8.0</version>
</dependency>
```

## License
This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.