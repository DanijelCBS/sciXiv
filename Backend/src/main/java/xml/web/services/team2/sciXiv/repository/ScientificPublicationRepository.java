package xml.web.services.team2.sciXiv.repository;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
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
import xml.web.services.team2.sciXiv.utils.factory.RDFConnectionPropertiesFactory;
import xml.web.services.team2.sciXiv.utils.factory.XMLConnectionPropertiesFactory;
import xml.web.services.team2.sciXiv.utils.xslt.DOMToXMLTransformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    RDFConnectionPropertiesFactory rdfConnectionPool;

    @Autowired
    BasicOperations basicOperations;

    @Autowired
    DOMToXMLTransformer transformer;

    public String findByNameAndVersion(String name, int version) throws DocumentLoadingFailedException, XMLDBException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
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

    public void saveMetadata(String metadata) {
        RDFConnectionProperties conn = rdfConnectionPool.getConnection();
        Model model = ModelFactory.createDefaultModel();
        model.read(new ByteArrayInputStream(metadata.getBytes()), null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        model.write(out, SparqlUtil.NTRIPLES);

        String sparqlUpdate = SparqlUtil.insertData(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI,
                new String(out.toByteArray()));

        UpdateRequest update = UpdateFactory.create(sparqlUpdate);

        UpdateProcessor processor = UpdateExecutionFactory.createRemote(update, conn.getUpdateEndpoint());
        processor.execute();

        rdfConnectionPool.releaseConnection(conn);
    }

    public int getLastVersionNumber(String collection) throws XMLDBException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        Collection col = basicOperations.getOrCreateCollection(collectionName + "/" + collection, 0, conn);
        int lastVersion = col.getResourceCount();
        xmlConnectionPool.releaseConnection(conn);

        return lastVersion;
    }

    public ArrayList<SciPubDTO> basicSearch(String parameter, TUser user) throws XMLDBException {
        XMLConnectionProperties connXML = xmlConnectionPool.getConnection();
        Collection col = basicOperations.getOrCreateCollection(collectionName, 0, connXML);
        ArrayList<SciPubDTO> sciPubs = new ArrayList<>();

        String[] docCollections = col.listChildCollections();
        for (String docCollection : docCollections) {
            if (user.getOwnPublications().getPublicationID().contains(docCollection)) {
                getTitlesAndAuthorsBasicSearch(docCollection, connXML, sciPubs, parameter, true);
            } else {
                getTitlesAndAuthorsBasicSearch(docCollection, connXML, sciPubs, parameter, false);
            }
        }

        xmlConnectionPool.releaseConnection(connXML);
        return sciPubs;
    }

    public ArrayList<SciPubDTO> advancedSearch(String query) {
        //RDFConnectionProperties conn = rdfConnectionPool.getConnection();
        ArrayList<SciPubDTO> sciPubs = getTitlesAndAuthors(query);
        //rdfConnectionPool.releaseConnection(conn);
        System.out.println();
        return sciPubs;
    }

    public ArrayList<SciPubDTO> getReferences(String title) {
        RDFConnectionProperties conn = rdfConnectionPool.getConnection();
        String sparqlQuery = SparqlUtil.selectData(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI,
                "?sciPub <http://schema.org/citation> " + title + " .");
        ArrayList<SciPubDTO> sciPubs = getTitlesAndAuthors(sparqlQuery);
        rdfConnectionPool.releaseConnection(conn);

        return sciPubs;
    }

    private ArrayList<SciPubDTO> getTitlesAndAuthors(String sparqlQuery) {
        RDFConnectionProperties conn = rdfConnectionPool.getConnection();
        RDFConnection connection = RDFConnectionFactory.connectFuseki(conn.getEndpoint() + "/" + conn.getDataset() + "/data", conn.getQueryEndpoint(), conn.getUpdateEndpoint(), conn.getDataEndpoint());
        QueryExecution queryExecution = executeSparqlQuery(sparqlQuery, connection);
        ResultSet results = queryExecution.execSelect();
        QueryExecution tempQueryExecution;
        ResultSet tempResults;
        ArrayList<SciPubDTO> sciPubs = new ArrayList<>();
        ArrayList<String> authors = new ArrayList<>();
        String sciPub = "sciPub";
        String name = "name";
        String query, title;
        QuerySolution querySolution;

        while (results.hasNext()) {
            querySolution = results.next();
            title = querySolution.get(sciPub).toString();
            System.out.println("--");
            query = SparqlUtil.selectDataWithAlias(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI, "<" + title + ">"
                    + " <http://schema.org/author> ?author .\n" + "\t?author <http://schema.org/name> ?nameAuthor .\n",
                    "?author", "?pred", "?nameAuthor", "?name");
            System.out.print("--");
            tempQueryExecution = executeSparqlQuery(query, connection);
            tempResults = tempQueryExecution.execSelect();
            while (tempResults.hasNext()) {
                authors.add(tempResults.next().get(name).toString());
            }

            sciPubs.add(new SciPubDTO(title, new ArrayList<>(authors)));
            authors.clear();
            tempQueryExecution.close();
        }

        queryExecution.close();
        connection.close();
        rdfConnectionPool.releaseConnection(conn);
        return sciPubs;
    }

    private void getTitlesAndAuthorsBasicSearch(String docCollection, XMLConnectionProperties connXML, ArrayList<SciPubDTO> sciPubs, String parameter, boolean ownPublication)
            throws XMLDBException {
        int lastVersion = getLastVersionNumber(docCollection);
        Collection docCol = basicOperations.getOrCreateCollection(collectionName + "/" + docCollection, 0, connXML);
        String docName = docCollection + "-v" + lastVersion;
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
        ResourceSet result = xPathService.query(xQuery);

        ResourceIterator i = result.getIterator();
        String title = (String) i.nextResource().getContent();
        String resourceName;
        RDFConnectionProperties connRDF = rdfConnectionPool.getConnection();

        if (!title.equalsIgnoreCase("")) {
            resourceName = "<http://ftn.uns.ac.rs/scientificPublication/" + title.replace(" ", "") + ">";
            String sparqlQuery = SparqlUtil.selectDataWithAlias(connRDF.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI, resourceName
                    + " <http://schema.org/author> ?author .\n" + "\t?author <http://schema.org/name> ?authorName .\n",
                    "?author", "?pred", "?authorName", "?name");
            rdfConnectionPool.releaseConnection(connRDF);
            QueryExecution qe = executeSparqlQuery(sparqlQuery, null);
            ResultSet tempResults = qe.execSelect();
            while (tempResults.hasNext()) {
                authors.add(tempResults.next().get(name).toString());
            }

            sciPubs.add(new SciPubDTO(title, authors));
        }

    }

    private QueryExecution executeSparqlQuery(String query, RDFConnection connection) {
        RDFConnectionProperties conn = rdfConnectionPool.getConnection();
        String completeQuery = String.format(query, conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI);
        QueryExecution queryExecution = connection.query(completeQuery) ;
        rdfConnectionPool.releaseConnection(conn);

        return queryExecution;
    }

    private void delete(String title, String name, XMLConnectionProperties conn) throws XMLDBException {
        Collection col = basicOperations.getOrCreateCollection(collectionName + "/" + title, 0, conn);
        Resource resource = col.getResource(name);
        col.removeResource(resource);
    }

    public void insertMetadata(String resourceName, String pred, String object) {
        RDFConnectionProperties conn = rdfConnectionPool.getConnection();
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

        rdfConnectionPool.releaseConnection(conn);
    }

    public SearchPublicationsDTO getPublicationsMetadata(String title) {
        RDFConnectionProperties conn = rdfConnectionPool.getConnection();
        String resourceName = "<http://ftn.uns.ac.rs/scientificPublication/" + title + ">";
        String sparqlQuery = SparqlUtil.selectDataWithAlias(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI,
                resourceName + "?pred ?object .", "?subj", "?pred", "?object", "?obj");
        QueryExecution qe = executeSparqlQuery(sparqlQuery, null);
        ResultSet results = qe.execSelect();
        SearchPublicationsDTO metadata = new SearchPublicationsDTO();
        QuerySolution querySolution;

        while (results.hasNext()) {
            querySolution = results.next();
            extractMetadata(querySolution, metadata);
        }

        rdfConnectionPool.releaseConnection(conn);

        return metadata;
    }

    private void extractMetadata(QuerySolution querySolution, SearchPublicationsDTO metadata) {
        String obj = querySolution.get("obj").toString();
        String pred = querySolution.get("pred").toString();
        RDFConnectionProperties conn = rdfConnectionPool.getConnection();
        String query;
        ResultSet tempResults;
        if (pred.contains("author")) {
            query = SparqlUtil.selectDataWithAlias(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI, "<" + obj + ">" +
                    " <http://schema.org/name> ?nameAuthor .", "?subj", "?pred", "?nameAuthor", "?name");
            QueryExecution qe = executeSparqlQuery(query, null);
            tempResults = qe.execSelect();
            querySolution = tempResults.next();
            metadata.getAuthors().add(querySolution.get("name").toString());

            query = SparqlUtil.selectDataWithAlias(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI, "<" + obj + ">" +
                    " <http://schema.org/affiliation> ?affiliationAuthor .", "?subj", "?pred", "?affiliationAuthor", "?affiliation");
            qe = executeSparqlQuery(query, null);
            tempResults = qe.execSelect();
            querySolution = tempResults.next();
            metadata.getAuthorsAffiliations().add(querySolution.get("affiliation").toString());
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

        rdfConnectionPool.releaseConnection(conn);
    }
}
