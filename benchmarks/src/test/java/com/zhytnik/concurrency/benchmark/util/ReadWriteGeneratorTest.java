package com.zhytnik.concurrency.benchmark.util;

import org.junit.Test;

import static com.zhytnik.concurrency.benchmark.util.ReadWriteGenerator.isWrite;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexey Zhytnik
 * @since 22-Mar-17
 */
public class ReadWriteGeneratorTest {

    int size = 17;

    ReadWriteGenerator generator = new ReadWriteGenerator();

    @Test
    public void generatesRightReadWriteSequence() {
        int writes = 0;

        final boolean[] actions = generator.generate(size, 1_000);

        for (boolean action : actions) {
            if (isWrite(action)) {
                writes++;
            } else {
                writes--;
            }
            assertThat(writes).isGreaterThanOrEqualTo(0).isLessThanOrEqualTo(size);
        }
    }
}
