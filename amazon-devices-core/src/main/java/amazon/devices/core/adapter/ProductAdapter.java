/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
/**
 * 
 */
package amazon.devices.core.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoGroup;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 */
public class ProductAdapter extends AMZDAdapter {

    /**
     * @param doc
     */
    public ProductAdapter(final DocumentModel doc) {
        super(doc);
    }

    @Override
    public Collection<String> getAllowedSubtypes() {
        List<String> subtypes = new ArrayList<String>();
        // get current User
        NuxeoPrincipal user = doc.getCoreSession().getPrincipal();

        // check if user is in the Device_Manager group
        if (user.isMemberOf(Constant.GROUP.DEVICE_MANAGER) || user.isAdministrator()) {
            subtypes.add(Constant.DOCTYPE.DEVICE_ASSET);
            return subtypes;
        }

        if (doc.getPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS) == null) {
            return subtypes;
        }
        List<String> createUserGroups = Arrays.asList(
                (String[]) doc.getPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS));
        if (createUserGroups.isEmpty()) {
            return subtypes;
        }
        // Check if current user is in the list
        if (createUserGroups.contains(user.getName())) {
            subtypes.add(Constant.DOCTYPE.DEVICE_ASSET);
            return subtypes;
        }

        UserManager userManager = Framework.getService(UserManager.class);
        // Check if user is the groups inside the list
        for (String userOrGroup : createUserGroups) {
            NuxeoGroup group = userManager.getGroup(userOrGroup);
            if (group == null) {
                continue;
            }
            if (user.isMemberOf(group.getName())) {
                subtypes.add(Constant.DOCTYPE.DEVICE_ASSET);
                return subtypes;
            }
        }
        return subtypes;
    }

}
