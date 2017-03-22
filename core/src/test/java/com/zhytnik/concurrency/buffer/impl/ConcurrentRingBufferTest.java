package com.zhytnik.concurrency.buffer.impl;

import com.zhytnik.concurrency.buffer.Buffer;
import com.zhytnik.concurrency.buffer.EmptyBufferException;
import com.zhytnik.concurrency.buffer.FullBufferException;
import org.junit.Test;

import static com.zhytnik.concurrency.buffer.impl.ConcurrentRingBuffer.NONE;
import static com.zhytnik.concurrency.buffer.impl.ConcurrentRingBuffer.READ;
import static com.zhytnik.concurrency.buffer.impl.ConcurrentRingBuffer.WRITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author Alexey Zhytnik
 * @since 18-Mar-17
 */
public class ConcurrentRingBufferTest extends DefaultRingBufferTest {

    @Test
    public void locksBeforeWritingAndReading() {
        final ConcurrentRingBuffer<Integer> buffer = spy(asConcurrent(this.buffer));

        buffer.add(1);
        verify(buffer).lockFor(WRITE);
        verify(buffer).unlock();

        reset(buffer);

        buffer.pool();
        verify(buffer).lockFor(READ);
        verify(buffer).unlock();
    }

    @Test
    public void unlocksBeforeFailingOnFull() {
        fillByCount(size);

        try {
            buffer.add(777);
        } catch (FullBufferException e) {
            assertThat(asConcurrent(buffer).state.get()).isEqualTo(NONE);
        }
    }

    @Test
    public void unlocksBeforeEmptyReading() {
        try {
            buffer.pool();
        } catch (EmptyBufferException e) {
            assertThat(asConcurrent(buffer).state.get()).isEqualTo(NONE);
        }
    }

    @Test
    public void resetsWithLock() {
        final ConcurrentRingBuffer<Integer> buffer = spy(asConcurrent(this.buffer));

        buffer.reset();
        verify(buffer).lockFor(WRITE);
        verify(buffer).unlock();
    }

    @Override
    Buffer<Integer> getBuffer(int size) {
        return new ConcurrentRingBuffer<>(size);
    }

    ConcurrentRingBuffer<Integer> asConcurrent(Buffer<Integer> buffer) {
        return (ConcurrentRingBuffer<Integer>) buffer;
    }
}