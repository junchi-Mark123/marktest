/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.operation;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 */
@Operation(id = SetPasswordForUserAccessOperation.ID, category = Constants.CAT_DOCUMENT, label = "AMZD.SetPasswordForUserAccessOperation", description = "Set Password for User Access Document")
public class SetPasswordForUserAccessOperation {

    public static final String ID = "AMZD.SetPasswordForUserAccessOperation";

    protected static final Log log = LogFactory.getLog(SetPasswordForUserAccessOperation.class);

    @Param(name = "username", required = true)
    protected String username;

    @Param(name = "password", required = true)
    protected String password;

    @Context
    protected CoreSession session;

    @OperationMethod(collector = DocumentModelCollector.class)
    public DocumentModel run(DocumentModel documentModel) {

        // Get usermanager service
        UserManager userManager = Framework.getService(UserManager.class);

        String pattern = username + ":" + userManager.getDigestAuthRealm() + ":" + password;

        documentModel.setPropertyValue(Constant.XPATH.PASSWORD, DigestUtils.md5Hex(pattern));
        documentModel = session.saveDocument(documentModel);
        session.save();

        return documentModel;
    }
}
