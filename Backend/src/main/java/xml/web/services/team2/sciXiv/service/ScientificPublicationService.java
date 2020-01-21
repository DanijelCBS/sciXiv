package xml.web.services.team2.sciXiv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;
import xml.web.services.team2.sciXiv.dto.SciPubDTO;
import xml.web.services.team2.sciXiv.dto.SearchPublicationsDTO;
import xml.web.services.team2.sciXiv.exception.*;
import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.repository.ScientificPublicationRepository;
import xml.web.services.team2.sciXiv.repository.UserRepository;
import xml.web.services.team2.sciXiv.utils.dom.DOMParser;
import xml.web.services.team2.sciXiv.utils.xslt.DOMToXMLTransformer;
import xml.web.services.team2.sciXiv.utils.xslt.MetadataExtractor;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    @Autowired
    DOMToXMLTransformer transformer;

    public String findByName(String name) throws XMLDBException, DocumentLoadingFailedException {
        return scientificPublicationRepository.findByName(name);
    }

    public String save(String sciPub) throws ParserConfigurationException, DocumentParsingFailedException, SAXException, IOException, DocumentStoringFailedException, TransformerException, UserSavingFailedException, UserRetrievingFailedException {
        Document document = domParser.buildAndValidateDocument(sciPub, schemaPath);
        NodeList nodeList = document.getElementsByTagName("sp:title");
        String title = nodeList.item(0).getTextContent();
        title = title.replace(' ', '-');
        String name = title + "-v1";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateReceived = sdf.format(new Date());

        NodeList authors = document.getElementsByTagName("sp:authors");
        NodeList keywords = document.getElementsByTagName("sp:keywords");

        Element dateReceivedElem = document.createElement("sp:dateReceived");
        dateReceivedElem.setTextContent(dateReceived);
        dateReceivedElem.setAttribute("property", "pred:dateCreated");
        NodeList metadataElem = document.getElementsByTagName("sp:metadata");
        metadataElem.item(0).insertBefore(dateReceivedElem, authors.item(0));

        Element status = document.createElement("sp:status");
        status.setTextContent("in process");
        status.setAttribute("property", "pred:creativeWorkStatus");
        metadataElem.item(0).insertBefore(status, keywords.item(0));

        sciPub = transformer.toXML(document);
        ByteArrayOutputStream metadataStream = new ByteArrayOutputStream();

        metadataExtractor.extractMetadata(new ByteArrayInputStream(sciPub.getBytes()), metadataStream);
        String metadata = new String(metadataStream.toByteArray());

        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        TUser user = userRepository.getByEmail(email);
        user.getOwnPublications().getPublicationID().add(title);
        userRepository.save(user);

        scientificPublicationRepository.saveMetadata(metadata);

        return scientificPublicationRepository.save(sciPub, title, name);
    }

    public String revise(String sciPub) throws ParserConfigurationException, DocumentParsingFailedException, SAXException, IOException, XMLDBException, DocumentStoringFailedException {
        Document document = domParser.buildAndValidateDocument(sciPub, schemaPath);
        NodeList nodeList = document.getElementsByTagName("sp:title");
        String title = nodeList.item(0).getTextContent();
        title = title.replace(' ', '-');
        int lastVersion = scientificPublicationRepository.getLastVersionNumber(title);
        lastVersion++;
        String name = title + "-v" + lastVersion;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateRevised = sdf.format(new Date());

        NodeList authors = document.getElementsByTagName("sp:authors");
        NodeList keywords = document.getElementsByTagName("sp:keywords");

        Element dateRevisedElem = document.createElement("sp:dateRevised");
        dateRevisedElem.setTextContent(dateRevised);
        dateRevisedElem.setAttribute("property", "pred:dateModified");
        NodeList metadataElem = document.getElementsByTagName("sp:metadata");
        metadataElem.item(0).insertBefore(dateRevisedElem, authors.item(0));

        Element status = document.createElement("sp:status");
        status.setTextContent("in process");
        status.setAttribute("property", "pred:creativeWorkStatus");
        metadataElem.item(0).insertBefore(status, keywords.item(0));

        sciPub = transformer.toXML(document);

        String resourceName = document.getDocumentElement().getAttribute("about");

        scientificPublicationRepository.insertMetadata(resourceName, "dateModified", dateRevised);

        return scientificPublicationRepository.save(sciPub, title, name);
    }

    public String withdraw(String title) throws XMLDBException, DocumentLoadingFailedException, DocumentStoringFailedException {
        scientificPublicationRepository.withdraw(title.replace(' ', '-'));
        return "Publication successfully withdrawn";
    }

    public ArrayList<SciPubDTO> basicSearch(String parameter) throws XMLDBException, UserRetrievingFailedException {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        TUser user = userRepository.getByEmail(email);
        return scientificPublicationRepository.basicSearch(parameter, user);
    }

    public ArrayList<SciPubDTO> advancedSearch(SearchPublicationsDTO searchParameters) throws UserRetrievingFailedException {
        String query = "SELECT * FROM <%s>\n" +
                "WHERE { \n" +
                "\t?sciPub";
        query = makeSparqlQuery(query, searchParameters);
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        TUser user = userRepository.getByEmail(email);
        ArrayList<String> userDocuments = (ArrayList<String>) user.getOwnPublications().getPublicationID();
        String resourceURL = "http://ftn.uns.ac.rs/scientificPublication/";
        StringBuilder titles = new StringBuilder("(");
        for (String title : userDocuments) {
            titles.append("<").append(resourceURL).append(title).append(">").append(", ");
        }
        titles.delete(titles.length() - 2, titles.length()).append(")");

        query += "\tFILTER (?sciPub IN " + titles + " || ?status = \"accepted\")\n}";

        return scientificPublicationRepository.advancedSearch(query);
    }

    public ArrayList<SciPubDTO> getReferences(String title) {
        return scientificPublicationRepository.getReferences(title);
    }

    private String makeSparqlQuery(String query, SearchPublicationsDTO parameters) {
        String sciPub = "";

        if (!parameters.getTitle().equals("")) {
            query += " <http://schema.org/headline> " + parameters.getTitle() + " ;\n";
        }
        if (!parameters.getDateReceived().equals("")) {
            query += " <http://schema.org/dateCreated> " + parameters.getDateReceived() + " ;\n";
        }
        if (!parameters.getDateRevised().equals("")) {
            query += " <http://schema.org/dateModified> " + parameters.getDateRevised() + " ;\n";
        }
        if (!parameters.getDateAccepted().equals("")) {
            query += " <http://schema.org/datePublished> " + parameters.getDateAccepted() + " ;\n";
        }
        if (!parameters.getKeyword().equals("")) {
            query += " <http://schema.org/keywords> " + parameters.getKeyword() + " ;\n";
        }
        if (!parameters.getAuthorName().equals("")) {
            query += " <http://schema.org/author> ?author .\n" +
                    "\t?author <http://schema.org/name> " + parameters.getAuthorName() + " .\n";
            sciPub = "?sciPub";
        }
        if (!parameters.getAuthorAffiliation().equals("")) {
            query += sciPub + " <http://schema.org/author> ?author .\n" +
                    "\t?author <http://schema.org/affiliation> " + parameters.getAuthorAffiliation() + " .\n";
            sciPub = "?sciPub";
        }

        query += sciPub + " <http://schema.org/creativeWorkStatus> ?status .\n";

        return query;
    }
}
