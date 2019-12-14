package xml.web.services.team2.sciXiv.repository;

import org.exist.xmldb.EXistResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;
import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.utils.connection.DbConnectionManager;
import xml.web.services.team2.sciXiv.utils.database.UpdateTemplate;

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
    DbConnectionManager dbManager;

    public TUser save(TUser user) {
        try {
            String userXML = marshal(user);
            Collection col = dbManager.getOrCreateCollection(usersCollection, 0);
            updateService.append(col, usersDocument, "", "/users", userXML);
        } catch (XMLDBException e) {
            e.printStackTrace();
        }

        return user;
    }

    public TUser getByEmail(String email) {
        Collection col;
        TUser user = null;
        try {
            col = dbManager.getOrCreateCollection(usersCollection, 0);
            XPathQueryService xPathService = (XPathQueryService) col.getService("XPathQueryService", "1.0");
            xPathService.setProperty("indent", "yes");
            ResourceSet result = xPathService.query("//user/[email = " + email + "]");
            ResourceIterator i = result.getIterator();
            Resource res = null;

            while(i.hasMoreResources()) {
                try {
                    res = i.nextResource();
                    user = unmarshal((XMLResource) res);
                }
                finally {
                    try {
                        ((EXistResource)res).freeResources();
                    } catch (XMLDBException xe) {
                        xe.printStackTrace();
                    }
                }
            }
        }
        catch (XMLDBException e) {
            e.printStackTrace();
        }

        return user;
    }

    private String marshal(TUser user) {
        OutputStream os = new ByteArrayOutputStream();
        JAXBContext context;
        try {
            context = JAXBContext.newInstance("xml.web.services.team2.sciXiv.model.TUser");
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(user, os);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return os.toString();
    }

    private TUser unmarshal(XMLResource res) {
        TUser user = null;
        try {
            JAXBContext context = JAXBContext.newInstance("xml.web.services.team2.sciXiv.model.TUser");
            Unmarshaller unmarshaller = context.createUnmarshaller();
            user = (TUser) unmarshaller.unmarshal(res.getContentAsDOM());
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return user;
    }
}
