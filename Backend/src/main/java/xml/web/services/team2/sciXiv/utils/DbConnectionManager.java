package xml.web.services.team2.sciXiv.utils;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;

import org.exist.xmldb.EXistResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

import xml.web.services.team2.sciXiv.utils.AuthenticationUtilities.ConnectionProperties;

public class DbConnectionManager {
	
	private static ConnectionProperties conn;
	
	private static String dbUri = "db/sciXiv/";
	
	public DbConnectionManager() throws Exception {
		super();
		initialize();
	}
	
	public static void initialize() throws Exception {
		// load connection properties
		conn = AuthenticationUtilities.loadProperties();
		// initialize database driver
		Class<?> cl = Class.forName(conn.driver);

		// encapsulation of the database driver functionality
		Database database = (Database) cl.newInstance();
		database.setProperty("create-database", "true");

		// entry point for the API which enables you to get the Collection reference
		DatabaseManager.registerDatabase(database);
	}
	
	public static void saveDocument(String collectionName, String documentId, Node domContentRoot) throws XMLDBException {
		Collection col = null;
		XMLResource res = null;

		try {
			col = getOrCreateCollection(collectionName);

			/*
			 * create new XMLResource with a given id an id is assigned to the new resource
			 * if left empty (null)
			 */
			res = (XMLResource) col.createResource(documentId, XMLResource.RESOURCE_TYPE);

			res.setContentAsDOM(domContentRoot);

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
	
	public static Collection getOrCreateCollection(String collectionName) throws XMLDBException {
		return getOrCreateCollection(dbUri + collectionName, 0);
	}

	private static Collection getOrCreateCollection(String collectionUri, int pathSegmentOffset) throws XMLDBException {

		Collection col = DatabaseManager.getCollection(conn.uri + collectionUri, conn.user, conn.password);

		// create the collection if it does not exist
		if (col == null) {

			if (collectionUri.startsWith("/")) {
				collectionUri = collectionUri.substring(1);
			}

			String pathSegments[] = collectionUri.split("/");

			if (pathSegments.length > 0) {
				StringBuilder path = new StringBuilder();

				for (int i = 0; i <= pathSegmentOffset; i++) {
					path.append("/" + pathSegments[i]);
				}

				Collection startCol = DatabaseManager.getCollection(conn.uri + path, conn.user, conn.password);

				if (startCol == null) {

					// child collection does not exist

					String parentPath = path.substring(0, path.lastIndexOf("/"));
					Collection parentCol = DatabaseManager.getCollection(conn.uri + parentPath, conn.user,
							conn.password);

					CollectionManagementService mgt = (CollectionManagementService) parentCol
							.getService("CollectionManagementService", "1.0");

					col = mgt.createCollection(pathSegments[pathSegmentOffset]);

					col.close();
					parentCol.close();

				} else {
					startCol.close();
				}
			}
			return getOrCreateCollection(collectionUri, ++pathSegmentOffset);
		} else {
			return col;
		}
	}
	
	// Write test document
	public static void main(String[] args) {
		try {
			DbConnectionManager dbc = new DbConnectionManager();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			Element rect = document.createElement("rectangle");
			rect.setAttribute("unit", "px");
			document.appendChild(rect);
			
			Element width = document.createElement("width");
			width.appendChild(document.createTextNode("1920"));
			rect.appendChild(width);
			
			Element heigth = document.createElement("heigth");
			heigth.appendChild(document.createTextNode("1080"));
			rect.appendChild(heigth);
			DbConnectionManager.saveDocument("testDocuments", "rectangle.xml", document);
			System.out.println("Saved rectangle.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
