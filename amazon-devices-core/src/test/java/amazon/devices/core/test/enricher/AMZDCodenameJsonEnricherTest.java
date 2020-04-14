/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
/**
 * 
 */
package amazon.devices.core.test.enricher;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoGroup;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.io.marshallers.json.AbstractJsonWriterTest;
import org.nuxeo.ecm.core.io.marshallers.json.JsonAssert;
import org.nuxeo.ecm.core.io.marshallers.json.document.DocumentModelJsonWriter;
import org.nuxeo.ecm.core.io.registry.context.RenderingContext.CtxBuilder;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.usermanager.NuxeoGroupImpl;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.transaction.TransactionHelper;

import amazon.devices.core.common.Constant;
import amazon.devices.core.enricher.AMZDCodenameJsonEnricher;
import amazon.devices.core.test.AMZDFeature;

/**
 * @author frank.zheng
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class AMZDCodenameJsonEnricherTest extends AbstractJsonWriterTest.Local<DocumentModelJsonWriter, DocumentModel> {

    @Inject
    private CoreSession session;

    @Inject
    private UserManager userManager;

    @Inject
    protected EventService eventService;

    private static final String NAME_TYPE = "name_type";

    private static final String NAME_VALUE = "name_value";

    private static final String NAME_CODENAME = "name_codename";

    /**
     * @param writerClass
     * @param marshalledClass
     */
    public AMZDCodenameJsonEnricherTest() {
        super(DocumentModelJsonWriter.class, DocumentModel.class);
    }

    @Before
    public void init() {
        // Create users and groups
        NuxeoGroup group = new NuxeoGroupImpl("test");
        userManager.createGroup(group.getModel());

        // user for retail test
        NuxeoPrincipal test1 = new NuxeoPrincipalImpl("test@test.com");
        test1.setFirstName("test_first");
        test1.setLastName("test_last");
        test1.setCompany("company1");
        test1.setEmail("test@test.com");
        DocumentModel user1 = userManager.createUser(test1.getModel());

        // user for group test
        NuxeoPrincipal test2 = new NuxeoPrincipalImpl("test2");
        test2.setFirstName("test_first");
        test2.setLastName("test_last");
        test2.setCompany("company1");
        test2.setEmail("test@test.com");
        test2.setGroups(Arrays.asList("test"));
        userManager.createUser(test2.getModel());

        // user for user test
        NuxeoPrincipal test3 = new NuxeoPrincipalImpl("test3");
        test3.setFirstName("test_first");
        test3.setLastName("test_last");
        test3.setCompany("company1");
        test3.setEmail("test@test.com");
        userManager.createUser(test3.getModel());

        // Create Company
        DocumentModel company = session.createDocumentModel(Constant.PATH.RETAIL_USERS, "test",
                Constant.DOCTYPE.COMPANY);
        company = session.createDocument(company);

        // Create Retail/Vendor/Agency User
        DocumentModel userAccess = session.createDocumentModel(Constant.PATH.RETAIL_USERS.concat("/test"),
                "testUserAccess", Constant.DOCTYPE.USER_ACCESS);
        userAccess.setPropertyValue("userAccess:related_User", user1.getId());
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

        // Create product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_MONTH, 8);
        product.setPropertyValue(Constant.XPATH.GTM, date);
        product.setPropertyValue(Constant.XPATH.TITLE, "product");
        product = session.createDocument(product);
        ACP acp = session.getACP(product.getRef());
        ACL localACL = acp.getOrCreateACL(ACL.LOCAL_ACL);
        localACL.add(new ACE("test@test.com", SecurityConstants.READ));
        localACL.add(new ACE("test2", SecurityConstants.READ));
        localACL.add(new ACE("test3", SecurityConstants.READ));
        session.setACP(product.getRef(), acp, true);
        session.save();

        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        eventService.waitForAsyncCompletion();

        // Create codename with specificnames
        List<Map<String, String>> specificNames = new ArrayList<Map<String, String>>();
        Map<String, String> userName = new HashMap<String, String>();
        userName.put(NAME_TYPE, "user");
        userName.put(NAME_VALUE, "test3");
        userName.put(NAME_CODENAME, "user codename");
        specificNames.add(userName);

        Map<String, String> groupName = new HashMap<String, String>();
        groupName.put(NAME_TYPE, "group");
        groupName.put(NAME_VALUE, "test");
        groupName.put(NAME_CODENAME, "group codename");
        specificNames.add(groupName);

        Map<String, String> retailName = new HashMap<String, String>();
        retailName.put(NAME_TYPE, "retail");
        retailName.put(NAME_VALUE, "test");
        retailName.put(NAME_CODENAME, "retail codename");
        specificNames.add(retailName);
        DocumentModel codename = session.getDocument(new PathRef(Constant.PATH.CODENAME_NORMAL.concat("/product")));
        codename.setPropertyValue(Constant.XPATH.SPECIFIC_NAMES, (Serializable) specificNames);
        codename = session.saveDocument(codename);
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
    }

    @Test
    public void testUserCode() throws IOException {
        NuxeoPrincipal user = userManager.getPrincipal("test3");
        try (CloseableCoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), user)) {
            DocumentModel doc = userSession.getDocument(new PathRef(Constant.PATH.DEVICES_NORMAL.concat("/product")));
            JsonAssert json = jsonAssert(doc, CtxBuilder.enrichDoc(AMZDCodenameJsonEnricher.NAME).get());
            json = json.has("contextParameters").isObject();
            json.properties(1);
            json = json.has(AMZDCodenameJsonEnricher.NAME).isEquals("user codename");
        } 
    }
    
    @Test
    public void testGroupCode() throws IOException {
        NuxeoPrincipal user = userManager.getPrincipal("test2");
        try (CloseableCoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), user)) {
            DocumentModel doc = userSession.getDocument(new PathRef(Constant.PATH.DEVICES_NORMAL.concat("/product")));
            JsonAssert json = jsonAssert(doc, CtxBuilder.enrichDoc(AMZDCodenameJsonEnricher.NAME).get());
            json = json.has("contextParameters").isObject();
            json.properties(1);
            json = json.has(AMZDCodenameJsonEnricher.NAME).isEquals("group codename");
        } 
    }
    
    @Test
    public void testRetailCode() throws IOException {
        NuxeoPrincipal user = userManager.getPrincipal("test@test.com");
        try (CloseableCoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), user)) {
            DocumentModel doc = userSession.getDocument(new PathRef(Constant.PATH.DEVICES_NORMAL.concat("/product")));
            JsonAssert json = jsonAssert(doc, CtxBuilder.enrichDoc(AMZDCodenameJsonEnricher.NAME).get());
            json = json.has("contextParameters").isObject();
            json.properties(1);
            json = json.has(AMZDCodenameJsonEnricher.NAME).isEquals("retail codename");
        } 
    }
    
    @Test
    public void testGTMCode() throws IOException {
        DocumentModel product = session.getDocument(new PathRef(Constant.PATH.DEVICES_NORMAL.concat("/product")));
        Calendar date = Calendar.getInstance();
        date.set(2018, 12, 20);
        product.setPropertyValue(Constant.XPATH.GTM, date);
        session.saveDocument(product);
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        eventService.waitForAsyncCompletion();
        
        NuxeoPrincipal user = userManager.getPrincipal("test@test.com");
        try (CloseableCoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), user)) {
            DocumentModel doc = userSession.getDocument(new PathRef(Constant.PATH.DEVICES_NORMAL.concat("/product")));
            JsonAssert json = jsonAssert(doc, CtxBuilder.enrichDoc(AMZDCodenameJsonEnricher.NAME).get());
            json = json.has("contextParameters").isObject();
            json.properties(1);
            json = json.has(AMZDCodenameJsonEnricher.NAME).isEquals("product");
        } 
    }

}
