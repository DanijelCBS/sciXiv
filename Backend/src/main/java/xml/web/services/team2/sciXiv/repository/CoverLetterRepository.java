package xml.web.services.team2.sciXiv.repository;

import java.io.IOException;

import org.exist.xmldb.EXistResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.utils.connection.XMLConnectionProperties;
import xml.web.services.team2.sciXiv.utils.database.BasicOperations;
import xml.web.services.team2.sciXiv.utils.database.DBExtractor;
import xml.web.services.team2.sciXiv.utils.database.UpdateTemplate;
import xml.web.services.team2.sciXiv.utils.dom.DOMParser;
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

	public String findById(String id) throws DocumentLoadingFailedException, XMLDBException, IOException {
		String coverLetterStr = "";
		String xPath = "//coverLetter[@id=\"" + id + "\"]";

		// ResourceSet is a container for a set of resources. Generally a ResourceSet is
		// obtained as the result of a query.
		ResourceSet resourceSet = DBExtractor.executeXPathQuery(collectionName, xPath,
				DBExtractor.getTARGET_NAMESPACE());

		if (resourceSet == null) {
			return coverLetterStr;
		}

		// ResourceIterator is used to iterate over a set of resources.
		ResourceIterator rit = resourceSet.getIterator();

		// Provides access to XML resources stored in the database. An XMLResource can
		// be
		// accessed either as text XML or via the DOM or SAX APIs.The default behaviour
		// for getContent and setContent is to work with XML data as text so these
		// methods work on String content.
		XMLResource xmlResource = null;

		// Returns true as long as there are still more resources to be iterated.
		while (rit.hasMoreResources()) {
			try {
				// Returns the next Resource instance in the iterator.
				xmlResource = (XMLResource) rit.nextResource();

				// Retrieves the content from the resource. The type of the content
				// varies depending what type of resource is being used.
				coverLetterStr = xmlResource.getContent().toString();

				return coverLetterStr;
			} finally {
				try {
					((EXistResource) xmlResource).freeResources();
				} catch (XMLDBException xmldbe) {
					xmldbe.printStackTrace();
				}
			}
		}
		return null;
	}

	public String save(String coverLetter) {
		
	}

	private void delete(String id) throws XMLDBException {
		String xPath = "/coverLetters";

	}

}
