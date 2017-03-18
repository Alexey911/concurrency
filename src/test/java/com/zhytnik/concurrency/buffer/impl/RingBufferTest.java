package com.zhytnik.concurrency.buffer.impl;

import com.zhytnik.concurrency.buffer.Buffer;
import org.junit.Test;

/**
 * @author Alexey Zhytnik
 * @since 14-Nov-16
 */
public class RingBufferTest extends DefaultRingBufferTest {

    @Test(expected = RuntimeException.class)
    public void checksFullState() {
        fillByCount(size + 1);
    }

    Buffer<Integer> getBuffer(int size) {
        return new ConcurrentRingBuffer<>(size);
    }
}