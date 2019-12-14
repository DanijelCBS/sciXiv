package xml.web.services.team2.sciXiv.utils.factory;

import org.springframework.beans.factory.FactoryBean;

import javax.xml.validation.SchemaFactory;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

public class SchemaFactoryFactory implements FactoryBean<SchemaFactory> {
    @Override
    public SchemaFactory getObject() {
        return SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
    }

    @Override
    public Class<?> getObjectType() {
        return SchemaFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
