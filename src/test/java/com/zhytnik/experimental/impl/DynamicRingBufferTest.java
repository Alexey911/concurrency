package com.zhytnik.experimental.impl;

import com.zhytnik.experimental.Buffer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 20-Nov-16
 */
public class DynamicRingBufferTest extends DefaultRingBufferTest {

    @Test
    public void changesSize() {
        int size = 50;
        for (int i = 0; i < size; i++) buffer.add(i);

        for (int i = 0; i < size; i++) {
            assertThat(buffer.pool()).isEqualTo(i);
        }
    }

    @Test
    public void worksWithFewExtendAndTrim() {
        buffer.add(1);
        buffer.add(2);
        assertThat(buffer.pool()).isEqualTo(1);
        assertThat(buffer.pool()).isEqualTo(2);

        buffer.add(3);
        assertThat(buffer.pool()).isEqualTo(3);

        buffer.add(4);
        buffer.add(5);
        buffer.add(6);
        buffer.add(7);
        buffer.add(8);
        buffer.add(9);
        assertThat(buffer.pool()).isEqualTo(4);
        assertThat(buffer.pool()).isEqualTo(5);
        assertThat(buffer.pool()).isEqualTo(6);
        assertThat(buffer.pool()).isEqualTo(7);
        assertThat(buffer.pool()).isEqualTo(8);
        assertThat(buffer.pool()).isEqualTo(9);
    }

    Buffer<Integer> getBuffer(int size) {
        return new DynamicRingBuffer<>(size);
    }
}