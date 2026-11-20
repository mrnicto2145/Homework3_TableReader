package reader.model;

import java.util.Objects;

/**
 * A single record of the city directory: one building at a specific address.
 *
 * <p>Two records are equal when all their fields are equal, which makes this
 * type suitable as a key in hash-based collections for duplicate detection.
 *
 * @param city   city name
 * @param street street name
 * @param house  house number; kept as a string because it identifies
 *               the building rather than being a quantity
 * @param floor  number of floors in the building
 */
public record Address(String city, String street, String house, int floor) {

    /**
     * Creates a record and checks that the text fields are present.
     *
     * @throws NullPointerException if {@code city}, {@code street}
     *                              or {@code house} is {@code null}
     */
    public Address {
        Objects.requireNonNull(city, "city");
        Objects.requireNonNull(street, "street");
        Objects.requireNonNull(house, "house");
    }
}
