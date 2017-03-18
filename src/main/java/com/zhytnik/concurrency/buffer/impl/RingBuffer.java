package com.zhytnik.concurrency.buffer.impl;

import com.zhytnik.concurrency.buffer.Buffer;
import com.zhytnik.concurrency.buffer.EmptyBufferException;
import com.zhytnik.concurrency.buffer.FullBufferException;

import static java.lang.String.format;

/**
 * @author Alexey Zhytnik
 * @since 14-Nov-16
 */
public class RingBuffer<T> implements Buffer<T> {

    protected int headIndex;
    protected int tailIndex;

    protected int capacity;

    protected T[] buffer;
    protected int bufferSize;

    public RingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
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
            throw new FullBufferException();
        }
    }

    private void checkTailIndex(int nextTailIndex) {
        if (nextTailIndex == headIndex) {
            throw new EmptyBufferException();
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
        return format("Buffer[capacity=%d, size=%d]", getCapacity(), getSize());
    }
}
