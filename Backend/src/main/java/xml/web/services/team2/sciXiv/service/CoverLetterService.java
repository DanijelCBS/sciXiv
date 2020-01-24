package xml.web.services.team2.sciXiv.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
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
		String scientificPublication = scientificPublicationRepository.findByName(scientificPublicationId);
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
		String spName = getScientificPublicationsID(coverLetter);

		// set cover letter for publication id=name
		setCoverLetter(spName, coverLetterId);

		return coverLetterId;
	}

	public String update(String coverLetter) throws SAXException, ParserConfigurationException, IOException,
			DocumentLoadingFailedException, XMLDBException, DocumentStoringFailedException {
		String coverLetterId = coverLetterRepository.update(coverLetter);

		return coverLetterId;
	}

	public void delete(String id, XMLConnectionProperties conn) throws XMLDBException {
		coverLetterRepository.delete(id, conn);
	}

}
