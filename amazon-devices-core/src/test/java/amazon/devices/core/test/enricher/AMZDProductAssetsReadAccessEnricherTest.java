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
import java.util.Arrays;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.io.marshallers.json.AbstractJsonWriterTest;
import org.nuxeo.ecm.core.io.marshallers.json.JsonAssert;
import org.nuxeo.ecm.core.io.marshallers.json.document.DocumentModelJsonWriter;
import org.nuxeo.ecm.core.io.registry.context.RenderingContext.CtxBuilder;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.transaction.TransactionHelper;

import amazon.devices.core.common.Constant;
import amazon.devices.core.enricher.AMZDProductAssetsReadAccessEnricher;
import amazon.devices.core.test.AMZDFeature;
import amazon.devices.core.test.UnitTestHelper;

/**
 * @author frank.zheng
 *
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class AMZDProductAssetsReadAccessEnricherTest extends AbstractJsonWriterTest.Local<DocumentModelJsonWriter, DocumentModel> {

    @Inject
    UserManager userManager;
    
    @Inject
    CoreSession session;
    
    /**
     * @param writerClass
     * @param marshalledClass
     * @param marshalledGenericType
     */
    public AMZDProductAssetsReadAccessEnricherTest() {
        super(DocumentModelJsonWriter.class, DocumentModel.class);
    }
    
    @Test
    public void testAccessWithAdministrator() throws IOException {
        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        product.setPropertyValue(Constant.XPATH.TITLE, "product");
        product = session.createDocument(product);
        
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        
        try (CloseableCoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), "Administrator")) {
            DocumentModel doc = userSession.getDocument(product.getRef());
            JsonAssert json = jsonAssert(doc, CtxBuilder.enrichDoc(AMZDProductAssetsReadAccessEnricher.NAME).get());
            json = json.has("contextParameters").isObject();
            json.properties(1);
            json = json.has(AMZDProductAssetsReadAccessEnricher.NAME).isBool();
            json.isEquals(false);
        }
    }
    
    @Test
    public void testAccessWithDeviceManagerUser() throws IOException {
        // Create test user
        NuxeoPrincipal user = UnitTestHelper.createUser("testUser", Arrays.asList("Device_Manager"), userManager);
        
        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        product.setPropertyValue(Constant.XPATH.TITLE, "product");
        product = session.createDocument(product);
        ACP acp = session.getACP(product.getRef());
        ACL localACL = acp.getOrCreateACL(ACL.LOCAL_ACL);
        localACL.add(new ACE("testUser", SecurityConstants.READ));
        session.setACP(product.getRef(), acp, true);
        
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        
        try (CloseableCoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), user)) {
            DocumentModel doc = userSession.getDocument(product.getRef());
            JsonAssert json = jsonAssert(doc, CtxBuilder.enrichDoc(AMZDProductAssetsReadAccessEnricher.NAME).get());
            json = json.has("contextParameters").isObject();
            json.properties(1);
            json = json.has(AMZDProductAssetsReadAccessEnricher.NAME).isBool();
            json.isEquals(true);
        }
    }
    
    @Test
    public void testAccessWithUserInList() throws IOException {
        // Create test user
        NuxeoPrincipal user = UnitTestHelper.createUser("testUser", Arrays.asList("Librarian"), userManager);
        
        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        product.setPropertyValue(Constant.XPATH.TITLE, "product");
        product.setPropertyValue(Constant.XPATH.PRODUCT_READ_ASSET_ACCESS, (Serializable) Arrays.asList("ADH_Manager", "testUser"));
        product = session.createDocument(product);
        ACP acp = session.getACP(product.getRef());
        ACL localACL = acp.getOrCreateACL(ACL.LOCAL_ACL);
        localACL.add(new ACE("testUser", SecurityConstants.READ));
        session.setACP(product.getRef(), acp, true);
        
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        
        try (CloseableCoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), user)) {
            DocumentModel doc = userSession.getDocument(product.getRef());
            JsonAssert json = jsonAssert(doc, CtxBuilder.enrichDoc(AMZDProductAssetsReadAccessEnricher.NAME).get());
            json = json.has("contextParameters").isObject();
            json.properties(1);
            json = json.has(AMZDProductAssetsReadAccessEnricher.NAME).isBool();
            json.isEquals(true);
        }
    }
    
    @Test
    public void testAccessWithUserInGroupList() throws IOException {
        // Create test user
        NuxeoPrincipal user = UnitTestHelper.createUser("testUser", Arrays.asList("ADH_Manager"), userManager);
        
        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        product.setPropertyValue(Constant.XPATH.TITLE, "product");
        product.setPropertyValue(Constant.XPATH.PRODUCT_READ_ASSET_ACCESS, (Serializable) Arrays.asList("ADH_Manager"));
        product = session.createDocument(product);
        ACP acp = session.getACP(product.getRef());
        ACL localACL = acp.getOrCreateACL(ACL.LOCAL_ACL);
        localACL.add(new ACE("testUser", SecurityConstants.READ));
        session.setACP(product.getRef(), acp, true);
        
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        
        try (CloseableCoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), user)) {
            DocumentModel doc = userSession.getDocument(product.getRef());
            JsonAssert json = jsonAssert(doc, CtxBuilder.enrichDoc(AMZDProductAssetsReadAccessEnricher.NAME).get());
            json = json.has("contextParameters").isObject();
            json.properties(1);
            json = json.has(AMZDProductAssetsReadAccessEnricher.NAME).isBool();
            json.isEquals(true);
        }
    }
}
