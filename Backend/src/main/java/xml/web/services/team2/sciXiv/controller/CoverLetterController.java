package xml.web.services.team2.sciXiv.controller;

import org.springframework.http.HttpStatus;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xmldb.api.base.XMLDBException;
import org.springframework.http.MediaType;

import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.service.CoverLetterService;

@RestController
@RequestMapping(value = "/coverLetter")
public class CoverLetterController {

	@Autowired
	CoverLetterService coverLetterService;

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> getCoverLetterById(@PathVariable("id") String id)
			throws DocumentLoadingFailedException, XMLDBException, IOException {
		String coverLetter = coverLetterService.findById(id);
		return new ResponseEntity<>(coverLetter, HttpStatus.OK);
	}

	
}
