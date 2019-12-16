package xml.web.services.team2.sciXiv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xml.web.services.team2.sciXiv.utils.connection.ConnectionProperties;
import xml.web.services.team2.sciXiv.utils.factory.ConnectionPropertiesFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ConnectionPropertiesConfiguration {

    @Bean
    public ConnectionPropertiesFactory connectionPropertiesFactory() {
        ConnectionPropertiesFactory connectionPool = new ConnectionPropertiesFactory();
        int initialPoolSize = connectionPool.getInitialPoolSize();
        List<ConnectionProperties> pool = new ArrayList<>(initialPoolSize);
        for (int i = 0; i < initialPoolSize; i++) {
            pool.add(connectionPool.getObject());
        }
        connectionPool.setConnectionPool(pool);

        return connectionPool;
    }

    @Bean
    public ConnectionProperties connectionProperties() {
        return connectionPropertiesFactory().getObject();
    }
}
