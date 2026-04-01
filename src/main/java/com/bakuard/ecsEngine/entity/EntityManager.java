package com.bakuard.ecsEngine.entity;

import com.bakuard.collections.Bits;
import com.bakuard.collections.ReadableBits;
import com.bakuard.ecsEngine.exception.DeadEntityException;
import com.bakuard.ecsEngine.exception.IllegalEntityStateException;

import java.util.Arrays;
import java.util.Objects;

/**
 * <p>
 *     Отвечает за создание и удаление сущностей ({@link Entity}). Также позволяет проверить, была ли
 *     сущность удалена.
 * </p>
 * <p>
 *     Объекты данного класса не являются потокобезопасными.
 * </p>
 */
public final class EntityManager {

	private static final int MIN_BITS_SIZE = 256;

	private int[] generations;
	private int[] recycledIndexes;
	private int generationsSize;
	private int recycledIndexesSize;
	private final Bits aliveEntitiesMask;

	public EntityManager() {
		generations = new int[10];
		recycledIndexes = new int[10];
		aliveEntitiesMask = new Bits(MIN_BITS_SIZE);
	}

	public EntityManager(EntityManager other) {
		this.generations = other.generations.clone();
		this.recycledIndexes = other.recycledIndexes.clone();
		this.generationsSize = other.generationsSize;
		this.recycledIndexesSize = other.recycledIndexesSize;
		this.aliveEntitiesMask = new Bits(other.aliveEntitiesMask);
	}

	/**
	 * <p>Создает и возвращает новую сущность.</p>
	 * <p>Каждая сущность (иначе говоря, каждая комбинация {@link Entity#index()}/{@link Entity#generation()})
	 * будет создана через данный менеджер только один раз. Эта гарантия может быть нарушена в результате вызова {@link #unsafeSet(Entity, boolean)}.</p>
	 */
	public Entity create() {
		if(recycledIndexesSize == 0) {
			final int index = generationsSize++;
			growGenerationsArrayToIndex(index);
			growAliveEntitiesMaskToIndex(index);
			aliveEntitiesMask.set(index);
			return new Entity(index, 0);
		} else {
			final int index = recycledIndexes[--recycledIndexesSize];
			aliveEntitiesMask.set(index);
			return new Entity(index, generations[index]);
		}
	}

	/**
	 * Удаляет сущность. Если переданная сущность уже ранее удалялась - выбрасывает {@link DeadEntityException}.
	 */
	public void remove(Entity entity) {
		assertIsAlive(entity);

		final int index = entity.index();
		++generations[index];
		growRecycledIndexesArrayToIndex(recycledIndexesSize);
		recycledIndexes[recycledIndexesSize++] = index;
		aliveEntitiesMask.clear(index);
	}

	/**
	 * <p>Добавляет указанную сущность в данный менеджер. Если уже есть сущность с таким же индексом - перезаписывает её
	 * {@link Entity#generation()} и состояние {@link #isAlive(Entity)} на указанные.</p>
	 *
	 * <p><b>Назначение данного метода</b> - облегчить тестирование в тех случаях, когда требуется точно восстановить определенное состояние мира.</p>
	 *
	 * <p><b>ВНИМАНИЕ!</b> Данный метод не является безопасным по следующим причинам:
	 * <ul>
	 *     <li>Сложность выполнения метода становится линейной, если в мире есть мертвая сущность с таким же индексом, или
	 *         индекс передаваемой сущности больше чем {@link #countReservedIndexes()}.</li>
	 *     <li>Метод может вызвать перерасход памяти, если {@link Entity#index()} возрождаемой сущности значительнно больше чем {@link #countReservedIndexes()}</li>
	 *     <li>Метод может нарушить гарантию метода {@link #create()}. Если возродить ранее удаленную сущность ({@link #remove(Entity)}),
	 *     то счетчик поколений ({@link Entity#generation()}) для всех сущностей с данным индексом будет сброшен до значения поколения передаваемой сущности.</li>
	 * </ul>
	 * <b>Используйте данный метод, только если хорошо понимаете, что делаете.</b>
	 * </p>
	 *
	 * @throws IllegalEntityStateException если индекс или поколение сущности отрицательны.
	 * @throws NullPointerException если entity равен null.
	 */
	public void unsafeSet(Entity entity, boolean isAlive) {
		final int entityIndex = entity.index();
		if(entityIndex < 0 || entity.generation() < 0)
			throw new IllegalEntityStateException("Entity index and generation must be non-negative: " + entity);

		final int newRecycledIndexesCount = entityIndex - generationsSize;
		if(newRecycledIndexesCount >= 0) {
			if(newRecycledIndexesCount > 0 || !isAlive) {
				int lastRecycledItemIndex = recycledIndexesSize + newRecycledIndexesCount;
				growRecycledIndexesArrayToIndex(lastRecycledItemIndex);
				for(int i = lastRecycledItemIndex, j = generationsSize; i >= recycledIndexesSize; --i, ++j)
					recycledIndexes[i] = j;
				recycledIndexesSize = lastRecycledItemIndex + 1;
			}

			generationsSize = entityIndex + 1;
			growGenerationsArrayToIndex(entityIndex);
			growAliveEntitiesMaskToIndex(entityIndex);
		} else if(isAlive && !aliveEntitiesMask.get(entityIndex)) {
			for(int i = 0; i < recycledIndexesSize; ++i)
				if(recycledIndexes[i] == entityIndex) {
					recycledIndexes[i] = recycledIndexes[--recycledIndexesSize];
					break;
				}
		}

		generations[entityIndex] = entity.generation();
		if(isAlive) aliveEntitiesMask.set(entityIndex);
		else aliveEntitiesMask.clear(entityIndex);
	}

