package xml.web.services.team2.sciXiv.utils.dom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import xml.web.services.team2.sciXiv.exception.DocumentParsingFailedException;


@Component
public class DOMParser {

    @Autowired
    private DocumentBuilderFactory documentBuilderFactory;

    @Autowired
    private SchemaFactory schemaFactory;

    public DOMParser() {}

    public Document buildAndValidateDocument(String xmlFile, String schemaPath) throws ParserConfigurationException, SAXException, IOException, DocumentParsingFailedException {
        documentBuilderFactory.setSchema(schemaFactory.newSchema(new File(schemaPath)));
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

        Document document = builder.parse(new InputSource(new StringReader(xmlFile)));

        if (document != null)
            throw new DocumentParsingFailedException("Failed to parse document");

        return document;
    }
}
