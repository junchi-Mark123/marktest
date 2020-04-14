package amazon.devices.core.adapter;

import amazon.devices.core.common.Constant;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author ray.huang
 */

public class YearFolderAdapter extends AMZDAdapter  {

    private final static String AMZ_DEVICE_HUB_STAGING = "Amazon Device Hub Staging";

    public YearFolderAdapter(final DocumentModel doc) {
        super(doc);
    }

    @Override
    public Collection<String> getAllowedSubtypes() {
        List<String> subtypes = new ArrayList<String>();

        // Get the parent AMZDeviceHubType of Year Folder
        CoreSession session = doc.getCoreSession();
        DocumentModel amzDeviceHubType = session.getParentDocument(doc.getRef());
        if(AMZ_DEVICE_HUB_STAGING.equals(amzDeviceHubType.getPropertyValue(Constant.XPATH.TITLE))) {
            subtypes.add(Constant.DOCTYPE.SUBFOLDER);
            //TODO: add more subtypes
        }
        return subtypes;
    }
}
