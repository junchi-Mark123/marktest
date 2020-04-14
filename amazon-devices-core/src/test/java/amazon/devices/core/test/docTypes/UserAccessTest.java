/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test.docTypes;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import amazon.devices.core.common.Constant;
import amazon.devices.core.test.AMZDFeature;

/**
 * @author frank.zheng
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class UserAccessTest {

    @Inject
    private CoreSession session;

    private final static String TEST_USER_ACCESS = "Nuxeo";

    @Test(expected = RuntimeException.class)
    public void testUserAccessCreatedWithoutCompany() {
        DocumentModel doc = session.createDocumentModel(Constant.PATH.DOMAIN, TEST_USER_ACCESS, Constant.DOCTYPE.USER_ACCESS);
        doc.setPropertyValue("fName", "test");
        doc.setPropertyValue("lName", "test");
        doc.setPropertyValue("email", "test");
        doc.setPropertyValue("typeOfAccess", "retail");
        doc.setPropertyValue("password", "test123");
        session.createDocument(doc);
    }
    
    @Test(expected = RuntimeException.class)
    public void testUserAccessCreatedWithoutFirstName() {
        DocumentModel doc = session.createDocumentModel(Constant.PATH.DOMAIN, TEST_USER_ACCESS, Constant.DOCTYPE.USER_ACCESS);
        doc.setPropertyValue("company", "test");
        doc.setPropertyValue("lName", "test");
        doc.setPropertyValue("email", "test");
        doc.setPropertyValue("typeOfAccess", "retail");
        doc.setPropertyValue("password", "test123");
        session.createDocument(doc);
    }
    
    @Test(expected = RuntimeException.class)
    public void testUserAccessWithoutLastName() {
        DocumentModel doc = session.createDocumentModel(Constant.PATH.DOMAIN, TEST_USER_ACCESS,
                Constant.DOCTYPE.USER_ACCESS);
        doc.setPropertyValue("company", "test");
        doc.setPropertyValue("fName", "test");
        doc.setPropertyValue("email", "test");
        doc.setPropertyValue("typeOfAccess", "retail");
        doc.setPropertyValue("password", "test123");

        doc = session.createDocument(doc);

        session.save();
        Assert.assertEquals(Constant.LIFECYCLE.USER_ACCESS, doc.getLifeCyclePolicy());

    }
    
    @Test(expected = RuntimeException.class)
    public void testUserAccessWithoutEmail() {
        DocumentModel doc = session.createDocumentModel(Constant.PATH.DOMAIN, TEST_USER_ACCESS,
                Constant.DOCTYPE.USER_ACCESS);
        doc.setPropertyValue("company", "test");
        doc.setPropertyValue("fName", "test");
        doc.setPropertyValue("lName", "test");
        doc.setPropertyValue("typeOfAccess", "retail");
        doc.setPropertyValue("password", "test123");

        doc = session.createDocument(doc);

        session.save();
        Assert.assertEquals(Constant.LIFECYCLE.USER_ACCESS, doc.getLifeCyclePolicy());

    }
    
    @Test(expected = RuntimeException.class)
    public void testUserAccessWithWrongEmail() {
        DocumentModel doc = session.createDocumentModel(Constant.PATH.DOMAIN, TEST_USER_ACCESS,
                Constant.DOCTYPE.USER_ACCESS);
        doc.setPropertyValue("company", "test");
        doc.setPropertyValue("fName", "test");
        doc.setPropertyValue("lName", "test");
        doc.setPropertyValue("email", "test");
        doc.setPropertyValue("typeOfAccess", "retail");
        doc.setPropertyValue("password", "test123");

        doc = session.createDocument(doc);

        session.save();
        Assert.assertEquals(Constant.LIFECYCLE.USER_ACCESS, doc.getLifeCyclePolicy());

    }
    
    @Test(expected = RuntimeException.class)
    public void testUserAccessWithoutAccessType() {
        DocumentModel doc = session.createDocumentModel(Constant.PATH.DOMAIN, TEST_USER_ACCESS,
                Constant.DOCTYPE.USER_ACCESS);
        doc.setPropertyValue("company", "test");
        doc.setPropertyValue("fName", "test");
        doc.setPropertyValue("lName", "test");
        doc.setPropertyValue("email", "test");
        doc.setPropertyValue("password", "test123");

        doc = session.createDocument(doc);

        session.save();
        Assert.assertEquals(Constant.LIFECYCLE.USER_ACCESS, doc.getLifeCyclePolicy());

    }
    
    @Test(expected = RuntimeException.class)
    public void testUserAccessWithWrongAccessType() {
        DocumentModel doc = session.createDocumentModel(Constant.PATH.DOMAIN, TEST_USER_ACCESS,
                Constant.DOCTYPE.USER_ACCESS);
        doc.setPropertyValue("company", "test");
        doc.setPropertyValue("fName", "test");
        doc.setPropertyValue("lName", "test");
        doc.setPropertyValue("email", "test");
        doc.setPropertyValue("typeOfAccess", "test");
        doc.setPropertyValue("password", "test123");

        doc = session.createDocument(doc);

        session.save();
        Assert.assertEquals(Constant.LIFECYCLE.USER_ACCESS, doc.getLifeCyclePolicy());

    }
    
    @Test(expected = RuntimeException.class)
    public void testUserAccessWithoutPassword() {
        DocumentModel doc = session.createDocumentModel(Constant.PATH.DOMAIN, TEST_USER_ACCESS,
                Constant.DOCTYPE.USER_ACCESS);
        doc.setPropertyValue("company", "test");
        doc.setPropertyValue("fName", "test");
        doc.setPropertyValue("lName", "test");
        doc.setPropertyValue("email", "test");
        doc.setPropertyValue("typeOfAccess", "retail");

        doc = session.createDocument(doc);

        session.save();
        Assert.assertEquals(Constant.LIFECYCLE.USER_ACCESS, doc.getLifeCyclePolicy());

    }
    
    @Test(expected = RuntimeException.class)
    public void testUserAccessWithInvalidPassword() {
        DocumentModel doc = session.createDocumentModel(Constant.PATH.DOMAIN, TEST_USER_ACCESS,
                Constant.DOCTYPE.USER_ACCESS);
        doc.setPropertyValue("company", "test");
        doc.setPropertyValue("fName", "test");
        doc.setPropertyValue("lName", "test");
        doc.setPropertyValue("email", "test");
        doc.setPropertyValue("typeOfAccess", "retail");
        doc.setPropertyValue("password", "test");

        doc = session.createDocument(doc);

        session.save();
        Assert.assertEquals(Constant.LIFECYCLE.USER_ACCESS, doc.getLifeCyclePolicy());

    }

    public void testUserAccessWithAllRequiredFieldsCreated() {
        DocumentModel doc = session.createDocumentModel(Constant.PATH.DOMAIN, TEST_USER_ACCESS,
                Constant.DOCTYPE.USER_ACCESS);
        doc.setPropertyValue("company", "test");
        doc.setPropertyValue("fName", "test");
        doc.setPropertyValue("lName", "test");
        doc.setPropertyValue("email", "test@gmail.com");
        doc.setPropertyValue("typeOfAccess", "retail");
        doc.setPropertyValue("password", "test123");

        doc = session.createDocument(doc);

        session.save();
        Assert.assertEquals(Constant.LIFECYCLE.USER_ACCESS, doc.getLifeCyclePolicy());

    }

}
