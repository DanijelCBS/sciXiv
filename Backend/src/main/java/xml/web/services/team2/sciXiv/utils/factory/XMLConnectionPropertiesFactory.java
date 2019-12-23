package xml.web.services.team2.sciXiv.utils.factory;

import org.springframework.beans.factory.FactoryBean;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Database;
import xml.web.services.team2.sciXiv.utils.connection.XMLConnectionProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class XMLConnectionPropertiesFactory implements FactoryBean<XMLConnectionProperties> {

    private List<XMLConnectionProperties> connectionPool;
    private List<XMLConnectionProperties> usedConnections = new ArrayList<>();
    private int initialPoolSize = 10;

    public XMLConnectionProperties getConnection() {
        if (connectionPool.isEmpty()) {
            throw new RuntimeException("Maximum pool size reached, no available connections!");
        }

        XMLConnectionProperties connection = connectionPool.remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    public boolean releaseConnection(XMLConnectionProperties connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

    @Override
    public XMLConnectionProperties getObject() {
        String propsName = "exist.properties";
        XMLConnectionProperties conn = null;

        try {
            InputStream propsStream = XMLConnectionPropertiesFactory.class.getClassLoader().getResourceAsStream(propsName);
            if (propsStream == null)
                throw new IOException("Could not read properties " + propsName);

            Properties props = new Properties();
            props.load(propsStream);

            conn = new XMLConnectionProperties(props);

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
        return XMLConnectionProperties.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public List<XMLConnectionProperties> getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(List<XMLConnectionProperties> connectionPool) {
        this.connectionPool = connectionPool;
    }

    public List<XMLConnectionProperties> getUsedConnections() {
        return usedConnections;
    }

    public void setUsedConnections(List<XMLConnectionProperties> usedConnections) {
        this.usedConnections = usedConnections;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }
}
