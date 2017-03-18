package com.zhytnik.concurrency.buffer.impl;

import com.zhytnik.concurrency.buffer.Buffer;
import com.zhytnik.concurrency.buffer.EmptyBufferException;
import com.zhytnik.concurrency.buffer.FullBufferException;

import static java.lang.Integer.numberOfLeadingZeros;
import static java.lang.String.format;

/**
 * @author Alexey Zhytnik
 * @since 21-Nov-16
 */
@SuppressWarnings("Duplicates")
public final class UnmodifiableRingBuffer<T> implements Buffer<T> {

    private int headIndex;
    private int tailIndex;

    private int capacity;

    private final T[] buffer;
    private final int mask;

    public UnmodifiableRingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }

        this.capacity = 0;
        this.headIndex = 0;
        this.tailIndex = 1;

        final int size = getBufferSize(capacity + 1);
        this.mask = size - 1;
        this.buffer = createBuffer(size);
    }

    @Override
    public void add(T value) {
        final int index = tailIndex;
        checkTailIndex(index);
        tailIndex = index(index + 1);

        buffer[index] = value;
        ++capacity;
    }

    @Override
    public T pool() {
        final int index = index(headIndex + 1);
        checkHeadIndex(index);
        headIndex = index;

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
        return tailIndex == headIndex;
    }

    private void checkTailIndex(int tail) {
        if (tail == headIndex) {
            throw new FullBufferException();
        }
    }

    private void checkHeadIndex(int nextHeadIndex) {
        if (nextHeadIndex == tailIndex) {
            throw new EmptyBufferException();
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
        return format("Buffer[capacity=%d, size=%d]", getCapacity(), getSize());
    }
}
