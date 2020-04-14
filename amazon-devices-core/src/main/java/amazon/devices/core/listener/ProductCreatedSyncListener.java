/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.listener;

import java.util.Arrays;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class ProductCreatedSyncListener implements PostCommitEventListener {

    private final static Log logger = LogFactory.getLog(ProductCreatedSyncListener.class);

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
            if (Constant.DOCTYPE.PRODUCT.equals(type) && !doc.isProxy() && !doc.isVersion()) {
                addPermission((DocumentEventContext) ctx, doc);
            }
        }
    }

    /**
     * @param ctx
     * @param doc
     * @throws LoginException
     */
    private void addPermission(DocumentEventContext ctx, DocumentModel doc) {

        ACP acp = doc.getACP() != null ? doc.getACP() : new ACPImpl();
        // check whether document is under normal folder or access controlled folder
        if (NORMAL.equals(ctx.getCoreSession().getDocument(doc.getParentRef()).getTitle())) {
            String creator = ctx.getCoreSession().getPrincipal().getName();
            acp.addACE("local", assignPermission(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT, creator));
            acp.addACE("local", assignPermission(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD, creator));
            acp.addACE("local", assignPermission(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT, creator));
            acp.addACE("local", assignPermission(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD, creator));
            acp.addACE("local",
                    assignPermission(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD, creator));
            acp.addACE("local",
                    assignPermission(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD, creator));
            acp.addACE("local", assignPermission(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD, creator));
            acp.addACE("local", assignPermission(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD, creator));
            acp.addACE("local", assignPermission(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD, creator));
        } else if (ACCESS_CONTROLLED.equals(ctx.getCoreSession().getDocument(doc.getParentRef()).getTitle())) {
            String creator = ctx.getCoreSession().getPrincipal().getName();
            acp.addACE("local", assignPermission(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT, creator));
            acp.addACE("local", assignPermission(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD, creator));
            acp.addACE("local", assignPermission(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT, creator));
            acp.addACE("local", assignPermission(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD, creator));
        } else {
            logger.warn(String.format("Unknow parent: %s for the document",
                    ctx.getCoreSession().getDocument(doc.getParentRef()).getTitle()));
        }
        // also add CreateContents permission for those in product:device_asset_access_create field
        if (doc.getPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS) != null) {
            List<String> createUserGroups = Arrays.asList(
                    (String[]) doc.getPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS));

            for (String userOrGroup : createUserGroups) {
                acp.addACE("product", assignPermission(userOrGroup, Constant.PERMISSION.CREATE_CONTENTS,
                        ctx.getCoreSession().getPrincipal().getName()));
            }
        }
        doc.setACP(acp, true);
        ctx.getCoreSession().save();

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
