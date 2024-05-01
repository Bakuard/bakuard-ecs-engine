package com.bakuard.ecsEngine.event;

import com.bakuard.collections.RingBuffer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EventManager {

    private RingBuffer<Event> writeBuffer;
    private RingBuffer<Event> readBuffer;
    private final HashMap<String, EventQueue> eventQueues;
    private final Set<String> flags;
    private final Object lock = new Object();

    public EventManager(int maxEventBufferSize) {
        writeBuffer = new RingBuffer<>(maxEventBufferSize);
        readBuffer = new RingBuffer<>(maxEventBufferSize);
        eventQueues = new HashMap<>();
        flags = new HashSet<>();
    }

    public EventQueue registerEventQueue(String queueName, int maxSize, String... eventNames) {
        EventQueue eventQueue = new EventQueue(queueName, maxSize, eventNames);
        eventQueues.put(queueName, eventQueue);
        return eventQueue;
    }

    public EventQueue getEventQueue(String queueName) {
        EventQueue eventQueue = eventQueues.get(queueName);
        if(eventQueue == null) {
            throw new UnknownEventQueueException("There is not EventQueue with name='" + queueName + '\'');
        }
        return eventQueue;
    }

    public void pushEventToBuffer(String eventName, Object eventPayload) {
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
            pushEvent(event);
        }
    }

    public void pushEvent(String eventName, Object eventPayload) {
        pushEvent(new Event(eventName, eventPayload));
    }

    public void setFlag(String flagName) {
        flags.add(flagName);
    }

    public void clearFlag(String flagName) {
        flags.remove(flagName);
    }

    public boolean checkFlag(String flagName) {
        return flags.contains(flagName);
    }


    private void pushEvent(Event event) {
        eventQueues.values().stream()
                .filter(eventQueue -> eventQueue.contains(event.eventName()))
                .forEach(eventQueue -> eventQueue.pushEvent(event));
    }
}
