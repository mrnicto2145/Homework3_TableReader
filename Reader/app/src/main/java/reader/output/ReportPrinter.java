package reader.output;

import java.io.PrintStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import reader.model.Address;
import reader.service.ProcessingResult;
import reader.statistics.FloorDistribution;
import reader.statistics.Statistics;

/**
 * Formats a {@link ProcessingResult} as a text report.
 *
 * <p>The report is built in memory and written in one call, which is much
 * faster than printing thousands of lines one by one.
 */
public class ReportPrinter {

    private static final String CITY_HEADER = "Город";
    private static final int NUMBER_WIDTH = 8;

    private final PrintStream out;
    private final Collator collator;

    /**
     * Creates a printer that writes to the given stream.
     *
     * @param out output stream
     */
    public ReportPrinter(PrintStream out) {
        this.out = Objects.requireNonNull(out, "out");
        this.collator = Collator.getInstance(Locale.of("ru"));
    }

    /**
     * Prints the report for one processed file.
     *
     * @param result processing result
     */
    public void print(ProcessingResult result) {
        StringBuilder report = new StringBuilder();
        Statistics statistics = result.statistics();

        report.append(System.lineSeparator())
                .append("Файл: ").append(result.file()).append(System.lineSeparator());
        appendDuplicates(report, statistics.duplicates());
        appendFloors(report, statistics.floorsByCity());
        report.append(System.lineSeparator())
                .append("Время обработки: ").append(result.elapsed().toMillis()).append(" мс")
                .append(System.lineSeparator());

        out.print(report);
        out.flush();
    }

    private void appendDuplicates(StringBuilder report, Map<Address, Integer> duplicates) {
        report.append(System.lineSeparator())
                .append("Дублирующиеся записи (").append(duplicates.size()).append("):")
                .append(System.lineSeparator());
        if (duplicates.isEmpty()) {
            report.append("  не найдены").append(System.lineSeparator());
            return;
        }

        Comparator<Map.Entry<Address, Integer>> order =
                Map.Entry.<Address, Integer>comparingByValue().reversed()
                        .thenComparing(entry -> entry.getKey().city(), collator)
                        .thenComparing(entry -> entry.getKey().street(), collator)
                        .thenComparing(entry -> entry.getKey().house(), collator);
        List<Map.Entry<Address, Integer>> entries = new ArrayList<>(duplicates.entrySet());
        entries.sort(order);

        for (Map.Entry<Address, Integer> entry : entries) {
            Address address = entry.getKey();
            report.append("  ")
                    .append(address.city()).append(", ")
                    .append(address.street()).append(", д. ")
                    .append(address.house()).append(", этажей: ")
                    .append(address.floor()).append(" — повторений: ")
                    .append(entry.getValue())
                    .append(System.lineSeparator());
        }
    }

    private void appendFloors(StringBuilder report, Map<String, FloorDistribution> floorsByCity) {
        report.append(System.lineSeparator())
                .append("Количество зданий по этажности:")
                .append(System.lineSeparator());
        if (floorsByCity.isEmpty()) {
            report.append("  нет данных").append(System.lineSeparator());
            return;
        }

        List<String> cities = new ArrayList<>(floorsByCity.keySet());
        cities.sort(collator);
        int cityWidth = CITY_HEADER.length();
        for (String city : cities) {
            cityWidth = Math.max(cityWidth, city.length());
        }

        String cityFormat = "  %-" + cityWidth + "s";
        String numberFormat = "%" + NUMBER_WIDTH + "s";

        report.append(String.format(cityFormat, CITY_HEADER));
        for (int floor = FloorDistribution.MIN_FLOOR; floor <= FloorDistribution.MAX_FLOOR; floor++) {
            report.append(String.format(numberFormat, floor + " эт."));
        }
        report.append(System.lineSeparator());

        for (String city : cities) {
            FloorDistribution distribution = floorsByCity.get(city);
            report.append(String.format(cityFormat, city));
            for (int floor = FloorDistribution.MIN_FLOOR; floor <= FloorDistribution.MAX_FLOOR; floor++) {
                report.append(String.format(numberFormat, distribution.count(floor)));
            }
            report.append(System.lineSeparator());
        }
    }
}
