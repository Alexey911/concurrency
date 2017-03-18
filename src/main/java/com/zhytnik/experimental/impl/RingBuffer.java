package com.zhytnik.experimental.impl;

import com.zhytnik.experimental.Queue;

import static java.lang.String.format;

/**
 * @author Alexey Zhytnik
 * @since 14-Nov-16
 */
public class RingBuffer<T> implements Queue<T> {

    protected int headIndex;
    protected int tailIndex;

    protected int capacity;

    protected T[] buffer;
    protected int bufferSize;

    public RingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Minimal Buffer capacity is 1, was: " + capacity);
        }
        setUpByOccupancy(createBuffer(capacity + 1), 0);
    }

    protected void setUpByOccupancy(T[] buffer, int count) {
        this.tailIndex = 0;
        this.headIndex = count + 1;

        this.capacity = count;

        this.bufferSize = buffer.length;
        this.buffer = buffer;
    }

    @Override
    public void add(T value) {
        checkHeadIndex();
        final int index = headIndex;
        headIndex = index(index + 1);

        buffer[index] = value;
        ++capacity;
    }

    @Override
    public T pool() {
        final int index = index(tailIndex + 1);
        checkTailIndex(index);
        tailIndex = index;

        final T value = buffer[index];
        buffer[index] = null;
        --capacity;
        return value;
    }

    @Override
    public boolean isEmpty() {
        return capacity == 0;
    }

    @Override
    public int getSize() {
        return bufferSize - 1;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean isFull() {
        return headIndex == tailIndex;
    }

    private void checkHeadIndex() {
        if (isFull()) {
            throw new RuntimeException("Overflow, there's no place!");
        }
    }

    private void checkTailIndex(int nextTailIndex) {
        if (nextTailIndex == headIndex) {
            throw new RuntimeException("Buffer is empty!");
        }
    }

    private int index(int index) {
        return index % bufferSize;
    }

    @SuppressWarnings("unchecked")
    protected T[] createBuffer(int size) {
        return (T[]) new Object[size];
    }

    @Override
    public String toString() {
        return format("Buffer[capacity=%d, size=%d, empty=%s, full=%s]",
                getCapacity(), getSize(), isEmpty(), isFull()
        );
    }
}
