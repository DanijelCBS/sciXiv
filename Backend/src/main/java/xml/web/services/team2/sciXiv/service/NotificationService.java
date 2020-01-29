package xml.web.services.team2.sciXiv.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.MessagingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.tools.ant.taskdefs.SendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.repository.NotificationRepository;
import xml.web.services.team2.sciXiv.utils.dom.DOMParser;
import xml.web.services.team2.sciXiv.utils.xslt.XSLTranspiler;

@Service
public class NotificationService {

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private XSLTranspiler xslTranspiler;

	public void notificationSendRequest(String[] emails, String content, String publicationName, TUser sender,
			TUser reciever)
			throws IOException, SAXException, ParserConfigurationException, TransformerException, MessagingException {
		Document document = notificationRepository.retrieveNotification();
		document = fillData(document, content, publicationName, sender, reciever);
		sendNotificationByMail(emails, document);

	}

	public void publicationAccepted(String[] emails, String publicationName, TUser sender, TUser reciever)
			throws IOException, SAXException, ParserConfigurationException, TransformerException, MessagingException {
		String content = "Scientific publication has been successfully accepted. Congratulations!";
		notificationSendRequest(emails, content, publicationName, sender, reciever);
	}

	public void publicationRejected(String[] emails, String publicationName, TUser sender, TUser reciever)
			throws IOException, SAXException, ParserConfigurationException, TransformerException, MessagingException {
		String content = "Unfortunately, scientific publication has been rejected.";
		notificationSendRequest(emails, content, publicationName, sender, reciever);
	}

	public void coverLetterAdded(String[] emails, String publicationName, TUser sender, TUser reciever)
			throws IOException, SAXException, ParserConfigurationException, TransformerException, MessagingException {
		String content = "Cover letter has been successfully added. Congratulations!";
		notificationSendRequest(emails, content, publicationName, sender, reciever);
	}

	public Document fillData(Document document, String content, String publicationId, TUser sender, TUser reciever) {
		document.getElementsByTagName("content").item(0).setTextContent(content);
		document.getElementsByTagName("publicationName").item(0).setTextContent(publicationId);
		Date currentDate = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		document.getElementsByTagName("notification").item(0).getAttributes().getNamedItem("date")
				.setTextContent((df.format(currentDate)));

		document.getElementsByTagName("sender").item(0).getChildNodes().item(0).setTextContent(sender.getFirstName());
		document.getElementsByTagName("sender").item(0).getChildNodes().item(1)
				.setTextContent(sender.getRole().toString());
		document.getElementsByTagName("sender").item(0).getChildNodes().item(2).setTextContent(sender.getEmail());

		document.getElementsByTagName("reciever").item(0).getChildNodes().item(0)
				.setTextContent(reciever.getFirstName());
		document.getElementsByTagName("reciever").item(0).getChildNodes().item(1)
				.setTextContent(reciever.getRole().toString());
		document.getElementsByTagName("reciever").item(0).getChildNodes().item(2).setTextContent(reciever.getEmail());

		return document;
	}

	public Document fillDataWithoutReciever(Document document, String content, String publicationId, TUser sender) {
		document.getElementsByTagName("content").item(0).setTextContent(content);
		document.getElementsByTagName("publicationName").item(0).setTextContent(publicationId);
		Date currentDate = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		document.getElementsByTagName("notification").item(0).getAttributes().getNamedItem("date")
				.setTextContent((df.format(currentDate)));

		document.getElementsByTagName("sender").item(0).getChildNodes().item(0).setTextContent(sender.getFirstName());
		document.getElementsByTagName("sender").item(0).getChildNodes().item(1)
				.setTextContent(sender.getRole().toString());
		document.getElementsByTagName("sender").item(0).getChildNodes().item(2).setTextContent(sender.getEmail());

		return document;
	}

	public void addedReviewerAssignment(String[] emails, String scientificPublicationTitle, TUser sender)
			throws IOException, SAXException, ParserConfigurationException, TransformerException, MessagingException {
		Document document = notificationRepository.retrieveNotification();
		String content = "Reviewer assignment has been added for the publication with title: \""
				+ scientificPublicationTitle + "\"";
		document = fillDataWithoutReciever(document, content, scientificPublicationTitle, sender);
		sendNotificationByMail(emails, document);
	}

	public void addedReviewerAssignments(String[] emails, String scientificPublicationTitle, TUser sender)
			throws IOException, SAXException, ParserConfigurationException, TransformerException, MessagingException {
		Document document = notificationRepository.retrieveNotification();
		String content = "You have been chosen as reviewer for the scientific publication with title: \""
				+ scientificPublicationTitle + "\"";
		document = fillDataWithoutReciever(document, content, scientificPublicationTitle, sender);
		sendNotificationByMail(emails, document);
	}

	public void sendNotificationByMail(String[] emails, Document document)
			throws TransformerException, ParserConfigurationException, MessagingException {
		String notificationStr = DOMParser.doc2String(document);
		String notificationHTML = xslTranspiler.generateHTML(notificationStr,
				NotificationRepository.notificationXSLPath);
		emailService.sendEmail(emails, notificationHTML);

	}
}
