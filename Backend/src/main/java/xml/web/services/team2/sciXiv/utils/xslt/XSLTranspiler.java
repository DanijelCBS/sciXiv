package xml.web.services.team2.sciXiv.utils.xslt;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.annotation.PostConstruct;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import java.io.ByteArrayOutputStream;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import org.apache.fop.apps.MimeConstants;

import net.sf.saxon.TransformerFactoryImpl;
import xml.web.services.team2.sciXiv.repository.NotificationRepository;
import xml.web.services.team2.sciXiv.utils.dom.DOMParser;

@Component
public class XSLTranspiler {

	// C:\Users\Nikola
	// Zubic\Desktop\XMLWEB\sciXiv\Backend\src\main\resources\static\conf

	private FopFactory fopFactory;

	private TransformerFactory transformerFactory;

	private static final String FOP_X_CONFIGURATION_PATH = "src/main/resources/static/conf/fop2.xconf";

	public XSLTranspiler() throws SAXException, IOException {
		fopFactory = FopFactory.newInstance(new File(FOP_X_CONFIGURATION_PATH));
		transformerFactory = new TransformerFactoryImpl();
	}

	public String generateHTML(String originalXMLDocument, String xsltFile) throws TransformerException {
		File templateFile = new File(xsltFile);
		StringWriter stringWriter = new StringWriter();
		StringReader stringReader = new StringReader(originalXMLDocument);

		Transformer xsltProcessor = transformerFactory.newTransformer(new StreamSource(templateFile));

		Source source = new StreamSource(stringReader);
		Result result = new StreamResult(stringWriter);

		xsltProcessor.transform(source, result);

		return stringWriter.toString();
	}

	public ByteArrayOutputStream generatePDF(String sourceStr, String xslt_fo_TemplatePath) throws Exception {
		File xslFile = new File(xslt_fo_TemplatePath);

		StreamSource transformSource = new StreamSource(xslFile);

		StreamSource source = new StreamSource(new StringReader(sourceStr));

		FOUserAgent userAgent = fopFactory.newFOUserAgent();

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		Transformer xslFoTransformer = transformerFactory.newTransformer(transformSource);

		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, outStream);

		Result res = new SAXResult(fop.getDefaultHandler());

		xslFoTransformer.transform(source, res);

		return outStream;
	}

	@PostConstruct
	public void testXSLTranspiler() throws TransformerException {
		String notificationStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<notification xmlns=\"http://ftn.uns.ac.rs/notification\"\r\n"
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ " xsi:schemaLocation=\"http://ftn.uns.ac.rs/notification file:/C:/Users/Nikola%20Zubic/Desktop/XMLWEB/sciXiv/Backend/src/main/resources/static/xmlSchemas/notification.xsd\" date=\"2006-05-04\">\r\n"
				+ "    <sender>\r\n" + "        <name>Gvido od Areca</name>\r\n" + "        <role>author</role>\r\n"
				+ "        <email>nestosedesilo@gmail.com</email>\r\n" + "    </sender>\r\n" + "    <reciever>\r\n"
				+ "        <name>Semso od Areca</name>\r\n" + "        <role>author</role>\r\n"
				+ "        <email>nestosezbiva@gmail.com</email>\r\n" + "    </reciever>\r\n"
				+ "    <publicationName>publicationName0</publicationName>\r\n"
				+ "    <notificationType>publication submitted</notificationType>\r\n"
				+ "    <content>Nam id pretium tortor, vel condimentum urna. Maecenas vestibulum vehicula tincidunt. Vestibulum rutrum dui condimentum nisl elementum, ac semper magna aliquam. Proin sed porttitor turpis, at fringilla eros. Nam sagittis leo et pharetra pretium. In hac habitasse platea dictumst. In consectetur maximus lorem, eu ullamcorper felis hendrerit iaculis. \r\n"
				+ "    </content>\r\n" + "</notification>\r\n" + "";
		String notificationHTML = generateHTML(notificationStr, NotificationRepository.notificationXSLPath);
		System.out.println(notificationHTML);
	}
}
