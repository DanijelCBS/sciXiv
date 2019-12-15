package xml.web.services.team2.sciXiv.utils.factory;

import org.springframework.beans.factory.FactoryBean;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Database;
import xml.web.services.team2.sciXiv.utils.connection.ConnectionProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConnectionPropertiesFactory implements FactoryBean<ConnectionProperties> {

    private List<ConnectionProperties> connectionPool;
    private List<ConnectionProperties> usedConnections = new ArrayList<>();
    private int initialPoolSize = 10;

    public ConnectionProperties getConnection() {
        if (connectionPool.isEmpty()) {
            throw new RuntimeException("Maximum pool size reached, no available connections!");
        }

        ConnectionProperties connection = connectionPool.remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    public boolean releaseConnection(ConnectionProperties connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

    @Override
    public ConnectionProperties getObject() {
        String propsName = "exist.properties";
        ConnectionProperties conn = null;

        try {
            InputStream propsStream = ConnectionPropertiesFactory.class.getClassLoader().getResourceAsStream(propsName);
            if (propsStream == null)
                throw new IOException("Could not read properties " + propsName);

            Properties props = new Properties();
            props.load(propsStream);

            conn = new ConnectionProperties(props);

            Class<?> cl = Class.forName(conn.getDriver());

            Database database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");

            DatabaseManager.registerDatabase(database);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

    @Override
    public Class<?> getObjectType() {
        return ConnectionProperties.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public List<ConnectionProperties> getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(List<ConnectionProperties> connectionPool) {
        this.connectionPool = connectionPool;
    }

    public List<ConnectionProperties> getUsedConnections() {
        return usedConnections;
    }

    public void setUsedConnections(List<ConnectionProperties> usedConnections) {
        this.usedConnections = usedConnections;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }
}
