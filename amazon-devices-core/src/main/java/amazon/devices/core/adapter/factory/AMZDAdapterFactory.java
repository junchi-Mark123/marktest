/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.adapter.factory;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import amazon.devices.core.adapter.AMZDAdapter;
import amazon.devices.core.adapter.AMZFolderAdapter;
import amazon.devices.core.adapter.ProductAdapter;
import amazon.devices.core.adapter.SubFolderAdapter;
import amazon.devices.core.adapter.YearFolderAdapter;
import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 */
public class AMZDAdapterFactory implements DocumentAdapterFactory {

    /*
     * (non-Javadoc)
     * @see org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory#getAdapter(org.nuxeo.ecm.core.api.DocumentModel,
     * java.lang.Class)
     */
    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        if (Constant.DOCTYPE.AMZFOLDER.equals(doc.getType())) {
            return new AMZFolderAdapter(doc);
        } else if (Constant.DOCTYPE.YEAR.equals(doc.getType())) {
            return new YearFolderAdapter(doc);
        } else if (Constant.DOCTYPE.PRODUCT.equals(doc.getType())) {
            return new ProductAdapter(doc);
        } else if (Constant.DOCTYPE.SUBFOLDER.equals(doc.getType())) {
            return new SubFolderAdapter(doc);
        } else {
            return new AMZDAdapter(doc);
        }
    }

}
