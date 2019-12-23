package xml.web.services.team2.sciXiv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xml.web.services.team2.sciXiv.utils.connection.XMLConnectionProperties;
import xml.web.services.team2.sciXiv.utils.factory.XMLConnectionPropertiesFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class XMLConnectionPropertiesConfiguration {

    @Bean
    public XMLConnectionPropertiesFactory xmlConnectionPropertiesFactory() {
        XMLConnectionPropertiesFactory connectionPool = new XMLConnectionPropertiesFactory();
        int initialPoolSize = connectionPool.getInitialPoolSize();
        List<XMLConnectionProperties> pool = new ArrayList<>(initialPoolSize);
        for (int i = 0; i < initialPoolSize; i++) {
            pool.add(connectionPool.getObject());
        }
        connectionPool.setConnectionPool(pool);

        return connectionPool;
    }

    @Bean
    public XMLConnectionProperties xmlConnectionProperties() {
        return xmlConnectionPropertiesFactory().getObject();
    }
}
