package reader;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import reader.output.ReportPrinter;
import reader.parser.ParserFactory;
import reader.service.FileProcessingService;
import reader.ui.ConsoleApp;

/**
 * Entry point: assembles the application components and starts the console loop.
 */
public final class App {

    private App() {
    }

    /**
     * Starts the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        FileProcessingService service = new FileProcessingService(new ParserFactory());
        ReportPrinter printer = new ReportPrinter(System.out);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        new ConsoleApp(service, printer, in, System.out).run();
    }
}
