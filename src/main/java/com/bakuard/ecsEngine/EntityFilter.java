package com.bakuard.ecsEngine;

import java.util.Arrays;

public final class EntityFilter {

    private static final Object[] empty = {};


    private final Object[] all;
    private final Object[] any;
    private final Object[] none;

    public EntityFilter() {
        all = empty;
        any = empty;
        none = empty;
    }

    private EntityFilter(Object[] all, Object[] any, Object[] none) {
        this.all = all;
        this.any = any;
        this.none = none;
    }

    public EntityFilter allOf(Object... components) {
        return new EntityFilter(components, any, none);
    }

    public EntityFilter anyOf(Object... components) {
        return new EntityFilter(all, components, none);
    }

    public EntityFilter noneOf(Object... components) {
        return new EntityFilter(all, any, components);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityFilter that = (EntityFilter) o;
        return Arrays.equals(all, that.all)
                && Arrays.equals(any, that.any)
                && Arrays.equals(none, that.none);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(all);
        result = 31 * result + Arrays.hashCode(any);
        result = 31 * result + Arrays.hashCode(none);
        return result;
    }

    @Override
    public String toString() {
        return "EntityFilter{" +
                "all=" + Arrays.toString(all) +
                ", any=" + Arrays.toString(any) +
                ", none=" + Arrays.toString(none) +
                '}';
    }
}
