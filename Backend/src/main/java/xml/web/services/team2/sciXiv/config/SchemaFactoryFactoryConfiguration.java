package xml.web.services.team2.sciXiv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xml.web.services.team2.sciXiv.utils.factory.SchemaFactoryFactory;

import javax.xml.validation.SchemaFactory;

@Configuration
public class SchemaFactoryFactoryConfiguration {

    @Bean
    public SchemaFactoryFactory schemaFactoryFactory() {
        return new SchemaFactoryFactory();
    }

    @Bean
    public SchemaFactory schemaFactory() {
        return schemaFactoryFactory().getObject();
    }
}
