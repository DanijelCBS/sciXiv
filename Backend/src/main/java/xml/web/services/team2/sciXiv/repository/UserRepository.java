package xml.web.services.team2.sciXiv.repository;

import org.exist.xmldb.EXistResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;
import xml.web.services.team2.sciXiv.exception.UserRetrievingFailedException;
import xml.web.services.team2.sciXiv.exception.UserSavingFailedException;
import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.utils.connection.ConnectionProperties;
import xml.web.services.team2.sciXiv.utils.database.BasicOperations;
import xml.web.services.team2.sciXiv.utils.database.UpdateTemplate;
import xml.web.services.team2.sciXiv.utils.factory.ConnectionPropertiesFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

@Repository
public class UserRepository {

    private String usersCollection = "/db/sciXiv/users";
    private String usersDocument = "users.xml";

    @Autowired
    UpdateTemplate updateService;

    @Autowired
    BasicOperations basicOperations;

    @Autowired
    ConnectionPropertiesFactory connectionPool;

    public TUser save(TUser user) throws UserSavingFailedException {
        try {
            ConnectionProperties conn = connectionPool.getConnection();
            String userXML = marshal(user);
            Collection col = basicOperations.getOrCreateCollection(usersCollection, 0, conn);
            updateService.append(col, usersDocument, "", "/users", userXML);
        } catch (Exception e) {
            throw new UserSavingFailedException("Failed to save user to database");
        }

        return user;
    }

    public TUser getByEmail(String email) throws UserRetrievingFailedException {
        Collection col;
        TUser user = null;
        try {
            ConnectionProperties conn = connectionPool.getConnection();
            col = basicOperations.getOrCreateCollection(usersCollection, 0, conn);
            XPathQueryService xPathService = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xPathService.setProperty("indent", "yes");
            ResourceSet result = xPathService.query("//user/[email = " + email + "]");
            ResourceIterator i = result.getIterator();
            Resource res = null;

            while(i.hasMoreResources()) {
                try {
                    res = i.nextResource();
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
            throw new UserRetrievingFailedException("Failed to get user from database");
        }

        return user;
    }

    private String marshal(TUser user) throws JAXBException {
        OutputStream os = new ByteArrayOutputStream();
        JAXBContext context;

        context = JAXBContext.newInstance("xml.web.services.team2.sciXiv.model.TUser");
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(user, os);

        return os.toString();
    }

    private TUser unmarshal(XMLResource res) throws JAXBException, XMLDBException {
        TUser user = null;

        JAXBContext context = JAXBContext.newInstance("xml.web.services.team2.sciXiv.model.TUser");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        user = (TUser) unmarshaller.unmarshal(res.getContentAsDOM());

        return user;
    }
}
