/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
/**
 * 
 */
package amazon.devices.core.listener;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.schema.FacetNames;

import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 */
public class RelationSchemaEmptyDocumentListener implements EventListener {

    private static final String RELATIONS = "relations";

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

        if (doc.hasSchema(RELATIONS)) {
            process(doc, ctx);
        }
    }

    /**
     * @param doc
     * @param ctx
     */
    private void process(DocumentModel doc, EventContext ctx) {
        String parentPath = (String) ctx.getProperty("parentPath");
        DocumentModel parentDoc = ctx.getCoreSession().getDocument(new PathRef(parentPath));
        if (checkSuperTypeIsWorkspace(parentDoc)) {
            doc.setPropertyValue(Constant.XPATH.RELATIONS_WORKSPACE_LOCATION, parentDoc.getId());
        }
        if (ctx.getCoreSession().getSuperSpace(parentDoc) != null && checkSuperTypeIsWorkspace(ctx.getCoreSession().getSuperSpace(parentDoc))) {
            doc.setPropertyValue(Constant.XPATH.RELATIONS_WORKSPACE_LOCATION,
                    ctx.getCoreSession().getSuperSpace(parentDoc).getId());
        }
    }

    private boolean checkSuperTypeIsWorkspace(DocumentModel doc) {
        if (doc == null) {
            return false;
        }
        if (doc.hasFacet(FacetNames.SUPER_SPACE) && doc.getDocumentType().getName().equals("Workspace")) {
            return true;
        }
        if (doc.hasFacet(FacetNames.SUPER_SPACE) && doc.getDocumentType().getSuperType() != null
                && doc.getDocumentType().getSuperType().getName().equals("Workspace")) {
            return true;
        }
        return false;
    }

}
