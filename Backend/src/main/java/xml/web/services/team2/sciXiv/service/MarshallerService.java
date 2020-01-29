package xml.web.services.team2.sciXiv.service;

import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Autowired;

import xml.web.services.team2.sciXiv.exception.OperationFailureException;

import java.io.ByteArrayOutputStream;

public class MarshallerService {

	// OBJECT => XML

	@Autowired
	Marshaller marshaller;

	public String marshal(Object data) {
		ByteArrayOutputStream result = new ByteArrayOutputStream();

		try {
			marshaller.marshal(data, result);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new OperationFailureException("[OperationFailureException] Failed to serialize an object to XML.");
		}
		return new String(result.toByteArray(), StandardCharsets.UTF_8);
	}
}
