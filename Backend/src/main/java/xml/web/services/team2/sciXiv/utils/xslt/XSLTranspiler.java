package xml.web.services.team2.sciXiv.utils.xslt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import com.itextpdf.html2pdf.ConverterProperties;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.itextpdf.html2pdf.HtmlConverter;

import org.apache.fop.apps.MimeConstants;

import net.sf.saxon.TransformerFactoryImpl;
import xml.web.services.team2.sciXiv.repository.CoverLetterRepository;
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

	public ByteArrayOutputStream generatePDf(String sourceStr, String xslt_fo_TemplatePath) throws Exception {
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
		// System.out.println(notificationHTML);
	}

	public void testXSLTranspiler2() throws TransformerException {
		String coverLetterStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<coverLetter xmlns=\"http://ftn.uns.ac.rs/coverLetter\"\r\n"
				+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "    xsi:schemaLocation=\"http://ftn.uns.ac.rs/coverLetter file:/C:/Users/Nikola%20Zubic/Desktop/XMLWEB/sciXiv/Backend/src/main/resources/static/xmlSchemas/coverLetter.xsd\" submissionDate=\"2006-05-04\" id=\"id1\">\r\n"
				+ "    <publicationTitle>publicationTitle0</publicationTitle>\r\n" + "    <version>50</version>\r\n"
				+ "    <author>\r\n" + "        <name>Ian Goodfellow</name>\r\n"
				+ "        <educationTitle>phD</educationTitle>\r\n" + "        <affiliation>Apple</affiliation>\r\n"
				+ "        <city>Los Angeles</city>\r\n" + "        <state>California, USA</state>\r\n"
				+ "        <phoneNumber>12948824895325779</phoneNumber>\r\n"
				+ "        <email>iangoodfellow@apple.com</email>\r\n"
				+ "        <signature>ZGVmYXVsdA==</signature>\r\n" + "    </author>\r\n" + "    <targetPublisher>\r\n"
				+ "        <editor>Machine Learning</editor>\r\n" + "        <journal>Apple AI journal</journal>\r\n"
				+ "    </targetPublisher>\r\n" + "    <content>\r\n" + "        <paragraph>\r\n"
				+ "            <boldText>Ovo je boldovan tekst.</boldText>\r\n"
				+ "            <emphasizedText>Ovo je emphasized tekst.</emphasizedText>\r\n"
				+ "            <quote>\r\n" + "                <source>Nikola Zubic</source>\r\n"
				+ "                <quoteContent>Stize AI, ne plasite se braco, sve cemo mi to kroz mreze da propustimo. Neka bude feed-forward propagacija neprestana, nek se ori aktivacija za aktivacijom.</quoteContent>\r\n"
				+ "            </quote>\r\n" + "            <list ordered=\"true\">\r\n"
				+ "                <listItem>Jabuka</listItem>\r\n" + "                <listItem>Kruska</listItem>\r\n"
				+ "            </list>\r\n" + "            <boldText>Boldovani tekst..</boldText>\r\n"
				+ "            <emphasizedText>Emphasized tekst...</emphasizedText>\r\n" + "            <quote>\r\n"
				+ "                <source>Izvor</source>\r\n"
				+ "                <quoteContent>Evo citira se nesto iz izvora</quoteContent>\r\n"
				+ "            </quote>\r\n" + "            <list ordered=\"false\">\r\n"
				+ "                <listItem>Nije ordered</listItem>\r\n"
				+ "                <listItem>Sta da se radi</listItem>\r\n" + "            </list>\r\n"
				+ "        </paragraph>\r\n" + "        <paragraph>\r\n" + "        <boldText>boldText2</boldText>\r\n"
				+ "        <emphasizedText>emphasizedText2</emphasizedText>\r\n" + "        <quote>\r\n"
				+ "            <source>source2</source>\r\n"
				+ "            <quoteContent>quoteContent2</quoteContent>\r\n" + "            </quote>\r\n"
				+ "            <list ordered=\"true\">\r\n" + "                <listItem>Ordered je</listItem>\r\n"
				+ "                <listItem>Svi srecni</listItem>\r\n" + "            </list>\r\n"
				+ "            <boldText>boldText3</boldText>\r\n"
				+ "            <emphasizedText>emphasizedText3</emphasizedText>\r\n" + "            <quote>\r\n"
				+ "                <source>source3</source>\r\n"
				+ "                <quoteContent>quoteContent3</quoteContent>\r\n" + "            </quote>-->\r\n"
				+ "            <list ordered=\"false\">\r\n" + "                <listItem>listItem6</listItem>\r\n"
				+ "                <listItem>listItem7</listItem>\r\n" + "            </list>\r\n"
				+ "        </paragraph>\r\n" + "    </content>\r\n" + "</coverLetter>";
		String coverLetterHTML = generateHTML(coverLetterStr, CoverLetterRepository.coverLetterXSLPath);
		System.out.println(coverLetterHTML);
	}

	public void testXSLTranspiler3() throws Exception {
		String coverLetterStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<coverLetter xmlns=\"http://ftn.uns.ac.rs/coverLetter\"\r\n"
				+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "    xsi:schemaLocation=\"http://ftn.uns.ac.rs/coverLetter file:/C:/Users/Nikola%20Zubic/Desktop/XMLWEB/sciXiv/Backend/src/main/resources/static/xmlSchemas/coverLetter.xsd\" submissionDate=\"2006-05-04\" id=\"id1\">\r\n"
				+ "    <publicationTitle>publicationTitle0</publicationTitle>\r\n" + "    <version>50</version>\r\n"
				+ "    <author>\r\n" + "        <name>Ian Goodfellow</name>\r\n"
				+ "        <educationTitle>phD</educationTitle>\r\n" + "        <affiliation>Apple</affiliation>\r\n"
				+ "        <city>Los Angeles</city>\r\n" + "        <state>California, USA</state>\r\n"
				+ "        <phoneNumber>12948824895325779</phoneNumber>\r\n"
				+ "        <email>iangoodfellow@apple.com</email>\r\n"
				+ "        <signature>ZGVmYXVsdA==</signature>\r\n" + "    </author>\r\n" + "    <targetPublisher>\r\n"
				+ "        <editor>Machine Learning</editor>\r\n" + "        <journal>Apple AI journal</journal>\r\n"
				+ "    </targetPublisher>\r\n" + "    <content>\r\n" + "        <paragraph>\r\n"
				+ "            <boldText>Ovo je boldovan tekst.</boldText>\r\n"
				+ "            <emphasizedText>Ovo je emphasized tekst.</emphasizedText>\r\n"
				+ "            <quote>\r\n" + "                <source>Nikola Zubic</source>\r\n"
				+ "                <quoteContent>Stize AI, ne plasite se braco, sve cemo mi to kroz mreze da propustimo. Neka bude feed-forward propagacija neprestana, nek se ori aktivacija za aktivacijom.</quoteContent>\r\n"
				+ "            </quote>\r\n" + "            <list ordered=\"true\">\r\n"
				+ "                <listItem>Jabuka</listItem>\r\n" + "                <listItem>Kruska</listItem>\r\n"
				+ "            </list>\r\n" + "            <boldText>Boldovani tekst..</boldText>\r\n"
				+ "            <emphasizedText>Emphasized tekst...</emphasizedText>\r\n" + "            <quote>\r\n"
				+ "                <source>Izvor</source>\r\n"
				+ "                <quoteContent>Evo citira se nesto iz izvora</quoteContent>\r\n"
				+ "            </quote>\r\n" + "            <list ordered=\"false\">\r\n"
				+ "                <listItem>Nije ordered</listItem>\r\n"
				+ "                <listItem>Sta da se radi</listItem>\r\n" + "            </list>\r\n"
				+ "        </paragraph>\r\n" + "        <paragraph>\r\n" + "        <boldText>boldText2</boldText>\r\n"
				+ "        <emphasizedText>emphasizedText2</emphasizedText>\r\n" + "        <quote>\r\n"
				+ "            <source>source2</source>\r\n"
				+ "            <quoteContent>quoteContent2</quoteContent>\r\n" + "            </quote>\r\n"
				+ "            <list ordered=\"true\">\r\n" + "                <listItem>Ordered je</listItem>\r\n"
				+ "                <listItem>Svi srecni</listItem>\r\n" + "            </list>\r\n"
				+ "            <boldText>boldText3</boldText>\r\n"
				+ "            <emphasizedText>emphasizedText3</emphasizedText>\r\n" + "            <quote>\r\n"
				+ "                <source>source3</source>\r\n"
				+ "                <quoteContent>quoteContent3</quoteContent>\r\n" + "            </quote>-->\r\n"
				+ "            <list ordered=\"false\">\r\n" + "                <listItem>listItem6</listItem>\r\n"
				+ "                <listItem>listItem7</listItem>\r\n" + "            </list>\r\n"
				+ "        </paragraph>\r\n" + "    </content>\r\n" + "</coverLetter>";
		generatePDF2(coverLetterStr, CoverLetterRepository.coverLetterXSLPath,
				CoverLetterRepository.coverLetterXSLFOPath);
	}

	public ByteArrayOutputStream generatePDF(String originalXMLDocument, String xsltFile, String xslt_fo_TemplatePath)
			throws TransformerException, IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		String inputForPdf = generateHTML(originalXMLDocument, xsltFile);
		ConverterProperties converterProperties = new ConverterProperties();
		HtmlConverter.convertToPdf(new ByteArrayInputStream(inputForPdf.getBytes(StandardCharsets.UTF_8)), result,
				converterProperties);
		return result;
	}

	public ByteArrayOutputStream generatePDF2(String originalXMLDocument, String xsltFile, String xslt_fo_TemplatePath)
			throws TransformerException, IOException {
		File pdfDest = new File("C:/Users/Nikola Zubic/Desktop/testing.pdf");
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		String inputForPdf = generateHTML(originalXMLDocument, xsltFile);
		ConverterProperties converterProperties = new ConverterProperties();
		HtmlConverter.convertToPdf(new ByteArrayInputStream(inputForPdf.getBytes(StandardCharsets.UTF_8)),
				new FileOutputStream(pdfDest), converterProperties);
		return result;
	}

}
