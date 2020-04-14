/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.listener;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import amazon.devices.core.common.Constant;

public class CodenameBeforeModificationListener implements EventListener {

    /*
     * (non-Javadoc)
     * @see org.nuxeo.ecm.core.event.EventListener#handleEvent(org.nuxeo.ecm.core.event.Event)
     */
    @Override
    public void handleEvent(Event event) {
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
            return;
        }
        DocumentModel doc = ((DocumentEventContext) ctx).getSourceDocument();
        if (doc == null) {
            return;
        }

        String type = doc.getType();
        if (Constant.DOCTYPE.CODENAME.equals(type) && doc.getProperty(Constant.XPATH.TITLE).isDirty()) {
            updateDeviceTitle((DocumentEventContext) ctx, doc);
        }
    }

    private void updateDeviceTitle(DocumentEventContext ctx, DocumentModel doc) {
        CoreSession session = ctx.getCoreSession();
        // Get linked device document
        DocumentModel device = session.getDocument(new IdRef((String) doc.getPropertyValue(Constant.XPATH.CODENAME_DEVICE)));

        // Update title to device
        device.setPropertyValue(Constant.XPATH.TITLE, doc.getPropertyValue(Constant.XPATH.TITLE));
        session.saveDocument(device);
    }

}
