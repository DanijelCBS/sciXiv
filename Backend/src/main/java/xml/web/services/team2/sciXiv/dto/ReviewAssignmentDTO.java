package xml.web.services.team2.sciXiv.dto;

public class ReviewAssignmentDTO {
	
	private String publicationTitle;
	private int publicationVersion;
	private String reviewStatus;
	
	public ReviewAssignmentDTO() {
		super();
	}

	public ReviewAssignmentDTO(String publicationTitle, int publicationVersion, String reviewStatus) {
		super();
		this.publicationTitle = publicationTitle;
		this.publicationVersion = publicationVersion;
		this.reviewStatus = reviewStatus;
	}

	public String getPublicationTitle() {
		return publicationTitle;
	}

	public void setPublicationTitle(String publicationTitle) {
		this.publicationTitle = publicationTitle;
	}

	public int getPublicationVersion() {
		return publicationVersion;
	}

	public void setPublicationVersion(int publicationVersion) {
		this.publicationVersion = publicationVersion;
	}

	public String getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}
	
}
