package xml.web.services.team2.sciXiv.utils.factory;

import org.apache.fop.apps.FopFactory;
import org.springframework.beans.factory.FactoryBean;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class FopFactoryFactory implements FactoryBean<FopFactory> {

    private static final String FOP_CONF = "src/main/resources/static/conf/fop.xconf";

    @Override
    public FopFactory getObject() throws IOException, SAXException {
        return FopFactory.newInstance(new File(FOP_CONF));
    }

    @Override
    public Class<?> getObjectType() {
        return FopFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
