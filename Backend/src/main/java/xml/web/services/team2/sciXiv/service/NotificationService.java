package xml.web.services.team2.sciXiv.service;

import javax.mail.MessagingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xml.web.services.team2.sciXiv.repository.NotificationRepository;
import xml.web.services.team2.sciXiv.utils.dom.DOMParser;
import xml.web.services.team2.sciXiv.utils.xslt.XSLTranspiler;

import org.w3c.dom.Document;

@Service
public class NotificationService {

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private XSLTranspiler xslTranspiler;

	public void sendNotificationByMail(String[] emails, Document document)
			throws TransformerException, ParserConfigurationException, MessagingException {
		String notificationStr = DOMParser.doc2String(document);
		String notificationHTML = xslTranspiler.generateHTML(notificationStr,
				NotificationRepository.notificationXSLPath);
		emailService.sendEmail(emails, notificationHTML);

	}
}
