package xml.web.services.team2.sciXiv.utils.xslt;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.apache.fop.apps.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;

@Component
public class XSLFOTransformer {

    @Autowired
    private FopFactory fopFactory;

    @Autowired
    private TransformerFactoryImpl transformerFactory;

    public String generateHTML(String xmlString, String templatePath) throws TransformerException {
        File xslFile = new File(templatePath);
        StreamSource transformSource = new StreamSource(xslFile);
        StreamSource source = new StreamSource(new StringReader(xmlString));

        Transformer transformer = transformerFactory.newTransformer(transformSource);
        transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xhtml");

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Result res = new StreamResult(outStream);
        transformer.transform(source, res);

        return outStream.toString();
    }

    public ByteArrayOutputStream generatePDF(String xmlString, String templatePath) throws TransformerException, FOPException {

        // Point to the XSL-FO file
        File xslFile = new File(templatePath);

        // Create transformation source
        StreamSource transformSource = new StreamSource(xslFile);

        // Initialize the transformation subject
        StreamSource source = new StreamSource(new StringReader(xmlString));

        // Initialize user agent needed for the transformation
        FOUserAgent userAgent = fopFactory.newFOUserAgent();

        // Create the output stream to store the results
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        // Initialize the XSL-FO transformer object
        Transformer xslFoTransformer = transformerFactory.newTransformer(transformSource);

        // Construct FOP instance with desired output format
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, outStream);

        // Resulting SAX events
        Result res = new SAXResult(fop.getDefaultHandler());

        // Start XSLT transformation and FOP processing
        xslFoTransformer.transform(source, res);

        return outStream;
    }
}
