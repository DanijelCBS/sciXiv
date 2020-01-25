package xml.web.services.team2.sciXiv.service;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;

import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentParsingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.exception.UserRetrievingFailedException;
import xml.web.services.team2.sciXiv.model.TPublications;
import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.repository.ReviewRepository;
import xml.web.services.team2.sciXiv.repository.UserRepository;
import xml.web.services.team2.sciXiv.utils.dom.DOMParser;
import xml.web.services.team2.sciXiv.utils.dom.XPathExpressionHandler;

@Service
public class ReviewService {

	private static String schemaPath = "src/main/resources/static/xmlSchemas/review.xsd";
	
	@Autowired
	private ScientificPublicationService scientificPublicationService;

	@Autowired
	private ReviewRepository reviewRepository;
	
	private UserRepository userRepository;

	@Autowired
	private DOMParser domParser;

	@Autowired
	private XPathExpressionHandler xpathExecuter;

	public String submitReview(String reviewXml) throws ParserConfigurationException, SAXException, IOException,
			DocumentParsingFailedException, XPathExpressionException, XMLDBException, DocumentStoringFailedException, DocumentLoadingFailedException {
		Document document = domParser.buildAndValidateDocument(reviewXml, schemaPath);
		
		Node publicationNameNode = xpathExecuter.findSingleNodeByXPath(document, "//metadata/publicationName");
		String publicationName = publicationNameNode.getNodeValue();
		//String sciPub = scientificPublicationService.findByName(publicationName); // checks if publication exists
		String reviewId = reviewRepository.save(reviewXml, publicationName);
		
		//TODO: remove review assignment for reviewer
		
		//TODO: send email notification to editor

		return reviewId;
	}
	
	public void deleteReviewsForPublication(String publicationName) throws XMLDBException {
		reviewRepository.deleteReviewsForPublication(publicationName);
	}
	
	public List<Node> getReviewsOfPublicationAsDom(String publicationName) throws XMLDBException {
		return reviewRepository.findAllByPublicationAsDom(publicationName);
	}
	
	
	
	public void removeReviewAssignment(String reviewerId, String publicationToReviewId) throws UserRetrievingFailedException {
		TUser reviewer = userRepository.getByEmail(reviewerId);
		reviewer.getPublicationsToReview().getPublicationID().remove(publicationToReviewId);
	}

}
