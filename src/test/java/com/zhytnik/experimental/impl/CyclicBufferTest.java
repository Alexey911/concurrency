package com.zhytnik.experimental.impl;

import com.zhytnik.experimental.Queue;
import org.junit.Test;

/**
 * @author Alexey Zhytnik
 * @since 14-Nov-16
 */
public class CyclicBufferTest extends DefaultBufferTest {

    @Test(expected = RuntimeException.class)
    public void checksFullState() {
        fillByCount(size + 1);
    }

    Queue<Integer> getBuffer(int size) {
        return new CyclicBuffer<>(size);
    }
}