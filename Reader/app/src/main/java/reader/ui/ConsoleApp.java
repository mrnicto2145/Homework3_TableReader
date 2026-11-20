package reader.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Objects;

import reader.exception.AddressParseException;
import reader.exception.UnsupportedFormatException;
import reader.output.ReportPrinter;
import reader.service.FileProcessingService;

/**
 * Interactive console loop: asks for a file path, prints its statistics
 * and repeats until the user enters the exit command.
 *
 * <p>Any error while processing a file is reported to the user and the loop
 * continues; the application terminates only on the exit command or when
 * the input stream ends.
 */
public class ConsoleApp {

    /** Command that terminates the application. */
    public static final String EXIT_COMMAND = "exit";

    private final FileProcessingService service;
    private final ReportPrinter printer;
    private final BufferedReader in;
    private final PrintStream out;

    /**
     * Creates the console application.
     *
     * @param service service that processes files
     * @param printer printer of the results
     * @param in      source of user input
     * @param out     destination for prompts and messages
     */
    public ConsoleApp(FileProcessingService service, ReportPrinter printer,
                      BufferedReader in, PrintStream out) {
        this.service = Objects.requireNonNull(service, "service");
        this.printer = Objects.requireNonNull(printer, "printer");
        this.in = Objects.requireNonNull(in, "in");
        this.out = Objects.requireNonNull(out, "out");
    }

    /**
     * Runs the loop until the exit command or the end of input.
     */
    public void run() {
        while (true) {
            out.print(System.lineSeparator()
                    + "Введите путь к файлу-справочнику (или " + EXIT_COMMAND + " для выхода): ");
            out.flush();

            String line;
            try {
                line = in.readLine();
            } catch (IOException e) {
                out.println("Ошибка чтения ввода: " + e.getMessage());
                return;
            }
            if (line == null) {
                out.println();
                return;
            }

            String input = unquote(line.strip());
            if (input.isEmpty()) {
                continue;
            }
            if (EXIT_COMMAND.equalsIgnoreCase(input)) {
                out.println("Завершение работы.");
                return;
            }
            handle(input);
        }
    }

    /**
     * Processes one entered path and reports the outcome.
     *
     * @param input path entered by the user
     */
    private void handle(String input) {
        try {
            Path file = Path.of(input);
            if (!Files.isRegularFile(file)) {
                out.println("Файл не найден: " + file);
                return;
            }
            out.println("Обработка файла...");
            printer.print(service.process(file));
        } catch (InvalidPathException e) {
            out.println("Некорректный путь: " + input);
        } catch (UnsupportedFormatException e) {
            out.println(e.getMessage());
        } catch (AddressParseException e) {
            out.println(e.getMessage() + " (" + e.getCause().getMessage() + ")");
        } catch (OutOfMemoryError e) {
            out.println("Недостаточно памяти для обработки файла. "
                    + "Попробуйте увеличить размер кучи JVM (-Xmx).");
        } catch (RuntimeException e) {
            out.println("Непредвиденная ошибка: " + e);
        }
    }

    /**
     * Removes the surrounding double quotes that Windows adds
     * when a path is copied with "Copy as path".
     *
     * @param value entered text
     * @return text without surrounding quotes
     */
    private static String unquote(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1).strip();
        }
        return value;
    }
}
