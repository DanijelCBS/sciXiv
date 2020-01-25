package xml.web.services.team2.sciXiv.utils.xslt;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

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

@Component
public class XSLTranspiler {

	// C:\Users\Nikola
	// Zubic\Desktop\XMLWEB\sciXiv\Backend\src\main\resources\static\conf

	private FopFactory fopFactory;

	private TransformerFactory transformerFactory;

	private static final String FOP_X_CONFIGURATION_PATH = "src/main/resources/static/conf/fop.xconf";

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
}
