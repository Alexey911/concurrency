package com.zhytnik.concurrency.buffer.impl;

import com.zhytnik.concurrency.buffer.Buffer;
import com.zhytnik.concurrency.buffer.EmptyBufferException;
import com.zhytnik.concurrency.buffer.FullBufferException;

import java.util.Arrays;

import static java.lang.String.format;

/**
 * @author Alexey Zhytnik
 * @since 21-Nov-16
 */
public class UnmodifiableRingBuffer<T> implements Buffer<T> {

    private int head;
    private int tail;
    private int capacity;

    private final T[] buffer;

    private final int mask;

    public UnmodifiableRingBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();

        this.head = 0;
        this.tail = 1;
        this.capacity = 0;

        this.mask = capacity;
        this.buffer = createBuffer(capacity + 1);
    }

    @Override
    public void add(T value) {
        final int index = tail;
        checkWriteTo(index);
        tail = index(index + 1);

        buffer[index] = value;
        ++capacity;
    }

    @Override
    public T pool() {
        final int index = index(head + 1);
        checkReadFrom(index);
        head = index;

        --capacity;
        return buffer[index];
    }

    private void checkWriteTo(int index) {
        if (index == head) {
            throw new FullBufferException();
        }
    }

    private void checkReadFrom(int index) {
        if (index == tail) {
            throw new EmptyBufferException();
        }
    }

    private int index(int index) {
        return index & mask;
    }

    @Override
    public boolean isEmpty() {
        return capacity == 0;
    }

    @Override
    public void reset() {
        head = 0;
        tail = 1;
        capacity = 0;

        Arrays.fill(buffer, null);
    }

    @Override
    public int getSize() {
        return mask;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean isFull() {
        return capacity == mask;
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
