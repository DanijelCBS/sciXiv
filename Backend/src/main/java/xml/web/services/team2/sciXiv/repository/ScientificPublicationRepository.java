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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;
import xml.web.services.team2.sciXiv.dto.SciPubDTO;
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
    private BasicOperations basicOperations;

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

    public void withdraw(String title) throws XMLDBException, DocumentLoadingFailedException, DocumentStoringFailedException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        int lastVersion = getLastVersionNumber(title);
        String name = title + "/v" + lastVersion;
        Document document = (Document) basicOperations.loadDocument(title, name, conn).getContentAsDOM();
        document.getElementsByTagName("status").item(0).setNodeValue("withdrawn");

        delete(title, name, conn);
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

        String sparqlUpdate = SparqlUtil
                .insertData(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI, new String(out.toByteArray()));

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
        RDFConnectionProperties connRDF = rdfConnectionPool.getConnection();
        Collection col = basicOperations.getOrCreateCollection(collectionName, 0, connXML);
        ArrayList<SciPubDTO> sciPubs = new ArrayList<>();
        String xPathQuery;

        String[] docCollections = col.listChildCollections();
        for (String docCollection : docCollections) {
            if (user.getOwnPublications().getPublicationID().contains(docCollection)) {
                xPathQuery = "//title[//*[text()[contains(.," + parameter + ")]]]";
            } else {
                xPathQuery = "//title[//*[text()[contains(.," + parameter + ")]] and //status[text()='accepted']]";
            }
            getTitlesAndAuthorsBasicSearch(docCollection, connXML, connRDF, sciPubs, xPathQuery);
        }

        rdfConnectionPool.releaseConnection(connRDF);
        xmlConnectionPool.releaseConnection(connXML);
        return sciPubs;
    }

    public ArrayList<SciPubDTO> advancedSearch(String query) {
        RDFConnectionProperties conn = rdfConnectionPool.getConnection();
        ArrayList<SciPubDTO> sciPubs = getTitlesAndAuthors(conn, query);
        rdfConnectionPool.releaseConnection(conn);
        return sciPubs;
    }


    public ArrayList<SciPubDTO> getReferences(String title) {
        RDFConnectionProperties conn = new RDFConnectionProperties();
        String sparqlQuery = SparqlUtil.selectData(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI,
                "?sciPub <http://schema.org/citation> " + title + " .");
        ArrayList<SciPubDTO> sciPubs = getTitlesAndAuthors(conn, sparqlQuery);
        rdfConnectionPool.releaseConnection(conn);

        return sciPubs;
    }

    private ArrayList<SciPubDTO> getTitlesAndAuthors(RDFConnectionProperties conn, String sparqlQuery) {
        ResultSet results = executeSparqlQuery(conn, sparqlQuery);
        ResultSet tempResults;
        ArrayList<SciPubDTO> sciPubs = new ArrayList<>();
        ArrayList<String> authors = new ArrayList<>();
        String sciPub = "sciPub";
        String author = "author";
        String query, title;
        QuerySolution querySolution;

        while (results.hasNext()) {
            querySolution = results.next();
            title = querySolution.get(sciPub).toString();
            query = SparqlUtil.selectData(conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI,
                    title + " <http://schema.org/author> ?author .");
            tempResults = executeSparqlQuery(conn, query);
            while (tempResults.hasNext()) {
                authors.add(results.next().get(author).toString());
            }

            sciPubs.add(new SciPubDTO(title, authors));
        }

        return sciPubs;
    }

    private void getTitlesAndAuthorsBasicSearch(String docCollection, XMLConnectionProperties connXML,
                                                RDFConnectionProperties connRDF, ArrayList<SciPubDTO> sciPubs,
                                                String query) throws XMLDBException {
        int lastVersion = getLastVersionNumber(docCollection);
        Collection docCol = basicOperations.getOrCreateCollection(docCollection, 0, connXML);
        String docName = docCollection + "/v" + lastVersion;
        ArrayList<String> authors = new ArrayList<>();
        String author = "author";
        XPathQueryService xPathService = (XPathQueryService) docCol.getService("XPathQueryService", "1.0");
        xPathService.setProperty("indent", "yes");
        ResourceSet result = xPathService
                .query("doc(" + docName + ")" + query);
        if (result.getSize() != 0) {
            ResourceIterator i = result.getIterator();
            String title = (String) i.nextResource().getContent();

            String sparqlQuery = SparqlUtil.selectData(connRDF.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI,
                    title + " <http://schema.org/author> ?author .");
            ResultSet tempResults = executeSparqlQuery(connRDF, sparqlQuery);
            while (tempResults.hasNext()) {
                authors.add(tempResults.next().get(author).toString());
            }

            sciPubs.add(new SciPubDTO(title, authors));
        }
    }

    private ResultSet executeSparqlQuery(RDFConnectionProperties conn, String query) {
        String completeQuery = String.format(query, conn.getDataEndpoint() + SPARQL_NAMED_GRAPH_URI);
        QueryExecution sparqlQuery = QueryExecutionFactory.sparqlService(conn.getQueryEndpoint(), completeQuery);

        return sparqlQuery.execSelect();
    }


    private void delete(String title, String name, XMLConnectionProperties conn) throws XMLDBException {
        Collection col = basicOperations.getOrCreateCollection(collectionName + "/" + title, 0, conn);
        Resource resource = col.getResource(name);
        col.removeResource(resource);
    }
}
