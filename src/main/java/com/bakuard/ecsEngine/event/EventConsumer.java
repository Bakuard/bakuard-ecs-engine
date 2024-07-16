package com.bakuard.ecsEngine.event;

import com.bakuard.collections.ReadableLinearStructure;
import com.bakuard.collections.RingBuffer;

import java.util.Set;

public final class EventConsumer {

    private final String name;
    private final RingBuffer<Event> events;
    private final Set<String> eventNames;

    EventConsumer(String name, int maxBufferSize, String... eventNames) {
        this.name = name;
        this.events = new RingBuffer<>(maxBufferSize);
        this.eventNames = Set.of(eventNames);
    }

    public boolean hasEvents() {
        return !events.isEmpty();
    }

    public Event consume() {
        return events.removeFirst();
    }

    public ReadableLinearStructure<Event> getAllEvents() {
        return events;
    }

    public String getName() {
        return name;
    }


    boolean canContainEventsWithName(String eventName) {
        return eventNames.contains(eventName);
    }

    void addEvent(Event event) {
        events.addLastOrReplace(event);
    }
}
