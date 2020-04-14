/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.common;

import java.util.Calendar;
import java.util.Map;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * @author frank.zheng
 *
 */
public class AMZDUtils {

    public static long calculateWeeks(Calendar date) {
        Calendar today = Calendar.getInstance();
        long diff = date.getTimeInMillis() - today.getTimeInMillis();
        if(diff < 0) {
            return 0;
        }
        return diff / (24 * 60 * 60 * 1000 * 7);
    }
    
    public static DocumentModel inheritMetadata(DocumentModel child, DocumentModel parent) {
        for(String schema : child.getSchemas()) {
            if(parent.hasSchema(schema)) {
                Map<String, Object> properties = child.getProperties(schema);
                for(String key : properties.keySet()) {
                    if(properties.get(key) == null) {
                        child.setProperty(schema, key, parent.getProperty(schema, key));
                    }
                }
            }
        }
        
        return child;
    }
}
