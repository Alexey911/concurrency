package com.zhytnik.concurrency.buffer.impl;

import com.zhytnik.concurrency.buffer.Buffer;
import com.zhytnik.concurrency.buffer.FullBufferException;
import org.junit.Test;

/**
 * @author Alexey Zhytnik
 * @since 21-Nov-16
 */
public class UnmodifiableRingBufferTest extends DefaultRingBufferTest {

    @Test(expected = FullBufferException.class)
    public void checksFullState() {
        fillByCount(size + 1);
    }

    Buffer<Integer> getBuffer(int size) {
        return new UnmodifiableRingBuffer<>(size);
    }
}
