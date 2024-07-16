package com.bakuard.ecsEngine.event;

import com.bakuard.collections.ReadableLinearStructure;
import com.bakuard.collections.RingBuffer;

import java.util.HashMap;

public final class EventManager {

    private RingBuffer<Event> writeBuffer;
    private RingBuffer<Event> readBuffer;
    private final HashMap<String, EventConsumer> consumers;
    private final HashMap<String, Event> singletonEvents;
    private final Object lock = new Object();

    public EventManager(int maxEventBufferSize) {
        writeBuffer = new RingBuffer<>(maxEventBufferSize);
        readBuffer = new RingBuffer<>(maxEventBufferSize);
        consumers = new HashMap<>();
        singletonEvents = new HashMap<>();
    }

    public EventManager registerEventConsumer(String consumerName, int maxSize, String... eventNames) {
        EventConsumer eventConsumer = new EventConsumer(consumerName, maxSize, eventNames);
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


    public void publishEventToBuffer(String eventName, Object eventPayload) {
        synchronized(lock) {
            writeBuffer.addLastOrReplace(new Event(eventName, eventPayload));
        }
    }

    public void flushBuffer() {
        synchronized(lock) {
            RingBuffer<Event> temp = writeBuffer;
            writeBuffer = readBuffer;
            readBuffer = temp;
        }

        while(!readBuffer.isEmpty()) {
            Event event = readBuffer.removeFirst();
            publishEvent(event);
        }
    }

    public void publishEvent(String eventName, Object eventPayload) {
        publishEvent(new Event(eventName, eventPayload));
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
}
