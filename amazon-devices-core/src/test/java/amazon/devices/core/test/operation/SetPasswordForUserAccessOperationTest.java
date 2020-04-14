/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test.operation;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import amazon.devices.core.common.Constant;
import amazon.devices.core.operation.SetPasswordForUserAccessOperation;
import amazon.devices.core.test.AMZDFeature;

/**
 * @author frank.zheng
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class SetPasswordForUserAccessOperationTest {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Test
    public void testSetPasswordForUserAccessDocument() throws OperationException {
        // Create Company
        DocumentModel company = session.createDocumentModel(Constant.PATH.RETAIL_USERS, "test",
                Constant.DOCTYPE.COMPANY);
        company = session.createDocument(company);
        session.save();

        // Create the User Access Document
        // Create User Access and link to new created User
        DocumentModel userAccess = session.createDocumentModel(Constant.PATH.RETAIL_USERS.concat("/test"),
                "testUserAccess", Constant.DOCTYPE.USER_ACCESS);
        userAccess.setPropertyValue("fName", "test");
        userAccess.setPropertyValue("lName", "test");
        userAccess.setPropertyValue("email", "test@test.com");
        userAccess.setPropertyValue("company", "test");
        userAccess.setPropertyValue("password", "123456$e");
        userAccess.setPropertyValue("typeOfAccess", "retail");
        userAccess = session.createDocument(userAccess);
        session.save();
        
        OperationContext ctx = new OperationContext(session);
        Map<String, String> params = new HashMap<>();
        params.put("username", "testUser");
        params.put("password", "123456$e");
        ctx.setInput(userAccess);
        DocumentModel res = (DocumentModel) automationService.run(ctx, SetPasswordForUserAccessOperation.ID, params);
        
        Assert.assertNotEquals("123456$e", res.getPropertyValue(Constant.XPATH.PASSWORD));
    }
}
