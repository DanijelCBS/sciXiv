package xml.web.services.team2.sciXiv.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentStoringFailedException;
import xml.web.services.team2.sciXiv.repository.CoverLetterRepository;
import xml.web.services.team2.sciXiv.repository.ScientificPublicationRepository;
import xml.web.services.team2.sciXiv.utils.connection.XMLConnectionProperties;
import xml.web.services.team2.sciXiv.utils.database.BasicOperations;
import xml.web.services.team2.sciXiv.utils.dom.DOMParser;
import xml.web.services.team2.sciXiv.utils.factory.XMLConnectionPropertiesFactory;
import xml.web.services.team2.sciXiv.utils.xslt.XSLTranspiler;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CoverLetterService {

	private static final String coverLetterXsdSchemaPath = "src/main/resources/static/xmlSchemas/coverLetter.xsd";

	private static final String collectionName = "/db/sciXiv/coverLetters";

	@Autowired
	CoverLetterRepository coverLetterRepository;

	@Autowired
	ScientificPublicationRepository scientificPublicationRepository;

	@Autowired
	BasicOperations basicOperations;

	@Autowired
	XMLConnectionPropertiesFactory xmlConnectionPool;

	@Autowired
	XSLTranspiler xslTranspiler;

	public String getScientificPublicationsID(String coverLetterStr) throws ParserConfigurationException, SAXException,
			IOException, DocumentLoadingFailedException, XMLDBException {
		Document coverLetter = DOMParser.buildDocumentNoSchema(coverLetterStr);
		Element root = coverLetter.getDocumentElement();
		String publicationTitle = root.getElementsByTagName("publicationTitle").item(0).toString();

		if (publicationTitle.length() == 0 || publicationTitle == null) {
			return null;
		}

		return publicationTitle;
	}

	public String setCoverLetter(String scientificPublicationId, String coverLetterId)
			throws DocumentLoadingFailedException, XMLDBException, ParserConfigurationException, SAXException,
			IOException, TransformerException {
		String scientificPublication = scientificPublicationRepository.findByNameAndVersion(scientificPublicationId, 1);
		Document spDocument = DOMParser.buildDocumentNoSchema(scientificPublication);
		spDocument.getElementsByTagName("coverLetter").item(0).setTextContent(coverLetterId);

		Element root = spDocument.getDocumentElement();
		String rootAttribute = root.getAttribute("id");

		String docStr = DOMParser.doc2String(spDocument);

		return null;
	}

	public String findById(String id) throws DocumentLoadingFailedException, XMLDBException, IOException {
		String coverLetter = coverLetterRepository.findById(id);

		if (coverLetter == null) {
			throw new ResourceNotFoundException("ResourceNotFoundException; Cover letter with [id: " + id + "]");
		}

		return coverLetter;
	}

	public String findByTitleAndVersion(String title, String version)
			throws DocumentLoadingFailedException, XMLDBException, IOException {
		String coverLetter = coverLetterRepository.findByTitleAndVersion(title, version);

		if (coverLetter == null) {
			throw new ResourceNotFoundException(
					"ResourceNotFoundException; Cover letter with [title: " + title + " and version: " + version + "]");
		}

		return coverLetter;
	}

	public String findByIdHTML(String id) {
		throw new UnsupportedOperationException();
	}

	public ByteArrayOutputStream findByIdPDF(String id) {
		throw new UnsupportedOperationException();
	}

	public String save(String coverLetter) throws SAXException, ParserConfigurationException, IOException,
			TransformerException, DocumentStoringFailedException, DocumentLoadingFailedException, XMLDBException {
		String coverLetterId = coverLetterRepository.save(coverLetter);

		// scientific publication name
		// String spName = getScientificPublicationsID(coverLetter);

		// set cover letter for publication id=name
		// setCoverLetter(spName, coverLetterId);

		return coverLetterId;
	}

	public String update(String coverLetter) throws SAXException, ParserConfigurationException, IOException,
			DocumentLoadingFailedException, XMLDBException, DocumentStoringFailedException {
		String coverLetterId = coverLetterRepository.update(coverLetter);

		return coverLetterId;
	}

	public void delete(String id) throws XMLDBException {
		coverLetterRepository.delete(id);
	}

	public String getCoverLetterByIdAndReturnAsXHTML(String id)
			throws DocumentLoadingFailedException, XMLDBException, IOException, TransformerException {
		String coverLetter = coverLetterRepository.findById(id);

		if (coverLetter == null) {
			throw new ResourceNotFoundException("ResourceNotFoundException; Cover letter with [id: " + id + "]");
		}

		return xslTranspiler.generateHTML(coverLetter, CoverLetterRepository.coverLetterXSLPath);
	}

	public String getCoverLetterByTitleAndVersionAndReturnAsXHTML(String title, String version)
			throws DocumentLoadingFailedException, XMLDBException, IOException, TransformerException {
		String coverLetter = coverLetterRepository.findByTitleAndVersion(title, version);

		if (coverLetter == null) {
			throw new ResourceNotFoundException(
					"ResourceNotFoundException; Cover letter with [title: " + title + " and version: " + version + "]");
		}

		return xslTranspiler.generateHTML(coverLetter, CoverLetterRepository.coverLetterXSLPath);
	}

	public Resource exportCoverLetterByIdAsXHTML(String id)
			throws DocumentLoadingFailedException, XMLDBException, IOException, TransformerException {
		String coverLetterXHTML = getCoverLetterByIdAndReturnAsXHTML(id);

		Path file = Paths.get("coverLetterId" + id + ".html");
		Files.write(file, coverLetterXHTML.getBytes(StandardCharsets.UTF_8));

		return new UrlResource(file.toUri());
	}

	public Resource exportCoverLetterByTitleAndVersionAsXHTML(String title, String version)
			throws DocumentLoadingFailedException, XMLDBException, IOException, TransformerException {
		String coverLetterXHTML = getCoverLetterByTitleAndVersionAndReturnAsXHTML(title, version);

		Path file = Paths.get("coverLetterForPublication" + title + "AndVersion" + version + ".html");
		Files.write(file, coverLetterXHTML.getBytes(StandardCharsets.UTF_8));

		return new UrlResource(file.toUri());
	}

	public Resource exportCoverLetterByIdAsPDF(String id)
			throws TransformerException, IOException, DocumentLoadingFailedException, XMLDBException {
		String coverLetterStr = coverLetterRepository.findById(id);

		ByteArrayOutputStream outputStream = xslTranspiler.generatePDF(coverLetterStr,
				CoverLetterRepository.coverLetterXSLPath, CoverLetterRepository.coverLetterXSLFOPath);

		Path file = Paths.get("coverLetterId" + id + ".pdf");
		Files.write(file, outputStream.toByteArray());

		return new UrlResource(file.toUri());
	}

	public Resource exportCoverLetterByIdAsPDF(String title, String version)
			throws TransformerException, IOException, DocumentLoadingFailedException, XMLDBException {
		String coverLetterStr = coverLetterRepository.findByTitleAndVersion(title, version);

		ByteArrayOutputStream outputStream = xslTranspiler.generatePDF(coverLetterStr,
				CoverLetterRepository.coverLetterXSLPath, CoverLetterRepository.coverLetterXSLFOPath);

		Path file = Paths.get("coverLetterForPublication" + title + "AndVersion" + version + ".pdf");
		Files.write(file, outputStream.toByteArray());

		return new UrlResource(file.toUri());
	}
}
