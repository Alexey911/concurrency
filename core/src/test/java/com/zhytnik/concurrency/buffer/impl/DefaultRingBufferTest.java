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

    @Test
    public void adds() {
        buffer.add(5);
        assertThat(buffer.isEmpty()).isFalse();
    }

    @Test
    public void pools() {
        buffer.add(10);
        assertThat(buffer.pool()).isEqualTo(10);
    }

    @Test(expected = EmptyBufferException.class)
    public void checksEmptyState() {
        buffer.pool();
    }

    @Test
    public void worksWithFewElements() {
        buffer.add(10);
        buffer.add(15);

        assertThat(buffer.pool()).isEqualTo(10);
        assertThat(buffer.pool()).isEqualTo(15);
    }

    @Test
    public void worksWithMaxSize() {
        fillByCount(size);
        poolByCount(size);
    }

    @Test
    public void getsEmpty() {
        assertThat(buffer.isEmpty()).isTrue();
    }

    @Test
    public void getsFull() {
        fillByCount(size);
        assertThat(buffer.isFull()).isTrue();
    }

    @Test
    public void getsCapacity() {
        for (int i = 0; i < size; i++) {
            assertThat(buffer.getCapacity()).isEqualTo(i);
            buffer.add(i);
        }
    }

    @Test(expected = FullBufferException.class)
    public void checksFullState() {
        fillByCount(size + 1);
    }

    @Test
    public void supportsFewReadWriteCycles() {
        fillByCount(size);
        poolByCount(size);

        fillByCount(size);
        poolByCount(size);
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
    public void stressTest() {
        Random random = new SecureRandom();

        int count = 20_000;
        int[] from = random.ints(count).toArray();
        int[] to = new int[count];

        int x = 0, y = 0, size = 0;

        while (y < count) {
            if (random.nextBoolean() && size > 0 || x == count) {
                to[y++] = buffer.pool();
                size--;
            } else if (x < count && size < this.size) {
                buffer.add(from[x++]);
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
