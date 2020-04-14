/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
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
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.io.marshallers.json.AbstractJsonWriterTest;
import org.nuxeo.ecm.core.io.marshallers.json.JsonAssert;
import org.nuxeo.ecm.core.io.marshallers.json.document.DocumentModelJsonWriter;
import org.nuxeo.ecm.core.io.registry.context.RenderingContext;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.transaction.TransactionHelper;

import amazon.devices.core.common.Constant;
import amazon.devices.core.enricher.AMZDSubtypesJsonEnricher;
import amazon.devices.core.test.AMZDFeature;
import amazon.devices.core.test.UnitTestHelper;

/**
 * @author frank.zheng
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class AMZDSubtypesEnricherTest extends AbstractJsonWriterTest.Local<DocumentModelJsonWriter, DocumentModel> {

    /**
     * @param writerClass
     * @param marshalledClass
     * @param marshalledGenericType
     */
    public AMZDSubtypesEnricherTest() {
        super(DocumentModelJsonWriter.class, DocumentModel.class);
    }

    @Inject
    private CoreSession session;

    @Inject
    private UserManager userManager;

    private final static String NORMAL = "Normal";

    private final static String ACCESS_CONTROLLED = "Access Controlled";

    @Test
    public void testAMZFolderSubtypesEnricher() throws IOException {

        // Get Devices AMZD Folder
        DocumentModel deviceNormal = session.getDocument(new PathRef(Constant.PATH.DEVICES.concat("/").concat(NORMAL)));
        JsonAssert json = jsonAssert(deviceNormal,
                RenderingContext.CtxBuilder.enrichDoc(AMZDSubtypesJsonEnricher.NAME).get());
        json.has("contextParameters").has(AMZDSubtypesJsonEnricher.NAME).isArray().length(1).childrenContains("type",
                Constant.DOCTYPE.DEVICE);

        DocumentModel deviceAccessControlled = session.getDocument(
                new PathRef(Constant.PATH.DEVICES.concat("/").concat(ACCESS_CONTROLLED)));
        json = jsonAssert(deviceAccessControlled,
                RenderingContext.CtxBuilder.enrichDoc(AMZDSubtypesJsonEnricher.NAME).get());
        json.has("contextParameters").has(AMZDSubtypesJsonEnricher.NAME).isArray().length(1).childrenContains("type",
                Constant.DOCTYPE.DEVICE);

        // Get Codenames AMZD Folder
        DocumentModel codenameNormal = session.getDocument(
                new PathRef(Constant.PATH.CODENAMES.concat("/").concat(NORMAL)));
        json = jsonAssert(codenameNormal, RenderingContext.CtxBuilder.enrichDoc(AMZDSubtypesJsonEnricher.NAME).get());
        json.has("contextParameters").has(AMZDSubtypesJsonEnricher.NAME).isArray().length(0);

        DocumentModel codenameAccessControlled = session.getDocument(
                new PathRef(Constant.PATH.CODENAMES.concat("/").concat(ACCESS_CONTROLLED)));
        json = jsonAssert(codenameAccessControlled,
                RenderingContext.CtxBuilder.enrichDoc(AMZDSubtypesJsonEnricher.NAME).get());
        json.has("contextParameters").has(AMZDSubtypesJsonEnricher.NAME).isArray().length(0);
    }

    @Test
    public void testDomainSubtypesEnricher() throws IOException {

        // Get Domain
        DocumentModel domain = session.getDocument(new PathRef("/default-domain"));
        JsonAssert json = jsonAssert(domain,
                RenderingContext.CtxBuilder.enrichDoc(AMZDSubtypesJsonEnricher.NAME).get());
        json.has("contextParameters").has(AMZDSubtypesJsonEnricher.NAME).isArray();
    }

    @Test
    public void testProductSubtypesEnricherWithDeviceManager() throws IOException {
        // Create test user
        NuxeoPrincipal user = UnitTestHelper.createUser("testUser", Arrays.asList("Device_Manager"), userManager);

        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        product.setPropertyValue(Constant.XPATH.TITLE, "product");
        product = session.createDocument(product);

        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        try (CloseableCoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), user)) {
            DocumentModel doc = userSession.getDocument(product.getRef());
            JsonAssert json = jsonAssert(doc,
                    RenderingContext.CtxBuilder.enrichDoc(AMZDSubtypesJsonEnricher.NAME).get());
            json.has("contextParameters").has(AMZDSubtypesJsonEnricher.NAME).isArray().length(1).childrenContains(
                    "type", Constant.DOCTYPE.DEVICE_ASSET);
        }
    }
    
    @Test
    public void testProductSubtypesEnricherWithUserInList() throws IOException {
        // Create test user
        NuxeoPrincipal user = UnitTestHelper.createUser("testUser", Arrays.asList("Librarian"), userManager);

        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        product.setPropertyValue(Constant.XPATH.TITLE, "product");
        product.setPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS, (Serializable) Arrays.asList("ADH_Manager", "testUser"));
        product = session.createDocument(product);

        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        try (CloseableCoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), user)) {
            DocumentModel doc = userSession.getDocument(product.getRef());
            JsonAssert json = jsonAssert(doc,
                    RenderingContext.CtxBuilder.enrichDoc(AMZDSubtypesJsonEnricher.NAME).get());
            json.has("contextParameters").has(AMZDSubtypesJsonEnricher.NAME).isArray().length(1).childrenContains(
                    "type", Constant.DOCTYPE.DEVICE_ASSET);
        }
    }
    
    @Test
    public void testProductSubtypesEnricherWithUserInGroupList() throws IOException {
        // Create test user
        NuxeoPrincipal user = UnitTestHelper.createUser("testUser", Arrays.asList("Librarian"), userManager);

        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        product.setPropertyValue(Constant.XPATH.TITLE, "product");
        product.setPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS, (Serializable) Arrays.asList("Librarian"));
        product = session.createDocument(product);

        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        try (CloseableCoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), user)) {
            DocumentModel doc = userSession.getDocument(product.getRef());
            JsonAssert json = jsonAssert(doc,
                    RenderingContext.CtxBuilder.enrichDoc(AMZDSubtypesJsonEnricher.NAME).get());
            json.has("contextParameters").has(AMZDSubtypesJsonEnricher.NAME).isArray().length(1).childrenContains(
                    "type", Constant.DOCTYPE.DEVICE_ASSET);
        }
    }
}
