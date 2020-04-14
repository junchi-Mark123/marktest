/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.operation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;

import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 *
 */
@Operation(id = AddPermissionForCompanyOperation.ID, category = Constants.CAT_DOCUMENT, label = "AMZD.AddPermissionForCompanyOperation", description = "Add permission for company doc type")
public class AddPermissionForCompanyOperation {

public static final String ID = "AMZD.AddPermissionForCompanyOperation";
    
    protected static final Log log = LogFactory.getLog(AddPermissionForCompanyOperation.class);
    
    @Context
    protected CoreSession session;

    @OperationMethod
    public DocumentModel run(DocumentModel documentModel) throws Exception {
        
        // Get parent of company doc
        // Can be "Retail Users", "Vendor Users" and "Agency Users"
        DocumentModel extUserType = session.getParentDocument(documentModel.getRef());
        
        // Get title of extUserType Document
        String title = (String) extUserType.getPropertyValue(Constant.XPATH.TITLE);
        // if title is "Retail Users", grant "view_noDownload","Edit" and "CreateContents" permission to "RAC Manager" and "Librarian"
        // if title is "Vendor Users" or "Agency Users", grant "view_noDownload","Edit" and "CreateContents" permission to only "Librarian"
        ACP acp = documentModel.getACP() != null ? documentModel.getACP() : new ACPImpl();
        String creator = session.getPrincipal().getName();
        if("Retail Users".equals(title)) {
            ACE ace = ACE.builder(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.EDIT)
                    .creator(creator)
                    .build();
            ACE ace1 = ACE.builder(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT)
                    .creator(creator)
                    .build();
            acp.addACE("local", ace);
            acp.addACE("local", ace1);
            
            
        } else if("Vendor Users".equals(title) || "Agency Users".equals(title)) {
            ACE ace = ACE.builder(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT)
                    .creator(creator)
                    .build();
            acp.addACE("local", ace);
        } else {
            log.warn(String.format("Unknown ExtUser_Type Document", title));
        }
        
        documentModel.setACP(acp, true);
        
        return documentModel;
    }
}
