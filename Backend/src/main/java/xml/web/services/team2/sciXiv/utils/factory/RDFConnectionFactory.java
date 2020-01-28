package xml.web.services.team2.sciXiv.utils.factory;

import org.apache.jena.rdfconnection.RDFConnection;
import org.springframework.beans.factory.FactoryBean;
import xml.web.services.team2.sciXiv.utils.connection.RDFConnectionProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RDFConnectionFactory implements FactoryBean<RDFConnection> {

    private Properties props;
    private List<RDFConnection> connectionPool;
    private List<RDFConnection> usedConnections = new ArrayList<>();
    private int initialPoolSize = 10;

    public RDFConnection getConnection() {
        if (connectionPool.isEmpty()) {
            throw new RuntimeException("Maximum pool size reached, no available connections!");
        }

        RDFConnection connection = connectionPool.remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    public boolean releaseConnection(RDFConnection connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

    @Override
    public RDFConnection getObject() {
        readProperties();
        RDFConnectionProperties conn = new RDFConnectionProperties(props);

        return org.apache.jena.rdfconnection.RDFConnectionFactory.connectFuseki(conn.getEndpoint() + "/" + conn.getDataset() + "/data",
                conn.getQueryEndpoint(), conn.getUpdateEndpoint(), conn.getDataEndpoint());
    }

    @Override
    public Class<?> getObjectType() {
        return RDFConnection.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private void readProperties() {
        if (props == null) {
            String propsName = "connection.properties";
            props = new Properties();

            try {
                InputStream propsStream = RDFConnectionFactory.class.getClassLoader().getResourceAsStream(propsName);
                if (propsStream == null)
                    throw new IOException("Could not read properties " + propsName);

                props.load(propsStream);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public RDFConnectionProperties getConnectionProperties() {
        return new RDFConnectionProperties(props);
    }

    public List<RDFConnection> getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(List<RDFConnection> connectionPool) {
        this.connectionPool = connectionPool;
    }

    public List<RDFConnection> getUsedConnections() {
        return usedConnections;
    }

    public void setUsedConnections(List<RDFConnection> usedConnections) {
        this.usedConnections = usedConnections;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }
}
