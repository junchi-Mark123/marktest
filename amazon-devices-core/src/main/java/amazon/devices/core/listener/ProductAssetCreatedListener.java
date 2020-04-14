/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
/**
 * 
 */
package amazon.devices.core.listener;

import java.util.Arrays;
import java.util.List;

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
public class ProductAssetCreatedListener implements PostCommitEventListener {

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
            if (Constant.DOCTYPE.DEVICE_ASSET.equals(type) && !doc.isProxy() && !doc.isVersion()) {
                addPermission((DocumentEventContext) ctx, doc);
            }
        }

    }

    /**
     * @param ctx
     * @param doc
     */
    private void addPermission(DocumentEventContext ctx, DocumentModel doc) {
        CoreSession session = ctx.getCoreSession();
        String creator = session.getPrincipal().getName();
        ACP acp = doc.getACP() != null ? doc.getACP() : new ACPImpl();
        // also add View_nodownload permission for those in product:device_asset_access_read field
        if (session.getDocument(doc.getParentRef())
                   .getPropertyValue(Constant.XPATH.PRODUCT_READ_ASSET_ACCESS) != null) {
            List<String> readUserGroups = Arrays.asList(
                    (String[]) session.getDocument(doc.getParentRef())
                                      .getPropertyValue(Constant.XPATH.PRODUCT_READ_ASSET_ACCESS));

            for (String userOrGroup : readUserGroups) {
                acp.addACE("product", assignPermission(userOrGroup, Constant.PERMISSION.VIEW_NODOWNLOAD, creator));
            }
        }
        
        // also add Edit/View_nodownload permission for those in product:device_asset_access_write field
        if (session.getDocument(doc.getParentRef())
                .getPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS) != null) {
         List<String> readUserGroups = Arrays.asList(
                 (String[]) session.getDocument(doc.getParentRef())
                                   .getPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS));

         for (String userOrGroup : readUserGroups) {
             acp.addACE("product", assignPermission(userOrGroup, Constant.PERMISSION.EDIT, creator));
             acp.addACE("product", assignPermission(userOrGroup, Constant.PERMISSION.VIEW_NODOWNLOAD, creator));
         }
     }
        doc.setACP(acp, true);
        session.saveDocument(doc);
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
