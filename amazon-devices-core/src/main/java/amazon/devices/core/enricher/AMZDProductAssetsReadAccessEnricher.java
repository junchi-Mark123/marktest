/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
/**
 * 
 */
package amazon.devices.core.enricher;

import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoGroup;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher;
import org.nuxeo.ecm.core.io.registry.reflect.Priorities;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import com.fasterxml.jackson.core.JsonGenerator;

import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 */
@Setup(mode = SINGLETON, priority = Priorities.REFERENCE)
public class AMZDProductAssetsReadAccessEnricher extends AbstractJsonEnricher<DocumentModel> {

    public static final String NAME = "hasProductAssetsReadAccess";

    /**
     * @param name
     */
    public AMZDProductAssetsReadAccessEnricher() {
        super(NAME);
    }

    /*
     * (non-Javadoc)
     * @see org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher#write(com.fasterxml.jackson.core.
     * JsonGenerator, java.lang.Object)
     */
    @Override
    public void write(JsonGenerator jg, DocumentModel enriched) throws IOException {
        jg.writeBooleanField(NAME, checkReadAccess(enriched));
    }

    private boolean checkReadAccess(DocumentModel enriched) {
        NuxeoPrincipal user = enriched.getCoreSession().getPrincipal();
        if (user.isMemberOf(Constant.GROUP.DEVICE_MANAGER) || user.isAdministrator()) {
            return true;
        }

        if (enriched.getPropertyValue(Constant.XPATH.PRODUCT_READ_ASSET_ACCESS) == null
                && enriched.getPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS) == null) {
            return false;
        }
        List<String> readUserGroups = enriched.getPropertyValue(Constant.XPATH.PRODUCT_READ_ASSET_ACCESS) != null ? Arrays.asList(
                (String[]) enriched.getPropertyValue(Constant.XPATH.PRODUCT_READ_ASSET_ACCESS)) : new ArrayList<String>();
        List<String> createUserGroups = enriched.getPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS) != null ? Arrays.asList(
                (String[]) enriched.getPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS)) : new ArrayList<String>();
        if (createUserGroups.isEmpty() && readUserGroups.isEmpty()) {
            return false;
        }
        List<String> userGroups = Stream.concat(readUserGroups.stream(), createUserGroups.stream())
                .collect(Collectors.toList());
        // Check if current user is in the list
        if (userGroups.contains(user.getName())) {
            return true;
        }

        UserManager userManager = Framework.getService(UserManager.class);
        // Check if user is the groups inside the list
        for (String userOrGroup : userGroups) {
            NuxeoGroup group = userManager.getGroup(userOrGroup);
            if (group == null) {
                continue;
            }
            if (user.isMemberOf(group.getName())) {
                return true;
            }
        }
        return false;
    }

}
