package xml.web.services.team2.sciXiv.config;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xml.web.services.team2.sciXiv.utils.factory.TransformerFactoryFactory;

@Configuration
public class TransformerFactoryFactoryConfiguration {

    @Bean
    public TransformerFactoryFactory transformerFactoryFactory() {
        return new TransformerFactoryFactory();
    }

    @Bean
    public TransformerFactoryImpl transformerFactory() {
        return transformerFactoryFactory().getObject();
    }
}
