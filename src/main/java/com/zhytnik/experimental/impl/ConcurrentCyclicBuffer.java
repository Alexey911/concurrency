package com.zhytnik.experimental.impl;

import com.zhytnik.experimental.Queue;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

/**
 * @author Alexey Zhytnik
 * @since 20-Nov-16
 */
public final class ConcurrentCyclicBuffer<T> implements Queue<T> {

    private final AtomicInteger headIndex;
    private final AtomicInteger tailIndex;
    private final AtomicInteger capacity;

    private final T[] buffer;
    private final int bufferSize;

    public ConcurrentCyclicBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Minimal Buffer capacity is 1, was: " + capacity);
        }
        this.tailIndex = new AtomicInteger(0);
        this.headIndex = new AtomicInteger(1);
        this.capacity = new AtomicInteger(0);

        this.buffer = createBuffer(capacity + 1);
        this.bufferSize = buffer.length;
    }

    @Override
    public void add(T value) {
        final int index = headIndex.getAndUpdate(this::incrementHead);
        buffer[index] = value;
        capacity.incrementAndGet();
    }

    @Override
    public T pool() {
        final int index = tailIndex.updateAndGet(this::incrementTail);
        final T value = buffer[index];
        capacity.decrementAndGet();
        buffer[index] = null;
        return value;
    }

    private int incrementHead(int head) {
        int tail = tailIndex.get();
        checkHeadIndex(head, tail);
        return index(head + 1);
    }

    private int incrementTail(int tail) {
        int next = index(tail + 1);
        int head = headIndex.get();
        checkTailIndex(head, next);
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
        return headIndex.get() == tailIndex.get();
    }

    private void checkHeadIndex(int head, int tail) {
        if (head == tail) {
            throw new RuntimeException("Overflow, there's no place!");
        }
    }

    private void checkTailIndex(int head, int nextTail) {
        if (nextTail == head) {
            throw new RuntimeException("Buffer is empty!");
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
        return format("Buffer[capacity=%d, size=%d, empty=%s, full=%s]",
                getCapacity(), getSize(), isEmpty(), isFull()
        );
    }
}
