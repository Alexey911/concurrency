package com.zhytnik.concurrency.buffer.impl;

import static java.lang.System.arraycopy;

/**
 * @author Alexey Zhytnik
 * @since 20-Nov-16
 */
public final class DynamicRingBuffer<T> extends RingBuffer<T> {

    private final int minTrimCapacity;

    public DynamicRingBuffer(int capacity) {
        super(capacity);
        minTrimCapacity = capacity;
    }

    @Override
    public void add(T value) {
        if (isFull()) extend();
        super.add(value);
    }

    @Override
    public T pool() {
        final T value = super.pool();
        if (hasGarbage()) trim();
        return value;
    }

    private void extend() {
        changeStorageSizeTo(2 * bufferSize + 1);
    }

    private boolean hasGarbage() {
        return (capacity > minTrimCapacity) && (2 * capacity < bufferSize);
    }

    private void trim() {
        changeStorageSizeTo((int) (bufferSize / 1.5f) + 1);
    }

    private void changeStorageSizeTo(int size) {
        final T[] temp = copy(buffer, headIndex, tailIndex, size);
        setUpByOccupancy(temp, capacity);
    }

    private T[] copy(T[] src, int head, int tail, int newLength) {
        final T[] copy = createBuffer(newLength);

        if (head > tail) {
            arraycopy(src, tail, copy, 0, head - tail);
        } else {
            int rightCount = src.length - tail - 1;

            arraycopy(src, tail + 1, copy, 1, rightCount);
            arraycopy(src, 0, copy, rightCount + 1, head);
        }
        return copy;
    }
}
