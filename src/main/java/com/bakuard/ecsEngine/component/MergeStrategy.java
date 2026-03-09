package com.bakuard.ecsEngine.component;

@FunctionalInterface
public interface MergeStrategy<K, T> {

	public T merge(K key, T originValue, T newValue);

}
