package xml.web.services.team2.sciXiv.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Repository;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import xml.web.services.team2.sciXiv.utils.dom.DOMParser;

@Repository
public class NotificationRepository {

	private static final String notificationXsdSchemaPath = "src/main/resources/static/xmlSchemas/notification.xsd";
	private static final String notificationTemplate = "src/main/resources/static/templates/notificationTemplate.xml";

	public static Document retrieveNotification() throws IOException, SAXException, ParserConfigurationException {
		File f = new File(notificationTemplate);
		BufferedReader br = new BufferedReader(new FileReader(f));
		StringBuilder sb = new StringBuilder("");
		String line;

		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		br.close();

		String notificationStr = sb.toString();

		Document document = DOMParser.buildDocument(notificationStr, notificationXsdSchemaPath);

		return document;
	}

}
