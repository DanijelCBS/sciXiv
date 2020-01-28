package xml.web.services.team2.sciXiv.dto;

public class UserBaicInfoDTO {
	
	private String email;
	private String firstName;
	private String lastName;
	
	public UserBaicInfoDTO() {
		super();
	}
	
	public UserBaicInfoDTO(String email, String firstName, String lastName) {
		super();
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
}
