package xml.web.services.team2.sciXiv.controller;

import org.springframework.http.HttpStatus;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;
import org.springframework.http.MediaType;

import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.service.CoverLetterService;
import xml.web.services.team2.sciXiv.utils.connection.XMLConnectionProperties;

@RestController
@RequestMapping(value = "/coverLetter")
public class CoverLetterController {

	@Autowired
	CoverLetterService coverLetterService;

	XMLConnectionProperties conn;

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> getCoverLetterById(@PathVariable("id") String id)
			throws DocumentLoadingFailedException, XMLDBException, IOException {
		String coverLetter = coverLetterService.findById(id);
		return new ResponseEntity<>(coverLetter, HttpStatus.OK);
	}

	@PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> addCoverLeter(@RequestBody String coverLetter)
			throws SAXException, ParserConfigurationException, IOException, TransformerException,
			DocumentStoringFailedException, DocumentLoadingFailedException, XMLDBException {
		String id = coverLetterService.save(coverLetter);
		return new ResponseEntity<>(String.format("Cover letter with id= %s is successfully added.", id),
				HttpStatus.OK);
	}

	@PutMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> updateCoverLetter(@RequestBody String coverLetter)
			throws SAXException, ParserConfigurationException, IOException, DocumentLoadingFailedException,
			XMLDBException, DocumentStoringFailedException {
		String coverLetterStr = coverLetterService.update(coverLetter);
		return new ResponseEntity<>(coverLetterStr, HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> deleteCoverLetter(@PathVariable("id") String id) throws XMLDBException {
		coverLetterService.delete(id, conn);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
}
