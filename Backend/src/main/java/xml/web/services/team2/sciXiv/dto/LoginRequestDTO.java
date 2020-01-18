package xml.web.services.team2.sciXiv.dto;

import javax.validation.constraints.NotBlank;

public class LoginRequestDTO {
	
	@NotBlank(message = "email is required")
	private String email;
	@NotBlank(message = "password is required")
	private String password;
	
	public LoginRequestDTO() {
		super();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
