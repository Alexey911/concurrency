package com.zhytnik.experimental.impl;

import com.zhytnik.experimental.Buffer;
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