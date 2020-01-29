package xml.web.services.team2.sciXiv.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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

	@GetMapping(value = "/{title}/version/{version}", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> getCoverLetterByTitleAndVersion(@PathVariable("title") String title,
			@PathVariable("version") String version)
			throws DocumentLoadingFailedException, XMLDBException, IOException {
		String coverLetter = coverLetterService.findByTitleAndVersion(title, version);
		return new ResponseEntity<>(coverLetter, HttpStatus.OK);
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> getCoverLetterById(@PathVariable("id") String id)
			throws DocumentLoadingFailedException, XMLDBException, IOException {
		String coverLetter = coverLetterService.findById(id);
		return new ResponseEntity<>(coverLetter, HttpStatus.OK);
	}

	@PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> addCoverLetter(@RequestBody String coverLetter)
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
		coverLetterService.delete(id);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	@GetMapping(value = "xhtml/{id}", produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<Object> getCoverLetterByIdAndReturnAsXHTML(@PathVariable("id") String id) {
		try {
			return new ResponseEntity<>(coverLetterService.getCoverLetterByIdAndReturnAsXHTML(id), HttpStatus.OK);
		} catch (DocumentLoadingFailedException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>("An error occurred while retrieving document", HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "xhtml/{title}/version/{version}", produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<Object> getCoverLetterByTitleAndVersionAndReturnAsXHTML(@PathVariable("title") String title,
			@PathVariable("version") String version) {
		try {
			return new ResponseEntity<>(
					coverLetterService.getCoverLetterByTitleAndVersionAndReturnAsXHTML(title, version), HttpStatus.OK);
		} catch (DocumentLoadingFailedException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>("An error occurred while retrieving document", HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "export/xhtml/{id}")
	public ResponseEntity<Object> exportCoverLetterByIdAsXHTML(@PathVariable("id") String id) {
		try {
			Resource resource = coverLetterService.exportCoverLetterByIdAsXHTML(id);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (Exception e) {
			return new ResponseEntity<>("An error occurred while exporting cover letter as HTML",
					HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "export/xhtml/{title}/version/{version}")
	public ResponseEntity<Object> exportCoverLetterByTitleAndVersionAsXHTML(@PathVariable("title") String title,
			@PathVariable("version") String version) {
		try {
			Resource resource = coverLetterService.exportCoverLetterByTitleAndVersionAsXHTML(title, version);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (Exception e) {
			return new ResponseEntity<>("An error occurred while exporting cover letter as HTML",
					HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "export/pdf/{id}")
	public ResponseEntity<Object> exportCoverLetterByIdAsPDF(@PathVariable("id") String id) {
		try {
			Resource resource = coverLetterService.exportCoverLetterByIdAsPDF(id);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (Exception e) {
			return new ResponseEntity<>("An error occurred while exporting cover letter as PDF",
					HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "export/pdf/{title}/version/{version}")
	public ResponseEntity<Object> exportCoverLetterByTitleAndVersionAsPDF(@PathVariable("title") String title,
			@PathVariable("version") String version) {
		try {
			Resource resource = coverLetterService.exportCoverLetterByTitleAndVersionAsPDF(title, version);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (Exception e) {
			return new ResponseEntity<>("An error occurred while exporting cover letter as PDF",
					HttpStatus.BAD_REQUEST);
		}
	}
}
