package xml.web.services.team2.sciXiv.utils.factory;

import org.springframework.beans.factory.FactoryBean;
import xml.web.services.team2.sciXiv.utils.connection.ConnectionProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConnectionPropertiesFactory implements FactoryBean<ConnectionProperties> {

    @Override
    public ConnectionProperties getObject() throws IOException {
        String propsName = "exist.properties";

        InputStream propsStream = ConnectionPropertiesFactory.class.getClassLoader().getResourceAsStream(propsName);
        if (propsStream == null)
            throw new IOException("Could not read properties " + propsName);

        Properties props = new Properties();
        props.load(propsStream);

        return new ConnectionProperties(props);
    }

    @Override
    public Class<?> getObjectType() {
        return ConnectionProperties.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
