package xml.web.services.team2.sciXiv.utils.dom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import xml.web.services.team2.sciXiv.exception.DocumentParsingFailedException;
import xml.web.services.team2.sciXiv.exception.InvalidXmlException;

@Component
public class DOMParser implements ErrorHandler{

	@Autowired
	private DocumentBuilderFactory documentBuilderFactory;

	@Autowired
	private SchemaFactory schemaFactory;

	public DOMParser() {
	}

	public Document buildAndValidateDocument(String xmlFile, String schemaPath)
			throws ParserConfigurationException, SAXException, IOException, DocumentParsingFailedException {
		/*documentBuilderFactory.setValidating(false); // disable validation against DTD
		documentBuilderFactory.setSchema(schemaFactory.newSchema(new File(schemaPath)));*/ // enable validation against XML Schema
		DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
		//builder.setErrorHandler(this);

		Document document = builder.parse(new InputSource(new StringReader(xmlFile)));

		/*if (document == null)
			throw new DocumentParsingFailedException("Failed to parse document");*/

		return document;
	}

	public static Document buildDocument(String xmlString, String schemaPath)
			throws SAXException, ParserConfigurationException, IOException {
		// Defines a factory API that enables applications to obtain a parser that
		// produces DOM object trees from XML documents and obtain a new instance of a
		// DocumentBuilderFactory.
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

		File f = new File(schemaPath);
		Document newDocument;

		// Specifies that the parser produced by this code will validate documents as
		// they are parsed.
		documentBuilderFactory.setValidating(false);

		// Specifies that the parser produced by this code will provide support for XML
		// namespaces
		documentBuilderFactory.setNamespaceAware(true);

		// Specifies that the parser produced by this code will ignore comments.
		documentBuilderFactory.setIgnoringComments(true);

		// Specifies that the parsers created by this factory must eliminate whitespace
		// in element content (sometimes known loosely as'ignorable whitespace') when
		// parsing XML documents
		documentBuilderFactory.setIgnoringElementContentWhitespace(true);

		// W3C XML Schema Namespace URI. Defined to be
		// "http://www.w3.org/2001/XMLSchema".
		String xmlSchemaNamespaceURI = XMLConstants.W3C_XML_SCHEMA_NS_URI;

		// XSD (XML Schema Definition)
		SchemaFactory xsdSchemaFactory = SchemaFactory.newInstance(xmlSchemaNamespaceURI);

		Schema schema = null;

		// Parses the specified File as a schema and returns it as a Schema.
		schema = xsdSchemaFactory.newSchema(f);

		// Set the Schema to be used by parsers created from this factory.
		documentBuilderFactory.setSchema(schema);

		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		newDocument = documentBuilder.parse(new InputSource(new StringReader(xmlString)));
		if (newDocument != null) {
			System.out.println("[INFO] File successfully parsed.");
		} else {
			System.out.println("[WARN] Document is null.");
		}

		return newDocument;
	}

	public static String doc2String(Document document) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setValidating(false);
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setIgnoringComments(true);
		documentBuilderFactory.setIgnoringElementContentWhitespace(true);

		StringWriter stringWriter = new StringWriter();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		StreamResult streamResult = new StreamResult(stringWriter);
		transformer.transform(new DOMSource(document), streamResult);

		return stringWriter.toString();
	}

	public static Document buildDocumentNoSchema(String xmlString)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setValidating(false);
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setIgnoringComments(true);
		documentBuilderFactory.setIgnoringElementContentWhitespace(true);

		Document document;
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));

		if (document == null) {
			System.out.println("[WARN] Document is null.");
		} else {
			System.out.println("[INFO] File successfully parsed.");
		}
		return document;
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		exception.printStackTrace();
		throw new InvalidXmlException("Failed to parse document. Please check if document is valid.");
		
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		throw new InvalidXmlException("Failed to parse document. Please check if document is valid.");
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		throw new InvalidXmlException("Failed to parse document. Please check if document is valid.");
	}

}
