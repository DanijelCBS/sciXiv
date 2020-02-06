package xml.web.services.team2.sciXiv.repository;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XPathQueryService;
import xml.web.services.team2.sciXiv.dto.SciPubDTO;
import xml.web.services.team2.sciXiv.dto.SearchPublicationsDTO;
import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.utils.connection.RDFConnectionProperties;
import xml.web.services.team2.sciXiv.utils.connection.XMLConnectionProperties;
import xml.web.services.team2.sciXiv.utils.database.BasicOperations;
import xml.web.services.team2.sciXiv.utils.database.SparqlUtil;
import xml.web.services.team2.sciXiv.utils.database.UpdateTemplate;
import xml.web.services.team2.sciXiv.utils.factory.RDFConnectionFactory;
import xml.web.services.team2.sciXiv.utils.factory.XMLConnectionPropertiesFactory;
import xml.web.services.team2.sciXiv.utils.xslt.DOMToXMLTransformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

@Repository
public class ScientificPublicationRepository {

    private static final String collectionName = "/db/sciXiv/scientificPublications";

    private static final String SPARQL_NAMED_GRAPH_URI = "/scientificPublication/metadata";

    @Autowired
    UpdateTemplate updateService;

    @Autowired
    XMLConnectionPropertiesFactory xmlConnectionPool;

    @Autowired
    RDFConnectionFactory rdfConnectionPool;

    @Autowired
    BasicOperations basicOperations;

    @Autowired
    DOMToXMLTransformer transformer;

    public String findByNameAndVersion(String name, int version) throws DocumentLoadingFailedException, XMLDBException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
		name = name.replace(" ", "");
		if (version == -1) {
		    version = getLastVersionNumber(name);
        }
        Document document = (Document) basicOperations
                .loadDocument(collectionName + "/" + name, name + "-v" + version, conn)
                .getContentAsDOM();
        String xmlEntity = transformer.toXML(document);
        xmlConnectionPool.releaseConnection(conn);

