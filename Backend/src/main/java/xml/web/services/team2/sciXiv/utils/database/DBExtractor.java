package xml.web.services.team2.sciXiv.utils.database;

import java.io.IOException;

import org.exist.xmldb.EXistResource;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;
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

	public static void save(String collectionId, String documentId, String xmlData) throws Exception {
		XMLConnectionProperties props = AuthenticationUtilities.loadProperties();
		// initialize database driver

		System.out.println("[INFO] Loading driver class: " + props.driver);

		Class<?> cl = Class.forName(props.driver);

		// encapsulation of the database driver functionality

		Database database = (Database) cl.newInstance();

		database.setProperty("create-database", "true");

		// entry point for the API which enables you to get the Collection reference

		DatabaseManager.registerDatabase(database);

		// a collection of Resources stored within an XML database

		Collection col = null;

		XMLResource res = null;

		try {
			System.out.println("[INFO] Retrieving the collection: " + collectionId);
			col = getOrCreateCollection(collectionId, props);
			/*
			 * 
			 * create new XMLResource with a given id
			 * 
			 * an id is assigned to the new resource if left empty (null)
			 * 
			 */
			System.out.println("[INFO] Inserting the document: " + documentId);

			res = (XMLResource) col.createResource(documentId + ".xml", XMLResource.RESOURCE_TYPE);

			res.setContent(xmlData);

			System.out.println("[INFO] Storing the document: " + res.getId());

			col.storeResource(res);

		} finally {
			// don't forget to cleanup
			if (res != null) {
				try {
					((EXistResource) res).freeResources();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
			if (col != null) {
				try {
					col.close();
				} catch (XMLDBException xe) {
					xe.printStackTrace();
				}
			}
		}
	}

	private static Collection getOrCreateCollection(String collectionUri, XMLConnectionProperties props)
			throws XMLDBException {

		return getOrCreateCollection(collectionUri, 0, props);

	}

	private static Collection getOrCreateCollection(String collectionUri, int pathSegmentOffset,
			XMLConnectionProperties props) throws XMLDBException {

		Collection col = DatabaseManager

				.getCollection(props.uri + collectionUri, props.user, props.password);

		// create the collection if it does not exist
		if (col == null) {
			if (collectionUri.startsWith("/")) {
				collectionUri = collectionUri.substring(1);
			}
			String[] pathSegments = collectionUri.split("/");
			if (pathSegments.length > 0) {
				StringBuilder path = new StringBuilder();
				for (int i = 0; i <= pathSegmentOffset; i++) {
					path.append("/").append(pathSegments[i]);
				}
				Collection startCol = DatabaseManager.getCollection(props.uri + path, props.user, props.password);
				if (startCol == null) {

					// child collection does not exist
					String parentPath = path.substring(0, path.lastIndexOf("/"));
					Collection parentCol = DatabaseManager.getCollection(props.uri + parentPath, props.user,
							props.password);
					CollectionManagementService mgt = (CollectionManagementService) parentCol
							.getService("CollectionManagementService", "1.0");
					System.out.println("[INFO] Creating the collection: " + pathSegments[pathSegmentOffset]);
					col = mgt.createCollection(pathSegments[pathSegmentOffset]);
					col.close();
					parentCol.close();
				} else {
					startCol.close();
				}
			}

			return getOrCreateCollection(collectionUri, ++pathSegmentOffset, props);
		} else {
			return col;
		}
	}

	public static String getTARGET_NAMESPACE() {
		return TARGET_NAMESPACE;
	}

	public static void setTARGET_NAMESPACE(String tARGET_NAMESPACE) {
		TARGET_NAMESPACE = tARGET_NAMESPACE;
	}

}