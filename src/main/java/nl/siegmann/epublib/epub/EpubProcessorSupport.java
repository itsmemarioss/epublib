package nl.siegmann.epublib.epub;

import nl.siegmann.epublib.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;

/**
 * Various low-level support methods for reading/writing epubs.
 * @author paul.siegmann
 */
public class EpubProcessorSupport {

    private static final Logger log = LoggerFactory.getLogger(EpubProcessorSupport.class);

    protected static DocumentBuilderFactory documentBuilderFactory;

    static {
        init();
    }

    static class EntityResolverImpl implements EntityResolver {
        private String previousLocation;

        @Override
        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException {
            String resourcePath;
            if (systemId.startsWith("http:")) {
                URL url = new URL(systemId);
                resourcePath = "dtd/" + url.getHost() + url.getPath();
                previousLocation = resourcePath.substring(0, resourcePath.lastIndexOf('/'));
            } else {
                resourcePath = previousLocation + systemId.substring(systemId.lastIndexOf('/'));
            }

            if (this.getClass().getClassLoader().getResource(resourcePath) == null) {
                throw new RuntimeException("remote resource is not cached : [" + systemId + "] cannot continue");
            }

            InputStream in = EpubProcessorSupport.class.getClassLoader().getResourceAsStream(resourcePath);
            return new InputSource(in);
        }
    }


    private static void init() {
        EpubProcessorSupport.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);
    }


    public static XmlSerializer createDefaultXmlSerializer() {
        XmlSerializer result = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setValidating(true);
            result = factory.newSerializer();
            result.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        } catch (Exception e) {
            log.error("When creating XmlSerializer: " + e.getClass().getName() + ": " + e.getMessage());
        }
        return result;
    }

    public static XmlSerializer createDefaultXmlSerializer(OutputStream out) throws UnsupportedEncodingException {
        return createDefaultXmlSerializer(new OutputStreamWriter(out, Constants.CHARACTER_ENCODING));
    }

    public static XmlSerializer createDefaultXmlSerializer(Writer out) {
        XmlSerializer result = null;
        try {
            createDefaultXmlSerializer().setOutput(out);
        } catch (Exception e) {
            log.error("When calling XmlSerializer.setOuput(): " + e.getClass().getName() + ": " + e.getMessage());
        }
        return result;
    }

    /**
     * Gets an EntityResolver that loads dtd's and such from the epublib classpath. In order to enable the loading of
     * relative urls the given EntityResolver contains the previousLocation. Because of a new EntityResolver is created
     * every time this method is called. Fortunately the EntityResolver created uses up very little memory per
     * instance.
     * @return an EntityResolver that loads dtd's and such from the epublib classpath.
     */
    public static EntityResolver getEntityResolver() {
        return new EntityResolverImpl();
    }

    public DocumentBuilderFactory getDocumentBuilderFactory() {
        return documentBuilderFactory;
    }

    /**
     * Creates a DocumentBuilder that looks up dtd's and schema's from epublib's classpath.
     * @return a DocumentBuilder that looks up dtd's and schema's from epublib's classpath.
     */
    public static DocumentBuilder createDocumentBuilder() {
        DocumentBuilder result = null;
        try {
            result = documentBuilderFactory.newDocumentBuilder();
            result.setEntityResolver(getEntityResolver());
        } catch (ParserConfigurationException e) {
            log.error(e.getMessage());
        }
        return result;
    }
}
