package com.zhytnik.concurrency.buffer.impl;

import com.zhytnik.concurrency.buffer.Buffer;

/**
 * @author Alexey Zhytnik
 * @since 21-Nov-16
 */
public class UnmodifiableRingBufferTest extends DefaultRingBufferTest {

    Buffer<Integer> getBuffer(int size) {
        return new UnmodifiableRingBuffer<>(size);
    }
}
