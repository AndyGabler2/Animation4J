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
     * Set the first item in the pair.
     *
     * @param aFirst The first item
     */
    public void setFirst(TYPE_ONE aFirst) {
        first = aFirst;
    }

    /**
     * Get second item in the pair.
     *
     * @return The second item
     */
    public TYPE_TWO getSecond() {
        return second;
    }

    /**
     * Set the second item in the pair.
     *
     * @param aSecond The second item
     */
    public void setSecond(TYPE_TWO aSecond) {
        second = aSecond;
    }
}
