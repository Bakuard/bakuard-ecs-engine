package com.bakuard.ecsEngine.event;

import com.bakuard.collections.RingBuffer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class EventManager {

    private RingBuffer<Event> writeBuffer;
    private RingBuffer<Event> readBuffer;
    private final HashMap<String, Topic> topic;
    private final Set<String> flags;
    private final HashMap<String, Event> singletonEvents;
    private final Object lock = new Object();

    public EventManager(int maxEventBufferSize) {
        writeBuffer = new RingBuffer<>(maxEventBufferSize);
        readBuffer = new RingBuffer<>(maxEventBufferSize);
        topic = new HashMap<>();
        flags = new HashSet<>();
        singletonEvents = new HashMap<>();
    }

    public EventManager registerTopic(String topicName, int maxSize, String... eventNames) {
        Topic topic = new Topic(topicName, maxSize, eventNames);
        this.topic.put(topicName, topic);
        return this;
    }

    public Topic getTopic(String topicName) {
        Topic topic = this.topic.get(topicName);
        if(topic == null) {
            throw new UnknownEventConsumerException("There is not EventConsumer with name='" + topicName + '\'');
        }
        return topic;
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


    public void setFlag(String flagName) {
        flags.add(flagName);
    }

    public void clearFlag(String flagName) {
        flags.remove(flagName);
    }

    public boolean checkFlag(String flagName) {
        return flags.contains(flagName);
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


    private void publishEvent(Event event) {
        topic.values().stream()
                .filter(topic -> topic.canContainEventsWithName(event.getName()))
                .forEach(topic -> topic.addEvent(event));
    }
}
