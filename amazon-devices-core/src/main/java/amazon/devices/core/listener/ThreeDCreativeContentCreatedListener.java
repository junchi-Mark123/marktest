package amazon.devices.core.listener;


import amazon.devices.core.common.Constant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoGroup;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.event.*;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.usermanager.NuxeoGroupImpl;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author ray.huang
 */

public class ThreeDCreativeContentCreatedListener implements PostCommitEventListener {

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
            if (Constant.DOCTYPE.ThreeD_CREATIVE_CONTENT.equals(type)) {
                createGroup((DocumentEventContext) ctx, doc);
            }
        }
    }

    /**
     * @param ctx
     * @param doc
     * @throws
     */
    private void createGroup(DocumentEventContext ctx, DocumentModel doc) {
        NuxeoGroup group = new NuxeoGroupImpl(doc.getName().replace(" ", "_"));
        UserManager userManager = Framework.getService(UserManager.class);
        if (userManager.getGroup(group.getName()) == null) {
            group.setParentGroups(Collections.singletonList(Constant.GROUP.CXLABS_CONSUMER));
            userManager.createGroup(group.getModel());
        }

        ACP acp = doc.getACP() != null ? doc.getACP() : new ACPImpl();

        acp.addACE("local", assignPermission(group.getName(), Constant.PERMISSION.VIEW_NODOWNLOAD));
        acp.addACE("local", assignPermission(group.getName(), Constant.PERMISSION.DOWNLOAD));
        acp.addACE("local", assignPermission(group.getName(), Constant.PERMISSION.EDIT));
        acp.addACE("local", assignPermission(group.getName(), Constant.PERMISSION.CREATE_CONTENTS));

        doc.setACP(acp, true);

        ctx.getCoreSession().save();
    }

    /**
     * @param user
     * @param permission
     * @return
     */
    private ACE assignPermission(String user, String permission) {
        return ACE.builder(user, permission).build();
    }
}
