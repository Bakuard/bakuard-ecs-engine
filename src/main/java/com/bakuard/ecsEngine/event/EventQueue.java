package com.bakuard.ecsEngine.event;

import com.bakuard.collections.ReadableLinearStructure;
import com.bakuard.collections.RingBuffer;

import java.util.Set;

public class EventQueue {

    private final String name;
    private final RingBuffer<Event> events;
    private final Set<String> eventNames;

    EventQueue(String name, int maxBufferSize, String... eventNames) {
        this.name = name;
        this.events = new RingBuffer<>(maxBufferSize);
        this.eventNames = Set.of(eventNames);
    }

    public boolean hasEvents() {
        return !events.isEmpty();
    }

    public Event pullEvent() {
        return events.removeFirst();
    }

    public ReadableLinearStructure<Event> getEvents() {
        return events;
    }

    public String getName() {
        return name;
    }


    boolean contains(String eventName) {
        return eventNames.contains(eventName);
    }

    void pushEvent(Event event) {
        events.addLastOrReplace(event);
    }
}
