package xml.web.services.team2.sciXiv.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidXmlException extends RuntimeException {
	
	public InvalidXmlException() {
		super();
	}
	
	public InvalidXmlException(String message) {
		super(message);
	}

}
