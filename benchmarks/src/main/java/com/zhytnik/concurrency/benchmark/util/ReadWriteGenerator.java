package com.zhytnik.concurrency.benchmark.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.fill;

/**
 * @author Alexey Zhytnik
 * @since 20-Mar-17
 */
public class ReadWriteGenerator {

    public static final boolean WRITE = true;
    public static final boolean READ = false;

    public boolean[] generate(int size, int queue) {
        boolean[] actions = new boolean[queue];

        final int writes = (int) Math.floor(queue * 0.5f);

        fill(actions, 0, writes, WRITE);

        actions = shuffle(actions);

        optimize(size, actions);

        return actions;
    }

    private boolean[] shuffle(boolean[] actions) {
        final List<Boolean> shuffled = new ArrayList<>(actions.length);
        for (boolean action : actions) shuffled.add(action);

        Collections.shuffle(shuffled);

        final boolean[] copy = new boolean[actions.length];
        for (int i = 0; i < actions.length; i++) copy[i] = shuffled.get(i);

        return copy;
    }

    private void optimize(int size, boolean[] actions) {
        int writeWaits = 0;
        int readWaits = isWrite(actions[0]) ? 0 : 1;

        actions[0] = WRITE;
        int writes = 1;

        for (int i = 1; i < actions.length; i++) {
            if (isWrite(actions[i])) {
                if (writes > 0 && readWaits > 0) {
                    readWaits--;

                    writes--;
                    actions[i] = READ;
                } else if (writes == size) {
                    writeWaits++;

                    writes--;
                    actions[i] = READ;
                } else {
                    writes++;
                    actions[i] = WRITE;
                }
            } else {
                if (writes == 0 || (writes < size && writeWaits > 0)) {
                    writeWaits--;

                    writes++;
                    actions[i] = WRITE;
                } else {
                    writes--;
                    actions[i] = READ;
                }
            }
        }
    }

    public static boolean isWrite(boolean action) {
        return action == WRITE;
    }
}
