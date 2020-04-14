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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 */
public class SubFolderAdapter extends AMZDAdapter {

    /**
     * @param doc
     */
    public SubFolderAdapter(DocumentModel doc) {
        super(doc);
    }

    private Map<String, List<String>> initialMapping() {
        Map<String, List<String>> mapping = new HashMap<String, List<String>>();
        mapping.put(Constant.DOCTYPE.CREATIVE_PROJECT_TEMPLATE, Arrays.asList(Constant.DOCTYPE.CREATIVE_ASSET));
        mapping.put(Constant.DOCTYPE.CREATIVE_PROJECT, Arrays.asList(Constant.DOCTYPE.CREATIVE_ASSET));
        return mapping;
    }

    @Override
    public Collection<String> getAllowedSubtypes() {
        List<String> subtypes = new ArrayList<String>();

        // Get the parent of SubFolder
        CoreSession session = doc.getCoreSession();
        DocumentModel subFolderParent = session.getParentDocument(doc.getRef());
        if (initialMapping().get(subFolderParent.getType()) != null) {
            subtypes.addAll(initialMapping().get(subFolderParent.getType()));
        }
        return subtypes;
    }

}
