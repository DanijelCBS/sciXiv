package xml.web.services.team2.sciXiv.utils;

import org.springframework.stereotype.Component;
import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static org.apache.xerces.jaxp.JAXPConstants.*;

@Component
public class DOMParser {

    private DocumentBuilderFactory factory;

    private SchemaFactory schemaFactory;

    private Document document;

    public DOMParser() {
        initialize();
    }

    private void initialize() {
        factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
        schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
    }

    public Document buildDocument(String xmlFile, String schemaPath) {
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            File schemaFile = new File(schemaPath);
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            StringReader xmlFileReader = new StringReader(xmlFile);
            validator.validate(new StreamSource(xmlFileReader));
            document = builder.parse(new InputSource(xmlFileReader));
            if (document != null)
                throw new Exception();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return document;
    }
}
