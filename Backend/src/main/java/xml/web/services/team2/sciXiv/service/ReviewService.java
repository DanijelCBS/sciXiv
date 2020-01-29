package xml.web.services.team2.sciXiv.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;

import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentParsingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.exception.InvalidDataException;
import xml.web.services.team2.sciXiv.exception.InvalidXmlException;
import xml.web.services.team2.sciXiv.exception.UserRetrievingFailedException;
import xml.web.services.team2.sciXiv.model.TPublications;
import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.repository.CoverLetterRepository;
import xml.web.services.team2.sciXiv.repository.ReviewRepository;
import xml.web.services.team2.sciXiv.repository.UserRepository;
import xml.web.services.team2.sciXiv.utils.dom.DOMParser;
import xml.web.services.team2.sciXiv.utils.dom.XPathExpressionHandler;
import xml.web.services.team2.sciXiv.utils.xslt.XSLTranspiler;

@Service
public class ReviewService {

	private static String schemaPath = "src/main/resources/static/xmlSchemas/review.xsd";

	private static String PUBLICATION_REVIEWS_MERGED_TO_HTML_XSL = "src/main/resources/static/xsl/publicationWithReviewsToHTML.xsl";
	
	private static String PUBLICATION_REVIEWS_MERGED_FOR_PDF_XSL = "src/main/resources/static/xsl/publicationWithReviewsForPdf.xsl";
	
	private static String PUBLICATION_BLIND_REVIEWS_MERGED_TO_HTML_XSL = "src/main/resources/static/xsl/publicationAndBlindReviewsToHTML.xsl";
	
	private static String PUBLICATION_BLIND_REVIEWS_MERGED_FOR_PDF_XSL = "src/main/resources/static/xsl/publicationWithBlindReviewsForPdf.xsl";

	
	public static String PUBBICATION_REVIEWS_XSLFO = "src/main/resources/static/xslfo/templatesForCoverLetterAndReviews.xsl";

	@Autowired
	private ScientificPublicationService scientificPublicationService;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private DOMParser domParser;

	@Autowired
	private XPathExpressionHandler xpathExecuter;

	@Autowired
	private XSLTranspiler xslTranspiler;

	public String findById(String reviewId) throws DocumentStoringFailedException, ParserConfigurationException,
			TransformerException, IOException, XMLDBException, DocumentLoadingFailedException {
		return this.reviewRepository.findById(reviewId);
	}

	public String submitReview(String reviewXml) throws ParserConfigurationException, IOException,
			XPathExpressionException, XMLDBException, DocumentStoringFailedException, TransformerException {
		// Check if review xml is valid
		Document document = this.buildAndValidateReview(reviewXml);

		String reviewId = reviewRepository.save(document);

		// TODO: remove review assignment for reviewer

		// TODO: send email notification to editor

		return reviewId;

	}

	public String updateReview(String newReviewContent) throws ParserConfigurationException, IOException, SAXException,
			DocumentLoadingFailedException, XMLDBException, DocumentStoringFailedException, TransformerException {
		Document document = this.buildAndValidateReview(newReviewContent);
		String id = document.getDocumentElement().getAttribute("id");

		return this.reviewRepository.update(document, id);
	}

	public void deleteReview(String reviewId) throws XMLDBException {
		boolean success = this.reviewRepository.delete(reviewId);
		if (!success) {
			throw new ResourceNotFoundException("ResourceNotFoundException; Review with [id: " + reviewId + "]");
		}
	}

	public List<Node> getReviewsOfPublicationAsDomNodes(String publicationTitle, int publicationVersion)
			throws XMLDBException {
		return this.reviewRepository.findReviewsOfPublicationAsDomNodes(publicationTitle, publicationVersion);
	}

	public String mergePublicationAndReviews(String publicationTitle, int publicationVersion)
			throws XMLDBException, DocumentLoadingFailedException, ParserConfigurationException, SAXException,
			IOException, TransformerException, DOMException, UserRetrievingFailedException {
		String publication = this.scientificPublicationService.findByNameAndVersion(publicationTitle,
				publicationVersion);
		Document publicationDOM = domParser.buildDocumentNoSchema(publication);
		List<Node> publicationReviews = this.getReviewsOfPublicationAsDomNodes(publicationTitle, publicationVersion);
		Element publicationRoot = (Element) publicationDOM.getElementsByTagNameNS("http://ftn.uns.ac.rs/scientificPublication", "scientificPublication").item(0);

		int i = 0;
		for (Node reviewNode : publicationReviews) {
			Node importedReviewNode = publicationDOM.importNode(reviewNode, true);
			Element importedReviewElement = (Element) importedReviewNode;
			Node reviewerNode = (Node) importedReviewElement.getElementsByTagNameNS("http://ftn.uns.ac.rs/review", "reviewer").item(0);
			TUser reviewer = this.userService.findByEmail(reviewerNode.getTextContent());
			if(reviewer != null) {
				String fullName = String.format("%s %s", reviewer.getFirstName(), reviewer.getLastName());
				reviewerNode.setTextContent(fullName);
			}
			publicationRoot.appendChild(importedReviewElement);
		}

		return DOMParser.doc2String(publicationDOM);
	}

