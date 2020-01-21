package xml.web.services.team2.sciXiv.utils.factory;

import org.springframework.beans.factory.FactoryBean;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.apache.xerces.jaxp.JAXPConstants.JAXP_SCHEMA_LANGUAGE;
import static org.apache.xerces.jaxp.JAXPConstants.W3C_XML_SCHEMA;

public class DocumentBuilderFactoryFactory implements FactoryBean<DocumentBuilderFactory> {

    @Override
    public DocumentBuilderFactory getObject() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        //factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

        return factory;
    }

    @Override
    public Class<?> getObjectType() {
        return DocumentBuilderFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
