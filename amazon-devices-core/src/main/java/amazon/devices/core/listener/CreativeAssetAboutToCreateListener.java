/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
/**
 * 
 */
package amazon.devices.core.listener;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import amazon.devices.core.common.AMZDUtils;
import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 *
 */
public class CreativeAssetAboutToCreateListener implements EventListener {

    /* (non-Javadoc)
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
        if (Constant.DOCTYPE.CREATIVE_ASSET.equals(type)) {
            process(doc);
        }
        
    }

    /**
     * @param doc
     */
    private void process(DocumentModel doc) {
        CoreSession session = doc.getCoreSession();
        
        // get parent project or project template doc
        DocumentModel parentProject = session.getDocument(doc.getParentRef());
        if(Constant.DOCTYPE.SUBFOLDER.equals(parentProject.getType())) {
            parentProject = session.getParentDocument(parentProject.getRef());
        }
        
        // inherit metadata not provided from project or project template doc
        doc = AMZDUtils.inheritMetadata(doc, parentProject);
    }

}