	public String mergePublicationAndNonCensoredReviewsToXHTML(String publicationTitle, int publicationVersion)
			throws TransformerException, XMLDBException, DocumentLoadingFailedException, ParserConfigurationException,
			SAXException, IOException, DOMException, UserRetrievingFailedException {
		String mergeToXml = this.mergePublicationAndReviews(publicationTitle, publicationVersion);
		return xslTranspiler.generateHTML(mergeToXml, PUBLICATION_REVIEWS_MERGED_TO_HTML_XSL);
	}
	
	public Resource mergePublicationAndNonCensoredReviewsToPDF(String publicationTitle, int publicationVersion) throws DOMException, XMLDBException, DocumentLoadingFailedException, ParserConfigurationException, SAXException, IOException, TransformerException, UserRetrievingFailedException {
		String mergeToXml = this.mergePublicationAndReviews(publicationTitle, publicationVersion);
		ByteArrayOutputStream outputStream = xslTranspiler.generatePDF(mergeToXml,
				PUBLICATION_REVIEWS_MERGED_FOR_PDF_XSL, PUBBICATION_REVIEWS_XSLFO);
		
		Path file = Paths.get(String.format("%s_withReviews.pdf", publicationTitle));
		Files.write(file, outputStream.toByteArray());

		return new UrlResource(file.toUri());
	}
	
	public String mergePublicationAndBlindReviewsToXHTML(String publicationTitle, int publicationVersion) throws DOMException, XMLDBException, DocumentLoadingFailedException, ParserConfigurationException, SAXException, IOException, TransformerException, UserRetrievingFailedException {
		String mergeToXml = this.mergePublicationAndReviews(publicationTitle, publicationVersion);
		return xslTranspiler.generateHTML(mergeToXml, PUBLICATION_BLIND_REVIEWS_MERGED_TO_HTML_XSL);
	}
	
	public Resource mergePublicationAndBlindReviewsToPDF(String publicationTitle, int publicationVersion) throws DOMException, XMLDBException, DocumentLoadingFailedException, ParserConfigurationException, SAXException, IOException, TransformerException, UserRetrievingFailedException {
		String mergeToXml = this.mergePublicationAndReviews(publicationTitle, publicationVersion);
		ByteArrayOutputStream outputStream = xslTranspiler.generatePDF(mergeToXml,
				PUBLICATION_BLIND_REVIEWS_MERGED_FOR_PDF_XSL, PUBBICATION_REVIEWS_XSLFO);
		
		Path file = Paths.get(String.format("%s_withReviews.pdf", publicationTitle));
		Files.write(file, outputStream.toByteArray());

		return new UrlResource(file.toUri());
	}

	public void removeReviewAssignment(String reviewerId, String publicationToReviewId)
			throws UserRetrievingFailedException {
		TUser reviewer = this.userService.findByEmail(reviewerId);
		reviewer.getPublicationsToReview().getPublicationID().remove(publicationToReviewId);
	}

	private Document buildAndValidateReview(String reviewXml)
			throws ParserConfigurationException, IOException, XMLDBException {
		Document document;
		try {
			document = domParser.buildAndValidateDocument(reviewXml, schemaPath);
		} catch (SAXException | DocumentParsingFailedException e) {
			e.printStackTrace();
			throw new InvalidXmlException("Invalid review xml.");
		}

		Node publicationTitleNode = document.getElementsByTagName("publicationTitle").item(0);
		String publicationTitle = publicationTitleNode.getTextContent();
		Node publicationVersionNode = document.getElementsByTagName("publicationVersion").item(0);
		int version = Integer.parseInt(publicationVersionNode.getTextContent());

		try {
			String sciPub = scientificPublicationService.findByNameAndVersion(publicationTitle, version); // checks if
																											// publication
																											// exists
		} catch (DocumentLoadingFailedException e) {
			throw new InvalidDataException("Publication id",
					"Can not find scientific publication with given title and version.");
		}

		return document;
	}

	private String nodeToXmlString(Node node) throws TransformerException {
		StringWriter stringWriter = new StringWriter();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		StreamResult streamResult = new StreamResult(stringWriter);
		transformer.transform(new DOMSource(node), streamResult);
		return stringWriter.toString();
	}

}
