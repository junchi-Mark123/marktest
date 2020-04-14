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

import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 */
public class UpdatePermissionEventListener implements PostCommitEventListener {

    /*
     * (non-Javadoc)
     * @see org.nuxeo.ecm.core.event.PostCommitEventListener#handleEvent(org.nuxeo.ecm.core.event.EventBundle)
     */
    @Override
    public void handleEvent(EventBundle events) {
        for (Event event : events) {
            if (!event.getName().equals(Constant.EVENT.UPDATE_PRODUCT_PERMISSION)) {
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
            if (Constant.DOCTYPE.PRODUCT.equals(type)) {
                handleEvent(doc.getId(), doc.getRepositoryName());
            }
        }
    }

    /**
     * @param event
     */
    private void handleEvent(String id, String repository) {
        try (CloseableCoreSession session = CoreInstance.openCoreSessionSystem(repository)) {

            Framework.doPrivileged(() -> {

                DocumentModel doc = session.getDocument(new IdRef(id));
                ACP acp = doc.getACP() != null ? doc.getACP() : new ACPImpl();
                acp.removeACL("product");
                // also add CreateContents permission for those in product:device_asset_access_create field
                if (doc.getPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS) != null) {
                    List<String> createUserGroups = Arrays.asList(
                            (String[]) doc.getPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS));

                    for (String userOrGroup : createUserGroups) {
                        acp.addACE("product", assignPermission(userOrGroup, Constant.PERMISSION.CREATE_CONTENTS,
                                session.getPrincipal().getName()));
                    }
                }
                doc.setACP(acp, true);

                // also add Edit permission for all the device asset under this device for those in
                // product:device_asset_access_create field
                DocumentModelList assets = session.getChildren(doc.getRef());
                if (assets != null && assets.size() > 0) {
                    for (DocumentModel asset : assets) {
                        ACP assetAcp = asset.getACP() != null ? asset.getACP() : new ACPImpl();
                        assetAcp.removeACL("product");
                        if (doc.getPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS) != null) {
                            List<String> createUserGroups = Arrays.asList(
                                    (String[]) doc.getPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS));
                            for (String userOrGroup : createUserGroups) {
                                assetAcp.addACE("product", assignPermission(userOrGroup, Constant.PERMISSION.EDIT,
                                        session.getPrincipal().getName()));
                                assetAcp.addACE("product", assignPermission(userOrGroup, Constant.PERMISSION.VIEW_NODOWNLOAD,
                                        session.getPrincipal().getName()));
                            }
                        }
                        if (doc.getPropertyValue(Constant.XPATH.PRODUCT_READ_ASSET_ACCESS) != null) {
                            List<String> readUserGroups = Arrays.asList(
                                    (String[]) doc.getPropertyValue(Constant.XPATH.PRODUCT_READ_ASSET_ACCESS));

                            for (String userOrGroup : readUserGroups) {
                                assetAcp.addACE("product", assignPermission(userOrGroup, Constant.PERMISSION.VIEW_NODOWNLOAD,
                                        session.getPrincipal().getName()));
                            }
                        }
                        asset.setACP(assetAcp, true);
                    }
                }
                session.save();
            });
        }
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
