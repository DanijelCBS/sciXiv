package xml.web.services.team2.sciXiv.dto;

public class LoginResponseDTO {
	
	private String jsonWebToken;

	public LoginResponseDTO() {
		super();
	}

	public LoginResponseDTO(String jsonWebToken) {
		super();
		this.jsonWebToken = jsonWebToken;
	}

	public String getJsonWebToken() {
		return jsonWebToken;
	}

	public void setJsonWebToken(String jsonWebToken) {
		this.jsonWebToken = jsonWebToken;
	}

}
