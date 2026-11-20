package reader.parser;

import java.nio.file.Path;
import java.util.function.Consumer;

import reader.exception.AddressParseException;
import reader.model.Address;

/**
 * Common contract for reading address records from a directory file.
 *
 * <p>Implementations read the file sequentially and pass each record to the
 * given consumer as soon as it is read, without keeping the whole file in
 * memory. This lets the rest of the application work the same way regardless
 * of the file format.
 */
public interface AddressParser {

    /**
     * Reads all records from the file and passes each one to {@code consumer}.
     *
     * <p>Records that cannot be interpreted (missing fields, non-numeric floor)
     * are skipped. Errors that make further reading impossible are reported
     * with an exception.
     *
     * @param file     path to the directory file
     * @param consumer receiver of the parsed records
     * @throws AddressParseException if the file cannot be read or its
     *                               structure is broken
     */
    void parse(Path file, Consumer<Address> consumer) throws AddressParseException;
}
