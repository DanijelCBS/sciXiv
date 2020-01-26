package xml.web.services.team2.sciXiv.repository;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.exist.xmldb.EXistResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;

import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.utils.connection.XMLConnectionProperties;
import xml.web.services.team2.sciXiv.utils.database.BasicOperations;
import xml.web.services.team2.sciXiv.utils.database.DBExtractor;
import xml.web.services.team2.sciXiv.utils.dom.DOMParser;
import xml.web.services.team2.sciXiv.utils.factory.XMLConnectionPropertiesFactory;

@Repository
public class ReviewRepository {
	
	private static String publicationReviewsCollection = "/db/sciXiv/publicationReviews";
	
	@Autowired
    private BasicOperations basicOperations;

    @Autowired
    private XMLConnectionPropertiesFactory connectionPool;
    
    /*
    public List<String> findAllByPublicationAsXml(String reviewedPublicationId) throws XMLDBException {
    	List<String> result = new ArrayList<String>();
    	String reviewsOfPublicationCollectionName = getReviewsOfPublicationCollectionName(reviewedPublicationId);
    	XMLConnectionProperties conn = connectionPool.getConnection();
    	
    	Collection reviewsOfPublicationCollection = basicOperations.getOrCreateCollection(reviewsOfPublicationCollectionName, 0, conn);
    	for (String reviewId : reviewsOfPublicationCollection.listResources()) {
    		XMLResource review = (XMLResource) reviewsOfPublicationCollection.getResource(reviewId);
    		result.add(review.getContent().toString());
		}
    	
    	connectionPool.releaseConnection(conn);
    	return result;
    }
    
    public List<Node> findAllByPublicationAsDom(String reviewedPublicationId) throws XMLDBException {
    	List<Node> result = new ArrayList<Node>();
    	String reviewsOfPublicationCollectionName = getReviewsOfPublicationCollectionName(reviewedPublicationId);
    	XMLConnectionProperties conn = connectionPool.getConnection();
    	
    	Collection reviewsOfPublicationCollection = basicOperations.getOrCreateCollection(reviewsOfPublicationCollectionName, 0, conn);
    	for (String reviewId : reviewsOfPublicationCollection.listResources()) {
    		XMLResource review = (XMLResource) reviewsOfPublicationCollection.getResource(reviewId);
    		result.add(review.getContentAsDOM());
		}
    	
    	connectionPool.releaseConnection(conn);
    	return result;
    }
    
    
    public String save(String xmlEntity, String reviewedPublicationId) throws XMLDBException, DocumentStoringFailedException {
    	String reviewsOfPublicationCollectionName = getReviewsOfPublicationCollectionName(reviewedPublicationId);
    	XMLConnectionProperties conn = connectionPool.getConnection();
    	
    	Collection reviewsOfPublicationCollection = basicOperations.getOrCreateCollection(reviewsOfPublicationCollectionName, 0, conn);
    	int reviewsNum = reviewsOfPublicationCollection.getResourceCount();
    	String reviewId = String.format("review%d.xml", reviewsNum++);
    	basicOperations.storeDocument(reviewsOfPublicationCollectionName, reviewId, xmlEntity, conn);
    	
    	connectionPool.releaseConnection(conn);
    	return reviewId;
    }
    */
    
