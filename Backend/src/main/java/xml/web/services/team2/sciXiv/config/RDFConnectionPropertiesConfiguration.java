package xml.web.services.team2.sciXiv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xml.web.services.team2.sciXiv.utils.connection.RDFConnectionProperties;
import xml.web.services.team2.sciXiv.utils.factory.RDFConnectionPropertiesFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RDFConnectionPropertiesConfiguration {

    @Bean
    public RDFConnectionPropertiesFactory connectionPropertiesFactory() {
        RDFConnectionPropertiesFactory connectionPool = new RDFConnectionPropertiesFactory();
        int initialPoolSize = connectionPool.getInitialPoolSize();
        List<RDFConnectionProperties> pool = new ArrayList<>(initialPoolSize);
        for (int i = 0; i < initialPoolSize; i++) {
            pool.add(connectionPool.getObject());
        }
        connectionPool.setConnectionPool(pool);

        return connectionPool;
    }

    @Bean
    public RDFConnectionProperties connectionProperties() {
        return connectionPropertiesFactory().getObject();
    }
}
