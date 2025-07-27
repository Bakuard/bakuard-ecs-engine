package com.bakuard.ecsEngine.entity;

import com.bakuard.collections.Bits;
import com.bakuard.collections.ReadableBits;

import java.util.Arrays;
import java.util.Objects;

/**
 * Отвечает за создание и удаление сущностей ({@link Entity}). Также позволяет проверить, была ли
 * сущность удалена.
 */
public final class EntityManager {

	private static final int MIN_BITS_SIZE = 256;

	private long[] entities;
	private int size;
	private final Bits aliveEntitiesMask;

	public EntityManager() {
		entities = new long[10];
		aliveEntitiesMask = new Bits(calculateBitsCapacity(10) + 1);
	}

	/**
	 * Создает и возвращает новую сущность.
	 * <br/><br/>
	 * Менеджер сущностей переиспользует индексы недавно удаленных сущностей в порядке возрастания
	 * их (индексов) значений.
	 */
	public Entity create() {
		int nextReusableEntityIndex = aliveEntitiesMask.nextClearBit(0);

		if(nextReusableEntityIndex < size) {
			aliveEntitiesMask.set(nextReusableEntityIndex);
			return Entity.fromLong(entities[nextReusableEntityIndex]);
		}

		growToIndex(nextReusableEntityIndex);
		aliveEntitiesMask.set(nextReusableEntityIndex);
		entities[nextReusableEntityIndex] = pack(nextReusableEntityIndex, 0);
		return new Entity(nextReusableEntityIndex, 0);
	}

	/**
	 * Удаляет сущность. Если переданная сущность уже ранее удалялась, то ничего не делает.
	 */
	public void remove(Entity entity) {
		if(isAlive(entity)) {
			int index = entity.index();
			entities[index] = pack(index, entity.generation() + 1);
			aliveEntitiesMask.clear(index);
		}
	}

	/**
	 * Добавляет указанную сущность. Если сущность с таким же индексом уже существует - перезаписывает её. После
	 * выполнения метода сущность будет живой ({@link #isAlive(Entity)}). Если entity равен null - метод ничего не делает.
	 *
	 * <p>
	 * <b>НАЗНАЧЕНИЕ:</b> предназначен для восстановления состояния {@link EntityManager} при загрузке игры.
	 * </p>
	 *
	 * <p>
	 * <b>ВНИМАНИЕ!</b> Данный метод не делает никаких проверок для значений {@link Entity#index()} и {@link Entity#generation()}.
	 * Использование этого метода может привести к излишнему расходу памяти (при очень больших значениях {@link Entity#index()})
	 * и неожиданным ошибкам.
	 * </p>
	 * @param entity добавляемая сущность.
	 */
	public void addOrReplaceUnsafe(Entity entity) {
		if(entity == null) return;

		final int entityIndex = entity.index();
		if(entityIndex >= size) growToIndex(entityIndex);

		aliveEntitiesMask.set(entityIndex);
		entities[entityIndex] = pack(entityIndex, entity.generation());
	}

	/**
	 * Сущность считается живой после её создания через {@link #create()} или {@link #addOrReplaceUnsafe(Entity)}
	 * и до её удаления через {@link #remove(Entity)}.
	 * <p>
	 * Особый случай: если entity равен null - метод вернет false.
	 * </p>
	 */
	public boolean isAlive(Entity entity) {
		return entity != null
				&& entity.index() < size
				&& extractGeneration(entities[entity.index()]) == entity.generation();
	}

	/**
	 * Возвращает сущность по её индексу. Возвращаемая сущность может быть живой ({@link #isAlive(Entity)}),
	 * либо мертвой, в зависимости от того, жива ли сущность под указанным индексом на момент вызова
	 * данного метода. <br/><br/>
	 * Особый случай: если сущность с указанным индексом никогда ранее не создавалась через данный
	 * менеджер сущностей - метод вернет мертвую сущность с {@link Entity#generation()} равным 0.
	 */
	public Entity getEntityByIndex(int index) {
		return index < size ? Entity.fromLong(entities[index]) : new Entity(index, 0);
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
		return (long)generation << 32 | (long)index;
	}

	private int extractGeneration(long entity) {
		return (int)(entity >>> 32);
	}


	private void growToIndex(int index) {
		size = index + 1;
		if(size > entities.length) {
			entities = Arrays.copyOf(entities, calculateCapacity(size));
		}

		aliveEntitiesMask.growToIndex(calculateBitsCapacity(index));
	}

	private int calculateCapacity(int size) {
		return size + (size >>> 1);
	}

	private int calculateBitsCapacity(int index) {
		int pageNumber = index / MIN_BITS_SIZE;
		return pageNumber * MIN_BITS_SIZE + MIN_BITS_SIZE;
	}
}
