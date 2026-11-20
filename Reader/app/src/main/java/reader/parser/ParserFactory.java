package reader.parser;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

import reader.exception.UnsupportedFormatException;

/**
 * Selects an {@link AddressParser} by the file extension.
 *
 * <p>Parsers are stateless, so one instance per format is created and reused.
 * A new format can be supported by passing an extended mapping to the
 * constructor without changing the rest of the application.
 */
public class ParserFactory {

    private final Map<String, AddressParser> parsers;

    /**
     * Creates a factory that supports CSV and XML files.
     */
    public ParserFactory() {
        this(Map.of(
                "csv", new CsvAddressParser(),
                "xml", new XmlAddressParser()));
    }

    /**
     * Creates a factory with the given mapping.
     *
     * @param parsers parsers keyed by lower-case file extension without a dot
     */
    public ParserFactory(Map<String, AddressParser> parsers) {
        this.parsers = Map.copyOf(parsers);
    }

    /**
     * Returns the parser suitable for the given file.
     *
     * @param file path to the directory file
     * @return parser for the file format
     * @throws UnsupportedFormatException if the file has no extension
     *                                    or the extension is not supported
     */
    public AddressParser forFile(Path file) throws UnsupportedFormatException {
        String extension = extensionOf(file);
        AddressParser parser = parsers.get(extension);
        if (parser == null) {
            throw new UnsupportedFormatException(
                    "Неподдерживаемый формат файла: " + file.getFileName()
                            + ". Поддерживаются: " + String.join(", ", parsers.keySet()));
        }
        return parser;
    }

    /**
     * Extracts the lower-case extension of the file name.
     *
     * @param file path to the file
     * @return extension without the dot, or an empty string if there is none
     */
    private static String extensionOf(Path file) {
        Path name = file.getFileName();
        if (name == null) {
            return "";
        }
        String fileName = name.toString();
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
