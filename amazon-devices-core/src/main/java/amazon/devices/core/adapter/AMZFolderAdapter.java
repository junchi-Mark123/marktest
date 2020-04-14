/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 *
 */
public class AMZFolderAdapter extends AMZDAdapter {
    
    private final static String DEVICES = "Devices";
    
    public AMZFolderAdapter(final DocumentModel doc) {
        super(doc);
    }

    /* (non-Javadoc)
     * @see amazon.devices.core.adapter.AMZDAdapter#getAllowedSubtypes()
     */
    @Override
    public Collection<String> getAllowedSubtypes() {
        List<String> subtypes = new ArrayList<String>();
        
        // Get the parent DeviceType of AMZFolder
        CoreSession session = doc.getCoreSession();
        DocumentModel deviceType = session.getParentDocument(doc.getRef());
        if(DEVICES.equals((String) deviceType.getPropertyValue(Constant.XPATH.TITLE))) {
            subtypes.add(Constant.DOCTYPE.DEVICE);
        }
        return subtypes;
    }
}
