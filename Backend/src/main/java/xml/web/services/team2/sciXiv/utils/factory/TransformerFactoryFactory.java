package xml.web.services.team2.sciXiv.utils.factory;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import org.springframework.beans.factory.FactoryBean;

public class TransformerFactoryFactory implements FactoryBean<TransformerFactoryImpl> {
    @Override
    public TransformerFactoryImpl getObject() {
        return new TransformerFactoryImpl();
    }

    @Override
    public Class<?> getObjectType() {
        return TransformerFactoryImpl.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
