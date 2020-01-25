package xml.web.services.team2.sciXiv.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Async
	public boolean sendEmail(String[] emails, String message) throws MessagingException {
		// Create a new JavaMail MimeMessage for the underlying JavaMail Session of this
		// sender. Needs to be called to create MimeMessage instances that can be
		// prepared by the client and passed to send(MimeMessage).
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();

		// Create a new MimeMessageHelper for the given MimeMessage,in multi-part mode
		// (supporting alternative texts, inline elements and attachments) if requested.
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

		// Set the given text directly as content in non-multipart mode or as default
		// body part in multi-part mode.The "html" flag determines the content type to
		// apply.
		mimeMessageHelper.setText(message, true);

		// mails to which we are sending a message
		mimeMessageHelper.setTo(emails);

		// Set the subject of the message, using the correct encoding.
		mimeMessageHelper.setSubject("Email Service");

		// Send the given JavaMail MIME message.The message needs to have been created
		// with createMimeMessage().
		javaMailSender.send(mimeMessage);

		return true;
	}
}
