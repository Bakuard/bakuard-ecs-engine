package com.bakuard.ecsEngine.entity;

/**
 * Уникальный идентификатор для игровых объектов. Представляет собой комбинацию index и generation, которая
 * гарантировано уникальна среди всех сущностей созданных через один и тот же экземпляр {@link EntityManager}.
 * @param index неотрицательное число, представляющее порядковый номер сущности.
 * @param generation индекс каждой сущности будет переиспользован после её удаления для создания новой
 *                   сущности. Указывает, какое кол-во раз данный индекс уже был переиспользован.
 */
public record Entity(int index, int generation) {

	/**
	 * Возвращает объектное представление сущности, соответствующее переданному представлению в виде 8 байтового идентификатора.
	 * Для {@link Entity#generation()} будут взяты старшие 32 бита переданного идентификатора, а для {@link Entity#index()} - 32 младших бита.
	 */
	public static Entity fromLong(long entityAsLong) {
		return new Entity((int) entityAsLong, (int) (entityAsLong >>> 32));
	}

	/**
	 * Возвращает 8 байтовое представление сущности, соответствующее переданному объектному представлению.
	 * Старшие 32 бита содержат {@link Entity#generation()} сущности, а младшие 32 бита - её {@link Entity#index()}.
	 */
	public static long toLong(Entity entity) {
		return  (long)entity.generation << 32 | (long)entity.index;
	}
}
