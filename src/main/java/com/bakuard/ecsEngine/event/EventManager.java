package com.bakuard.ecsEngine.event;

import com.bakuard.collections.RingBuffer;
import com.bakuard.ecsEngine.exception.UnknownEventBoxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class EventManager {

	private volatile RingBuffer<Event> writeBuffer;
	private volatile RingBuffer<Event> readBuffer;
	private final Lock lock = new ReentrantLock();

	private final HashMap<String, EventInbox> inputBoxes;
	private final HashMap<String, List<EventInbox>> eventToInputBoxes;
	private final ConcurrentHashMap<String, EventOutbox> outputBoxes;

	private final HashMap<String, Event> globalEvents;

	public EventManager(int maxEventBufferSize) {
		writeBuffer = new RingBuffer<>(maxEventBufferSize);
		readBuffer = new RingBuffer<>(maxEventBufferSize);
		inputBoxes = new HashMap<>();
		eventToInputBoxes = new HashMap<>();
		outputBoxes = new ConcurrentHashMap<>();
		globalEvents = new HashMap<>();
	}

	public EventManager registerInbox(String eventBoxName, int maxSize, String... eventNames) {
		return registerInbox(eventBoxName, maxSize, EventsOverflowPolicy.REWRITE_OLDEST, eventNames);
	}

	public EventManager registerInbox(String eventBoxName, int maxSize, EventsOverflowPolicy policy, String... eventNames) {
		unregisterInbox(eventBoxName);

		EventInbox eventBox = new EventInbox(eventBoxName, maxSize, policy);
		inputBoxes.put(eventBoxName, eventBox);
		for(String eventName : eventNames)
			eventToInputBoxes.computeIfAbsent(eventName, key -> new ArrayList<>()).add(eventBox);
		return this;
	}

	public EventManager unregisterInbox(String eventBoxName) {
		EventInbox eventBox = inputBoxes.remove(eventBoxName);
		eventToInputBoxes.forEach((eventName, eventInboxes) -> eventInboxes.remove(eventBox));
		return this;
	}

	public EventInbox getInbox(String eventBoxName) {
		EventInbox eventBox = inputBoxes.get(eventBoxName);
		if(eventBox == null) {
			throw new UnknownEventBoxException("There is not input event box with name='" + eventBoxName + '\'');
		}
		return eventBox;
	}

	public boolean hasInbox(String eventBoxName) {
		return inputBoxes.containsKey(eventBoxName);
	}


	public void publishAsyncInputEvent(String eventName, Object eventPayload) {
		publishAsyncInputEvent(new Event(eventName, eventPayload));
	}

	public void publishAsyncInputEvent(Event event) {
		try {
			lock.lock();
			writeBuffer.addLastOrReplace(event);
		} finally {
			lock.unlock();
		}
	}

	public void publishSyncInputEvent(String eventName, Object eventPayload) {
		publishSyncInputEvent(new Event(eventName, eventPayload));
	}

	public void publishSyncInputEvent(Event event) {
		publishEvent(event);
	}

	public void flushBufferOfAsyncEvents() {
		try {
			lock.lock();
			RingBuffer<Event> temp = writeBuffer;
			writeBuffer = readBuffer;
			readBuffer = temp;
		} finally {
			lock.unlock();
		}

		while(!readBuffer.isEmpty()) {
			Event event = readBuffer.removeFirst();
			publishEvent(event);
		}
	}


	public EventManager registerOutbox(String eventBoxName, int maxSize) {
		return registerOutbox(eventBoxName, maxSize, EventsOverflowPolicy.REWRITE_OLDEST);
	}

	public EventManager registerOutbox(String eventBoxName, int maxSize, EventsOverflowPolicy policy) {
		EventOutbox eventBox = new EventOutbox(eventBoxName, maxSize, policy);
		outputBoxes.put(eventBoxName, eventBox);
		return this;
	}

	public EventManager unregisterOutbox(String eventBoxName) {
		outputBoxes.remove(eventBoxName);
		return this;
	}

	public EventOutbox getOutbox(String eventBoxName) {
		EventOutbox eventBox = outputBoxes.get(eventBoxName);
		if(eventBox == null) {
			throw new UnknownEventBoxException("There is not output event box with name='" + eventBoxName + '\'');
		}
		return eventBox;
	}

	public boolean hasOutbox(String eventBoxName) {
		return outputBoxes.containsKey(eventBoxName);
	}

	public void unregisterAllOutboxes() {
		outputBoxes.clear();
	}


	public void setGlobalEvent(String eventName, Object eventPayload) {
		setGlobalEvent(new Event(eventName, eventPayload));
	}

	public void setGlobalEvent(Event event) {
		globalEvents.put(event.name(), event);
	}

	public Event getAndClearGlobalEvent(String eventName) {
		return globalEvents.remove(eventName);
	}

	public Event getGlobalEvent(String eventName) {
		return globalEvents.get(eventName);
	}


	private void publishEvent(Event event) {
		List<EventInbox> inputBoxes = eventToInputBoxes.get(event.name());
		if(inputBoxes != null) inputBoxes.forEach(inputBox -> inputBox.put(event));
	}
}
