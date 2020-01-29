package xml.web.services.team2.sciXiv.config;

import org.apache.jena.rdfconnection.RDFConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xml.web.services.team2.sciXiv.utils.factory.RDFConnectionFactory;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RDFConnectionConfiguration {

    @Bean
    public RDFConnectionFactory rdfConnectionPropertiesFactory() {
        RDFConnectionFactory connectionPool = new RDFConnectionFactory();
        int initialPoolSize = connectionPool.getInitialPoolSize();
        List<RDFConnection> pool = new ArrayList<>(initialPoolSize);
        for (int i = 0; i < initialPoolSize; i++) {
            pool.add(connectionPool.getObject());
        }
        connectionPool.setConnectionPool(pool);

        return connectionPool;
    }

    @Bean
    public RDFConnection rdfConnectionProperties() {
        return rdfConnectionPropertiesFactory().getObject();
    }
}
