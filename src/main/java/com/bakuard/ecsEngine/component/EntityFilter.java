package com.bakuard.ecsEngine.component;

import com.bakuard.collections.DynamicArray;
import com.bakuard.collections.ReadableLinearStructure;

import java.util.Objects;

public final class EntityFilter {

	private static final DynamicArray<String> emptyTags = new DynamicArray<>();
	private static final DynamicArray<Class<?>> emptyComps = new DynamicArray<>();

	private final DynamicArray<String> allTags;
	private final DynamicArray<String> noneTags;
	private final DynamicArray<Class<?>> allComps;
	private final DynamicArray<Class<?>> noneComps;

	public EntityFilter() {
		this(emptyTags, emptyTags, emptyComps, emptyComps);
	}

	private EntityFilter(DynamicArray<String> allTags,
						 DynamicArray<String> noneTags,
						 DynamicArray<Class<?>> allComps,
						 DynamicArray<Class<?>> noneComps) {
		this.allTags = allTags;
		this.noneTags = noneTags;
		this.allComps = allComps;
		this.noneComps = noneComps;
	}

	public EntityFilter allComps(Class<?>... compTypes) {
		return new EntityFilter(allTags, noneTags, DynamicArray.of(compTypes), noneComps);
	}

	public EntityFilter noneComps(Class<?>... compTypes) {
		return new EntityFilter(allTags, noneTags, allComps, DynamicArray.of(compTypes));
	}

	public EntityFilter allTags(String... tags) {
		return new EntityFilter(DynamicArray.of(tags), noneTags, allComps, noneComps);
	}

	public EntityFilter noneTags(String... tags) {
		return new EntityFilter(allTags, DynamicArray.of(tags), allComps, noneComps);
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
		EntityFilter entityFilter = (EntityFilter) o;
		return allTags.equals(entityFilter.allTags)
				&& noneTags.equals(entityFilter.noneTags)
				&& allComps.equals(entityFilter.allComps)
				&& noneComps.equals(entityFilter.noneComps);
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
