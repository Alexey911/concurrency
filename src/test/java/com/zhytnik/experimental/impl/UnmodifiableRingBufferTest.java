package com.zhytnik.experimental.impl;

import com.zhytnik.experimental.Queue;

/**
 * @author Alexey Zhytnik
 * @since 21-Nov-16
 */
public class UnmodifiableRingBufferTest extends DefaultRingBufferTest {
    Queue<Integer> getBuffer(int size) {
        return new UnmodifiableRingBuffer<>(size);
    }
}
