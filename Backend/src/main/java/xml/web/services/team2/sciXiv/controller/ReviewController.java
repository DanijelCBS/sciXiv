package xml.web.services.team2.sciXiv.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.jena.sparql.pfunction.library.version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import xml.web.services.team2.sciXiv.dto.AssingReviewersRequestDTO;
import xml.web.services.team2.sciXiv.dto.ReviewAssignmentDTO;
import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentParsingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.exception.InvalidDataException;
import xml.web.services.team2.sciXiv.exception.InvalidXmlException;
import xml.web.services.team2.sciXiv.exception.UserRetrievingFailedException;
import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.model.businessProcess.TReviewStatus;
import xml.web.services.team2.sciXiv.repository.ScientificPublicationRepository;
import xml.web.services.team2.sciXiv.service.BusinessProcessService;
import xml.web.services.team2.sciXiv.service.ReviewService;
import xml.web.services.team2.sciXiv.service.UserService;

@RestController
@RequestMapping(value = "/reviews")
@CrossOrigin
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private UserService userService;

	@Autowired
	private BusinessProcessService businessProcessService;

	@Autowired
	private ScientificPublicationRepository scientificPublicationRepository;

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

	@RequestMapping(value = "/assignments", method = RequestMethod.PUT, consumes = "application/json")
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<Object> assignReviewsers(@RequestBody AssingReviewersRequestDTO reviewAssignments) throws Exception {
		this.reviewService.assingReviews(reviewAssignments.getPublicationTitle(),
				reviewAssignments.getAssignedReviewerEmails());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/assignments", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
	@PreAuthorize("hasRole('REVIEWER') or hasRole('EDITOR')")
	public ResponseEntity<List<ReviewAssignmentDTO>> getReviewAssignments()
			throws UserRetrievingFailedException, XMLDBException, IOException, JAXBException {
		TUser currentUser = this.userService.findByEmail(this.getCurrentUserEmail());
		List<ReviewAssignmentDTO> result = new ArrayList<ReviewAssignmentDTO>();

		ReviewAssignmentDTO assignment;
		for (String publicationToReview : currentUser.getPublicationsToReview().getPublicationID()) {
			String collectionId = publicationToReview.replace(" ", "");
			int publicationVersion = this.scientificPublicationRepository.getLastVersionNumber(collectionId);
			TReviewStatus reviewStatus = this.businessProcessService.getReivewStatus(publicationToReview,
					currentUser.getEmail());
			if(reviewStatus != TReviewStatus.SUBMITTED && reviewStatus != TReviewStatus.REJECTED) {
				assignment = new ReviewAssignmentDTO(publicationToReview, publicationVersion,
						reviewStatus.toString());
				result.add(assignment);
			}
		}

		return new ResponseEntity<List<ReviewAssignmentDTO>>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/acceptAssignment", method = RequestMethod.PUT)
	@PreAuthorize("hasRole('REVIEWER') or hasRole('EDITOR')")
	public ResponseEntity<Object> acceptReviewAssignment(@RequestParam("forPublication") String publicationTitle)
			throws Exception {
		String userEmail = this.getCurrentUserEmail();
		this.reviewService.acceptReviewAssignment(publicationTitle, userEmail);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/rejectAssignment", method = RequestMethod.PUT)
	@PreAuthorize("hasRole('REVIEWER') or hasRole('EDITOR')")
	public ResponseEntity<Object> rejectReviewAssignment(@RequestParam("forPublication") String publicationTitle)
			throws Exception {
		String userEmail = this.getCurrentUserEmail();
		this.reviewService.rejectReviewAssignment(publicationTitle, userEmail);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, consumes = "application/xml")
	@PreAuthorize("hasRole('REVIEWER') or hasRole('EDITOR')")
	public ResponseEntity<String> submitReview(@RequestBody String reviewXml) throws Exception {
		String userEmail = this.getCurrentUserEmail();
		String id = this.reviewService.submitReview(reviewXml, userEmail);
		return new ResponseEntity<String>(id, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PUT, produces = "application/xml")
	@PreAuthorize("hasRole('REVIEWER') or hasRole('EDITOR')")
	public ResponseEntity<String> updateReview(@RequestBody String updatedReviewXml)
			throws ParserConfigurationException, IOException, SAXException, DocumentLoadingFailedException,
			XMLDBException, DocumentStoringFailedException, TransformerException {
		updatedReviewXml = this.reviewService.updateReview(updatedReviewXml);
		return new ResponseEntity<String>(updatedReviewXml, HttpStatus.OK);
	}

	@DeleteMapping(value = "/{reviewId}")
	@PreAuthorize("hasRole('REVIEWER') or hasRole('EDITOR')")
	public ResponseEntity<?> deleteReview(@PathVariable("reviewId") String reviewId) throws XMLDBException {
		this.reviewService.deleteReview(reviewId);
		return ResponseEntity.ok("Review deleted.");
	}

	@RequestMapping(value = "/publication/{pubTitle}/version/{pubVersion}", method = RequestMethod.GET, produces = "application/xml")
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<String> getPublicationWithReviews(@PathVariable("pubTitle") String publicationTitle,
			@PathVariable("pubVersion") int publicationVersion)
			throws XMLDBException, DocumentLoadingFailedException, ParserConfigurationException, SAXException,
			IOException, TransformerException, DOMException, UserRetrievingFailedException {
		String xml = this.reviewService.mergePublicationAndReviews(publicationTitle, publicationVersion);
		return new ResponseEntity<String>(xml, HttpStatus.OK);
	}

	@GetMapping(value = "/mergeToHtml/{pubTitle}/version/{pubVersion}", produces = MediaType.TEXT_HTML_VALUE)
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<Object> getScientificPublicationWithReviewsAsXHTML(
			@PathVariable("pubTitle") String publicationTitle, @PathVariable("pubVersion") int publicationVersion)
			throws TransformerException, XMLDBException, DocumentLoadingFailedException, ParserConfigurationException,
			SAXException, IOException, DOMException, UserRetrievingFailedException {

		String ret = this.reviewService.mergePublicationAndNonCensoredReviewsToXHTML(publicationTitle,
				publicationVersion);
		return new ResponseEntity<Object>(ret, HttpStatus.OK);
	}

	@GetMapping(value = "/mergeToPdf/{pubTitle}/version/{pubVersion}", produces = MediaType.APPLICATION_PDF_VALUE)
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<Object> getScientificPublicationWithReviewsAsPDF(
			@PathVariable("pubTitle") String publicationTitle, @PathVariable("pubVersion") int publicationVersion)
			throws TransformerException, XMLDBException, DocumentLoadingFailedException, ParserConfigurationException,
			SAXException, IOException, DOMException, UserRetrievingFailedException {

		Resource ret = this.reviewService.mergePublicationAndNonCensoredReviewsToPDF(publicationTitle,
				publicationVersion);

		return new ResponseEntity<Object>(ret, HttpStatus.OK);
	}

	@GetMapping(value = "/mergeBlindToHtml/{pubTitle}/version/{pubVersion}", produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<Object> getScientificPublicationWithBlindReviewsAsXHTML(
			@PathVariable("pubTitle") String publicationTitle, @PathVariable("pubVersion") int publicationVersion)
			throws TransformerException, XMLDBException, DocumentLoadingFailedException, ParserConfigurationException,
			SAXException, IOException, DOMException, UserRetrievingFailedException {

		String ret = this.reviewService.mergePublicationAndBlindReviewsToXHTML(publicationTitle, publicationVersion);
		return new ResponseEntity<Object>(ret, HttpStatus.OK);
	}

	@GetMapping(value = "/mergeBlindToPdf/{pubTitle}/version/{pubVersion}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<Object> getScientificPublicationWithBlindReviewsAsPDF(
			@PathVariable("pubTitle") String publicationTitle, @PathVariable("pubVersion") int publicationVersion)
			throws TransformerException, XMLDBException, DocumentLoadingFailedException, ParserConfigurationException,
			SAXException, IOException, DOMException, UserRetrievingFailedException {

		Resource ret = this.reviewService.mergePublicationAndBlindReviewsToPDF(publicationTitle, publicationVersion);
		return new ResponseEntity<Object>(ret, HttpStatus.OK);
	}

	private String getCurrentUserEmail() {
		User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return currentUser.getUsername();
	}

}
