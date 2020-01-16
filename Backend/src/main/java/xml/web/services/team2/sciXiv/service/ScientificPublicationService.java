package xml.web.services.team2.sciXiv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;
import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentParsingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.repository.ScientificPublicationRepository;
import xml.web.services.team2.sciXiv.utils.xslt.MetadataExtractor;
import xml.web.services.team2.sciXiv.utils.dom.DOMParser;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ScientificPublicationService {

    private static String schemaPath = "src/main/resources/static/xmlSchemas/scientificPublication.xsd";

    @Autowired
    ScientificPublicationRepository scientificPublicationRepository;

    @Autowired
    DOMParser domParser;

    @Autowired
    MetadataExtractor metadataExtractor;

    public String findByName(String name) throws XMLDBException, DocumentLoadingFailedException {
        return scientificPublicationRepository.findByName(name);
    }

    public String save(String sciPub) throws ParserConfigurationException, DocumentParsingFailedException, SAXException, IOException, DocumentStoringFailedException, TransformerException {
        Document document = domParser.buildAndValidateDocument(sciPub, schemaPath);
        String name = document.getDocumentElement().getAttribute("title");
        ByteArrayOutputStream metadataStream = new ByteArrayOutputStream();

        metadataExtractor.extractMetadata(new ByteArrayInputStream(sciPub.getBytes()), metadataStream);
        String metadata = new String(metadataStream.toByteArray());

        scientificPublicationRepository.saveMetadata(metadata);

        return scientificPublicationRepository.save(sciPub, name);
    }

    public String update(String sciPub) throws ParserConfigurationException, DocumentParsingFailedException, SAXException, IOException, XMLDBException, DocumentStoringFailedException {
        Document document = domParser.buildAndValidateDocument(sciPub, schemaPath);
        String name = document.getDocumentElement().getAttribute("title");

        return scientificPublicationRepository.update(sciPub, name);
    }

    public void delete(String name) throws XMLDBException {
        scientificPublicationRepository.delete(name);
    }
}
