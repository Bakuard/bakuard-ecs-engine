package com.bakuard.ecsEngine.component;

import com.bakuard.collections.DynamicArray;
import com.bakuard.collections.ReadableLinearStructure;

import java.util.Objects;

public class Filter {

    private final ReadableLinearStructure<String> allTags;
    private final ReadableLinearStructure<String> noneTags;
    private final ReadableLinearStructure<Class<?>> allComps;
    private final ReadableLinearStructure<Class<?>> noneComps;

    public Filter() {
        allTags = new DynamicArray<>();
        noneTags = new DynamicArray<>();
        allComps = new DynamicArray<>();
        noneComps = new DynamicArray<>();
    }

    private Filter(ReadableLinearStructure<String> allTags,
                  ReadableLinearStructure<String> noneTags,
                  ReadableLinearStructure<Class<?>> allComps,
                  ReadableLinearStructure<Class<?>> noneComps) {
        this.allTags = allTags;
        this.noneTags = noneTags;
        this.allComps = allComps;
        this.noneComps = noneComps;
    }

    public Filter allComps(Class<?>... compTypes) {
        return new Filter(allTags, noneTags, DynamicArray.of(compTypes), noneComps);
    }

    public Filter noneComps(Class<?>... compTypes) {
        return new Filter(allTags, noneTags, allComps, DynamicArray.of(compTypes));
    }

    public Filter allTags(String... tags) {
        return new Filter(DynamicArray.of(tags), noneTags, allComps, noneComps);
    }

    public Filter noneTags(String... tags) {
        return new Filter(allTags, DynamicArray.of(tags), allComps, noneComps);
    }

    public ReadableLinearStructure<Class<?>> getAllComps() {
        return allComps;
    }

    public ReadableLinearStructure<Class<?>> getNoneComps() {
        return noneComps;
    }

    public ReadableLinearStructure<String> getAllTags() {
        return allTags;
    }

    public ReadableLinearStructure<String> getNoneTags() {
        return noneTags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filter filter = (Filter) o;
        return allTags.equals(filter.allTags)
                && noneTags.equals(filter.noneTags)
                && allComps.equals(filter.allComps)
                && noneComps.equals(filter.noneComps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allTags, noneTags, allComps, noneComps);
    }

    @Override
    public String toString() {
        return "Filter{"
                + "allTags: " + getAllTags()
                + ", noneTags: " + getNoneTags()
                + ", allComps: " + getAllComps()
                + ", noneComps: " + getNoneComps()
                + "}";
    }
}
