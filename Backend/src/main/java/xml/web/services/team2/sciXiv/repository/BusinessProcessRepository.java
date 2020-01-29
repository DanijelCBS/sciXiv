package xml.web.services.team2.sciXiv.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.exist.xmldb.EXistResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import xml.web.services.team2.sciXiv.model.businessProcess.BusinessProcess;
import xml.web.services.team2.sciXiv.service.MarshallerService;
import xml.web.services.team2.sciXiv.service.UnmarshallerService;
import xml.web.services.team2.sciXiv.utils.database.BasicOperations;
import xml.web.services.team2.sciXiv.utils.database.DBExtractor;
import xml.web.services.team2.sciXiv.utils.factory.XMLConnectionPropertiesFactory;

@Repository
public class BusinessProcessRepository {

	private static final String collectionName = "/db/sciXiv/businessProcesses";

	static String TARGET_NAMESPACE_BUSINESS_PROCESS = "http://ftn.uns.ac.rs/businessProcess";

	@Autowired
	private MarshallerService marshallerService;

	@Autowired
	private UnmarshallerService unMarshallerService;

	@Autowired
	BasicOperations basicOperations;

	@Autowired
	XMLConnectionPropertiesFactory xmlConnectionPool;

	public String save(String xmlProcess, String scientificPublicationTitle) throws Exception {
		DBExtractor.save(collectionName, scientificPublicationTitle, xmlProcess);
		return scientificPublicationTitle;
	}

	public String saveObject(BusinessProcess businessProcess) throws Exception {
		String xmlProcess = marshallerService.marshal(businessProcess);
		DBExtractor.save(collectionName, businessProcess.getScientificPublicationTitle(), xmlProcess);
		return businessProcess.getScientificPublicationTitle();
	}

	public String findByScientificPublicationTitle(String scientificPublicationTitle)
			throws IOException, XMLDBException {
		String xPathSelector = String.format("//businessProcess[scientificPublicationTitle[text()='%s']]",
				scientificPublicationTitle);
		ResourceSet resultSet = DBExtractor.executeXPathQuery(collectionName, xPathSelector,
				TARGET_NAMESPACE_BUSINESS_PROCESS);

		if (resultSet == null)
			return null;

		ResourceIterator i = resultSet.getIterator();

		XMLResource res = null;

		String retVal = null;

		if (i.hasMoreResources()) {
			res = (XMLResource) i.nextResource();
			retVal = res.getContent().toString();
		}

		if (res != null)
			try {
				((EXistResource) res).freeResources();
			} catch (XMLDBException exception) {
				exception.printStackTrace();
			}

		return retVal;
	}

	public BusinessProcess findObjectByScientificPublicationTitle(String scientificPublicationTitle)
			throws IOException, XMLDBException {
		String xmlProcess = findByScientificPublicationTitle(scientificPublicationTitle);

		if (xmlProcess == null) {
			return null;
		}
		return (BusinessProcess) unMarshallerService.unmarshal(xmlProcess);
	}

	public List<BusinessProcess> findAll() throws IOException, XMLDBException {
		String xPathSelector = "//businessProcess";
		ResourceSet resultSet = DBExtractor.executeXPathQuery(collectionName, xPathSelector,
				TARGET_NAMESPACE_BUSINESS_PROCESS);

		if (resultSet == null)
			return null;

		ResourceIterator i = resultSet.getIterator();

		XMLResource res = null;

		List<BusinessProcess> processes = new ArrayList<>();

		while (i.hasMoreResources()) {
			res = (XMLResource) i.nextResource();
			processes.add((BusinessProcess) unMarshallerService.unmarshal(res.getContent().toString()));
		}

		if (res != null)
			try {
				((EXistResource) res).freeResources();
			} catch (XMLDBException exception) {
				exception.printStackTrace();
			}

		return processes;
	}

	public List<BusinessProcess> findByReviewerEmail(String reviewerEmail) throws IOException, XMLDBException {
		String xPathSelector = String.format(
				"//businessProcess[reviewerAssignments/reviewerAssignment/reviewerEmail[text()='%s']]", reviewerEmail);
		ResourceSet resultSet = DBExtractor.executeXPathQuery(collectionName, xPathSelector,
				TARGET_NAMESPACE_BUSINESS_PROCESS);

		if (resultSet == null)
			return null;

		ResourceIterator i = resultSet.getIterator();

		XMLResource res = null;

		List<BusinessProcess> processes = new ArrayList<>();

		while (i.hasMoreResources()) {
			res = (XMLResource) i.nextResource();
			processes.add((BusinessProcess) unMarshallerService.unmarshal(res.getContent().toString()));
		}

		if (res != null)
			try {
				((EXistResource) res).freeResources();
			} catch (XMLDBException exception) {
				exception.printStackTrace();
			}

		return processes;
	}
}
