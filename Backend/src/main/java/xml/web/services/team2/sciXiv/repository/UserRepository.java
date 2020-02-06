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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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
    
    private String editorEmail = "mihajlokusljic97@gmail.com";
    
    public UserRepository() {
    	super();
    }
    
    public TUser save(TUser user) throws UserSavingFailedException {
        XMLConnectionProperties conn = null;
        try {
            conn = connectionPool.getConnection();
            String userXML = createUserXmlFragment(user);
            Collection col = basicOperations.getOrCreateCollection(usersCollection, 0, conn);
            // delete old data
            this.deleteUser(user.getEmail(), col);
            // insert new data
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
                    break;
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
    
    public TUser getEditor() {
    	try {
    		
			TUser editor = this.getByEmail(this.editorEmail);
			if(editor == null) {
				editor = this.initEditor();
			}
			return editor;
			
		} catch (UserRetrievingFailedException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public List<TUser> getPossibleReviewersForPublicaton(String publicationTitle) throws UserRetrievingFailedException, UnsupportedEncodingException {
    	List<TUser> reviewers = new ArrayList<TUser>();
    	String publicationId = URLEncoder.encode(publicationTitle, "UTF-8");
    	String xQuery = String.format(
    			"for $user in doc(\"%s\")//user\n" + 
    			"where ($user/role = \"reviewer\" or $user/role = \"editor\")\n" + 
    			"and not (\"%s\" = $user/ownPublications/publicationID)\n" + 
    			"and not (\"%s\" = $user/publicationsToReview/publicationID)\n" + 
    			"return $user", 
    			usersCollection + "/" + usersDocument, publicationId, publicationTitle);
    	
    	Collection col;
        TUser user = null;
        XMLConnectionProperties conn = null;
        try {
            conn = connectionPool.getConnection();
            col = basicOperations.getOrCreateCollection(usersCollection, 0, conn);
            XPathQueryService xPathService = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xPathService.setProperty("indent", "yes");
            ResourceSet result = xPathService.query(xQuery);
            ResourceIterator it = result.getIterator();
            Resource res = null;

            while(it.hasMoreResources()) {
                try {
                    res = it.nextResource();
                    user = unmarshal((XMLResource) res);
                    reviewers.add(user);
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
    	
    	
    	return reviewers;
    }
    
    private TUser initEditor() {
    	TUser editor = new TUser();
		editor.setEmail(this.editorEmail);
		editor.setPassword(passwordEncoder.encode("admin"));
		editor.setRole(TRole.EDITOR);
		editor.setFirstName("System");
		editor.setLastName("Administrator");
		editor.setOwnPublications(new TPublications());
		editor.setPublicationsToReview(new TPublications());
		return editor;
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
				TUser systemAdmin = this.initEditor();
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
    
    private void deleteUser(String userEmail, Collection usersCollection) throws UserRetrievingFailedException, XMLDBException {
    	String xQuery = String.format(
    			"for $user in doc(\"%s\")//user\n" + 
    			"where $user/email = \"%s\"\n" + 
    			"return (update delete $user)", 
    			this.usersCollection + "/" + this.usersDocument,
    			userEmail);
    	
    	XPathQueryService xPathService = (XPathQueryService) usersCollection.getService("XPathQueryService", "1.0");
		xPathService.setProperty("indent", "yes");
		xPathService.query(xQuery);
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

	public List<TUser> findAuthorsOfPublication(String publicationTitle) throws UserRetrievingFailedException {
		List<TUser> authors = new ArrayList<TUser>();
    	String publicationId = publicationTitle.replace(" ", "");
    	String xQuery = String.format(
    			"for $user in doc(\"%s\")//user\n" + 
    			"where \"%s\" = $user/ownPublications/publicationID\n" + 
    			"return $user", 
    			usersCollection + "/" + usersDocument, publicationId);
    	
    	Collection col;
        TUser user = null;
        XMLConnectionProperties conn = null;
        try {
            conn = connectionPool.getConnection();
            col = basicOperations.getOrCreateCollection(usersCollection, 0, conn);
            XPathQueryService xPathService = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xPathService.setProperty("indent", "yes");
            ResourceSet result = xPathService.query(xQuery);
            ResourceIterator it = result.getIterator();
            Resource res = null;

            while(it.hasMoreResources()) {
                try {
                    res = it.nextResource();
                    user = unmarshal((XMLResource) res);
                    authors.add(user);
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
    	
    	
    	return authors;
	}
}
