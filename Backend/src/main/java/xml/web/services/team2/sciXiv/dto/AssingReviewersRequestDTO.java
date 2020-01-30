package xml.web.services.team2.sciXiv.dto;

import java.util.List;

public class AssingReviewersRequestDTO {
	
	private String publicationTitle;
	
	private List<String> assignedReviewerEmails;

	public AssingReviewersRequestDTO() {
		super();
	}

	public String getPublicationTitle() {
		return publicationTitle;
	}

	public void setPublicationTitle(String publicationTitle) {
		this.publicationTitle = publicationTitle;
	}

	public List<String> getAssignedReviewerEmails() {
		return assignedReviewerEmails;
	}

	public void setAssignedReviewerEmails(List<String> assignedReviewerEmails) {
		this.assignedReviewerEmails = assignedReviewerEmails;
	}
	
}
