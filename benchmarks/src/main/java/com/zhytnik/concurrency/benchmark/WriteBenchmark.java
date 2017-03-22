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

import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

/**
 * @author Alexey Zhytnik
 * @since 20-Mar-17
 */
@State(Scope.Thread)
public class WriteBenchmark {

    @Param({"15", "31", "63", "127", "255"})
    int size;

    Buffer<Integer> buffer;
    List<Integer> elements;

    @Setup(value = Level.Invocation)
    public void setUp() {
        buffer = new UnmodifiableRingBuffer<>(size);
        elements = new Random().ints(size).boxed().collect(toList());
    }

    @Benchmark
    @Warmup(iterations = 20)
    @Measurement(iterations = 4)
    public void writeSpeed(Blackhole bh) {
        elements.forEach(buffer::add);
        bh.consume(buffer);
    }
}
