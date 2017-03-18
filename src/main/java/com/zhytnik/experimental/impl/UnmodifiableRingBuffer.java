package com.zhytnik.experimental.impl;

import com.zhytnik.experimental.Queue;

import static java.lang.Integer.numberOfLeadingZeros;
import static java.lang.String.format;

/**
 * @author Alexey Zhytnik
 * @since 21-Nov-16
 */
@SuppressWarnings("Duplicates")
public final class UnmodifiableRingBuffer<T> implements Queue<T> {

    private int headIndex;
    private int tailIndex;

    private int capacity;

    private final T[] buffer;
    private final int mask;

    public UnmodifiableRingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Minimal Buffer capacity is 1, was: " + capacity);
        }

        this.capacity = 0;
        this.tailIndex = 0;
        this.headIndex = 1;

        final int size = getBufferSize(capacity + 1);
        this.mask = size - 1;
        this.buffer = createBuffer(size);
    }

    @Override
    public void add(T value) {
        final int index = headIndex;
        checkHeadIndex(index);
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
        return mask + 1;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean isFull() {
        return headIndex == tailIndex;
    }

    private void checkHeadIndex(int head) {
        if (head == tailIndex) {
            throw new RuntimeException("Overflow, there's no place!");
        }
    }

    private void checkTailIndex(int nextTailIndex) {
        if (nextTailIndex == headIndex) {
            throw new RuntimeException("Buffer is empty!");
        }
    }

    private int index(int index) {
        return index & mask;
    }

    @SuppressWarnings("unchecked")
    protected T[] createBuffer(int size) {
        return (T[]) new Object[size];
    }

    private int getBufferSize(int size) {
        final int max = 2 * size - 1;
        return 1 << (31 - numberOfLeadingZeros(max));
    }

    @Override
    public String toString() {
        return format("Buffer[capacity=%d, size=%d, empty=%s, full=%s]",
                getCapacity(), getSize(), isEmpty(), isFull()
        );
    }
}
