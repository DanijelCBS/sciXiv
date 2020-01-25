package xml.web.services.team2.sciXiv.controller;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentParsingFailedException;
import xml.web.services.team2.sciXiv.exception.InvalidDataException;
import xml.web.services.team2.sciXiv.exception.InvalidXmlException;

@ControllerAdvice
public class ExceptionResponds {
	
	@ExceptionHandler(value = { 
			DocumentLoadingFailedException.class, 
			DocumentParsingFailedException.class,
			InvalidDataException.class,
			InvalidXmlException.class
			})
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	String badRequest(Exception ex) {
		return ex.getMessage();
	}
	
	@ExceptionHandler(value = ResourceNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	String notFound(ResourceNotFoundException ex) {
		return ex.getMessage();
	}
	

}
