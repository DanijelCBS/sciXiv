package xml.web.services.team2.sciXiv.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.xmldb.api.base.XMLDBException;

import xml.web.services.team2.sciXiv.exception.ChangeProcessStateException;
import xml.web.services.team2.sciXiv.exception.ChangeReviewStatusException;
import xml.web.services.team2.sciXiv.exception.NotOnTheReviewerListException;
import xml.web.services.team2.sciXiv.exception.UserRetrievingFailedException;
import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.model.businessProcess.BusinessProcess;
import xml.web.services.team2.sciXiv.model.businessProcess.BusinessProcess.ReviewerAssignments;
import xml.web.services.team2.sciXiv.model.businessProcess.TProcessStateEnum;
import xml.web.services.team2.sciXiv.model.businessProcess.TReviewStatus;
import xml.web.services.team2.sciXiv.model.businessProcess.TReviewerAssignment;
import xml.web.services.team2.sciXiv.repository.BusinessProcessRepository;
import xml.web.services.team2.sciXiv.repository.ScientificPublicationRepository;
import xml.web.services.team2.sciXiv.repository.UserRepository;

@Service
public class BusinessProcessService {

	@Autowired
	BusinessProcessRepository businessProcessRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ScientificPublicationRepository scientificPublicationRepository;

	@Autowired
	NotificationService notificationService;

	public String createBusinessProcess(String scientificPublicationTitle) throws Exception {
		BusinessProcess businessProcess = new BusinessProcess();

		int lastVersion = scientificPublicationRepository.getLastVersionNumber(scientificPublicationTitle);

		businessProcess.setScientificPublicationTitle(scientificPublicationTitle);
		businessProcess.setVersion(BigInteger.valueOf(lastVersion));
		businessProcess.setProcessState(TProcessStateEnum.SUBMITTED);
		businessProcess.setReviewerAssignments(new BusinessProcess.ReviewerAssignments());

		businessProcessRepository.saveObject(businessProcess);

		return scientificPublicationTitle;
	}

	public BusinessProcess getBusinessProcessObject(String scientificPublicationTitle)
			throws IOException, XMLDBException, JAXBException {
		BusinessProcess businessProcess = businessProcessRepository
				.findObjectByScientificPublicationTitle(scientificPublicationTitle);

		if (businessProcess == null) {
			throw new ResourceNotFoundException(
					"[ResourceNotFoundException] Business process with scientific publication title: \""
							+ scientificPublicationTitle + "\" does not exist.");
		}

		return businessProcess;
	}

	public String getBusinessProcessAsXMLString(String scientificPublicationTitle) throws IOException, XMLDBException {
		String businessProcess = businessProcessRepository.findByScientificPublicationTitle(scientificPublicationTitle);

		if (businessProcess == null) {
			throw new ResourceNotFoundException(
					"[ResourceNotFoundException] Business process with scientific publication title: \""
							+ scientificPublicationTitle + "\" does not exist.");
		}

		return businessProcess;
	}

	public List<BusinessProcess> getAllBusinessProcesses() throws IOException, XMLDBException, JAXBException {
		return businessProcessRepository.findAll();
	}

	public boolean checkIfUsersExist(List<String> usersEmails) throws UserRetrievingFailedException {
		for (String userEmail : usersEmails) {
			if (userRepository.getByEmail(userEmail) == null) {
				return false;
			}
		}

		return true;
	}

