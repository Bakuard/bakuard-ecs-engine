package com.bakuard.ecsEngine;

import com.bakuard.collections.ReadableLinearStructure;

/**
 * Снимок состояния {@link EntityManager}. Подробнее см. {@link EntityManager#snapshot()}.
 * @param alive все сущности, для которых вызов {@link EntityManager#isAlive(Entity)} на момент
 *              создания этого снимка возвращал true.
 * @param notAlive все сущности, для которых вызов {@link EntityManager#isAlive(Entity)} на момент
 *                 создания этого снимка возвращал false.
 */
public record EntityManagerSnapshot(ReadableLinearStructure<Entity> alive,
                                    ReadableLinearStructure<Entity> notAlive) {}
