package xml.web.services.team2.sciXiv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xml.web.services.team2.sciXiv.utils.connection.ConnectionProperties;
import xml.web.services.team2.sciXiv.utils.factory.ConnectionPropertiesFactory;

import java.io.IOException;

@Configuration
public class ConnectionPropertiesConfiguration {

    @Bean
    public ConnectionPropertiesFactory connectionPropertiesFactory() {
        return new ConnectionPropertiesFactory();
    }

    @Bean
    public ConnectionProperties connectionProperties() throws IOException {
        return connectionPropertiesFactory().getObject();
    }
}
