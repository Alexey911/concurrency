package com.zhytnik.concurrency.buffer.impl;

import com.zhytnik.concurrency.buffer.Buffer;
import org.junit.Test;

/**
 * @author Alexey Zhytnik
 * @since 18-Mar-17
 */
public class ConcurrentRingBufferTest extends DefaultRingBufferTest {

    @Test(expected = RuntimeException.class)
    public void checksFullState() {
        fillByCount(size + 1);
    }

    @Override
    Buffer<Integer> getBuffer(int size) {
        return new ConcurrentRingBuffer<>(size);
    }
}