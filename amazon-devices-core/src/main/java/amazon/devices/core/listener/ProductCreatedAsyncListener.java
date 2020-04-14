/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 */
public class ProductCreatedAsyncListener implements PostCommitEventListener {

    private final static Log logger = LogFactory.getLog(ProductCreatedAsyncListener.class);

    private final static String NORMAL = "Normal";

    private final static String ACCESS_CONTROLLED = "Access Controlled";

    /*
     * (non-Javadoc)
     * @see org.nuxeo.ecm.core.event.PostCommitEventListener#handleEvent(org.nuxeo.ecm.core.event.EventBundle)
     */
    @Override
    public void handleEvent(EventBundle events) {
        for (Event event : events) {
            if (!event.getName().equals(Constant.EVENT.DOCUMENT_CREATED)) {
                continue;
            }
            EventContext ctx = event.getContext();
            if (!(ctx instanceof DocumentEventContext)) {
                return;
            }
            DocumentModel doc = ((DocumentEventContext) ctx).getSourceDocument();
            if (doc == null) {
                return;
            }
            String type = doc.getType();
            // Since we use eventHandler to handle the initial version 0.1
            // A new version will be created when a product document is created
            // Listener will get new version and new proxy event as well
            if (Constant.DOCTYPE.PRODUCT.equals(type) && !doc.isProxy() && !doc.isVersion()) {
                handleEvent((DocumentEventContext) ctx, doc);
            }
        }
    }

    /**
     * @param event
     */
    private void handleEvent(DocumentEventContext ctx, DocumentModel doc) {
        CoreSession session = ctx.getCoreSession();
        if (NORMAL.equals(ctx.getCoreSession().getDocument(doc.getParentRef()).getTitle())) {
            DocumentModel codename = session.createDocumentModel(Constant.PATH.CODENAME_NORMAL, doc.getTitle(),
                    Constant.DOCTYPE.CODENAME);
            codename.setPropertyValue(Constant.XPATH.TITLE, doc.getTitle());
            codename.setPropertyValue(Constant.XPATH.CODENAME_DEVICE, doc.getId());
            codename = session.createDocument(codename);
            addPermission(session, codename);
        } else if (ACCESS_CONTROLLED.equals(ctx.getCoreSession().getDocument(doc.getParentRef()).getTitle())) {
            DocumentModel codename = session.createDocumentModel(Constant.PATH.CODENAME_ACCESS_CONTROLLED,
                    doc.getTitle(), Constant.DOCTYPE.CODENAME);
            codename.setPropertyValue(Constant.XPATH.TITLE, doc.getTitle());
            codename.setPropertyValue(Constant.XPATH.CODENAME_DEVICE, doc.getId());
            codename = session.createDocument(codename);
            addPermission(session, codename);
        } else {
            logger.warn(String.format("Unknown parent: %s for the document",
                    ctx.getCoreSession().getDocument(doc.getParentRef()).getTitle()));
        }
        session.save();
    }

    private void addPermission(CoreSession session, DocumentModel doc) {
        ACP acp = doc.getACP() != null ? doc.getACP() : new ACPImpl();
        String creator = session.getPrincipal().getName();
        acp.addACE("local", assignPermission(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT, creator));
        acp.addACE("local", assignPermission(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD, creator));
        acp.addACE("local", assignPermission(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT, creator));
        acp.addACE("local", assignPermission(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD, creator));
        doc.setACP(acp, true);
    }

    /**
     * @param user
     * @param permission
     * @param creator
     * @return
     */
    private ACE assignPermission(String user, String permission, String creator) {
        return ACE.builder(user, permission).creator(creator).build();
    }

}
