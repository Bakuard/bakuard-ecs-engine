package com.bakuard.ecsEngine.entity;

/**
 * Уникальный идентификатор для игровых объектов. Представляет собой комбинацию index и generation, которая
 * гарантировано уникальна среди всех сущностей созданных через один и тот же экземпляр {@link EntityManager}.
 * @param index неотрицательное число представляющее порядковый номер сущности.
 * @param generation индекс каждой сущности будет переиспользован после её удаления для создания новой
 *                   сущности. Указывает, какое кол-во раз данный индекс уже был переиспользован.
 */
public record Entity(int index, int generation) {

    public Entity(long entityAsLong) {
        this((int) entityAsLong, (int) (entityAsLong >>> 32));
    }

    public long asLong() {
        return (long)generation << 32 | (long)index;
    }

}
