package com.zhytnik.concurrency.buffer.impl;

import com.zhytnik.concurrency.buffer.Buffer;
import com.zhytnik.concurrency.buffer.FullBufferException;
import com.zhytnik.concurrency.buffer.EmptyBufferException;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

/**
 * @author Alexey Zhytnik
 * @since 20-Nov-16
 */
public class ConcurrentRingBuffer<T> implements Buffer<T> {

    protected static final int NONE = 0;
    protected static final int WRITE = 1;
    protected static final int READ = 2;

    protected final AtomicInteger state;

    private int head;
    private int tail;
    private int capacity;

    private final T[] buffer;

    private final int mask;

    public ConcurrentRingBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();

        this.head = 0;
        this.tail = 1;
        this.capacity = 0;

        this.state = new AtomicInteger(NONE);

        this.mask = capacity;
        this.buffer = createBuffer(capacity + 1);
    }

    @Override
    public void add(T value) {
        lockFor(WRITE);

        final int index = tail;

        checkWriteTo(index);
        buffer[index] = value;
        tail = index(index + 1);
        ++capacity;

        unlock();
    }

    @Override
    public T pool() {
        lockFor(READ);

        final int index = index(head + 1);
        checkReadFrom(index);
        head = index;
        --capacity;

        unlock();

        return buffer[index];
    }

    protected void lockFor(int action) {
        //noinspection StatementWithEmptyBody
        while (!state.compareAndSet(NONE, action)) {
        }
    }

    protected void unlock() {
        state.set(NONE);
    }

    private void checkWriteTo(int index) {
        if (index == head) {
            unlock();
            throw new FullBufferException();
        }
    }

    private void checkReadFrom(int index) {
        if (index == tail) {
            unlock();
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
        lockFor(WRITE);

        head = 0;
        tail = 1;
        capacity = 0;
        Arrays.fill(buffer, null);

        unlock();
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
