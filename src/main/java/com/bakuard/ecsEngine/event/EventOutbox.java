package com.bakuard.ecsEngine.event;

import com.bakuard.collections.RingBuffer;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class EventOutbox {

	private final String name;
	private final EventsOverflowPolicy policy;
	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	private volatile RingBuffer<Event> writeBuffer;
	private volatile RingBuffer<Event> readBuffer;

	EventOutbox(String name, int maxEventBufferSize, EventsOverflowPolicy policy) {
		this.name = name;
		this.policy = policy;
		this.writeBuffer = new RingBuffer<>(maxEventBufferSize);
		this.readBuffer = new RingBuffer<>(maxEventBufferSize);
	}

	public String getName() {
		return name;
	}

	public Event pull() {
		try {
			lock.lock();
			return readBuffer.removeFirst();
		} finally {
			lock.unlock();
		}
	}

	public Event pullOrWait(long timeoutInMs) {
		try {
			lock.lock();
			long nanosRemaining = TimeUnit.MILLISECONDS.toNanos(timeoutInMs);
			while(readBuffer.isEmpty() && nanosRemaining > 0L) nanosRemaining = condition.awaitNanos(nanosRemaining);
			return readBuffer.removeFirst();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		} finally {
			lock.unlock();
		}
	}

	public void put(Event event) {
		if(policy == EventsOverflowPolicy.REWRITE_OLDEST) {
			writeBuffer.addLastOrReplace(event);
		} else {
			writeBuffer.addLastOrSkip(event);
		}
	}

	public void put(String eventName, Object eventPayload) {
		put(new Event(eventName, eventPayload));
	}

	public void clearWriteBuffer() {
		writeBuffer.clear();
	}

	public void swapEventBuffers() {
		try {
			lock.lock();
			RingBuffer<Event> temp = writeBuffer;
			writeBuffer = readBuffer;
			readBuffer = temp;
			condition.signalAll();
		} finally {
			lock.unlock();
		}
		writeBuffer.clear();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		EventOutbox that = (EventOutbox) o;
		return Objects.equals(getName(), that.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getName());
	}

	@Override
	public String toString() {
		return "EventOutbox{ name: '" + name + "' }";
	}
}
