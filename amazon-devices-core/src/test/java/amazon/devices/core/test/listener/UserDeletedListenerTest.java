/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test.listener;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.transaction.TransactionHelper;

import amazon.devices.core.common.Constant;
import amazon.devices.core.test.AMZDFeature;

/**
 * @author frank.zheng
 *
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class UserDeletedListenerTest {

    @Inject
    private CoreSession session;
    
    @Inject
    protected UserManager userManager;
    
    @Inject
    protected EventService eventService;
    
    private final static String USER_ACCESS_QUERY_BY_USER_ID = "SELECT * FROM Document WHERE ecm:primaryType = 'UserAccess' AND ecm:isVersion = 0 AND userAccess:related_User = '%s' AND ecm:isTrashed = 0";

    @Test
    public void testDeleteUserFromAccessDocument() throws NuxeoException {

        // Create test User
        NuxeoPrincipal test1 = new NuxeoPrincipalImpl("test1");
        test1.setFirstName("test_first");
        test1.setLastName("test_last");
        test1.setCompany("company1");
        test1.setEmail("test@test.com");
        
        DocumentModel user = userManager.createUser(test1.getModel());

        // Create Company
        DocumentModel company = session.createDocumentModel(Constant.PATH.RETAIL_USERS, "test",
                Constant.DOCTYPE.COMPANY);
        company = session.createDocument(company);
        session.save();

        // Create User Access and link to new created User
        DocumentModel userAccess = session.createDocumentModel(Constant.PATH.RETAIL_USERS.concat("/test"),
                "testUserAccess", Constant.DOCTYPE.USER_ACCESS);
        userAccess.setPropertyValue("userAccess:related_User", user.getId());
        userAccess.setPropertyValue("fName", "test");
        userAccess.setPropertyValue("lName", "test");
        userAccess.setPropertyValue("email", "test@test.com");
        userAccess.setPropertyValue("company", "test");
        userAccess.setPropertyValue("password", "123456$e");
        userAccess.setPropertyValue("typeOfAccess", "retail");
        userAccess = session.createDocument(userAccess);
        session.save();
        
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        
        eventService.waitForAsyncCompletion();
        
        Assert.assertEquals(1, session.query(String.format(USER_ACCESS_QUERY_BY_USER_ID, user.getId())).size());
        
        // delete user
        userManager.deleteUser(user.getId());
        
        Assert.assertEquals(0, session.query(String.format(USER_ACCESS_QUERY_BY_USER_ID, user.getId())).size());
    }

}
