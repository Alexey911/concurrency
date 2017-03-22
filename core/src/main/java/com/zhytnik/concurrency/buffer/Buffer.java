package com.zhytnik.concurrency.buffer;

/**
 * @author Alexey Zhytnik
 * @since 17-Nov-16
 */
public interface Buffer<T> {

    void add(T value);

    T pool();

    int getSize();

    int getCapacity();

    boolean isFull();

    boolean isEmpty();

    void reset();
}
