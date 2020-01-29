package xml.web.services.team2.sciXiv.service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;

import xml.web.services.team2.sciXiv.exception.OperationFailureException;

public class UnmarshallerService {

	// XML => OBJECT

	@Autowired
	Unmarshaller unmarshaller;

	public Object unmarshal(String xmlSource) {
		try {
			return unmarshaller.unmarshal(new ByteArrayInputStream(xmlSource.getBytes(StandardCharsets.UTF_8)));
		} catch (JAXBException e) {
			throw new OperationFailureException("[OperationFailureException] Failed to deserialize XML to an object.");
		}
	}
}
