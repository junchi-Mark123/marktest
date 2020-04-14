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
import amazon.devices.core.operation.CreateUserAccessOperation;
import amazon.devices.core.test.AMZDFeature;

/**
 * @author frank.zheng
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class CreateUserAccessOperationTest {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    private static final String RELATIVE_RETAIL_USER_PATH = "Retail Users";
    
    private static final String RELATIVE_COMPANY_PATH = RELATIVE_RETAIL_USER_PATH.concat("/test");

    @Test
    public void testCreateUserAccessWithoutCompanyExisted() throws OperationException {
        OperationContext ctx = new OperationContext(session);
        Map<String, String> params = new HashMap<>();
        params.put("request", generateRequest());
        DocumentModel res = (DocumentModel) automationService.run(ctx, CreateUserAccessOperation.ID, params);

        Assert.assertNotNull(res);

        Assert.assertEquals("test", res.getPropertyValue("userAccess:company"));
        Assert.assertEquals("test", res.getPropertyValue("userAccess:fName"));
        Assert.assertEquals("test1", res.getPropertyValue("userAccess:lName"));
        Assert.assertEquals("test@gmail.com", res.getPropertyValue("userAccess:email"));
        Assert.assertEquals("123456$e", res.getPropertyValue("userAccess:password"));
        Assert.assertEquals("retail", res.getPropertyValue("userAccess:typeOfAccess"));
        Assert.assertEquals(Constant.LIFECYCLE.USER_ACCESS, res.getLifeCyclePolicy());
    }

    @Test
    public void testCreateUserAccessWithCompanyExisted() throws OperationException {
        DocumentModel company = session.createDocumentModel(
                Constant.PATH.EXTERNAL_USER_MANAGEMENT.concat(RELATIVE_RETAIL_USER_PATH), "test",
                Constant.DOCTYPE.COMPANY);
        company = session.createDocument(company);
        session.save();

        Assert.assertEquals(
                Constant.PATH.EXTERNAL_USER_MANAGEMENT.concat(RELATIVE_COMPANY_PATH),
                company.getPathAsString());

        OperationContext ctx = new OperationContext(session);
        Map<String, String> params = new HashMap<>();
        params.put("request", generateRequest());
        DocumentModel res = (DocumentModel) automationService.run(ctx, CreateUserAccessOperation.ID, params);

        Assert.assertNotNull(res);
        
        Assert.assertEquals("test", res.getPropertyValue("userAccess:company"));
        Assert.assertEquals("test", res.getPropertyValue("userAccess:fName"));
        Assert.assertEquals("test1", res.getPropertyValue("userAccess:lName"));
        Assert.assertEquals("test@gmail.com", res.getPropertyValue("userAccess:email"));
        Assert.assertEquals("123456$e", res.getPropertyValue("userAccess:password"));
        Assert.assertEquals("retail", res.getPropertyValue("userAccess:typeOfAccess"));
        Assert.assertEquals(Constant.LIFECYCLE.USER_ACCESS, res.getLifeCyclePolicy());
    }

    private String generateRequest() {
        return "{\"company\":\"test\",\"firstname\":\"test\",\"lastname\":\"test1\",\"email\":\"test@gmail.com\",\"password\":\"123456$e\",\"accesstype\":\"Retail\"}";
    }

}
