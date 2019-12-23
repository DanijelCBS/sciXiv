package xml.web.services.team2.sciXiv.utils.factory;

import org.springframework.beans.factory.FactoryBean;
import xml.web.services.team2.sciXiv.utils.connection.RDFConnectionProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RDFConnectionPropertiesFactory implements FactoryBean<RDFConnectionProperties> {

    private List<RDFConnectionProperties> connectionPool;
    private List<RDFConnectionProperties> usedConnections = new ArrayList<>();
    private int initialPoolSize = 10;

    public RDFConnectionProperties getConnection() {
        if (connectionPool.isEmpty()) {
            throw new RuntimeException("Maximum pool size reached, no available connections!");
        }

        RDFConnectionProperties connection = connectionPool.remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    public boolean releaseConnection(RDFConnectionProperties connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

    @Override
    public RDFConnectionProperties getObject() {
        String propsName = "connection.properties";
        RDFConnectionProperties conn = null;

        try {
            InputStream propsStream = RDFConnectionPropertiesFactory.class.getClassLoader().getResourceAsStream(propsName);
            if (propsStream == null)
                throw new IOException("Could not read properties " + propsName);

            Properties props = new Properties();
            props.load(propsStream);

            conn = new RDFConnectionProperties(props);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

    @Override
    public Class<?> getObjectType() {
        return RDFConnectionProperties.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public List<RDFConnectionProperties> getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(List<RDFConnectionProperties> connectionPool) {
        this.connectionPool = connectionPool;
    }

    public List<RDFConnectionProperties> getUsedConnections() {
        return usedConnections;
    }

    public void setUsedConnections(List<RDFConnectionProperties> usedConnections) {
        this.usedConnections = usedConnections;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }
}