    public String save(Document reviewDocValid) throws DocumentStoringFailedException, ParserConfigurationException, TransformerException {
    	String id = String.format("rvw%d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
    	reviewDocValid.getElementsByTagName("review").item(0).getAttributes().getNamedItem("id").setTextContent(id);

		String saveReviewXml = DOMParser.doc2String(reviewDocValid);

		XMLConnectionProperties conn = connectionPool.getConnection();
		basicOperations.storeDocument(publicationReviewsCollection, String.format("%s.xml", id), saveReviewXml, conn);
		connectionPool.releaseConnection(conn);

		return id;
    }
    
    public String findById(String id) throws DocumentStoringFailedException, ParserConfigurationException, TransformerException, IOException, XMLDBException, DocumentLoadingFailedException {
    	XMLConnectionProperties conn = connectionPool.getConnection();
    	String reviewDocumentName = String.format("%s.xml", id);
		XMLResource resource = basicOperations.loadDocument(publicationReviewsCollection, reviewDocumentName, conn);
		connectionPool.releaseConnection(conn);
		
		if(resource == null) {
			throw new ResourceNotFoundException(String.format("ResourceNotFoundException; Review with [id: %s]", id));
		}

		return resource.getContent().toString();
    }
    
    public boolean delete(String id, XMLConnectionProperties conn) throws XMLDBException {
		Collection col = basicOperations.getOrCreateCollection(publicationReviewsCollection, 0, conn);
		String reviewDocumentId = String.format("%s.xml", id);
		Resource resource = col.getResource(reviewDocumentId);
		if(resource != null) {
			col.removeResource(resource);
			return true;
		}
		return false;
	}

	public boolean delete(String id) throws XMLDBException {
		XMLConnectionProperties conn = connectionPool.getConnection();
		boolean success = delete(id, conn);
		connectionPool.releaseConnection(conn);
		return success;
	}
	
	public String update(Document updatedReviewValid, String id) throws SAXException, ParserConfigurationException, IOException,
		DocumentLoadingFailedException, XMLDBException, DocumentStoringFailedException, TransformerException {
		//Document document = DOMParser.buildDocument(coverLetter, PATH);
		//String id = document.getDocumentElement().getAttribute("id");
		
		String odlReview = findById(id);
		
		if (odlReview == null) {
			throw new ResourceNotFoundException("ResourceNotFoundException; Review with [id: " + id + "]");
		}
		
		XMLConnectionProperties conn = connectionPool.getConnection();
		
		delete(id, conn);
		
		basicOperations.storeDocument(publicationReviewsCollection, String.format("%s.xml", id), DOMParser.doc2String(updatedReviewValid), conn);
		
		connectionPool.releaseConnection(conn);
		
		return DOMParser.doc2String(updatedReviewValid);
	}
	
	public List<Node> findReviewsOfPublicationAsDomNodes(String publicationTitle, int publicationVersion) throws XMLDBException {
		XMLConnectionProperties conn = connectionPool.getConnection();
		Collection col = basicOperations.getOrCreateCollection(publicationReviewsCollection, 0, conn);
		
		String xQuery = String.format(
				"declare namespace rvw = \"http://ftn.uns.ac.rs/review\";\n"
				+ "collection(\"%s\")//rvw:review[rvw:metadata/rvw:publicationTitle = \"%s\""
				+ " and rvw:metadata/rvw:publicationVersion = %d]",
				publicationReviewsCollection, publicationTitle, publicationVersion);
		
		XPathQueryService xPathService = (XPathQueryService) col.getService("XPathQueryService", "1.0");
		xPathService.setProperty("indent", "yes");
		ResourceSet result = xPathService.query(xQuery);
		
		ResourceIterator rit = result.getIterator();
		XMLResource reviewXmlResource = null;
		List<Node> reviews = new ArrayList<Node>();
		
		while (rit.hasMoreResources()) {
			try {
				reviewXmlResource = (XMLResource) rit.nextResource();
				reviews.add(reviewXmlResource.getContentAsDOM());
			} finally {
				try {
					((EXistResource) reviewXmlResource).freeResources();
				} catch (XMLDBException xmldbe) {
					xmldbe.printStackTrace();
				}
			}
		}
		
		return reviews;
	}
    
    /*
    public void deleteReviewsForPublication(String reviewedPublicationId) throws XMLDBException {
    	String reviewsOfPublicationCollectionName = getReviewsOfPublicationCollectionName(reviewedPublicationId);
    	XMLConnectionProperties conn = connectionPool.getConnection();
    	
    	Collection reviewsOfPublicationCollection = basicOperations.getOrCreateCollection(reviewsOfPublicationCollectionName, 0, conn);
    	for (String reviewId : reviewsOfPublicationCollection.listResources()) {
    		Resource review = reviewsOfPublicationCollection.getResource(reviewId);
    		reviewsOfPublicationCollection.removeResource(review);
		}
    	
    	connectionPool.releaseConnection(conn);
    }
    */
    

	
	
	

}