        return xmlEntity;
    }

    public String save(String xmlEntity, String title, String name) throws DocumentStoringFailedException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        basicOperations.storeDocument(collectionName + "/" + title, name, xmlEntity, conn);
        xmlConnectionPool.releaseConnection(conn);

        return name;
    }

    public void withdraw(String title)
            throws XMLDBException, DocumentLoadingFailedException, DocumentStoringFailedException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        int lastVersion = getLastVersionNumber(title);
        String name = title + "-v" + lastVersion;
        Document document = (Document) basicOperations.loadDocument(collectionName + "/" + title, name, conn)
                .getContentAsDOM();
        NodeList nodeList = document.getDocumentElement().getElementsByTagName("sp:status");
        Node status = nodeList.item(0);
        status.setTextContent("withdrawn");

        delete(title, name, conn);
        String xmlEntity = transformer.toXML(document);
        save(xmlEntity, title, name);

        String resourceName = document.getDocumentElement().getAttribute("about");
        insertMetadata(resourceName, "creativeWorkStatus", "withdrawn");

        xmlConnectionPool.releaseConnection(conn);
    }

    public void accept(String title)
            throws XMLDBException, DocumentLoadingFailedException, DocumentStoringFailedException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        int lastVersion = getLastVersionNumber(title);
        String name = title + "-v" + lastVersion;
        Document document = (Document) basicOperations.loadDocument(collectionName + "/" + title, name, conn)
                .getContentAsDOM();
        NodeList nodeList = document.getDocumentElement().getElementsByTagName("sp:status");
        Node status = nodeList.item(0);
        status.setTextContent("accepted");

        delete(title, name, conn);
        String xmlEntity = transformer.toXML(document);
        save(xmlEntity, title, name);

        String resourceName = document.getDocumentElement().getAttribute("about");
        insertMetadata(resourceName, "creativeWorkStatus", "accepted");

        xmlConnectionPool.releaseConnection(conn);
    }

    public void reject(String title)
            throws XMLDBException, DocumentLoadingFailedException, DocumentStoringFailedException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        int lastVersion = getLastVersionNumber(title);
        String name = title + "-v" + lastVersion;
        Document document = (Document) basicOperations.loadDocument(collectionName + "/" + title, name, conn)
                .getContentAsDOM();
        NodeList nodeList = document.getDocumentElement().getElementsByTagName("sp:status");
        Node status = nodeList.item(0);
        status.setTextContent("rejected");

        delete(title, name, conn);
        String xmlEntity = transformer.toXML(document);
        save(xmlEntity, title, name);

        String resourceName = document.getDocumentElement().getAttribute("about");
        insertMetadata(resourceName, "creativeWorkStatus", "rejected");

        xmlConnectionPool.releaseConnection(conn);
    }

    public void saveMetadata(String metadata) {
        RDFConnection connection = rdfConnectionPool.getConnection();
        RDFConnectionProperties conn = rdfConnectionPool.getConnectionProperties();
        Model model = ModelFactory.createDefaultModel();
        model.read(new ByteArrayInputStream(metadata.getBytes()), null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        model.write(out, SparqlUtil.NTRIPLES);

        String sparqlUpdate = SparqlUtil.insertData(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI,
                new String(out.toByteArray()));

        UpdateRequest update = UpdateFactory.create(sparqlUpdate);

        UpdateProcessor processor = UpdateExecutionFactory.createRemote(update, conn.getUpdateEndpoint());
        processor.execute();

        rdfConnectionPool.releaseConnection(connection);
    }

    public int getLastVersionNumber(String collection) throws XMLDBException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        Collection col = basicOperations.getOrCreateCollection(collectionName + "/" + collection, 0, conn);
        int lastVersion = col.getResourceCount();
        xmlConnectionPool.releaseConnection(conn);

        return lastVersion;
    }

    public ArrayList<SciPubDTO> basicSearch(String parameter, TUser user) throws XMLDBException, UnsupportedEncodingException {
        XMLConnectionProperties connXML = xmlConnectionPool.getConnection();
        Collection col = basicOperations.getOrCreateCollection(collectionName, 0, connXML);
        ArrayList<SciPubDTO> sciPubs = new ArrayList<>();
        boolean ownPub = false;

        String[] docCollections = col.listChildCollections();
        for (String docCollection : docCollections) {
            if (user != null) {
                for(String title : user.getOwnPublications().getPublicationID()) {
                    String temp = URLDecoder.decode(title, "UTF-8").replace(" ", "");
                    if (temp.equals(docCollection)) {
                        ownPub = true;
                        getTitlesAndAuthorsBasicSearch(docCollection, connXML, sciPubs, parameter, true);
                        break;
                    }
                }
                if (!ownPub) {
                    getTitlesAndAuthorsBasicSearch(docCollection, connXML, sciPubs, parameter, false);
                }
                ownPub = false;
            } else {
                getTitlesAndAuthorsBasicSearch(docCollection, connXML, sciPubs, parameter, false);
            }
        }

        xmlConnectionPool.releaseConnection(connXML);
        return sciPubs;
    }

    public ArrayList<SciPubDTO> advancedSearch(String query, TUser user) {
        RDFConnection connection = rdfConnectionPool.getConnection();

        RDFConnectionProperties conn = rdfConnectionPool.getConnectionProperties();
        ArrayList<SciPubDTO> sciPubs = null;
        try {
            sciPubs = getTitlesAndAuthors(query, connection, conn, user,true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        rdfConnectionPool.releaseConnection(connection);
        return sciPubs;
    }

    public ArrayList<SciPubDTO> getReferences(String title) {
        RDFConnection connection = rdfConnectionPool.getConnection();

        RDFConnectionProperties conn = rdfConnectionPool.getConnectionProperties();
        String resourceName = "";
        try {
            resourceName = "<http://ftn.uns.ac.rs/scientificPublication/" + URLEncoder.encode(title, "UTF-8") + ">";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String sparqlQuery = SparqlUtil.selectData(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI,
                "?sciPub <https://schema.org/citation> " + resourceName + " .");
        ArrayList<SciPubDTO> sciPubs = null;
        try {
            sciPubs = getTitlesAndAuthors(sparqlQuery, connection, conn, null, false);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        rdfConnectionPool.releaseConnection(connection);

        return sciPubs;
    }

    private ArrayList<SciPubDTO> getTitlesAndAuthors(String sparqlQuery, RDFConnection connection, RDFConnectionProperties conn, TUser user, boolean formatting) throws UnsupportedEncodingException {
        QueryExecution queryExecution;
        if (formatting) {
            queryExecution = executeSparqlQuery(sparqlQuery, connection, conn);
        }
        else {
            queryExecution = connection.query(sparqlQuery);
        }
        ResultSet results = queryExecution.execSelect();
        QueryExecution tempQueryExecution;
        ResultSet tempResults;
        ArrayList<SciPubDTO> sciPubs = new ArrayList<>();
        ArrayList<String> authors = new ArrayList<>();
        String sciPub = "sciPub";
        String name = "name";
        String query, title;
        QuerySolution querySolution;
        SciPubDTO sciPubDTO;

        while (results.hasNext()) {
            sciPubDTO = new SciPubDTO();
            querySolution = results.next();
            title = querySolution.get(sciPub).toString();
            query = SparqlUtil.selectDataWithAlias(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI, "<" + title + ">"
                    + " <https://schema.org/author> ?author .\n" + "\t?author <https://schema.org/name> ?nameAuthor .\n",
                    "?author", "?pred", "?nameAuthor", "?name");
            tempQueryExecution = connection.query(query);
            tempResults = tempQueryExecution.execSelect();
            while (tempResults.hasNext()) {
                authors.add(tempResults.next().get(name).toString());
            }

            try {
                title = URLDecoder.decode(title, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            title = title.replace("http://ftn.uns.ac.rs/scientificPublication/","");

            if (user != null) {
                String encodedTitle = URLEncoder.encode(title, "UTF-8");
                for (String pubTitle : user.getOwnPublications().getPublicationID()) {
                    if (pubTitle.equals(encodedTitle)) {
                        sciPubDTO.setOwnPublication(true);
                    }
                }
            }

            sciPubDTO.setTitle(title);
            sciPubDTO.setAuthors(new ArrayList<>(authors));
            sciPubs.add(sciPubDTO);
            authors.clear();
            tempQueryExecution.close();
        }

        queryExecution.close();
        rdfConnectionPool.releaseConnection(connection);

        return sciPubs;
    }

    private void getTitlesAndAuthorsBasicSearch(String docCollection, XMLConnectionProperties connXML, ArrayList<SciPubDTO> sciPubs, String parameter, boolean ownPublication)
            throws XMLDBException {
        int lastVersion = getLastVersionNumber(docCollection);
        Collection docCol = basicOperations.getOrCreateCollection(collectionName + "/" + docCollection, 0, connXML);
        String docName = docCollection + "-v" + lastVersion;
        SciPubDTO sciPub = new SciPubDTO();
        ArrayList<String> authors = new ArrayList<>();
        String name = "name";
        XPathQueryService xPathService = (XPathQueryService) docCol.getService("XPathQueryService", "1.0");
        xPathService.setProperty("indent", "yes");
        String xQuery = "declare namespace sp = \"http://ftn.uns.ac.rs/scientificPublication\"; ";
        String doc = "doc(\"" + docName + "\")";
        if (ownPublication) {
            xQuery += "if (" + doc + "/node()[contains(., \"" + parameter + "\")]) then " + doc + "/node()"
                    + "/sp:metadata/sp:title/text() else \"\"";
        } else {
            xQuery += "if (" + doc + "/node()[contains(., \"" + parameter + "\")] and " + doc
                    + "//sp:status[text()=\"accepted\"]) " + "then " + doc + "/node()"
                    + "/sp:metadata/sp:title/text() else \"\"";
        }
        sciPub.setOwnPublication(ownPublication);
        ResourceSet result = xPathService.query(xQuery);

        ResourceIterator i = result.getIterator();
        String title = (String) i.nextResource().getContent();
        String resourceName = "";
        RDFConnection connection = rdfConnectionPool.getConnection();
        RDFConnectionProperties connRDF = rdfConnectionPool.getConnectionProperties();
        QueryExecution queryExecution = null;

        if (!title.equalsIgnoreCase("")) {
            try {
                resourceName = "<http://ftn.uns.ac.rs/scientificPublication/" + URLEncoder.encode(title, "UTF-8") + ">";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String sparqlQuery = SparqlUtil.selectDataWithAlias(connRDF.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI, resourceName
                    + " <https://schema.org/author> ?author .\n" + "\t?author <https://schema.org/name> ?authorName .\n",
                    "?author", "?pred", "?authorName", "?name");
            queryExecution = executeSparqlQuery(sparqlQuery, connection, connRDF);
            ResultSet tempResults = queryExecution.execSelect();
            while (tempResults.hasNext()) {
                authors.add(tempResults.next().get(name).toString());
            }

            sciPub.setTitle(title);
            sciPub.setAuthors(authors);
            sciPubs.add(sciPub);
        }

        if (queryExecution != null)
            queryExecution.close();

    }

    private QueryExecution executeSparqlQuery(String query, RDFConnection connection, RDFConnectionProperties conn) {
        String completeQuery = String.format(query, conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI);

        return connection.query(completeQuery);
    }

    private void delete(String title, String name, XMLConnectionProperties conn) throws XMLDBException {
        Collection col = basicOperations.getOrCreateCollection(collectionName + "/" + title, 0, conn);
        Resource resource = col.getResource(name);
        col.removeResource(resource);
    }

    public void insertMetadata(String resourceName, String pred, String object) {
        RDFConnectionProperties conn = rdfConnectionPool.getConnectionProperties();
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("pred", "http://schema.org/");
        org.apache.jena.rdf.model.Resource resource = model.createResource(resourceName);
        Property property = model.createProperty("http://schema.org/", pred);
        Literal literal = model.createLiteral(object);
        Statement statement = model.createStatement(resource, property, literal);
        model.add(statement);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        model.write(stream, SparqlUtil.NTRIPLES);

        String sparqlUpdate = SparqlUtil.insertData(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI,
                new String(stream.toByteArray()));
        UpdateRequest update = UpdateFactory.create(sparqlUpdate);

        UpdateProcessor processor = UpdateExecutionFactory.createRemote(update, conn.getUpdateEndpoint());
        processor.execute();
    }

    public SearchPublicationsDTO getPublicationsMetadata(String title) {
        RDFConnection connection = rdfConnectionPool.getConnection();
        RDFConnectionProperties conn = rdfConnectionPool.getConnectionProperties();
        String resourceName = "";
        try {
            resourceName = "<http://ftn.uns.ac.rs/scientificPublication/" + URLEncoder.encode(title, "UTF-8") + ">";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String sparqlQuery = SparqlUtil.selectDataWithAlias(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI,
                resourceName + "?pred ?object .", "?subj", "?pred", "?object", "?obj");
        QueryExecution queryExecution = connection.query(sparqlQuery);
        ResultSet results = queryExecution.execSelect();
        SearchPublicationsDTO metadata = new SearchPublicationsDTO();
        QuerySolution querySolution;

        while (results.hasNext()) {
            querySolution = results.next();
            extractMetadata(querySolution, metadata, connection, conn);
        }

        queryExecution.close();
        rdfConnectionPool.releaseConnection(connection);

        return metadata;
    }

    private void extractMetadata(QuerySolution querySolution, SearchPublicationsDTO metadata, RDFConnection connection,
                                 RDFConnectionProperties conn) {
        String obj = querySolution.get("obj").toString();
        String pred = querySolution.get("pred").toString();
        String query;
        ResultSet tempResults;
        if (pred.contains("author")) {
            query = SparqlUtil.selectDataWithAlias(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI, "<" + obj + ">" +
                    " <https://schema.org/name> ?nameAuthor .", "?subj", "?pred", "?nameAuthor", "?name");
            QueryExecution queryExecution = connection.query(query);
            tempResults = queryExecution.execSelect();
            querySolution = tempResults.next();
            metadata.getAuthors().add(querySolution.get("name").toString());
            queryExecution.close();

            query = SparqlUtil.selectDataWithAlias(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI, "<" + obj + ">" +
                    " <https://schema.org/affiliation> ?affiliationAuthor .", "?subj", "?pred", "?affiliationAuthor", "?affiliation");
            queryExecution = connection.query(query);
            tempResults = queryExecution.execSelect();
            querySolution = tempResults.next();
            metadata.getAuthorsAffiliations().add(querySolution.get("affiliation").toString());
            queryExecution.close();
        } else if (pred.contains("keywords")) {
            metadata.getKeywords().add(obj);
        } else if (pred.contains("dateCreated")) {
            metadata.setDateReceived(obj);
        } else if (pred.contains("dateModified")) {
            metadata.setDateRevised(obj);
        } else if (pred.contains("datePublished")) {
            metadata.setDateAccepted(obj);
        } else if (pred.contains("headline")) {
            metadata.setTitle(obj);
        }
    }
}
