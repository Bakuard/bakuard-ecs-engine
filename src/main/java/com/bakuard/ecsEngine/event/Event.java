package com.bakuard.ecsEngine.event;

import java.util.Objects;

public final class Event {

    private final String name;
    private final Object payload;

    public Event(String name, Object payload) {
        this.name = name;
        this.payload = payload;
    }

    public String getName() {
        return name;
    }

    public boolean hasName(String name) {
        return this.name.equals(name);
    }

    public <T> T getPayload() {
        return (T)payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(name, event.name)
                && Objects.equals(payload, event.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, payload);
    }

    @Override
    public String toString() {
        return "Event{"
                + "name: \"" + name + "\""
                + ", payload: " + payload
                + "}";
    }

}
