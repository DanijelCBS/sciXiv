package xml.web.services.team2.sciXiv.utils.database;

import java.io.IOException;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import xml.web.services.team2.sciXiv.utils.connection.AuthenticationUtilities;
import xml.web.services.team2.sciXiv.utils.connection.AuthenticationUtilities.XMLConnectionProperties;

public class DBExtractor {

	static String TARGET_NAMESPACE = "http://ftn.uns.ac.rs/coverLetter";

	public static ResourceSet executeXPathQuery(String collectionNameInDb, String xPathQuery, String targetNamespace)
			throws IOException, XMLDBException {

		ResourceSet result = null;

		XMLConnectionProperties conn = AuthenticationUtilities.loadProperties();

		// driver initialization
		System.out.println("[INFO] Loading driver class: " + conn.driver);

		Class<?> cl;
		Database db;

		try {
			cl = Class.forName(conn.driver);
			db = (Database) cl.newInstance();
			db.setProperty("create-database", "true");
			DatabaseManager.registerDatabase(db);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		Collection collection = null;

		try {
			System.out.println("[INFO] Retrieving the collection: " + collectionNameInDb);
			collection = DatabaseManager.getCollection(conn.uri + collectionNameInDb);

			if (collection == null) {
				return null;
			}

			XPathQueryService xPathService = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
			xPathService.setProperty("indent", "yes");

			// Set default namespace to: http://ftn.uns.ac.rs/"
			xPathService.setNamespace("", TARGET_NAMESPACE);
			System.out.println("[INFO] Invoking XPath query service for following query: " + xPathQuery);
			result = xPathService.query(xPathQuery);
			System.out.println("[INFO] Computing the results... ");
		} finally {
			if (collection != null) {
				try {
					collection.close();
				} catch (XMLDBException xmldbe) {
					xmldbe.printStackTrace();
				}
			}
		}

		return result;
	}

	public static String getTARGET_NAMESPACE() {
		return TARGET_NAMESPACE;
	}

	public static void setTARGET_NAMESPACE(String tARGET_NAMESPACE) {
		TARGET_NAMESPACE = tARGET_NAMESPACE;
	}

}