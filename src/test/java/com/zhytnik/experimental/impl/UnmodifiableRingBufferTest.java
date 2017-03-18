package com.zhytnik.experimental.impl;

import com.zhytnik.experimental.Buffer;

/**
 * @author Alexey Zhytnik
 * @since 21-Nov-16
 */
public class UnmodifiableRingBufferTest extends DefaultRingBufferTest {
    Buffer<Integer> getBuffer(int size) {
        return new UnmodifiableRingBuffer<>(size);
    }
}