	/**
	 * <p>Сущность считается живой после её создания через {@link #create()} или {@link #unsafeSet(Entity, boolean)} и до её удаления через {@link #remove(Entity)}.</p>
	 * <p>
	 *     <b>Метод гарантирует</b>, что среди всех сущностей с одним и тем же индексом, живой может считаться не более одной сущности.
	 *     При этом, все сущности с таким же индексом, для которых был вызван {@link #remove(Entity)} и не вызывался в дальнейшем {@link #unsafeSet(Entity, boolean)},
	 *     будут считаться мертвыми.
	 * </p>
	 * <p>Особый случай: если entity равен null - метод вернет false.</p>
	 */
	public boolean isAlive(Entity entity) {
		return entity != null
					   && entity.index() < generationsSize
					   && generations[entity.index()] == entity.generation()
					   && aliveEntitiesMask.get(entity.index());
	}

	/**
	 * <p>Если переданная сущность не является живой (см. {@link #isAlive(Entity)}) - выбрасывает исключение {@link DeadEntityException}.</p>
	 */
	public void assertIsAlive(Entity entity) {
		if(!isAlive(entity)) throw new DeadEntityException("Entity is dead or null: " + entity);
	}

	/**
	 * <p>Возвращает true - если на момент вызова этого метода есть живая сущность с указанным индексом.
	 * Иначе возвращает false.</p>
	 */
	public boolean hasAliveEntityWith(int index) {
		return aliveEntitiesMask.inBound(index) && aliveEntitiesMask.get(index);
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
		if(index < generationsSize) generation = generations[index];
		return new Entity(index, generation);
	}

	/**
	 * <p>Возвращает кол-во всех зарезервированных индексов сущностей внутри данного менеджера сущностей.</p>
	 * <p>
	 *     <b>ВАЖНО!</b>
	 *     <ol>
	 *         <li>Если ни одной сущности не было создано - возвращает 0.</li>
	 *         <li>При удалении сущностей - возвращаемое значение не уменьшается.</li>
	 *         <li>Возвращаемое значение не отображает кол-во живых сущностей.</li>
	 *     </ol>
	 * </p>
	 */
	public int countReservedIndexes() {
		return generationsSize;
	}

	/**
	 * Возвращает битовую маску. Индексы единичных битов соответствуют индексам сущностей, которые были живы
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

		boolean result = generationsSize == other.generationsSize
				&& recycledIndexesSize == other.recycledIndexesSize
				&& aliveEntitiesMask.equals(other.aliveEntitiesMask);
		for(int i = 0; i < generationsSize && result; ++i) result = generations[i] == other.generations[i];
		for(int i = 0; i < recycledIndexesSize && result; ++i) result = recycledIndexes[i] == other.recycledIndexes[i];
		return result;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + generationsSize;
		result = 31 * result + recycledIndexesSize;
		result = 31 * result + aliveEntitiesMask.hashCode();
		for(int i = 0; i < generationsSize; ++i) result = 31 * result + generations[i];
		for(int i = 0; i < recycledIndexesSize; ++i) result = 31 * result + recycledIndexes[i];
		return result;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("EntityManager{ totalEntities: ").append(generationsSize).append(", alive: [");

		for(int i = 0; i < generationsSize; ++i) {
			long generation = generations[i];
			if(aliveEntitiesMask.get(i)) {
				result.append("{index: ").append(i).append(", generation: ").append(generation).append("},");
			}
		}
		result.append("], notAlive: [");

		for(int i = 0; i < generationsSize; ++i) {
			long generation = generations[i];
			if(!aliveEntitiesMask.get(i)) {
				result.append("{index: ").append(i).append(", generation: ").append(generation).append("},");
			}
		}
		result.append("], aliveEntitiesMask: ").append(aliveEntitiesMask).append('}');

		return result.toString();
	}


	private void growGenerationsArrayToIndex(int index) {
		if(index >= generations.length)
			generations = Arrays.copyOf(generations, calculateArrayCapacity(index + 1));
	}

	private void growAliveEntitiesMaskToIndex(int index) {
		aliveEntitiesMask.growToIndex(calculateBitsCapacity(index));
	}

	private void growRecycledIndexesArrayToIndex(int index) {
		if(index >= recycledIndexes.length)
			recycledIndexes = Arrays.copyOf(recycledIndexes, calculateArrayCapacity(index + 1));
	}

	private int calculateArrayCapacity(int size) {
		return size + (size >>> 1);
	}

	private int calculateBitsCapacity(int index) {
		int pageNumber = index / MIN_BITS_SIZE;
		return pageNumber * MIN_BITS_SIZE + MIN_BITS_SIZE;
	}
}
