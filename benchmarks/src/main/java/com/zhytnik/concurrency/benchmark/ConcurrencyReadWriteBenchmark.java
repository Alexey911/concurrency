package com.zhytnik.concurrency.benchmark;

import com.zhytnik.concurrency.buffer.Buffer;
import com.zhytnik.concurrency.buffer.impl.ConcurrentRingBuffer;
import com.zhytnik.concurrency.benchmark.util.ReadWriteGenerator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.zhytnik.concurrency.benchmark.util.ReadWriteGenerator.isWrite;
import static java.util.stream.Collectors.toList;

/**
 * @author Alexey Zhytnik
 * @since 20-Mar-17
 */
@State(Scope.Benchmark)
public class ConcurrencyReadWriteBenchmark {

    @Param({"15", "31", "63", "127", "255"})
    int size;

    Buffer<Integer> buffer;

    boolean[] actions;
    List<Integer> elements;

    AtomicInteger action;

    @Setup(value = Level.Invocation)
    public void setUp() {
        int operations = 10_000;

        action = new AtomicInteger(0);

        buffer = new ConcurrentRingBuffer<>(size);
        elements = new Random().ints(operations).boxed().collect(toList());
        actions = new ReadWriteGenerator().generate(size, operations);
    }

    @Benchmark
    @Threads(4)
    @Warmup(iterations = 6, batchSize = 10_010, time = 100)
    @Measurement(iterations = 3, batchSize = 10_000, time = 100)
    public void doWork(Blackhole bh) {
        final int index = action.getAndIncrement();

        if (isWrite(actions[index])) {
            buffer.add(elements.get(index));
        } else {
            bh.consume(buffer.pool());
        }
    }

    public static void main(String[] args) throws RunnerException {
        final Options options = new OptionsBuilder()
                .include(ReadWriteBenchmark.class.getCanonicalName())
                .forks(2)
                .mode(Mode.AverageTime)
                .shouldDoGC(true)
                .shouldFailOnError(true)
                .timeUnit(TimeUnit.NANOSECONDS)
                .build();
        new Runner(options).run();
    }
}
