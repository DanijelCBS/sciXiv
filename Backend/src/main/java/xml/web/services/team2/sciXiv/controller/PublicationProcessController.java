package xml.web.services.team2.sciXiv.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xmldb.api.base.XMLDBException;

import xml.web.services.team2.sciXiv.dto.PublicationProcessDTO;
import xml.web.services.team2.sciXiv.dto.ReviewTaskDTO;
import xml.web.services.team2.sciXiv.exception.UserRetrievingFailedException;
import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.model.businessProcess.BusinessProcess;
import xml.web.services.team2.sciXiv.model.businessProcess.TProcessStateEnum;
import xml.web.services.team2.sciXiv.model.businessProcess.TReviewerAssignment;
import xml.web.services.team2.sciXiv.service.BusinessProcessService;
import xml.web.services.team2.sciXiv.service.UserService;

@RestController
@RequestMapping(value = "/publicationProcesses")
public class PublicationProcessController {

	@Autowired
	BusinessProcessService businessProcessService;
	
	@Autowired
	UserService userService;

	@GetMapping
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<List<PublicationProcessDTO>> getPublicationsInProcess() throws IOException, XMLDBException, JAXBException, UserRetrievingFailedException {
		List<BusinessProcess> allProcesses = businessProcessService.getAllBusinessProcesses();
		List<PublicationProcessDTO> result = new ArrayList<PublicationProcessDTO>();
		
		for (BusinessProcess businessProcess : allProcesses) {
			if(isNonTerminated(businessProcess)) {
				String publicationTitle = businessProcess.getScientificPublicationTitle();
				BigInteger publicationVersion = businessProcess.getVersion();
				String publicationProcessState = businessProcess.getProcessState().toString();
				List<ReviewTaskDTO> reviewTasks = new ArrayList<ReviewTaskDTO>();
				for (TReviewerAssignment assignment : businessProcess.getReviewerAssignments().getReviewerAssignment()) {
					String reviewerEmail = assignment.getReviewerEmail();
					TUser reviewer = this.userService.findByEmail(reviewerEmail);
					String reviewerFullName = String.format("%s %s", reviewer.getFirstName(), reviewer.getLastName());
					String reviewStatus = assignment.getStatus().toString();
					ReviewTaskDTO reviewTaskDTO = new ReviewTaskDTO(reviewerEmail, reviewerFullName, reviewStatus);
					reviewTasks.add(reviewTaskDTO);
				}
				PublicationProcessDTO publicationProcessDTO = new PublicationProcessDTO(publicationTitle, publicationVersion, publicationProcessState, reviewTasks);
				result.add(publicationProcessDTO);
			}
		}
		
		return new ResponseEntity<List<PublicationProcessDTO>>(result, HttpStatus.OK);
	}
	
	@PutMapping("/publish")
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<Object> publishPaper(@RequestParam("title") String publicationTitle) throws Exception {
		businessProcessService.publishPaper(publicationTitle);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("/reject")
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<Object> rejectPaper(@RequestParam("title") String publicationTitle) throws Exception {
		businessProcessService.rejectPaper(publicationTitle);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("/requestRevision")
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<Object> requestPaperRevision(@RequestParam("title") String publicationTitle) throws Exception {
		businessProcessService.requestRevision(publicationTitle);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@DeleteMapping
	public ResponseEntity<Object> withdrawPaper(@RequestParam("title") String publicationTitle) throws Exception {
		businessProcessService.withdrawPaper(publicationTitle, this.currentUserEmail());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private boolean isNonTerminated(BusinessProcess process) {
		TProcessStateEnum processState = process.getProcessState();
		return processState != TProcessStateEnum.PUBLISHED && processState != TProcessStateEnum.REJECTED
				&& processState != TProcessStateEnum.WITHDRAWN;
	}
	
	private String currentUserEmail() {
		User current = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return current.getUsername();
	}

}
