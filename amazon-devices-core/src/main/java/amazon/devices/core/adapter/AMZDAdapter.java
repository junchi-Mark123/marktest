/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.adapter;

import java.util.Collection;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * @author frank.zheng
 *
 */
public class AMZDAdapter {

    protected DocumentModel doc;
    
    public AMZDAdapter(DocumentModel doc) {
        this.doc = doc;
    }
    
    public Collection<String> getAllowedSubtypes() { 
        return doc.getDocumentType().getAllowedSubtypes();
    }
}
