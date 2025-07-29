package com.bakuard.ecsEngine.entity;

import com.bakuard.collections.Bits;
import com.bakuard.collections.ReadableBits;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * Отвечает за создание и удаление сущностей ({@link Entity}). Также позволяет проверить, была ли
 * сущность удалена.
 */
public final class EntityManager {

	private static final int MIN_BITS_SIZE = 256;

	private long[] entities;
	private int size;
	private int head = -1;
	private int tail = -1;
	private final Bits aliveEntitiesMask;

	public EntityManager() {
		entities = new long[10];
		aliveEntitiesMask = new Bits(MIN_BITS_SIZE);
	}

	public EntityManager(InitialEntityIterator entityIterator) {
		this();

		while(entityIterator.next()) {
			Entity entity = entityIterator.getEntity();
			final int index = entity.index();
			growToIndex(index);
			if(entityIterator.isEntityAlive()) {
				entities[index] = Entity.toLong(entity);
				aliveEntitiesMask.set(index);
			} else {
				pushEntityToImplicitList(entity);
			}
		}

		for(int i = 0; i < size; i++)
			if(entities[i] == 0L)
				entities[i] = pack(i, 0);
	}

	/**
	 * Создает и возвращает новую сущность.
	 * <p>
	 * Менеджер сущностей переиспользует индексы недавно удаленных сущностей в порядке возрастания
	 * их (индексов) значений.
	 * </p>
	 */
	public Entity create() {
		if(head != -1) {
			final int index = head;
			head = extractIndex(entities[index]);
			if(head == -1)
				tail = -1;
			entities[index] = pack(index, extractGeneration(entities[index]));
			aliveEntitiesMask.set(index);
			return Entity.fromLong(entities[index]);
		} else {
			final int index = size;
			growToIndex(index);
			entities[index] = pack(index, 0);
			aliveEntitiesMask.set(index);
			return new Entity(index, 0);
		}
	}

	/**
	 * Удаляет сущность. Если переданная сущность уже ранее удалялась, то ничего не делает.
	 */
	public void remove(Entity entity) {
		if(isAlive(entity)) {
			pushEntityToImplicitList(entity);
			aliveEntitiesMask.clear(entity.index());
		}
	}

	/**
	 * Сущность считается живой после её создания через {@link #create()} и до её удаления через {@link #remove(Entity)}.
	 * <p>Особый случай: если entity равен null - метод вернет false.</p>
	 */
	public boolean isAlive(Entity entity) {
		return entity != null
					   && entity.index() < size
					   && extractGeneration(entities[entity.index()]) == entity.generation()
					   && aliveEntitiesMask.get(entity.index());
	}

	/**
	 * Возвращает сущность по её индексу. Возвращаемая сущность может быть как живой ({@link #isAlive(Entity)}), так
	 * и мертвой.
	 * <p>
	 * Особый случай: если сущность с указанным индексом никогда ранее не создавалась через данный
	 * менеджер сущностей - метод вернет мертвую сущность с {@link Entity#generation()} равным 0.
	 * </p>
	 */
	public Entity getEntityByIndex(int index) {
		int generation = 0;
		if(index < size) generation = extractGeneration(entities[index]);
		return new Entity(index, generation);
	}

	/**
	 * Возвращает битовую маску. Индексы единичных битов соответствуют сущностям, которые были живы
	 * на момент вызова этого метода.
	 */
	public ReadableBits getAliveEntitiesMask() {
		return aliveEntitiesMask;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		EntityManager other = (EntityManager) o;

		boolean result = size == other.size && aliveEntitiesMask.equals(other.aliveEntitiesMask);
		for(int i = 0; i < size && result; ++i) result = entities[i] == other.entities[i];
		return result;
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(size, aliveEntitiesMask);
		result = 31 * result + Arrays.hashCode(entities);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("EntityManager{ totalEntities: ")
				.append(size)
				.append(", alive: [");

		for(int i = 0; i < size; ++i) {
			long packedEntity = entities[i];
			if(aliveEntitiesMask.get(i)) {
				result.append("{index: ")
						.append(i)
						.append(", generation: ")
						.append(extractGeneration(packedEntity))
						.append("},");
			}
		}
		result.append("], notAlive: [");

		for(int i = 0; i < size; ++i) {
			long packedEntity = entities[i];
			if(!aliveEntitiesMask.get(i)) {
				result.append("{index: ")
						.append(i)
						.append(", generation: ")
						.append(extractGeneration(packedEntity))
						.append("},");
			}
		}
		result.append("], aliveEntitiesMask: ")
				.append(aliveEntitiesMask)
				.append('}');

		return result.toString();
	}


	private long pack(int index, int generation) {
		return (long)generation << 32 | ((long)index & 0xFFFFFFFFL);
	}

	private int extractGeneration(long entity) {
		return (int)(entity >>> 32);
	}

	private int extractIndex(long entity) {
		return (int)entity;
	}

	private void pushEntityToImplicitList(Entity entity) {
		final int index = entity.index();
		if(tail != -1)
			entities[tail] = pack(index, extractGeneration(entities[tail]));
		else
			head = index;
		tail = index;
		entities[index] = pack(-1, entity.generation() + 1);
	}


	private void growToIndex(int index) {
		size = index + 1;
		if(size > entities.length)
			entities = Arrays.copyOf(entities, calculateArrayCapacity(size));
		aliveEntitiesMask.growToIndex(calculateBitsCapacity(index));
	}

	private int calculateArrayCapacity(int size) {
		return size + (size >>> 1);
	}

	private int calculateBitsCapacity(int index) {
		int pageNumber = index / MIN_BITS_SIZE;
		return pageNumber * MIN_BITS_SIZE + MIN_BITS_SIZE;
	}
}
