package xml.web.services.team2.sciXiv.service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xml.web.services.team2.sciXiv.exception.OperationFailureException;
import xml.web.services.team2.sciXiv.model.TUser;

@Service
public class UnmarshallerService {

	// XML => OBJECT

	public Object unmarshal(String xmlSource, String dataClassesPackage) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(dataClassesPackage);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        
		try {
			return unmarshaller.unmarshal(new ByteArrayInputStream(xmlSource.getBytes(StandardCharsets.UTF_8)));
		} catch (JAXBException e) {
			throw new OperationFailureException("[OperationFailureException] Failed to deserialize XML to an object.");
		}
	}
}
