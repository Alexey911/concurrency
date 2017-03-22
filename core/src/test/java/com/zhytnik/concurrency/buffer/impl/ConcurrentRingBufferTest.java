package com.zhytnik.concurrency.buffer.impl;

import com.zhytnik.concurrency.buffer.Buffer;

/**
 * @author Alexey Zhytnik
 * @since 18-Mar-17
 */
public class ConcurrentRingBufferTest extends DefaultRingBufferTest {

    @Override
    Buffer<Integer> getBuffer(int size) {
        return new ConcurrentRingBuffer<>(size);
    }
}