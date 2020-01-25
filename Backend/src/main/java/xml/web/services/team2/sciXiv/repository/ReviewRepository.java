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
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
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
    
    public String findById(String id) throws DocumentStoringFailedException, ParserConfigurationException, TransformerException, IOException, XMLDBException {
    	String reviewXml = null;
		String xPath = "//review[@id=\"" + id + "\"]";

		ResourceSet resourceSet = DBExtractor.executeXPathQuery(publicationReviewsCollection, xPath,
				DBExtractor.getTARGET_NAMESPACE());

		if (resourceSet == null) {
			return reviewXml;
		}

		ResourceIterator rit = resourceSet.getIterator();

		XMLResource xmlResource = null;

		while (rit.hasMoreResources()) {
			try {
				xmlResource = (XMLResource) rit.nextResource();

				reviewXml = xmlResource.getContent().toString();

				return reviewXml;
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
    
    public void delete(String id, XMLConnectionProperties conn) throws XMLDBException {
		Collection col = basicOperations.getOrCreateCollection(publicationReviewsCollection + "/", 0, conn);
		Resource resource = col.getResource(id);
		col.removeResource(resource);
	}

	public void delete(String id) throws XMLDBException {
		XMLConnectionProperties conn = connectionPool.getConnection();
		delete(id, conn);
		connectionPool.releaseConnection(conn);
	}
	
	public String update(Document updatedReviewValid, String id) throws SAXException, ParserConfigurationException, IOException,
		DocumentLoadingFailedException, XMLDBException, DocumentStoringFailedException, TransformerException {
		//Document document = DOMParser.buildDocument(coverLetter, PATH);
		//String id = document.getDocumentElement().getAttribute("id");
		
		String oldCoverLetter = findById(id);
		
		if (oldCoverLetter == null) {
			throw new ResourceNotFoundException("ResourceNotFoundException; Review with [id: " + id + "]");
		}
		
		XMLConnectionProperties conn = connectionPool.getConnection();
		
		delete(id, conn);
		
		basicOperations.storeDocument(publicationReviewsCollection, String.format("%s.xml", id), DOMParser.doc2String(updatedReviewValid), conn);
		
		connectionPool.releaseConnection(conn);
		
		return id;
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
