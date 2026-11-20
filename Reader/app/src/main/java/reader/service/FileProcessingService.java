package reader.service;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

import reader.exception.AddressParseException;
import reader.exception.UnsupportedFormatException;
import reader.parser.AddressParser;
import reader.parser.ParserFactory;
import reader.statistics.Statistics;
import reader.statistics.StatisticsCollector;

/**
 * Processes a directory file: selects a parser, collects statistics
 * and measures the processing time.
 */
public class FileProcessingService {

    private final ParserFactory parserFactory;

    /**
     * Creates a service that obtains parsers from the given factory.
     *
     * @param parserFactory factory of parsers
     */
    public FileProcessingService(ParserFactory parserFactory) {
        this.parserFactory = Objects.requireNonNull(parserFactory, "parserFactory");
    }

    /**
     * Reads the file and builds its statistics.
     *
     * @param file path to the directory file
     * @return statistics together with the processing time
     * @throws UnsupportedFormatException if the file format is not supported
     * @throws AddressParseException      if the file cannot be read or is broken
     */
    public ProcessingResult process(Path file) throws UnsupportedFormatException, AddressParseException {
        AddressParser parser = parserFactory.forFile(file);
        StatisticsCollector collector = new StatisticsCollector();

        long start = System.nanoTime();
        parser.parse(file, collector);
        Statistics statistics = collector.result();
        Duration elapsed = Duration.ofNanos(System.nanoTime() - start);

        return new ProcessingResult(file, statistics, elapsed);
    }
}
