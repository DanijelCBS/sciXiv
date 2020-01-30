package xml.web.services.team2.sciXiv.dto;

import java.math.BigInteger;
import java.util.List;

public class PublicationProcessDTO {
	
	private String publicationTitle;
	private BigInteger publicationVersion;
	private String processState;
	private List<ReviewTaskDTO> reviewerAssignments;
	
	public PublicationProcessDTO() {
		super();
	}
	
	public PublicationProcessDTO(String publicationTitle, BigInteger publicationVersion, String processState,
			List<ReviewTaskDTO> reviewerAssignments) {
		super();
		this.publicationTitle = publicationTitle;
		this.publicationVersion = publicationVersion;
		this.processState = processState;
		this.reviewerAssignments = reviewerAssignments;
	}
	
	public String getPublicationTitle() {
		return publicationTitle;
	}
	public void setPublicationTitle(String publicationTitle) {
		this.publicationTitle = publicationTitle;
	}
	public BigInteger getPublicationVersion() {
		return publicationVersion;
	}
	public void setPublicationVersion(BigInteger publicationVersion) {
		this.publicationVersion = publicationVersion;
	}
	public String getProcessState() {
		return processState;
	}
	public void setProcessState(String processState) {
		this.processState = processState;
	}
	public List<ReviewTaskDTO> getReviewerAssignments() {
		return reviewerAssignments;
	}
	public void setReviewerAssignments(List<ReviewTaskDTO> reviewerAssignments) {
		this.reviewerAssignments = reviewerAssignments;
	}
	
}
