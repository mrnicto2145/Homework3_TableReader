package reader.statistics;

import java.util.Arrays;

/**
 * Number of buildings of each floor count from {@value #MIN_FLOOR}
 * to {@value #MAX_FLOOR} in one city. Instances are immutable.
 */
public final class FloorDistribution {

    /** Lowest floor count taken into account. */
    public static final int MIN_FLOOR = 1;

    /** Highest floor count taken into account. */
    public static final int MAX_FLOOR = 5;

    private final int[] counts;

    /**
     * Creates a distribution from counters indexed by {@code floor - MIN_FLOOR}.
     *
     * @param counts counters; the array is copied
     * @throws IllegalArgumentException if the array length does not match
     *                                  the floor range
     */
    FloorDistribution(int[] counts) {
        if (counts.length != MAX_FLOOR - MIN_FLOOR + 1) {
            throw new IllegalArgumentException("Unexpected number of counters: " + counts.length);
        }
        this.counts = counts.clone();
    }

    /**
     * Checks whether a floor count is within the tracked range.
     *
     * @param floor floor count
     * @return {@code true} if {@code MIN_FLOOR <= floor <= MAX_FLOOR}
     */
    public static boolean isTracked(int floor) {
        return floor >= MIN_FLOOR && floor <= MAX_FLOOR;
    }

    /**
     * Returns the number of buildings with the given floor count.
     *
     * @param floor floor count from {@code MIN_FLOOR} to {@code MAX_FLOOR}
     * @return number of buildings
     * @throws IllegalArgumentException if the floor count is out of range
     */
    public int count(int floor) {
        if (!isTracked(floor)) {
            throw new IllegalArgumentException("Floor out of range: " + floor);
        }
        return counts[floor - MIN_FLOOR];
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FloorDistribution other && Arrays.equals(counts, other.counts);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(counts);
    }

    @Override
    public String toString() {
        return "FloorDistribution" + Arrays.toString(counts);
    }
}