	public void addReviewerAssignmentsToBusinessProcess(String scientificPublicationTitle, List<String> userEmails)
			throws Exception {
		BusinessProcess businessProcess = businessProcessRepository
				.findObjectByScientificPublicationTitle(scientificPublicationTitle);

		if (businessProcess == null) {
			throw new ResourceNotFoundException(
					"[ResourceNotFoundException] Business process with scientific publication title: \""
							+ scientificPublicationTitle + "\" does not exist.");
		}

		if (businessProcess.getReviewerAssignments() == null) {
			businessProcess.setReviewerAssignments(new BusinessProcess.ReviewerAssignments());
		}

		List<TReviewerAssignment> reviewerAssignments = businessProcess.getReviewerAssignments()
				.getReviewerAssignment();

		if (!checkIfUsersExist(userEmails)) {
			throw new ResourceNotFoundException(
					"List of reviewer assignments is not valid because some of the user's e-mails are not present in our database");
		}

		TUser sender = userRepository.getEditor();

		String[] emails = new String[userEmails.size()];
		int i = 0;

		for (String userEmail : userEmails) {
			TUser user = userRepository.getByEmail(userEmail);
			emails[i] = userEmail;
			i += 1;

			TReviewerAssignment reviewerAssignment = new TReviewerAssignment();
			reviewerAssignment.setReviewerEmail(userEmail);
			reviewerAssignment.setStatus(TReviewStatus.ASSIGNED);
			reviewerAssignments.add(reviewerAssignment);
		}

		// send notifications to users
		notificationService.addedReviewerAssignments(emails, scientificPublicationTitle, sender);

		businessProcessRepository.saveObject(businessProcess);
	}

	public void changeReviewStatus(String scientificPublicationTitle, String userEmail, TReviewStatus reviewStatus) throws Exception {
		BusinessProcess businessProcess = businessProcessRepository
				.findObjectByScientificPublicationTitle(scientificPublicationTitle);

		if (businessProcess.getReviewerAssignments() == null) {
			businessProcess.setReviewerAssignments(new BusinessProcess.ReviewerAssignments());
		}

		List<TReviewerAssignment> reviewerAssignments = businessProcess.getReviewerAssignments()
				.getReviewerAssignment();

		TReviewerAssignment reviewerAssignment = reviewerAssignments.stream()
				.filter(ra -> ra.getReviewerEmail().equals(userEmail)).findFirst().orElse(null);

		if (reviewerAssignment == null) {
			throw new NotOnTheReviewerListException(
					"User with email: " + userEmail + " is not on this list for reviews.");
		}

		if (reviewerAssignment.getStatus().equals(TReviewStatus.SUBMITTED)
				|| reviewerAssignment.getStatus().equals(TReviewStatus.REJECTED)) {
			throw new ChangeReviewStatusException(
					"[ChangeReviewStatusException] SUBMITTED and REJECTED are terminal states.");
		}

		reviewerAssignment.setStatus(reviewStatus);
		businessProcessRepository.saveObject(businessProcess);
	}
	
	public TReviewStatus getReivewStatus(String scientificPublicationTitle, String userEmail) throws IOException, XMLDBException, JAXBException {
		BusinessProcess businessProcess = businessProcessRepository
				.findObjectByScientificPublicationTitle(scientificPublicationTitle);

		if (businessProcess.getReviewerAssignments() == null) {
			businessProcess.setReviewerAssignments(new BusinessProcess.ReviewerAssignments());
		}

		List<TReviewerAssignment> reviewerAssignments = businessProcess.getReviewerAssignments()
				.getReviewerAssignment();

		TReviewerAssignment reviewerAssignment = reviewerAssignments.stream()
				.filter(ra -> ra.getReviewerEmail().equals(userEmail)).findFirst().orElse(null);

		if (reviewerAssignment == null) {
			throw new NotOnTheReviewerListException(
					"User with email: " + userEmail + " is not on this list for reviews.");
		}
		
		return reviewerAssignment.getStatus();
	}

	public void changeProcessState(String scientificPublicationTitle, TProcessStateEnum processState) throws Exception {
		BusinessProcess businessProcess = businessProcessRepository
				.findObjectByScientificPublicationTitle(scientificPublicationTitle);

		if (businessProcess == null) {
			throw new ResourceNotFoundException(
					"[ResourceNotFoundException] Business process with scientific publication title: \""
							+ scientificPublicationTitle + "\" does not exist.");
		}

		if (businessProcess.getProcessState().equals(TProcessStateEnum.PUBLISHED)
				|| businessProcess.getProcessState().equals(TProcessStateEnum.REJECTED)
				|| businessProcess.getProcessState().equals(TProcessStateEnum.WITHDRAWN)) {
			throw new ChangeProcessStateException(
					"[ChangeProcessStateException] PUBLISHED, REJECTED and WITHDRAWN are terminal states.");
		}

		businessProcess.setProcessState(processState);
		businessProcessRepository.saveObject(businessProcess);
	}

}
