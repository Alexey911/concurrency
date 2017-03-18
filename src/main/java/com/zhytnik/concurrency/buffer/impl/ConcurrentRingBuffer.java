package com.zhytnik.concurrency.buffer.impl;

import com.zhytnik.concurrency.buffer.Buffer;
import com.zhytnik.concurrency.buffer.EmptyBufferException;
import com.zhytnik.concurrency.buffer.FullBufferException;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

/**
 * @author Alexey Zhytnik
 * @since 20-Nov-16
 */
public final class ConcurrentRingBuffer<T> implements Buffer<T> {

    private final AtomicInteger headIndex;
    private final AtomicInteger tailIndex;

    private final AtomicInteger capacity;

    private final T[] buffer;
    private final int bufferSize;

    public ConcurrentRingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Minimal Buffer capacity is 1, was: " + capacity);
        }

        this.headIndex = new AtomicInteger(0);
        this.tailIndex = new AtomicInteger(1);

        this.capacity = new AtomicInteger(0);

        this.buffer = createBuffer(capacity + 1);
        this.bufferSize = buffer.length;
    }

    @Override
    public void add(T value) {
        final int index = tailIndex.getAndUpdate(this::incrementTail);
        capacity.incrementAndGet();
        buffer[index] = value;
    }

    @Override
    public T pool() {
        final int index = headIndex.updateAndGet(this::incrementHead);
        capacity.decrementAndGet();
        final T value = buffer[index];
        buffer[index] = null;
        return value;
    }

    private int incrementTail(int tail) {
        int head = headIndex.get();
        checkTailIndex(tail, head);
        return index(tail + 1);
    }

    private int incrementHead(int head) {
        int tail = tailIndex.get();
        int next = index(head + 1);
        checkHeadIndex(tail, next);
        return next;
    }

    @Override
    public boolean isEmpty() {
        return capacity.get() == 0;
    }

    @Override
    public int getSize() {
        return bufferSize - 1;
    }

    @Override
    public int getCapacity() {
        return capacity.get();
    }

    @Override
    public boolean isFull() {
        return tailIndex.get() == headIndex.get();
    }

    private void checkTailIndex(int head, int tail) {
        if (head == tail) {
            throw new FullBufferException();
        }
    }

    private void checkHeadIndex(int head, int nextTail) {
        if (nextTail == head) {
            throw new EmptyBufferException();
        }
    }

    private int index(int index) {
        return index % bufferSize;
    }

    @SuppressWarnings("unchecked")
    private T[] createBuffer(int size) {
        return (T[]) new Object[size];
    }

    @Override
    public String toString() {
        return format("Buffer[capacity=%d, size=%d]", getCapacity(), getSize());
    }
}
