package com.zhytnik.experimental;

/**
 * @author Alexey Zhytnik
 * @since 17-Nov-16
 */
public interface Queue<T> {

    void add(T value);

    T pool();

    int getSize();

    int getCapacity();

    boolean isFull();

    boolean isEmpty();
}
