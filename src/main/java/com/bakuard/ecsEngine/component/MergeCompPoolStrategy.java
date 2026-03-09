package com.bakuard.ecsEngine.component;

import com.bakuard.ecsEngine.entity.Entity;

public interface MergeCompPoolStrategy<T> {

	public T merge(Entity entity, T currentComp, T srcComp);

}
