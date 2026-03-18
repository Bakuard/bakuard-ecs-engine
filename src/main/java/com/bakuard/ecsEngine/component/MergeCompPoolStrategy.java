package com.bakuard.ecsEngine.component;

import com.bakuard.ecsEngine.entity.Entity;

@FunctionalInterface
public interface MergeCompPoolStrategy<T> {

	public T merge(Entity entity, T originComp, T newComp);

}
