package xml.web.services.team2.sciXiv.repository;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.exist.xmldb.EXistResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Repository;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.utils.connection.XMLConnectionProperties;
import xml.web.services.team2.sciXiv.utils.database.BasicOperations;
import xml.web.services.team2.sciXiv.utils.database.DBExtractor;
import xml.web.services.team2.sciXiv.utils.database.UpdateTemplate;
import xml.web.services.team2.sciXiv.utils.dom.DOMParser;
import xml.web.services.team2.sciXiv.utils.factory.RDFConnectionFactory;
import xml.web.services.team2.sciXiv.utils.factory.XMLConnectionPropertiesFactory;
import xml.web.services.team2.sciXiv.utils.xslt.DOMToXMLTransformer;

@Repository
public class CoverLetterRepository {

	private static final String collectionName = "/db/sciXiv/coverLetters";

	private static final String coverLetterXsdSchemaPath = "src/main/resources/static/xmlSchemas/coverLetter.xsd";

	public static final String coverLetterXSLPath = "src/main/resources/static/xsl/coverLetter.xsl";

	public static final String coverLetterXSLFOPath = "src/main/resources/static/xslfo/coverLetter.xsl";

	private static final String SPARQL_NAMED_GRAPH_URI = "/coverLetter/metadata";

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

	@Autowired
	ScientificPublicationRepository scientificPublicationRepository;

	// findByTitleAndVersion
	public String findByTitleAndVersion(String title, String version)
			throws DocumentLoadingFailedException, XMLDBException, IOException {
		String coverLetterStr = null;
		String xPath = "//coverLetter[/publicationTitle=\"" + title + "\" and /version=" + version + "]";

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

	public String findById(String id) throws DocumentLoadingFailedException, XMLDBException, IOException {
		String coverLetterStr = null;
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

	public String save(String coverLetter) throws SAXException, ParserConfigurationException, IOException,
			TransformerException, DocumentStoringFailedException, XMLDBException {
		Document document = DOMParser.buildDocument(coverLetter, coverLetterXsdSchemaPath);
		String lUUID = String.format("%d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
		String id = "cl" + lUUID;
		document.getElementsByTagName("coverLetter").item(0).getAttributes().getNamedItem("id").setTextContent(id);

		// izvucem title rada, pozovem metodu getLastVersionNumber(prosljedis ime rada
		// tj pubTitle)
		String publicationTitle = document.getElementsByTagName("publicationTitle").item(0).getTextContent();
		int latestVersion = scientificPublicationRepository.getLastVersionNumber(publicationTitle);

		document.getElementsByTagName("version").item(0).setTextContent(new Integer(latestVersion).toString());

		String saveCoverLetter = DOMParser.doc2String(document);

		XMLConnectionProperties conn = xmlConnectionPool.getConnection();
		basicOperations.storeDocument(collectionName + "/", id, saveCoverLetter, conn);
		xmlConnectionPool.releaseConnection(conn);

		return id;
	}

	public String update(String coverLetter) throws SAXException, ParserConfigurationException, IOException,
			DocumentLoadingFailedException, XMLDBException, DocumentStoringFailedException {
		Document document = DOMParser.buildDocument(coverLetter, coverLetterXsdSchemaPath);
		String id = document.getDocumentElement().getAttribute("id");

		String oldCoverLetter = findById(id);

		if (oldCoverLetter == null) {
			throw new ResourceNotFoundException("ResourceNotFoundException; Cover letter with [id: " + id + "]");
		}

		XMLConnectionProperties conn = xmlConnectionPool.getConnection();

		delete(id);

		basicOperations.storeDocument(collectionName + "/", id, coverLetter, conn);
		xmlConnectionPool.releaseConnection(conn);

		return id;
	}

	public void delete(String id, XMLConnectionProperties conn) throws XMLDBException {
		Collection col = basicOperations.getOrCreateCollection(collectionName + "/", 0, conn);
		Resource resource = col.getResource(id);
		col.removeResource(resource);
	}

	public void delete(String id) throws XMLDBException {
		XMLConnectionProperties conn = xmlConnectionPool.getConnection();
		delete(id, conn);
		xmlConnectionPool.releaseConnection(conn);
	}
}
