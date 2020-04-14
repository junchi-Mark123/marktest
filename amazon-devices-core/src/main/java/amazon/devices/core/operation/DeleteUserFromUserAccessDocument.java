/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.operation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 *
 */
@Operation(id = DeleteUserFromUserAccessDocument.ID, category = Constants.CAT_DOCUMENT, label = "AMZD.DeleteUserFromUserAccessDocument", description = "Delete User From UserAccess Document")
public class DeleteUserFromUserAccessDocument {
    
    public static final String ID = "AMZD.DeleteUserFromUserAccessDocument";
    
    protected static final Log log = LogFactory.getLog(DeleteUserFromUserAccessDocument.class);
    
    @OperationMethod
    public DocumentModel run(DocumentModel documentModel) throws Exception {
        
        if(documentModel.getPropertyValue(Constant.XPATH.RELATED_USER) == null) {
            String message = String.format("Related User does not existed for UserAccess Document: %s", documentModel.getPropertyValue(Constant.XPATH.TITLE));
            log.warn(message);
            return documentModel;
        }
        
        // Get user from UserAccess Document
        String user = (String) documentModel.getPropertyValue(Constant.XPATH.RELATED_USER);
        
        // Get usermanager service
        UserManager userManager = Framework.getService(UserManager.class);
        
        // Check whether user exists or not
        if(userManager.getUserModel(user) == null) {
            return documentModel;
        }
        
        // Remove the user from system
        userManager.deleteUser(user);
    
        return documentModel;
    }

}
