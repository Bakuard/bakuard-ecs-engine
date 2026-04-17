package com.bakuard.ecsEngine.event;

public record Event(String name, Object payload) {

	public boolean hasName(String name) {
		return this.name.equals(name);
	}

	public <T> T payloadAs() {
		return (T) payload;
	}
}
