package xml.web.services.team2.sciXiv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xml.web.services.team2.sciXiv.utils.factory.DocumentBuilderFactoryFactory;

import javax.xml.parsers.DocumentBuilderFactory;

@Configuration
public class DocumentBuilderFactoryConfiguration {

    @Bean
    public DocumentBuilderFactoryFactory documentBuilderFactoryFactory() {
        return new DocumentBuilderFactoryFactory();
    }

    @Bean
    public DocumentBuilderFactory documentBuilderFactory() {
        return documentBuilderFactoryFactory().getObject();
    }

}
