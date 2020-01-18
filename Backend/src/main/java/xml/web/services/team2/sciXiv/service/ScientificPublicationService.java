package xml.web.services.team2.sciXiv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;
import xml.web.services.team2.sciXiv.dto.SciPubDTO;
import xml.web.services.team2.sciXiv.dto.SearchPublicationsDTO;
import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentParsingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.exception.UserSavingFailedException;
import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.repository.ScientificPublicationRepository;
import xml.web.services.team2.sciXiv.repository.UserRepository;
import xml.web.services.team2.sciXiv.utils.dom.DOMParser;
import xml.web.services.team2.sciXiv.utils.xslt.MetadataExtractor;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

@Service
public class ScientificPublicationService {

    private static String schemaPath = "src/main/resources/static/xmlSchemas/scientificPublication.xsd";

    @Autowired
    ScientificPublicationRepository scientificPublicationRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DOMParser domParser;

    @Autowired
    MetadataExtractor metadataExtractor;

    public String findByName(String name) throws XMLDBException, DocumentLoadingFailedException {
        return scientificPublicationRepository.findByName(name);
    }

    public String save(String sciPub) throws ParserConfigurationException, DocumentParsingFailedException, SAXException, IOException, DocumentStoringFailedException, TransformerException, UserSavingFailedException {
        Document document = domParser.buildAndValidateDocument(sciPub, schemaPath);
        String title = document.getDocumentElement().getFirstChild().getFirstChild().getNodeValue();
        String name = title + "/v1";
        ByteArrayOutputStream metadataStream = new ByteArrayOutputStream();

        metadataExtractor.extractMetadata(new ByteArrayInputStream(sciPub.getBytes()), metadataStream);
        String metadata = new String(metadataStream.toByteArray());

        TUser user = (TUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.getOwnPublications().getPublicationID().add(title);
        userRepository.save(user);

        scientificPublicationRepository.saveMetadata(metadata);

        return scientificPublicationRepository.save(sciPub, title, name);
    }

    public String revise(String sciPub) throws ParserConfigurationException, DocumentParsingFailedException, SAXException, IOException, XMLDBException, DocumentStoringFailedException {
        Document document = domParser.buildAndValidateDocument(sciPub, schemaPath);
        String title = document.getDocumentElement().getFirstChild().getFirstChild().getNodeValue();
        int lastVersion = scientificPublicationRepository.getLastVersionNumber(title);
        lastVersion++;
        String name = title + "/v" + lastVersion;

        return scientificPublicationRepository.save(sciPub, title, name);
    }

    public void withdraw(String title) throws XMLDBException, DocumentLoadingFailedException, DocumentStoringFailedException {
        scientificPublicationRepository.withdraw(title);
    }

    public ResponseEntity<ArrayList<SciPubDTO>> basicSearch(String parameter) throws XMLDBException {
        TUser user = (TUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<>(scientificPublicationRepository.basicSearch(parameter, user), HttpStatus.OK);
    }

    public ResponseEntity<ArrayList<SciPubDTO>> advancedSearch(SearchPublicationsDTO searchParameters) {
        String query = "SELECT * FROM <%s>\n" +
                "WHERE { \n" +
                "\t?sciPub";
        query = makeSparqlQuery(query, searchParameters);
        TUser user = (TUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ArrayList<String> userDocuments = (ArrayList<String>) user.getOwnPublications().getPublicationID();
        StringBuilder titles = new StringBuilder("(");
        for (String title : userDocuments) {
            titles.append("\"").append(title).append("\"").append(", ");
        }
        titles.delete(titles.length() - 2, titles.length()).append(")");

        query += "\tFILTER (?sciPub IN " + titles + " | ?status = \"accepted\")\n}";

        return new ResponseEntity<>(scientificPublicationRepository.advancedSearch(query), HttpStatus.OK);
    }

    public ResponseEntity<ArrayList<SciPubDTO>> getReferences(String title) {
        return new ResponseEntity<>(scientificPublicationRepository.getReferences(title), HttpStatus.OK);
    }

    private String makeSparqlQuery(String query, SearchPublicationsDTO parameters) {
        if (!parameters.getTitle().equals("")) {
            query += " <http://schema.org/headline> " + parameters.getTitle() + " ;\n";
        }
        if (!parameters.getDateReceived().equals("")) {
            query += "\t<http://schema.org/dateCreated> " + parameters.getDateReceived() + " ;\n";
        }
        if (!parameters.getDateRevised().equals("")) {
            query += "\t<http://schema.org/dateModified> " + parameters.getDateRevised() + " ;\n";
        }
        if (!parameters.getDateAccepted().equals("")) {
            query += "\t<http://schema.org/datePublished> " + parameters.getDateAccepted() + " ;\n";
        }
        if (!parameters.getAuthorName().equals("")) {
            query += "\t<http://schema.org/author> ?author ;\n" +
                    "\t?author <http://schema.org/name> " + parameters.getAuthorName() + " ;\n";
        }
        if (!parameters.getAuthorAffiliation().equals("")) {
            query += "\t<http://schema.org/author> ?author ;\n" +
                    "\t?author <http://schema.org/affiliation> " + parameters.getAuthorAffiliation() + " ;\n";
        }
        if (!parameters.getKeyword().equals("")) {
            query += "\t<http://schema.org/keywords> " + parameters.getKeyword() + " ;\n";
        }

        query += "\t<http://schema.org/creativeWorkStatus> ?status .\n";

        return query;
    }
}
