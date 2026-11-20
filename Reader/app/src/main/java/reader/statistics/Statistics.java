package reader.statistics;

import java.util.Map;

import reader.model.Address;

/**
 * Summary statistics of one directory file. Instances are immutable.
 *
 * @param duplicates   records that occur more than once, with the number
 *                     of occurrences
 * @param floorsByCity distribution of unique buildings by floor count
 *                     for each city
 */
public record Statistics(Map<Address, Integer> duplicates,
                         Map<String, FloorDistribution> floorsByCity) {

    /**
     * Creates statistics and makes defensive copies of the maps.
     */
    public Statistics {
        duplicates = Map.copyOf(duplicates);
        floorsByCity = Map.copyOf(floorsByCity);
    }
}
