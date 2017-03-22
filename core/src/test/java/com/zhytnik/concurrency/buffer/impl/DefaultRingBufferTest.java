package com.zhytnik.concurrency.buffer.impl;

import com.zhytnik.concurrency.buffer.Buffer;
import com.zhytnik.concurrency.buffer.EmptyBufferException;
import com.zhytnik.concurrency.buffer.FullBufferException;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 21-Nov-16
 */
abstract class DefaultRingBufferTest {

    int size = 3;

    Buffer<Integer> buffer = getBuffer(size);

    @Test(expected = IllegalArgumentException.class)
    public void failsOnSmallerThanOneCapacities() {
        getBuffer(0);
    }

    @Test
    public void adds() {
        assertThat(buffer.isEmpty()).isTrue();

        buffer.add(5);

        assertThat(buffer.isEmpty()).isFalse();
    }

    @Test
    public void pools() {
        buffer.add(10);
        assertThat(buffer.pool()).isEqualTo(10);
    }

    @Test(expected = EmptyBufferException.class)
    public void failsOnEmptyReading() {
        buffer.pool();
    }

    @Test(expected = FullBufferException.class)
    public void failsOWriteToFull() {
        fillByCount(size + 1);
    }

    @Test
    public void getsCapacity() {
        for (int i = 0; i < size; i++) {
            assertThat(buffer.getCapacity()).isEqualTo(i);
            buffer.add(0);
        }
    }

    @Test
    public void checksFullState() {
        assertThat(buffer.isFull()).isFalse();

        fillByCount(size);

        assertThat(buffer.isFull()).isTrue();
    }

    @Test(expected = EmptyBufferException.class)
    public void resets() {
        fillByCount(size);
        buffer.reset();

        buffer.pool();
    }

    @Test
    public void indicatesStateAndCapacity() {
        buffer.add(3);
        buffer.add(2);

        assertThat(buffer.toString()).isEqualTo("Buffer[capacity=2, size=3]");
    }

    @Test
    public void integrationTest() {
        /* initial condition */
        assertThat(buffer.isEmpty()).isTrue();
        assertThat(buffer.isFull()).isFalse();

        fillByCount(size);

        /* full condition */
        assertThat(buffer.isEmpty()).isFalse();
        assertThat(buffer.isFull()).isTrue();

        poolByCount(size);

        /* empty condition */
        assertThat(buffer.isEmpty()).isTrue();
        assertThat(buffer.isFull()).isFalse();

        buffer.add(777);

        /* partial occupancy condition */
        assertThat(buffer.isEmpty()).isFalse();
        assertThat(buffer.isFull()).isFalse();
        assertThat(buffer.getCapacity()).isEqualTo(1);

        assertThat(buffer.pool()).isEqualTo(777);

        /* initial condition */
        assertThat(buffer.isEmpty()).isTrue();
        assertThat(buffer.isFull()).isFalse();
    }

    @Test
    public void supportsManyReadWriteCycles() {
        Random random = new SecureRandom();

        int count = 1_000;
        int[] from = random.ints(count).toArray();
        int[] to = new int[count];

        int writes = 0, reads = 0, size = 0;

        while (reads < count) {
            if (random.nextBoolean() && size > 0 || writes == count) {
                to[reads++] = buffer.pool();
                size--;
            } else if (writes < count && size < this.size) {
                buffer.add(from[writes++]);
                size++;
            }
        }

        assertThat(from).containsSequence(to);

        assertThat(buffer.isEmpty()).isTrue();
        assertThat(buffer.isFull()).isFalse();
    }

    abstract Buffer<Integer> getBuffer(int size);

    void fillByCount(int count) {
        for (int i = 0; i < count; i++) buffer.add(i + 1);
    }

    void poolByCount(int count) {
        for (int i = 0; i < count; i++) {
            assertThat(buffer.pool()).isEqualTo(i + 1);
        }
    }
}
