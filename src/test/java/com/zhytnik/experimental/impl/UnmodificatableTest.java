package com.zhytnik.experimental.impl;

import com.zhytnik.experimental.Queue;

/**
 * @author Alexey Zhytnik
 * @since 21-Nov-16
 */
public class UnmodificatableTest extends DefaultBufferTest {
    Queue<Integer> getBuffer(int size) {
        return new UnmodifictableCyclicBuffer<>(size);
    }
}
