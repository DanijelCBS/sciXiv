package xml.web.services.team2.sciXiv.controller;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.jena.sparql.pfunction.library.version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;

import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentParsingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.exception.InvalidDataException;
import xml.web.services.team2.sciXiv.exception.InvalidXmlException;
import xml.web.services.team2.sciXiv.exception.UserRetrievingFailedException;
import xml.web.services.team2.sciXiv.service.ReviewService;

@RestController
@RequestMapping(value = "/reviews")
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	@RequestMapping(value = "/{reviewId}", method = RequestMethod.GET, produces = "application/xml")
	public ResponseEntity<String> getReview(@PathVariable("reviewId") String reviewId)
			throws DocumentStoringFailedException, ParserConfigurationException, TransformerException, IOException,
			XMLDBException, DocumentLoadingFailedException {
		String reviewXml = this.reviewService.findById(reviewId);
		if (reviewXml != null) {
			return new ResponseEntity<String>(reviewXml, HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Review with given id does not exist.", HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(method = RequestMethod.POST, consumes = "application/xml")
	@PreAuthorize("hasRole('REVIEWER')")
	public ResponseEntity<String> submitReview(@RequestBody String reviewXml)
			throws XPathExpressionException, ParserConfigurationException, IOException, XMLDBException,
			DocumentStoringFailedException, TransformerException {
		String id = this.reviewService.submitReview(reviewXml);
		return new ResponseEntity<String>(id, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PUT, produces = "application/xml")
	@PreAuthorize("hasRole('REVIEWER')")
	public ResponseEntity<String> updateReview(@RequestBody String updatedReviewXml)
			throws ParserConfigurationException, IOException, SAXException, DocumentLoadingFailedException,
			XMLDBException, DocumentStoringFailedException, TransformerException {
		updatedReviewXml = this.reviewService.updateReview(updatedReviewXml);
		return new ResponseEntity<String>(updatedReviewXml, HttpStatus.OK);
	}

	@DeleteMapping(value = "/{reviewId}")
	@PreAuthorize("hasRole('REVIEWER')")
	public ResponseEntity<?> deleteReview(@PathVariable("reviewId") String reviewId) throws XMLDBException {
		this.reviewService.deleteReview(reviewId);
		return ResponseEntity.ok("Review deleted.");
	}

	@RequestMapping(value = "/publication/{pubTitle}/version/{pubVersion}", method = RequestMethod.GET, produces = "application/xml")
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<String> getPublicationWithReviews(@PathVariable("pubTitle") String publicationTitle,
			@PathVariable("pubVersion") int publicationVersion) throws XMLDBException, DocumentLoadingFailedException,
			ParserConfigurationException, SAXException, IOException, TransformerException, DOMException, UserRetrievingFailedException {
		String xml = this.reviewService.mergePublicationAndReviews(publicationTitle, publicationVersion);
		return new ResponseEntity<String>(xml, HttpStatus.OK);
	}

	@GetMapping(value = "/merge/{pubTitle}/version/{pubVersion}", produces = MediaType.TEXT_HTML_VALUE)
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<Object> getScientificPublicationWithReviewsAsXHTML(
			@PathVariable("pubTitle") String publicationTitle, @PathVariable("pubVersion") int publicationVersion)
			throws TransformerException, XMLDBException, DocumentLoadingFailedException, ParserConfigurationException, SAXException, IOException, DOMException, UserRetrievingFailedException {
		
		String ret = this.reviewService.mergePublicationAndNonCensoredReviewsToXHTML(publicationTitle,
				publicationVersion);
		return new ResponseEntity<Object>(ret, HttpStatus.OK);
	}
	
	@GetMapping(value = "/mergeBlind/{pubTitle}/version/{pubVersion}", produces = MediaType.TEXT_HTML_VALUE)
	@PreAuthorize("hasRole('REVIEWER')")
	public ResponseEntity<Object> getScientificPublicationWithBlindReviewsAsXHTML(
			@PathVariable("pubTitle") String publicationTitle, @PathVariable("pubVersion") int publicationVersion)
			throws TransformerException, XMLDBException, DocumentLoadingFailedException, ParserConfigurationException, SAXException, IOException, DOMException, UserRetrievingFailedException {
		
		String ret = this.reviewService.mergePublicationAndBlindReviewsToXHTML(publicationTitle,
				publicationVersion);
		return new ResponseEntity<Object>(ret, HttpStatus.OK);
	}

}
