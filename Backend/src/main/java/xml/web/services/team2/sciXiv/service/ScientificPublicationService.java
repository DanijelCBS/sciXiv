package xml.web.services.team2.sciXiv.service;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
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
import xml.web.services.team2.sciXiv.utils.database.SparqlUtil;
import xml.web.services.team2.sciXiv.utils.dom.DOMParser;
import xml.web.services.team2.sciXiv.utils.xslt.DOMToXMLTransformer;
import xml.web.services.team2.sciXiv.utils.xslt.MetadataExtractor;
import xml.web.services.team2.sciXiv.utils.xslt.XSLFOTransformer;
import xml.web.services.team2.sciXiv.utils.xslt.XSLTranspiler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class ScientificPublicationService {

    private static String schemaPath = "src/main/resources/static/xmlSchemas/scientificPublication.xsd";

    private static String xslPath = "src/main/resources/static/xsl/scientificPublicationToHTML.xsl";
    
    private static String xslForAnonymusPublicatonPath = "src/main/resources/static/xsl/publicationAnonymusToHTML.xsl";

    private static String xslFOPath = "src/main/resources/static/xsl/xsl-fo/scientificPublicationToPDF.xsl";

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

    @Autowired
    XSLFOTransformer xslTransformer;

    @Autowired
    XSLTranspiler xslTranspiler;

    public String findByNameAndVersion(String name, int version) throws XMLDBException, DocumentLoadingFailedException {
        return scientificPublicationRepository.findByNameAndVersion(name.replace(" ", ""), version);
    }

    public String save(String sciPub) throws ParserConfigurationException, DocumentParsingFailedException, SAXException, IOException, DocumentStoringFailedException, TransformerException, UserSavingFailedException, UserRetrievingFailedException {
        Document document = domParser.buildAndValidateDocument(sciPub, schemaPath);
        NodeList nodeList = document.getElementsByTagName("sp:title");
        String title = nodeList.item(0).getTextContent();
        title = title.replace(" ", "");
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

        /*String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        TUser user = userRepository.getByEmail(email);
        user.getOwnPublications().getPublicationID().add(title);
        userRepository.save(user);*/

        scientificPublicationRepository.saveMetadata(metadata);

        return scientificPublicationRepository.save(sciPub, title, name);
    }

    public String revise(String sciPub) throws ParserConfigurationException, DocumentParsingFailedException, SAXException, IOException, XMLDBException, DocumentStoringFailedException {
        Document document = domParser.buildAndValidateDocument(sciPub, schemaPath);
        NodeList nodeList = document.getElementsByTagName("sp:title");
        String title = nodeList.item(0).getTextContent();
        title = title.replace(" ", "");
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
        scientificPublicationRepository.withdraw(title.replace(" ", ""));
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
        /*String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        TUser user = userRepository.getByEmail(email);
        ArrayList<String> userDocuments = (ArrayList<String>) user.getOwnPublications().getPublicationID();
        String resourceURL = "http://ftn.uns.ac.rs/scientificPublication/";
        StringBuilder titles = new StringBuilder("(");
        for (String title : userDocuments) {
            titles.append("<").append(resourceURL).append(title).append(">").append(", ");
        }
        titles.delete(titles.length() - 2, titles.length()).append(")");

        query += "\tFILTER (?sciPub IN " + titles + " || ?status = \"accepted\")\n}";*/
        query += "\n}";

        return scientificPublicationRepository.advancedSearch(query);
    }

    public ArrayList<SciPubDTO> getReferences(String title) {
        return scientificPublicationRepository.getReferences(title);
    }

    public String getScientificPublicationAsXHTML(String title) throws XMLDBException, DocumentLoadingFailedException, TransformerException {
        int lastVersion = scientificPublicationRepository.getLastVersionNumber(title.replace(" ", ""));
        String xmlDocument = scientificPublicationRepository.findByNameAndVersion(title, lastVersion);

        return xslTranspiler.generateHTML(xmlDocument, xslPath);
    }
    
    public String getAnonymusScientificPublicationAsXHTML(String title, int version) throws XMLDBException, DocumentLoadingFailedException, TransformerException {
        String xmlDocument = scientificPublicationRepository.findByNameAndVersion(title, version);

        return xslTranspiler.generateHTML(xmlDocument, xslForAnonymusPublicatonPath);
    }

    public SearchPublicationsDTO getPublicationsMetadata(String title) {
        return scientificPublicationRepository.getPublicationsMetadata(title.replace(" ", ""));
    }

    private String makeSparqlQuery(String query, SearchPublicationsDTO parameters) {
        String sciPub = "";
        String literalType = "^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral>";

        if (!parameters.getTitle().equals("")) {
            query += " <http://schema.org/headline> " + "\"" + parameters.getTitle() + "\"" + literalType + " ;\n";
        }
        if (!parameters.getDateReceived().equals("")) {
            query += " <http://schema.org/dateCreated> " + "\"" + parameters.getDateReceived() + "\"" + literalType + " ;\n";
        }
        if (!parameters.getDateRevised().equals("")) {
            query += " <http://schema.org/dateModified> " + "\"" + parameters.getDateRevised() + "\"" + literalType + " ;\n";
        }
        if (!parameters.getDateAccepted().equals("")) {
            query += " <http://schema.org/datePublished> " + "\"" + parameters.getDateAccepted() + "\"" + literalType + " ;\n";
        }
        if (!parameters.getKeyword().equals("")) {
            query += " <http://schema.org/keywords> " + "\"" + parameters.getKeyword() + "\"" + literalType + " ;\n";
        }
        if (!parameters.getAuthorName().equals("")) {
            query += " <http://schema.org/author> ?author .\n" +
                    "\t?author <http://schema.org/name> " + "\"" + parameters.getAuthorName() + "\"" + literalType + " .\n";
            sciPub = "?sciPub";
        }
        if (!parameters.getAuthorAffiliation().equals("")) {
            query += sciPub + " <http://schema.org/author> ?author .\n" +
                    "\t?author <http://schema.org/affiliation> " + "\"" + parameters.getAuthorAffiliation() + "\"" + literalType + " .\n";
            sciPub = "?sciPub";
        }

        query += sciPub + " <http://schema.org/creativeWorkStatus> ?status .\n";

        return query;
    }

    public Resource exportScientificPublicationAsXHTML(String title) throws XMLDBException, DocumentLoadingFailedException, TransformerException, IOException {
        String sciPubHTML = getScientificPublicationAsXHTML(title);

        Path file = Paths.get(title + ".html");
        Files.write(file, sciPubHTML.getBytes(StandardCharsets.UTF_8));

        return new UrlResource(file.toUri());
    }

    public Resource exportScientificPublicationAsPDF(String title) throws Exception {
        int lastVersion = scientificPublicationRepository.getLastVersionNumber(title.replace(" ", ""));
        String xmlDocument = scientificPublicationRepository.findByNameAndVersion(title, lastVersion);

        ByteArrayOutputStream outputStream = xslTranspiler.generatePDf(xmlDocument, xslFOPath);

        Path file = Paths.get(title + ".pdf");
        Files.write(file, outputStream.toByteArray());

        return new UrlResource(file.toUri());
    }

    public Resource getPublicationsMetadataAsRDF(String title) throws XMLDBException, DocumentLoadingFailedException, TransformerException, IOException {
        int lastVersion = scientificPublicationRepository.getLastVersionNumber(title.replace(" ", ""));
        String xmlDocument = scientificPublicationRepository.findByNameAndVersion(title, lastVersion);
        ByteArrayOutputStream metadataStream = new ByteArrayOutputStream();

        metadataExtractor.extractMetadata(new ByteArrayInputStream(xmlDocument.getBytes()), metadataStream);
        String metadata = new String(metadataStream.toByteArray());

        Model model = ModelFactory.createDefaultModel();
        model.read(new ByteArrayInputStream(metadata.getBytes()), null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        model.write(out, SparqlUtil.RDF_XML);

        Path file = Paths.get(title + "-metadata.rdf");
        Files.write(file, out.toByteArray());

        return new UrlResource(file.toUri());
    }

    public Resource getPublicationsMetadataAsJSON(String title) throws XMLDBException, DocumentLoadingFailedException, IOException {
        int lastVersion = scientificPublicationRepository.getLastVersionNumber(title.replace(" ", ""));
        String xmlDocument = scientificPublicationRepository.findByNameAndVersion(title, lastVersion);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content", xmlDocument);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://rdf-translator.appspot.com/convert/rdfa/json-ld/content";

        String content = restTemplate.postForEntity(url, request, String.class).getBody();

        Path file = Paths.get(title + "-metadata.json");
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));

        return new UrlResource(file.toUri());
    }
}
