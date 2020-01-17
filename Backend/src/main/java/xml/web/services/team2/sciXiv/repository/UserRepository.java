package xml.web.services.team2.sciXiv.repository;

import org.exist.xmldb.EXistResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;
import xml.web.services.team2.sciXiv.exception.UserRetrievingFailedException;
import xml.web.services.team2.sciXiv.exception.UserSavingFailedException;
import xml.web.services.team2.sciXiv.model.TPublications;
import xml.web.services.team2.sciXiv.model.TRole;
import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.model.Users;
import xml.web.services.team2.sciXiv.utils.connection.XMLConnectionProperties;
import xml.web.services.team2.sciXiv.utils.database.BasicOperations;
import xml.web.services.team2.sciXiv.utils.database.UpdateTemplate;
import xml.web.services.team2.sciXiv.utils.factory.XMLConnectionPropertiesFactory;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

@Repository
public class UserRepository {

    private static String usersCollection = "/db/sciXiv/users";
    private static String usersDocument = "users.xml";

    @Autowired
    UpdateTemplate updateService;

    @Autowired
    BasicOperations basicOperations;

    @Autowired
    XMLConnectionPropertiesFactory connectionPool;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    public TUser save(TUser user) throws UserSavingFailedException {
        XMLConnectionProperties conn = null;
        try {
            conn = connectionPool.getConnection();
            String userXML = createUserXmlFragment(user);
            Collection col = basicOperations.getOrCreateCollection(usersCollection, 0, conn);
            updateService.append(col, usersDocument, "", "/users", userXML);
        }
        catch (Exception e) {
        	e.printStackTrace();
            throw new UserSavingFailedException("Failed to save user to database");
        }
        finally {
            connectionPool.releaseConnection(conn);
        }

        return user;
    }

    public TUser getByEmail(String email) throws UserRetrievingFailedException {
        Collection col;
        TUser user = null;
        XMLConnectionProperties conn = null;
        try {
            conn = connectionPool.getConnection();
            col = basicOperations.getOrCreateCollection(usersCollection, 0, conn);
            XPathQueryService xPathService = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xPathService.setProperty("indent", "yes");
            ResourceSet result = xPathService.query(String.format("doc(\"%s\")//user[email = \"%s\"]", usersDocument, email));
            ResourceIterator it = result.getIterator();
            Resource res = null;

            while(it.hasMoreResources()) {
                try {
                    res = it.nextResource();
                    user = unmarshal((XMLResource) res);
                } finally {
                    try {
                        ((EXistResource)res).freeResources();
                    } catch (XMLDBException xe) {
                        xe.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e) {
        	e.printStackTrace();
            throw new UserRetrievingFailedException("Failed to get user from database");
        }
        finally {
            connectionPool.releaseConnection(conn);
        }

        return user;
    }
    
    @PostConstruct
    private void initializeUsersIfNone() throws UserSavingFailedException {
    	XMLConnectionProperties conn = null;
    	try {
			conn = connectionPool.getConnection();
			Collection col = basicOperations.getOrCreateCollection(usersCollection, 0, conn);
			XMLResource usersFile = (XMLResource) col.getResource(usersDocument);
			if (usersFile == null) {
				Users users = new Users();
				TUser systemAdmin = new TUser();
				systemAdmin.setEmail("system@admin.com");
				systemAdmin.setPassword(passwordEncoder.encode("admin"));
				systemAdmin.setRole(TRole.EDITOR);
				systemAdmin.setFirstName("System");
				systemAdmin.setLastName("Administrator");
				systemAdmin.setOwnPublications(new TPublications());
				systemAdmin.setPublicationsToReview(new TPublications());
				users.getUsers().add(systemAdmin);
				String initialUsersContent = marshal(users);
				usersFile = (XMLResource) col.createResource(usersDocument, XMLResource.RESOURCE_TYPE);
				usersFile.setContent(initialUsersContent);
				col.storeResource(usersFile);
			} 
		} 
    	catch (Exception e) {
    		e.printStackTrace();
			throw new UserSavingFailedException("Failed to save user to database");
		}
    	finally {
            connectionPool.releaseConnection(conn);
        }
    }

    private String createUserXmlFragment(TUser user) throws JAXBException {
        OutputStream os = new ByteArrayOutputStream();
        JAXBContext context;

        context = JAXBContext.newInstance("xml.web.services.team2.sciXiv.model");
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(user, os);
        String userXml = os.toString();
        // Remove xml declaration from first line
        String pureUserXml = userXml.substring(userXml.indexOf('\n') + 1);

        return pureUserXml;
    }
    
    private String marshal(Users users) throws JAXBException {
        OutputStream os = new ByteArrayOutputStream();
        JAXBContext context;

        context = JAXBContext.newInstance("xml.web.services.team2.sciXiv.model");
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(users, os);

        return os.toString();
    }

    private TUser unmarshal(XMLResource res) throws JAXBException, XMLDBException {
        TUser user = null;

        JAXBContext context = JAXBContext.newInstance("xml.web.services.team2.sciXiv.model");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        user = (TUser) unmarshaller.unmarshal(res.getContentAsDOM());

        return user;
    }
}
