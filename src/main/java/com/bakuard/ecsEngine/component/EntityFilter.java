package com.bakuard.ecsEngine.component;

import com.bakuard.collections.DynamicArray;
import com.bakuard.collections.ReadableLinearStructure;

import java.util.Objects;

public final class EntityFilter {

	private static final DynamicArray<String> EMPTY = new DynamicArray<>();

	private final DynamicArray<String> allTags;
	private final DynamicArray<String> noneTags;
	private final DynamicArray<String> allComps;
	private final DynamicArray<String> noneComps;
	private final boolean withoutComps;
	private final boolean withoutTags;

	public EntityFilter() {
		this(EMPTY, EMPTY, EMPTY, EMPTY, false, false);
	}

	private EntityFilter(DynamicArray<String> allTags,
						 DynamicArray<String> noneTags,
						 DynamicArray<String> allComps,
						 DynamicArray<String> noneComps,
						 boolean withoutComps,
						 boolean withoutTags) {
		this.allTags = allTags;
		this.noneTags = noneTags;
		this.allComps = allComps;
		this.noneComps = noneComps;
		this.withoutComps = withoutComps;
		this.withoutTags = withoutTags;
	}

	public EntityFilter allComps(Class<?>... compTypes) {
		DynamicArray<String> allComps = DynamicArray.of(compTypes).mappedCopy((Class<?> type, int i) -> type.getName());
		return new EntityFilter(allTags, noneTags, allComps, noneComps, false, withoutTags);
	}

	public EntityFilter allComps(String... poolNames) {
		return new EntityFilter(allTags, noneTags, DynamicArray.of(poolNames), noneComps, false, withoutTags);
	}

	public EntityFilter noneComps(Class<?>... compTypes) {
		DynamicArray<String> noneComps = DynamicArray.of(compTypes).mappedCopy((Class<?> type, int i) -> type.getName());
		return new EntityFilter(allTags, noneTags, allComps, noneComps, false, withoutTags);
	}

	public EntityFilter noneComps(String... poolNames) {
		return new EntityFilter(allTags, noneTags, allComps, DynamicArray.of(poolNames), false, withoutTags);
	}

	public EntityFilter allTags(String... tags) {
		return new EntityFilter(DynamicArray.of(tags), noneTags, allComps, noneComps, withoutComps, false);
	}

	public EntityFilter noneTags(String... tags) {
		return new EntityFilter(allTags, DynamicArray.of(tags), allComps, noneComps, withoutComps, false);
	}

	public EntityFilter withoutComps() {
		return new EntityFilter(allTags, noneTags, EMPTY, EMPTY, true, withoutTags);
	}

	public EntityFilter withoutTags() {
		return new EntityFilter(EMPTY, EMPTY, allComps, noneComps, withoutComps, true);
	}

	public ReadableLinearStructure<String> getAllComps() {
		return allComps;
	}

	public ReadableLinearStructure<String> getNoneComps() {
		return noneComps;
	}

	public ReadableLinearStructure<String> getAllTags() {
		return allTags;
	}

	public ReadableLinearStructure<String> getNoneTags() {
		return noneTags;
	}

	public boolean isWithoutComps() {
		return withoutComps;
	}

	public boolean isWithoutTags() {
		return withoutTags;
	}

	public boolean isAll() {
		return allTags.isEmpty() && allComps.isEmpty() && noneTags.isEmpty() && noneComps.isEmpty() && !withoutComps && !withoutTags;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null || getClass() != o.getClass()) return false;
		EntityFilter that = (EntityFilter) o;
		return withoutComps == that.withoutComps
					   && withoutTags == that.withoutTags
					   && Objects.equals(allTags, that.allTags)
					   && Objects.equals(noneTags, that.noneTags)
					   && Objects.equals(allComps, that.allComps)
					   && Objects.equals(noneComps, that.noneComps);
	}

	@Override
	public int hashCode() {
		return Objects.hash(allTags,
				noneTags,
				allComps,
				noneComps,
				withoutComps,
				withoutTags);
	}

	@Override
	public String toString() {
		return "EntityFilter{"
					   + "allTags: " + allTags
					   + ", noneTags: " + noneTags
					   + ", allComps: " + allComps
					   + ", noneComps: " + noneComps
					   + ", withoutComps: " + withoutComps
					   + ", withoutTags: " + withoutTags
					   + "}";
	}
}
