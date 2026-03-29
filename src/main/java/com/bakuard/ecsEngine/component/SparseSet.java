package com.bakuard.ecsEngine.component;

import com.bakuard.collections.Bits;
import com.bakuard.collections.ReadableBits;
import com.bakuard.ecsEngine.entity.Entity;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class SparseSet implements CompPool {

	private static final int INIT_CAPACITY = 10;


	private int[] entityIndexToComp;
	private Object[] comps;
	private Entity[] entities;
	private int size;
	private final Bits entityIndexes;

	private int actualModCount;

	public SparseSet() {
		entityIndexToComp = new int[INIT_CAPACITY];
		comps = new Object[INIT_CAPACITY];
		entities = new Entity[INIT_CAPACITY];
		entityIndexes = new Bits(128);

		Arrays.fill(entityIndexToComp, -1);
	}

	private SparseSet(SparseSet other) {
		this.entityIndexToComp = other.entityIndexToComp.clone();
		this.comps = other.comps.clone();
		this.entities = other.entities.clone();
		this.size = other.size;
		this.entityIndexes = new Bits(other.entityIndexes);
	}

	@Override
	public void attachComp(Entity entity, Object component) {
		++actualModCount;

		int compIndex = getCompIndex(entity);
		if(compIndex == -1) {
			compIndex = size;
			growSparseArray(entity.index() + 1);
			growDensityArrays(++size);
			entityIndexToComp[entity.index()] = compIndex;
			entities[compIndex] = entity;
			entityIndexes.growToIndex(entity.index()).set(entity.index());
		}

		comps[compIndex] = component;
	}

	@Override
	public void detachComp(Entity entity) {
		++actualModCount;

		final int compIndex = getCompIndex(entity);
		if(compIndex > -1) detachCompIgnoringPresence(entity, compIndex);
	}

	public void swap(Entity first, Entity second) {
		++actualModCount;

		if(first.index() < entityIndexToComp.length && second.index() < entityIndexToComp.length) {
			final int firstIndex = entityIndexToComp[first.index()];
			final int secondIndex = entityIndexToComp[second.index()];
			if(firstIndex > -1 && secondIndex > -1) {
				swapEntityIndexToComp(first.index(), second.index());
				swapEntities(firstIndex, secondIndex);
			}
		}
	}

	public Entity getEntityFromDensityArray(int index) {
		assertInBound(index);
		return entities[index];
	}

	public <T> T getCompFromDensityArray(int index) {
		assertInBound(index);
		return (T)comps[index];
	}

	@Override
	public <T> T getComp(Entity entity) {
		final int compIndex = getCompIndex(entity);
		return compIndex > -1 ? (T)comps[compIndex] : null;
	}

	@Override
	public boolean hasComp(Entity entity) {
		return entity.index() < entityIndexToComp.length && entityIndexToComp[entity.index()] != -1;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size > 0;
	}

	@Override
	public <T> void forEach(BiConsumer<Entity, T> consumer) {
		for(int i = size - 1; i >= 0; --i) {
			consumer.accept(entities[i], (T) comps[i]);
		}
	}

	@Override
	public <T> EntryIterator<T> iterator() {
		return new EntryIteratorImpl<T>(actualModCount, size);
	}

	@Override
	public ReadableBits getEntityIndexesMask() {
		return entityIndexes;
	}

	@Override
	public <T> void merge(CompPool src, MergeCompPoolStrategy<T> mergeStrategy) {
		++actualModCount;

		for(int i = size - 1; i >= 0; --i) {
			Entity entity = entities[i];
			T originComp = (T) comps[i];
			T newComp = src.getComp(entity);
			T result = mergeStrategy.merge(entity, originComp, newComp);
			if(result == null) detachCompIgnoringPresence(entity, i);
			else comps[i] = result;
		}

		final SparseSet origin = this;
		src.<T>forEach((entity, comp) -> {
			if(origin.hasComp(entity)) return;

			T result = mergeStrategy.merge(entity, null, comp);
			if(result != null) origin.attachComp(entity, result);
		});
	}

	@Override
	public <T> CompPool copy(BiFunction<Entity, T, T> mapper) {
		SparseSet result = new SparseSet(this);
		for(int i = 0; i < size; ++i) {
			result.comps[i] = mapper.apply(entities[i], (T) comps[i]);
		}
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && getClass() == o.getClass()) {
			SparseSet other = (SparseSet)o;

			boolean result = size == other.size;
			for(int i = 0; i < size && result; ++i) {
				int indexInOther = other.getCompIndex(entities[i]);
				result = indexInOther != -1 && Objects.equals(comps[i], other.comps[indexInOther]) && entities[i].equals(other.entities[indexInOther]);
			}

			return result;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int result = this.size * 31;
		for(int i = 0; i < size; ++i) {
			result += entities[i].hashCode() ^ Objects.hashCode(comps[i]);
		}
		return result;
	}

	@Override
	public String toString() {
		return "SparseSet{" +
				"size: " + size +
				", comps: " + toString(comps, size) +
				", entities: " + toString(entities, size) +
				'}';
	}


	private int calculateCapacity(int size) {
		return size + (size >>> 1);
	}

	private void growSparseArray(int newSize) {
		if(newSize > entityIndexToComp.length) {
			int oldSize = entityIndexToComp.length;
			entityIndexToComp = Arrays.copyOf(entityIndexToComp, calculateCapacity(newSize));
			Arrays.fill(entityIndexToComp, oldSize, entityIndexToComp.length, -1);
		}
	}

	private void growDensityArrays(int newSize) {
		if(newSize > comps.length) {
			comps = Arrays.copyOf(comps, calculateCapacity(newSize));
			entities = Arrays.copyOf(entities, calculateCapacity(newSize));
		}
	}

	private void assertInBound(int index) {
		if(index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Expected: index >= 0 and index < size. Actual: index = %d, size = %d".formatted(index, size));
		}
	}

	private void swapEntityIndexToComp(int firstIndex, int secondIndex) {
		int firstValue = entityIndexToComp[firstIndex];
		entityIndexToComp[firstIndex] = entityIndexToComp[secondIndex];
		entityIndexToComp[secondIndex] = firstValue;
	}

	private void swapEntities(int firstIndex, int secondIndex) {
		Entity firstComp = entities[firstIndex];
		entities[firstIndex] = entities[secondIndex];
		entities[secondIndex] = firstComp;
	}

	private void detachCompIgnoringPresence(Entity entity, int compIndex) {
		final int lastCompIndex = --size;

		entityIndexToComp[entity.index()] = -1;
		entityIndexes.clear(entity.index());

		if(compIndex < lastCompIndex) {
			Entity lastEntity = entities[lastCompIndex];

			entities[compIndex] = lastEntity;
			comps[compIndex] = comps[lastCompIndex];
			entityIndexToComp[lastEntity.index()] = compIndex;
		}

		comps[lastCompIndex] = null;
		entities[lastCompIndex] = null;
	}

	private int getCompIndex(Entity entity) {
		return entity.index() < entityIndexToComp.length ? entityIndexToComp[entity.index()] : -1;
	}

	private String toString(Object[] array, int arraySize) {
		StringBuilder sb = new StringBuilder("[");
		if(arraySize > 0) {
			sb.append(array[0]);
			for(int i = 1; i < arraySize; ++i) sb.append(',').append(array[i]);
		}
		sb.append(']');
		return sb.toString();
	}


	private class EntryIteratorImpl<E> implements EntryIterator<E> {
		private final int expectedModCount;
		private final int itemsNumber;
		private int currentIndex = -1;
		private Entity recentEntity;
		private E recentComp;

		public EntryIteratorImpl(int expectedModCount, int itemsNumber) {
			this.expectedModCount = expectedModCount;
			this.itemsNumber = itemsNumber;
		}

		@Override
		public boolean next() {
			assertCompPoolWasNotBeenChanged();
			boolean hasNext = ++currentIndex < itemsNumber;
			if(hasNext) {
				recentEntity = entities[currentIndex];
				recentComp = (E)comps[currentIndex];
			} else {
				recentEntity = null;
				recentComp = null;
			}
			return hasNext;
		}

		@Override
		public Entity recentEntity() {
			return recentEntity;
		}

		@Override
		public E recentComp() {
			return recentComp;
		}

		private void assertCompPoolWasNotBeenChanged() {
			if(actualModCount != expectedModCount)
				throw new ConcurrentModificationException();
		}
	}
}
