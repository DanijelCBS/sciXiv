package xml.web.services.team2.sciXiv.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.utils.connection.XMLConnectionProperties;
import xml.web.services.team2.sciXiv.utils.database.BasicOperations;
import xml.web.services.team2.sciXiv.utils.factory.XMLConnectionPropertiesFactory;

@Repository
public class ScientificPublicationRepository {

    private static String collectionName = "/db/sciXiv/scientificPublications";

    @Autowired
    private BasicOperations basicOperations;

    @Autowired
    XMLConnectionPropertiesFactory connectionPool;

    public String findByName(String name) throws DocumentLoadingFailedException, XMLDBException {
        XMLConnectionProperties conn = connectionPool.getConnection();
        XMLResource resource = basicOperations.loadDocument(collectionName, name, conn);
        connectionPool.releaseConnection(conn);

        return resource.getContent().toString();
    }

    public String save(String xmlEntity, String name) throws DocumentStoringFailedException {
        XMLConnectionProperties conn = connectionPool.getConnection();
        basicOperations.storeDocument(collectionName, name, xmlEntity, conn);
        connectionPool.releaseConnection(conn);

        return name;
    }

    public String update(String xmlEntity, String name) throws XMLDBException, DocumentStoringFailedException {
        XMLConnectionProperties conn = connectionPool.getConnection();
        Collection col = basicOperations.getOrCreateCollection(collectionName, 0, conn);

        Resource resource = col.getResource(name);
        col.removeResource(resource);

        basicOperations.storeDocument(collectionName, name, xmlEntity, conn);
        connectionPool.releaseConnection(conn);

        return name;
    }

    public void delete(String name) throws XMLDBException {
        XMLConnectionProperties conn = connectionPool.getConnection();
        Collection col = basicOperations.getOrCreateCollection(collectionName, 0, conn);

        Resource resource = col.getResource(name);
        col.removeResource(resource);

        connectionPool.releaseConnection(conn);
    }
}
