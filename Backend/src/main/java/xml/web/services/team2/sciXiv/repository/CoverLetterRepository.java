package xml.web.services.team2.sciXiv.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.utils.connection.XMLConnectionProperties;
import xml.web.services.team2.sciXiv.utils.database.BasicOperations;
import xml.web.services.team2.sciXiv.utils.database.UpdateTemplate;
import xml.web.services.team2.sciXiv.utils.factory.RDFConnectionPropertiesFactory;
import xml.web.services.team2.sciXiv.utils.factory.XMLConnectionPropertiesFactory;
import xml.web.services.team2.sciXiv.utils.xslt.DOMToXMLTransformer;

@Repository
public class CoverLetterRepository {

	private static final String collectionName = "/db/sciXiv/coverLetters";

	private static final String SPARQL_NAMED_GRAPH_URI = "/coverLetter/metadata";

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

	public String findById(String id) throws DocumentLoadingFailedException, XMLDBException {
		String coverLetterStr = "";
		String xPath = "//coverLetter[@id=\"" + id + "\"]";

		// ResourceSet is a container for a set of resources. Generally a ResourceSet is
		// obtained as the result of a query.
		ResourceSet resourceSet;
	}

	public String save(String xmlEntity, String title, String name) throws DocumentStoringFailedException {
		XMLConnectionProperties conn = xmlConnectionPool.getConnection();
		basicOperations.storeDocument(collectionName + "/" + title, name, xmlEntity, conn);
		xmlConnectionPool.releaseConnection(conn);

		return name;
	}

	private void delete(String id) throws XMLDBException {
		String xPath = "/coverLetters";

	}

}
