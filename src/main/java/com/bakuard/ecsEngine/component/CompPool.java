package com.bakuard.ecsEngine.component;

import com.bakuard.collections.ReadableBits;
import com.bakuard.ecsEngine.entity.Entity;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface CompPool {

	public void attachComp(Entity entity, Object component);

	public void detachComp(Entity entity);

	public <T> T getComp(Entity entity);

	public boolean hasComp(Entity entity);

	public int size();

	public <T> void forEach(BiConsumer<Entity, T> consumer);

	public <T> EntryIterator<T> iterator();

	public ReadableBits getEntityIndexesMask();

	public <T> void merge(CompPool src, MergeStrategy<Entity, T> strategy);

	public <T> CompPool copy(BiFunction<Entity, T, T> mapper);


	public static interface EntryIterator<E> {
		public boolean next();
		public Entity recentEntity();
		public E recentComp();
	}
}
