package xml.web.services.team2.sciXiv.utils;

import org.exist.xupdate.XUpdateProcessor;
import org.springframework.stereotype.Component;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XUpdateQueryService;

@Component
public class UpdateTemplate {

    private String insertAfter;
    private String insertBefore;
    private String append;
    private String update;
    private String remove;

    public UpdateTemplate() {
        insertAfter = "<xu:modifications version=\"1.0\" xmlns:xu=\""
                + XUpdateProcessor.XUPDATE_NS + "\" xmlns=\"%1$s\">"
                + "<xu:insert-after select=\"%2$s\">%3$s</xu:insert-after>" + "</xu:modifications>";
        insertBefore = "<xu:modifications version=\"1.0\" xmlns:xu=\""
                + XUpdateProcessor.XUPDATE_NS + "\" xmlns=\"%1$s\">"
                + "<xu:insert-before select=\"%2$s\">%3$s</xu:insert-before>" + "</xu:modifications>";
        append = "<xu:modifications version=\"1.0\" xmlns:xu=\"" + XUpdateProcessor.XUPDATE_NS
                + "\" xmlns=\"%1$s\">" + "<xu:append select=\"%2$s\" child=\"last()\">%3$s</xu:append>"
                + "</xu:modifications>";
        update = "<xu:modifications version=\"1.0\" xmlns:xu=\"" + XUpdateProcessor.XUPDATE_NS
                + "\" xmlns=\"%1$s\">" + "<xu:update select=\"%2$s\">%3$s</xu:update>"
                + "</xu:modifications>";
        remove = "<xu:modifications version=\"1.0\" xmlns:xu=\"" + XUpdateProcessor.XUPDATE_NS
                + "\" xmlns=\"%1$s\">" + "<xu:remove select=\"%2$s\"/>" + "</xu:modifications>";
    }

    public void insertAfter(Collection col, String documentId, String targetNamespace, String contextXPath, String xmlFragment) {
        try {
            col.setProperty("indent", "yes");
            XUpdateQueryService xUpdateService = (XUpdateQueryService) col.getService("XUpdateQueryService", "1.0");
            xUpdateService.setProperty("indent", "yes");
            xUpdateService.updateResource(documentId, String.format(insertAfter, targetNamespace, contextXPath, xmlFragment));
        } catch (XMLDBException e) {
            e.printStackTrace();
        }
    }

    public void insertBefore(Collection col, String documentId, String targetNamespace, String contextXPath, String xmlFragment) {
        try {
            col.setProperty("indent", "yes");
            XUpdateQueryService xUpdateService = (XUpdateQueryService) col.getService("XUpdateQueryService", "1.0");
            xUpdateService.setProperty("indent", "yes");
            xUpdateService.updateResource(documentId, String.format(insertBefore, targetNamespace, contextXPath, xmlFragment));
        } catch (XMLDBException e) {
            e.printStackTrace();
        }
    }
    public void append(Collection col, String documentId, String targetNamespace, String contextXPath, String xmlFragment) {
        try {
            col.setProperty("indent", "yes");
            XUpdateQueryService xUpdateService = (XUpdateQueryService) col.getService("XUpdateQueryService", "1.0");
            xUpdateService.setProperty("indent", "yes");
            xUpdateService.updateResource(documentId, String.format(append, targetNamespace, contextXPath, xmlFragment));
        } catch (XMLDBException e) {
            e.printStackTrace();
        }
    }
    public void update(Collection col, String documentId, String targetNamespace, String contextXPath, String xmlFragment) {
        try {
            col.setProperty("indent", "yes");
            XUpdateQueryService xUpdateService = (XUpdateQueryService) col.getService("XUpdateQueryService", "1.0");
            xUpdateService.setProperty("indent", "yes");
            xUpdateService.updateResource(documentId, String.format(update, targetNamespace, contextXPath, xmlFragment));
        } catch (XMLDBException e) {
            e.printStackTrace();
        }
    }
    public void remove(Collection col, String documentId, String targetNamespace, String contextXPath, String xmlFragment) {
        try {
            col.setProperty("indent", "yes");
            XUpdateQueryService xUpdateService = (XUpdateQueryService) col.getService("XUpdateQueryService", "1.0");
            xUpdateService.setProperty("indent", "yes");
            xUpdateService.updateResource(documentId, String.format(remove, targetNamespace, contextXPath, xmlFragment));
        } catch (XMLDBException e) {
            e.printStackTrace();
        }
    }
}
