package com.bakuard.ecsEngine.event;

import com.bakuard.collections.ReadableLinearStructure;
import com.bakuard.collections.RingBuffer;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class EventManager {

    private volatile RingBuffer<Event> writeBuffer;
    private volatile RingBuffer<Event> readBuffer;
    private final HashMap<String, EventConsumer> consumers;
    private final HashMap<String, Event> singletonEvents;
    private final Lock lock = new ReentrantLock();

    public EventManager(int maxEventBufferSize) {
        writeBuffer = new RingBuffer<>(maxEventBufferSize);
        readBuffer = new RingBuffer<>(maxEventBufferSize);
        consumers = new HashMap<>();
        singletonEvents = new HashMap<>();
    }

    public EventManager registerEventConsumer(String consumerName, int maxSize, String... eventNames) {
        return registerEventConsumer(consumerName, maxSize, EventsOverflowPolicy.REWRITE_OLDEST, eventNames);
    }

    public EventManager registerEventConsumer(String consumerName, int maxSize, EventsOverflowPolicy policy, String... eventNames) {
        EventConsumer eventConsumer = new EventConsumer(maxSize, policy, eventNames);
        this.consumers.put(consumerName, eventConsumer);
        return this;
    }

    public boolean hasEvents(String consumerName) {
        return getEventConsumer(consumerName).hasEvents();
    }

    public Event consume(String consumerName) {
        return getEventConsumer(consumerName).consume();
    }

    public ReadableLinearStructure<Event> getAllEvents(String consumerName) {
        return getEventConsumer(consumerName).getAllEvents();
    }


    public void publishAsyncEvent(String eventName, Object eventPayload) {
        try {
            lock.lock();
            writeBuffer.addLastOrReplace(new Event(eventName, eventPayload));
        } finally {
            lock.unlock();
        }
    }

    public void publishSyncEvent(String eventName, Object eventPayload) {
        publishEvent(new Event(eventName, eventPayload));
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


    public void setSingletonEvent(Event event) {
        singletonEvents.put(event.getName(), event);
    }

    public Event getAndClearSingletonEvent(String eventName) {
        return singletonEvents.remove(eventName);
    }

    public Event getSingletonEvent(String eventName) {
        return singletonEvents.get(eventName);
    }


    private EventConsumer getEventConsumer(String consumerName) {
        EventConsumer eventConsumer = this.consumers.get(consumerName);
        if(eventConsumer == null) {
            throw new UnknownEventConsumerException("There is not EventConsumer with name='" + consumerName + '\'');
        }
        return eventConsumer;
    }

    private void publishEvent(Event event) {
        consumers.values().stream()
                .filter(eventConsumer -> eventConsumer.canContainEventsWithName(event.getName()))
                .forEach(eventConsumer -> eventConsumer.addEvent(event));
    }


    private static final class EventConsumer {

        private final EventsOverflowPolicy policy;
        private final RingBuffer<Event> events;
        private final Set<String> eventNames;

        EventConsumer(int maxBufferSize, EventsOverflowPolicy policy, String... eventNames) {
            this.policy = policy;
            this.events = new RingBuffer<>(maxBufferSize);
            this.eventNames = Set.of(eventNames);
        }

        boolean hasEvents() {
            return !events.isEmpty();
        }

        Event consume() {
            return events.removeFirst();
        }

        ReadableLinearStructure<Event> getAllEvents() {
            return events;
        }


        boolean canContainEventsWithName(String eventName) {
            return eventNames.contains(eventName);
        }

        void addEvent(Event event) {
            if(policy == EventsOverflowPolicy.REWRITE_OLDEST) {
                events.addLastOrReplace(event);
            } else {
                events.addLastOrSkip(event);
            }
        }
    }
}
