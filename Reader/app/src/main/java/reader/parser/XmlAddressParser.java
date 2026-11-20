package reader.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import reader.exception.AddressParseException;
import reader.model.Address;

/**
 * Reads address records from an XML file using the streaming StAX API.
 *
 * <p>Each record is an {@code item} element with the attributes
 * {@code city}, {@code street}, {@code house} and {@code floor}. The file is
 * processed element by element, so its size does not affect memory usage.
 * The encoding is taken from the XML declaration.
 */
public class XmlAddressParser implements AddressParser {

    private static final String ITEM = "item";
    private static final String CITY = "city";
    private static final String STREET = "street";
    private static final String HOUSE = "house";
    private static final String FLOOR = "floor";

    private final XMLInputFactory factory;

    /**
     * Creates a parser with DTD and external entity processing disabled.
     */
    public XmlAddressParser() {
        factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    }

    @Override
    public void parse(Path file, Consumer<Address> consumer) throws AddressParseException {
        try (InputStream input = new BufferedInputStream(Files.newInputStream(file))) {
            XMLStreamReader reader = factory.createXMLStreamReader(input);
            try {
                while (reader.hasNext()) {
                    if (reader.next() == XMLStreamConstants.START_ELEMENT
                            && ITEM.equals(reader.getLocalName())) {
                        Address address = toAddress(reader);
                        if (address != null) {
                            consumer.accept(address);
                        }
                    }
                }
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            throw new AddressParseException("Не удалось прочитать XML-файл: " + file, e);
        } catch (XMLStreamException e) {
            throw new AddressParseException("Нарушена структура XML-файла: " + file, e);
        }
    }

    /**
     * Converts the attributes of the current {@code item} element to an address.
     *
     * @param reader reader positioned at the start of an {@code item} element
     * @return the address, or {@code null} if an attribute is missing or empty
     *         or the floor is not a number
     */
    private static Address toAddress(XMLStreamReader reader) {
        String city = reader.getAttributeValue(null, CITY);
        String street = reader.getAttributeValue(null, STREET);
        String house = reader.getAttributeValue(null, HOUSE);
        String floor = reader.getAttributeValue(null, FLOOR);
        if (isBlank(city) || isBlank(street) || isBlank(house) || floor == null) {
            return null;
        }
        try {
            return new Address(city.strip(), street.strip(), house.strip(),
                    Integer.parseInt(floor.strip()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
