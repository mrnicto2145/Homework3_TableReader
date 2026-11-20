package reader.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import reader.model.Address;

/**
 * Accumulates statistics from address records passed one at a time.
 *
 * <p>Counts occurrences of every record to find duplicates, and counts
 * buildings by floor count per city. A building that occurs several times
 * is counted in the floor statistics once; floor counts outside the range
 * {@link FloorDistribution#MIN_FLOOR}..{@link FloorDistribution#MAX_FLOOR}
 * are ignored there.
 *
 * <p>One instance is intended for one file and is not thread-safe.
 */
public class StatisticsCollector implements Consumer<Address> {

    private static final int FLOOR_RANGE = FloorDistribution.MAX_FLOOR - FloorDistribution.MIN_FLOOR + 1;

    private final Map<Address, Integer> occurrences = new HashMap<>();
    private final Map<String, int[]> floorCounters = new HashMap<>();

    @Override
    public void accept(Address address) {
        int count = occurrences.merge(address, 1, Integer::sum);
        if (count == 1 && FloorDistribution.isTracked(address.floor())) {
            int[] counters = floorCounters.computeIfAbsent(address.city(), city -> new int[FLOOR_RANGE]);
            counters[address.floor() - FloorDistribution.MIN_FLOOR]++;
        }
    }

    /**
     * Builds the result from the records received so far.
     *
     * @return immutable statistics
     */
    public Statistics result() {
        Map<Address, Integer> duplicates = new HashMap<>();
        for (Map.Entry<Address, Integer> entry : occurrences.entrySet()) {
            if (entry.getValue() > 1) {
                duplicates.put(entry.getKey(), entry.getValue());
            }
        }
        Map<String, FloorDistribution> floorsByCity = new HashMap<>();
        for (Map.Entry<String, int[]> entry : floorCounters.entrySet()) {
            floorsByCity.put(entry.getKey(), new FloorDistribution(entry.getValue()));
        }
        return new Statistics(duplicates, floorsByCity);
    }
}
