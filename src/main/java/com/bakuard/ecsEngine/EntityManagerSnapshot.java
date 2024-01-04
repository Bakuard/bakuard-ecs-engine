package com.bakuard.ecsEngine;

import com.bakuard.collections.ReadableLinearStructure;

public record EntityManagerSnapshot(ReadableLinearStructure<Entity> alive,
                                    ReadableLinearStructure<Entity> notAlive) {}
