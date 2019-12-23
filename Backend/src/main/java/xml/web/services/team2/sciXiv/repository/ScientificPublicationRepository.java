package xml.web.services.team2.sciXiv.repository;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.utils.connection.RDFConnectionProperties;
import xml.web.services.team2.sciXiv.utils.connection.XMLConnectionProperties;
import xml.web.services.team2.sciXiv.utils.database.BasicOperations;
import xml.web.services.team2.sciXiv.utils.database.SparqlUtil;
import xml.web.services.team2.sciXiv.utils.factory.RDFConnectionPropertiesFactory;
import xml.web.services.team2.sciXiv.utils.factory.XMLConnectionPropertiesFactory;

import java.io.ByteArrayOutputStream;

@Repository
public class ScientificPublicationRepository {

    private static final String collectionName = "/db/sciXiv/scientificPublications";

    private static final String SPARQL_NAMED_GRAPH_URI = "/scientificPublication/metadata";

    @Autowired
    private BasicOperations basicOperations;

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

    public String save(String xmlEntity, String name) throws DocumentStoringFailedException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        basicOperations.storeDocument(collectionName, name, xmlEntity, conn);
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

    public void delete(String name) throws XMLDBException {
        XMLConnectionProperties conn = xmlConnectionPool.getConnection();
        Collection col = basicOperations.getOrCreateCollection(collectionName, 0, conn);

        Resource resource = col.getResource(name);
        col.removeResource(resource);

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
}
