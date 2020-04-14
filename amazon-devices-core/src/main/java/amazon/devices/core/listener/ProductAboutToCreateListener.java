/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.listener;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import amazon.devices.core.common.AMZDUtils;
import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 */
public class ProductAboutToCreateListener implements EventListener {

    private final static Log logger = LogFactory.getLog(ProductAboutToCreateListener.class);

    /*
     * (non-Javadoc)
     * @see org.nuxeo.ecm.core.event.EventListener#handleEvent(org.nuxeo.ecm.core.event.Event)
     */
    @Override
    public void handleEvent(Event event) {

        // check if announceDate is empty
        // if it is empty, do nothing
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
            return;
        }
        DocumentModel doc = ((DocumentEventContext) ctx).getSourceDocument();
        if (doc == null) {
            return;
        }
        String type = doc.getType();
        if (Constant.DOCTYPE.PRODUCT.equals(type) && doc.getPropertyValue(Constant.XPATH.GTM) != null) {
            process(doc);
        }

    }

    /**
     * @param doc
     */
    private void process(DocumentModel doc) {
        logger.info(String.format("Start handling the document: %s in ProductAboutToCreate Listener", doc.getTitle()));
        // calculate and save T-WEEK
        // we shall not save the session since it is in an aboutToCreate listener
        Calendar GTM = (Calendar) doc.getPropertyValue(Constant.XPATH.GTM);
        doc.setPropertyValue(Constant.XPATH.T_WEEK, AMZDUtils.calculateWeeks(GTM));
    }

}
