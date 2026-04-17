package com.bakuard.ecsEngine.event;

import com.bakuard.collections.ReadableLinearStructure;
import com.bakuard.collections.RingBuffer;

import java.util.Objects;

public final class EventInbox {

	private final String name;
	private final EventsOverflowPolicy policy;
	private final RingBuffer<Event> events;

	EventInbox(String name, int maxBufferSize, EventsOverflowPolicy policy) {
		this.name = name;
		this.policy = policy;
		this.events = new RingBuffer<>(maxBufferSize);
	}

	public String getName() {
		return name;
	}

	public Event pull() {
		return events.removeFirst();
	}

	public void put(Event event) {
		if(policy == EventsOverflowPolicy.REWRITE_OLDEST) {
			events.addLastOrReplace(event);
		} else {
			events.addLastOrSkip(event);
		}
	}

	public void put(String eventName, Object eventPayload) {
		put(new Event(eventName, eventPayload));
	}

	public void clear() {
		events.clear();
	}

	public boolean isEmpty() {
		return events.isEmpty();
	}

	public ReadableLinearStructure<Event> getAllEvents() {
		return events;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		EventInbox that = (EventInbox) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	@Override
	public String toString() {
		return "EventInbox{ name: '" + name + "' }";
	}
}
