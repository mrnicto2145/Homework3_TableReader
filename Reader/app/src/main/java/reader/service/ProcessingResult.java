package reader.service;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

import reader.statistics.Statistics;

/**
 * Result of processing one directory file.
 *
 * @param file       processed file
 * @param statistics collected statistics
 * @param elapsed    time spent reading the file and building the statistics
 */
public record ProcessingResult(Path file, Statistics statistics, Duration elapsed) {

    /**
     * Creates a result and checks that all components are present.
     *
     * @throws NullPointerException if any component is {@code null}
     */
    public ProcessingResult {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(statistics, "statistics");
        Objects.requireNonNull(elapsed, "elapsed");
    }
}
