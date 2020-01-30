package xml.web.services.team2.sciXiv.dto;

public class ReviewTaskDTO {
	
	private String reviewerEmail;
	private String reviewerFullName;
	private String reviewStatus;
	
	public ReviewTaskDTO() {
		super();
	}
	
	public ReviewTaskDTO(String reviewerEmail, String reviewerFullName, String reviewStatus) {
		super();
		this.reviewerEmail = reviewerEmail;
		this.reviewerFullName = reviewerFullName;
		this.reviewStatus = reviewStatus;
	}

	public String getReviewerEmail() {
		return reviewerEmail;
	}
	public void setReviewerEmail(String reviewerEmail) {
		this.reviewerEmail = reviewerEmail;
	}
	public String getReviewerFullName() {
		return reviewerFullName;
	}
	public void setReviewerFullName(String reviewerFullName) {
		this.reviewerFullName = reviewerFullName;
	}
	public String getReviewStatus() {
		return reviewStatus;
	}
	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

}
