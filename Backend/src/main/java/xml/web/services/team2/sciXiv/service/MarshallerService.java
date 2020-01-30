package xml.web.services.team2.sciXiv.service;

import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xml.web.services.team2.sciXiv.exception.OperationFailureException;

import java.io.ByteArrayOutputStream;

@Service
public class MarshallerService {

	// OBJECT => XML

	public String marshal(Object data, String dataClassesPackage) throws JAXBException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		JAXBContext context;

        context = JAXBContext.newInstance(dataClassesPackage);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		try {
			marshaller.marshal(data, result);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new OperationFailureException("[OperationFailureException] Failed to serialize an object to XML.");
		}
		return new String(result.toByteArray(), StandardCharsets.UTF_8);
	}
}
