package com.andronikus.animation4j.util;

/**
 * Pair of two objects. Mimicking <code>javafx.util.Pair</code>.
 *
 * @param <TYPE_ONE> Type of first object
 * @param <TYPE_TWO> Type of second object
 * @author Andronikus
 */
public class Pair<TYPE_ONE, TYPE_TWO> {
    private TYPE_ONE first;
    private TYPE_TWO second;

    public Pair() {

    }

    public Pair(TYPE_ONE first, TYPE_TWO second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Get first item in the pair.
     *
     * @return The first item
     */
    public TYPE_ONE getFirst() {
        return first;
    }

    /**
     * Get second item in the pair.
     *
     * @return The second item.
     */
    public TYPE_TWO getSecond() {
        return second;
    }
}
