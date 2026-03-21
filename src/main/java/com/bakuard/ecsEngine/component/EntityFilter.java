package com.bakuard.ecsEngine.component;

import com.bakuard.collections.DynamicArray;
import com.bakuard.collections.ReadableLinearStructure;
import com.bakuard.ecsEngine.exception.IllegalEntityFilterStateException;

import java.util.Objects;

public final class EntityFilter {

	private static final DynamicArray<String> emptyTags = new DynamicArray<>();
	private static final DynamicArray<String> emptyComps = new DynamicArray<>();

	private final DynamicArray<String> allTags;
	private final DynamicArray<String> noneTags;
	private final DynamicArray<String> allComps;
	private final DynamicArray<String> noneComps;
	private final boolean withoutComps;
	private final boolean withoutTags;

	public EntityFilter() {
		this(emptyTags, emptyTags, emptyComps, emptyComps, false, false);
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
		assertNotWithoutComps(compTypes);
		DynamicArray<String> allComps = DynamicArray.of(compTypes).mappedCopy((Class<?> type, int i) -> type.getName());
		return new EntityFilter(allTags, noneTags, allComps, noneComps, withoutComps, withoutTags);
	}

	public EntityFilter allComps(String... poolNames) {
		assertNotWithoutComps(poolNames);
		return new EntityFilter(allTags, noneTags, DynamicArray.of(poolNames), noneComps, withoutComps, withoutTags);
	}

	public EntityFilter noneComps(Class<?>... compTypes) {
		assertNotWithoutComps(compTypes);
		DynamicArray<String> noneComps = DynamicArray.of(compTypes).mappedCopy((Class<?> type, int i) -> type.getName());
		return new EntityFilter(allTags, noneTags, allComps, noneComps, withoutComps, withoutTags);
	}

	public EntityFilter noneComps(String... poolNames) {
		assertNotWithoutComps(poolNames);
		return new EntityFilter(allTags, noneTags, allComps, DynamicArray.of(poolNames), withoutComps, withoutTags);
	}

	public EntityFilter allTags(String... tags) {
		assertNotWithoutTags(tags);
		return new EntityFilter(DynamicArray.of(tags), noneTags, allComps, noneComps, withoutComps, withoutTags);
	}

	public EntityFilter noneTags(String... tags) {
		assertNotWithoutTags(tags);
		return new EntityFilter(allTags, DynamicArray.of(tags), allComps, noneComps, withoutComps, withoutTags);
	}

	public EntityFilter withoutComps(boolean withoutComps) {
		if(withoutComps && (!allComps.isEmpty() || !noneComps.isEmpty()))
			throw new IllegalEntityFilterStateException("Cannot set withoutComps to true if there are any components restrictions.");
		return new EntityFilter(allTags, noneTags, allComps, noneComps, withoutComps, withoutTags);
	}

	public EntityFilter withoutTags(boolean withoutTags) {
		if(withoutTags && (!allTags.isEmpty() || !noneTags.isEmpty()))
			throw new IllegalEntityFilterStateException("Cannot set withoutTags to true if there are any tags restrictions.");
		return new EntityFilter(allTags, noneTags, allComps, noneComps, withoutComps, withoutTags);
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


	private void assertNotWithoutComps(Class<?>... compTypes) {
		if(withoutComps && Objects.requireNonNull(compTypes).length > 0)
			throw new IllegalEntityFilterStateException("Cannot set components restriction if withoutComps is true.");
	}

	private void assertNotWithoutComps(String[] poolNames) {
		if(withoutComps && Objects.requireNonNull(poolNames).length > 0)
			throw new IllegalEntityFilterStateException("Cannot set components restriction if withoutComps is true.");
	}

	private void assertNotWithoutTags(String[] tags) {
		if(withoutTags && Objects.requireNonNull(tags).length > 0)
			throw new IllegalEntityFilterStateException("Cannot set tags restriction if withoutTags is true.");
	}
}
