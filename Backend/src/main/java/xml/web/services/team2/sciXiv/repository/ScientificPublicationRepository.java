package xml.web.services.team2.sciXiv.repository;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.exist.xmldb.EXistResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;
import xml.web.services.team2.sciXiv.dto.SciPubDTO;
import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.utils.connection.RDFConnectionProperties;
import xml.web.services.team2.sciXiv.utils.connection.XMLConnectionProperties;
import xml.web.services.team2.sciXiv.utils.database.BasicOperations;
import xml.web.services.team2.sciXiv.utils.database.SparqlUtil;
import xml.web.services.team2.sciXiv.utils.database.UpdateTemplate;
import xml.web.services.team2.sciXiv.utils.factory.RDFConnectionPropertiesFactory;
import xml.web.services.team2.sciXiv.utils.factory.XMLConnectionPropertiesFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

@Repository
public class ScientificPublicationRepository {

    private static final String collectionName = "/db/sciXiv/scientificPublications";

    private static final String SPARQL_NAMED_GRAPH_URI = "/scientificPublication/metadata";

    @Autowired
    private BasicOperations basicOperations;

    @Autowired
    UpdateTemplate updateService;

    @Autowired
    XMLConnectionPropertiesFactory xmlConnectionPool;

    @Autowired
    RDFConnectionPropertiesFactory rdfConnectionPool;

    public String findByName(String name) throws DocumentLoadingFailedException, XMLDBException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        XMLResource resource = basicOperations.loadDocument(collectionName, name, conn);
        xmlConnectionPool.releaseConnection(conn);

        return resource.getContent().toString();
    }

    public String save(String xmlEntity, String title, String name) throws DocumentStoringFailedException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        basicOperations.storeDocument(collectionName + "/" + title, name, xmlEntity, conn);
        xmlConnectionPool.releaseConnection(conn);

        return name;
    }

    public String update(String xmlEntity, String name) throws XMLDBException, DocumentStoringFailedException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        Collection col = basicOperations.getOrCreateCollection(collectionName, 0, conn);

        Resource resource = col.getResource(name);
        col.removeResource(resource);

        basicOperations.storeDocument(collectionName, name, xmlEntity, conn);
        xmlConnectionPool.releaseConnection(conn);

        return name;
    }

    public void delete(String title, String name) throws XMLDBException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        Collection col = basicOperations.getOrCreateCollection(collectionName + "/" + title, 0, conn);

        Resource resource = col.getResource(name);
        col.removeResource(resource);

        xmlConnectionPool.releaseConnection(conn);
    }

    public void withdraw(String title) throws XMLDBException, DocumentLoadingFailedException, DocumentStoringFailedException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        int lastVersion = getLastVersionNumber(title);
        String name = title + "/v" + lastVersion;
        Document document = (Document) basicOperations.loadDocument(title, name, conn).getContentAsDOM();
        document.getElementsByTagName("status").item(0).setNodeValue("withdrawn");

        delete(title, name);
        String xmlEntity = document.getTextContent();
        save(xmlEntity, title, name);

        xmlConnectionPool.releaseConnection(conn);
    }

    public void saveMetadata(String metadata) {
        RDFConnectionProperties conn = rdfConnectionPool.getConnection();
        Model model = ModelFactory.createDefaultModel();
        model.read(metadata);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        model.write(out, SparqlUtil.NTRIPLES);

        String sparqlUpdate = SparqlUtil.insertData(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI, new String(out.toByteArray()));

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

    public ArrayList<SciPubDTO> basicSearch(String parameter) throws XMLDBException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        Collection col = basicOperations.getOrCreateCollection(collectionName, 0, conn);
        Collection docCol;
        ArrayList<SciPubDTO> sciPubs = new ArrayList<>();
        String docName;
        String title;
        ArrayList<String> authors = new ArrayList<>();
        int lastVersion;
        XPathQueryService xPathService;

        String[] docCollections = col.listChildCollections();
        for (String docCollection : docCollections) {
            lastVersion = getLastVersionNumber(docCollection);
            docCol = basicOperations.getOrCreateCollection(docCollection, 0, conn);
            docName = docCollection + "/v" + lastVersion;
            xPathService = (XPathQueryService) docCol.getService("XPathQueryService", "1.0");
            xPathService.setProperty("indent", "yes");
            ResourceSet result = xPathService.query("doc(" + docName + ")//title[//*[text()[contains(.," + parameter + ")] " +
                    "|| //author/name[//*[text()[contains(.," + parameter + ")]");
            ResourceIterator i = result.getIterator();
            title = (String) i.nextResource().getContent();
            Resource res = null;

            while(i.hasMoreResources()) {
                try {
                    res = i.nextResource();
                    authors.add((String) res.getContent());
                } finally {
                    try {
                        ((EXistResource)res).freeResources();
                    } catch (XMLDBException xe) {
                        xe.printStackTrace();
                    }
                }
            }

            sciPubs.add(new SciPubDTO(title, authors));
        }

        xmlConnectionPool.releaseConnection(conn);
        return sciPubs;
    }

    public ArrayList<SciPubDTO> advancedSearch(String query) throws XMLDBException {
        RDFConnectionProperties conn = rdfConnectionPool.getConnection();
        ResultSet results = executeSparqlQuery(conn, query);
        ResultSet tempResults;
        ArrayList<SciPubDTO> sciPubs = new ArrayList<>();
        ArrayList<String> authors = new ArrayList<>();
        String title, resourceName, sparqlQuery;
        String sciPub = "sciPub";
        String author = "author";
        QuerySolution querySolution;

        while(results.hasNext()) {
            querySolution = results.next();
            resourceName = querySolution.get(sciPub).toString();
            title = resourceName.substring(resourceName.lastIndexOf('/') + 1);
            sparqlQuery = SparqlUtil.selectData(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI,
                    resourceName + " <http://schema.org/author> ?author .");
            tempResults = executeSparqlQuery(conn, sparqlQuery);
            while(tempResults.hasNext()) {
                authors.add(results.next().get(author).toString());
            }

            sciPubs.add(new SciPubDTO(title, authors));
        }

        rdfConnectionPool.releaseConnection(conn);
        return sciPubs;
    }

    private ResultSet executeSparqlQuery(RDFConnectionProperties conn, String query) {
        String completeQuery = String.format(query, conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI);
        QueryExecution sparqlQuery = QueryExecutionFactory.sparqlService(conn.getQueryEndpoint(), completeQuery);

        return sparqlQuery.execSelect();
    }
}
