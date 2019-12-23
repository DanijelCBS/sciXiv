package xml.web.services.team2.sciXiv.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.utils.connection.ConnectionProperties;
import xml.web.services.team2.sciXiv.utils.database.BasicOperations;
import xml.web.services.team2.sciXiv.utils.factory.ConnectionPropertiesFactory;

@Repository
public class ReviewRepository {
	
	private static String publicationReviewCollections = "/db/sciXiv/publicationReviews";
	
	@Autowired
    private BasicOperations basicOperations;

    @Autowired
    private ConnectionPropertiesFactory connectionPool;
    
    public List<String> findAllByPublicationAsXml(String reviewedPublicationId) throws XMLDBException {
    	List<String> result = new ArrayList<String>();
    	String reviewsOfPublicationCollectionName = getReviewsOfPublicationCollectionName(reviewedPublicationId);
    	ConnectionProperties conn = connectionPool.getConnection();
    	
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
    	ConnectionProperties conn = connectionPool.getConnection();
    	
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
    	ConnectionProperties conn = connectionPool.getConnection();
    	
    	Collection reviewsOfPublicationCollection = basicOperations.getOrCreateCollection(reviewsOfPublicationCollectionName, 0, conn);
    	int reviewsNum = reviewsOfPublicationCollection.getResourceCount();
    	String reviewId = String.format("review%d.xml", reviewsNum++);
    	basicOperations.storeDocument(reviewsOfPublicationCollectionName, reviewId, xmlEntity, conn);
    	
    	connectionPool.releaseConnection(conn);
    	return reviewId;
    }
    
    public void deleteReviewsForPublication(String reviewedPublicationId) throws XMLDBException {
    	String reviewsOfPublicationCollectionName = getReviewsOfPublicationCollectionName(reviewedPublicationId);
    	ConnectionProperties conn = connectionPool.getConnection();
    	
    	Collection reviewsOfPublicationCollection = basicOperations.getOrCreateCollection(reviewsOfPublicationCollectionName, 0, conn);
    	for (String reviewId : reviewsOfPublicationCollection.listResources()) {
    		Resource review = reviewsOfPublicationCollection.getResource(reviewId);
    		reviewsOfPublicationCollection.removeResource(review);
		}
    	
    	connectionPool.releaseConnection(conn);
    }
    
    private static String getReviewsOfPublicationCollectionName(String reviewedPublicationName) {
    	return String.format("%s/%s", publicationReviewCollections, reviewedPublicationName);
    }
	
	
	

}
