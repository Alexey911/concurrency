package com.zhytnik.concurrency.buffer.test;

import com.zhytnik.concurrency.buffer.Buffer;
import com.zhytnik.concurrency.buffer.impl.ConcurrentRingBuffer;
import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.IntResult2;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;

/**
 * @author Alexey Zhytnik
 * @since 21-Mar-17
 */
@State
@JCStressTest
@Outcome(id = "1, 2", expect = ACCEPTABLE, desc = "X reads first, Y reads second")
@Outcome(id = "2, 1", expect = ACCEPTABLE, desc = "Y reads first, X reads second")
public class ConcurrencyCorrectnessTest {

    Buffer<Integer> buffer = new ConcurrentRingBuffer<>(3);

    @Actor
    public void X(IntResult2 r) {
        buffer.add(1);
        r.r1 = buffer.pool();
    }

    @Actor
    public void Y(IntResult2 r) {
        buffer.add(2);
        r.r2 = buffer.pool();
    }
}
