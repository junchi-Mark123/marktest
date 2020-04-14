/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.listener;

import java.util.Calendar;

import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

import amazon.devices.core.common.AMZDUtils;
import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 */
public class ProductBeforeModificationListener implements EventListener {

    private final static Log logger = LogFactory.getLog(ProductBeforeModificationListener.class);

    /*
     * (non-Javadoc)
     * @see org.nuxeo.ecm.core.event.EventListener#handleEvent(org.nuxeo.ecm.core.event.Event)
     */
    @Override
    public void handleEvent(Event event) {
        // check if announceDate is dirty
        // if it is not dirty, do nothing
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
            return;
        }
        DocumentModel doc = ((DocumentEventContext) ctx).getSourceDocument();
        if (doc == null) {
            return;
        }
        String type = doc.getType();
        if (Constant.DOCTYPE.PRODUCT.equals(type) && doc.getProperty(Constant.XPATH.GTM).isDirty()) {
            process(doc);
        }
        if (Constant.DOCTYPE.PRODUCT.equals(type)
                && (doc.getProperty(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS).isDirty() || doc.getProperty(Constant.XPATH.PRODUCT_READ_ASSET_ACCESS).isDirty())) {
            fireUpdatePermissionEvent((DocumentEventContext) ctx, doc);
        }
    }

    /**
     * @param doc
     */
    private void process(DocumentModel doc) {
        logger.info(
                String.format("Start handling the document: %s in ProductBeforeModification Listener", doc.getTitle()));
        // if GTM is null, set T-WEEK to null
        // otherwise calculate T-WEEK
        // we shall not save the session since it is in an aboutToCreate listener
        Calendar GTM = (Calendar) doc.getPropertyValue(Constant.XPATH.GTM);
        if (GTM != null) {
            doc.setPropertyValue(Constant.XPATH.T_WEEK, AMZDUtils.calculateWeeks(GTM));
        } else {
            doc.setPropertyValue(Constant.XPATH.T_WEEK, null);
        }

    }

    /**
     * @param doc
     * @param doc
     * @throws LoginException
     */
    private void fireUpdatePermissionEvent(DocumentEventContext ctx, DocumentModel doc) {
        DocumentEventContext newCtx = new DocumentEventContext(ctx.getCoreSession(), ctx.getPrincipal(), doc);
        Event event = newCtx.newEvent(Constant.EVENT.UPDATE_PRODUCT_PERMISSION);
        event.setIsCommitEvent(true);
        event.setInline(false);
        Framework.getService(EventService.class).fireEvent(event);
    }

}
