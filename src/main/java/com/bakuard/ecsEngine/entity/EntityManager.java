package com.bakuard.ecsEngine.entity;

import com.bakuard.collections.Bits;
import com.bakuard.collections.ReadableBits;

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

	private long[] entities;
	private int size;
	private int head = -1;
	private int tail = -1;
	private final Bits aliveEntitiesMask;

	public EntityManager() {
		entities = new long[10];
		aliveEntitiesMask = new Bits(MIN_BITS_SIZE);
	}

	/**
	 * <p>Создает и возвращает новую сущность.</p>
	 * <p>Каждая сущность (иначе говоря, каждая комбинация {@link Entity#index()}/{@link Entity#generation()})
	 * будет создана через данный менеджер только один раз. Эта гарантия может быть нарушена в результате вызова {@link #unsafeRevive(Entity)}.</p>
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
	 * <p>Если нет живой ({@link #isAlive(Entity)}) сущности с таким же индексом - данная сущность становится живой, включая её {@link Entity#generation()}.
	 * Если уже есть живая сущность с таким же индексом - выбрасывает исключение.</p>
	 *
	 * <p><b>Назначение данного метода</b> - облегчить тестирование в тех случаях, когда требуется точно восстановить определенное состояние мира.</p>
	 *
	 * <p><b>ВНИМАНИЕ!</b> Данный метод не является безопасным по следующим причинам:
	 * <ul>
	 *     <li>Метод рабоатет медленно. Его сложность выполнения метода - O(n), где n - кол-во мертвых сущностей, зарезервированных для переиспользования.</li>
	 *     <li>Метод может вызвать перерасход памяти, если {@link Entity#index()} возрождаемой сущности больше чем {@link #entityIndexHighWaterMark()}</li>
	 *     <li>Метод может нарушить гарантию метода {@link #create()}. Если возродить ранее удаленную сущность ({@link #remove(Entity)}),
	 *     то счетчик поколений ({@link Entity#generation()}) для всех сущностей с данным индексом будет сброшен до значения поколения передаваемой сущности.</li>
	 *     <li>Метод не выполняет проверок на отрицательность {@link Entity#index()} и {@link Entity#generation()}.</li>
	 * </ul>
	 * <b>Используйте данный метод, только если хорошо понимаете, что делаете.</b>
	 * </p>
	 *
	 * @throws IllegalStateException если уже есть живая сущность с таким же индексом ({@link #isAlive(Entity)}).
	 * @throws NullPointerException если entity равен null.
	 */
	public void unsafeRevive(Entity entity) {
		if(hasAliveEntityWith(entity.index()))
			throw new IllegalStateException("There is already a living entity with index " + entity.index());

		int previousEntityIndex = -1;
		int entityIndex = head;
		while(entityIndex != -1 && entityIndex != entity.index()) {
			previousEntityIndex = entityIndex;
			entityIndex = extractIndex(entities[entityIndex]);
		}

		if(entityIndex == -1) {
			previousEntityIndex = size;
			entityIndex = entity.index();
			growToIndex(entityIndex);
			if(entityIndex > previousEntityIndex) {
				if(head == -1)
					head = previousEntityIndex;
				else
					entities[tail] = pack(previousEntityIndex, extractGeneration(entities[tail]));
				tail = entityIndex - 1;
				entities[tail] = pack(-1, 0);
				while(previousEntityIndex < tail) {
					entities[previousEntityIndex] = pack(previousEntityIndex + 1, 0);
					++previousEntityIndex;
				}
			}
		} else if(entityIndex == head && entityIndex == tail) {
			head = -1;
			tail = -1;
		} else if(entityIndex == head) {
			head = extractIndex(entities[entityIndex]);
		} else if(entityIndex == tail) {
			tail = previousEntityIndex;
			entities[previousEntityIndex] = pack(-1, extractGeneration(entities[previousEntityIndex]));
		} else {
			entities[previousEntityIndex] = pack(extractIndex(entities[entityIndex]), extractGeneration(entities[previousEntityIndex]));
		}

		entities[entityIndex] = Entity.toLong(entity);
		aliveEntitiesMask.set(entityIndex);
	}

	/**
	 * <p>Сущность считается живой после её создания через {@link #create()} или {@link #unsafeRevive(Entity)} и до её удаления через {@link #remove(Entity)}.</p>
	 * <p>
	 *     <b>Метод гарантирует</b>, что среди всех сущностей с одним и тем же индексом, живой может считаться не более одной сущности.
	 *     При этом, все сущности с таким же индексом, для которых был вызван {@link #remove(Entity)} и не вызывался в дальнейшем {@link #unsafeRevive(Entity)},
	 *     будут считаться мертвыми.
	 * </p>
	 * <p>Особый случай: если entity равен null - метод вернет false.</p>
	 */
	public boolean isAlive(Entity entity) {
		return entity != null
					   && entity.index() < size
					   && extractGeneration(entities[entity.index()]) == entity.generation()
					   && aliveEntitiesMask.get(entity.index());
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
		if(index < size) generation = extractGeneration(entities[index]);
		return new Entity(index, generation);
	}

	/**
	 * <p>Возвращает кол-во всех зарезервированных индексов сущностей внутри данного менеджера сущностей.</p>
	 * <p>
	 *     Уточнения по поведению метода:
	 *     <ol>
	 *         <li>Если ни одной сущности не было создано - возвращает 0.</li>
	 *         <li>При удалении сущностей - возвращаемое значение не уменьшается.</li>
	 *         <li>Возвращаемое значение не отображает кол-во живых сущностей.</li>
	 *     </ol>
	 * </p>
	 */
	public int entityIndexHighWaterMark() {
		return size;
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
