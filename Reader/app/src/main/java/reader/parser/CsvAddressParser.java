package reader.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import reader.exception.AddressParseException;
import reader.model.Address;

/**
 * Reads address records from a CSV file.
 *
 * <p>Expected format: UTF-8, {@code ;} as the delimiter, values optionally
 * enclosed in double quotes, a header line followed by records with the
 * columns {@code city;street;house;floor}. Columns are accessed by position,
 * so the header names themselves are not checked.
 */
public class CsvAddressParser implements AddressParser {

    private static final int CITY = 0;
    private static final int STREET = 1;
    private static final int HOUSE = 2;
    private static final int FLOOR = 3;
    private static final int COLUMN_COUNT = 4;

    private static final CSVFormat FORMAT = CSVFormat.DEFAULT.builder()
            .setDelimiter(';')
            .setSkipHeaderRecord(true)
            .setHeader()
            .setIgnoreSurroundingSpaces(true)
            .setIgnoreEmptyLines(true)
            .get();

    @Override
    public void parse(Path file, Consumer<Address> consumer) throws AddressParseException {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
             CSVParser parser = FORMAT.parse(reader)) {
            for (CSVRecord csvRecord : parser) {
                Address address = toAddress(csvRecord);
                if (address != null) {
                    consumer.accept(address);
                }
            }
        } catch (IOException | UncheckedIOException e) {
            throw new AddressParseException("Не удалось прочитать CSV-файл: " + file, e);
        } catch (IllegalStateException e) {
            throw new AddressParseException("Нарушена структура CSV-файла: " + file, e);
        }
    }

    /**
     * Converts a CSV record to an address.
     *
     * @param csvRecord record read from the file
     * @return the address, or {@code null} if the record is incomplete
     *         or the floor is not a number
     */
    private static Address toAddress(CSVRecord csvRecord) {
        if (csvRecord.size() < COLUMN_COUNT) {
            return null;
        }
        String city = csvRecord.get(CITY);
        String street = csvRecord.get(STREET);
        String house = csvRecord.get(HOUSE);
        if (city.isEmpty() || street.isEmpty() || house.isEmpty()) {
            return null;
        }
        try {
            int floor = Integer.parseInt(csvRecord.get(FLOOR));
            return new Address(city, street, house, floor);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
