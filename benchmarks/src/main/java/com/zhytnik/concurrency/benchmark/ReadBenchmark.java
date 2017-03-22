package com.zhytnik.concurrency.benchmark;

import com.zhytnik.concurrency.buffer.Buffer;
import com.zhytnik.concurrency.buffer.impl.UnmodifiableRingBuffer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;

/**
 * @author Alexey Zhytnik
 * @since 20-Mar-17
 */
@State(Scope.Thread)
public class ReadBenchmark {

    @Param({"15", "63", "127", "255"})
    int size;

    Buffer<Integer> buffer;

    @Setup(value = Level.Invocation)
    public void setUp() {
        buffer = new UnmodifiableRingBuffer<>(size);
        new Random().ints(size).forEach(buffer::add);
    }

    @Benchmark
    @Warmup(iterations = 20)
    @Measurement(iterations = 4)
    public void readSpeed(Blackhole bh) {
        for (int i = 0; i < size; ++i) {
            bh.consume(buffer.pool());
        }
    }
}
