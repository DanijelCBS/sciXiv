package xml.web.services.team2.sciXiv.config;

import org.apache.fop.apps.FopFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xml.sax.SAXException;
import xml.web.services.team2.sciXiv.utils.factory.FopFactoryFactory;

import java.io.IOException;

@Configuration
public class FopFactoryFactoryConfiguration {

    @Bean
    public FopFactoryFactory fopFactoryFactory() {
        return new FopFactoryFactory();
    }

    @Bean
    public FopFactory fopFactory() throws IOException, SAXException {
        return fopFactoryFactory().getObject();
    }
}
