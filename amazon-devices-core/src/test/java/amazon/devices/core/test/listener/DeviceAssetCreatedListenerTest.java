/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
/**
 * 
 */
package amazon.devices.core.test.listener;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.security.Access;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.transaction.TransactionHelper;

import amazon.devices.core.common.Constant;
import amazon.devices.core.test.AMZDFeature;

/**
 * @author frank.zheng
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class DeviceAssetCreatedListenerTest {

    @Inject
    private CoreSession session;

    @Inject
    private EventService eventService;

    @Test
    public void testDeviceAssetCreated() {
        // Create product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        String[] list = {"Alice", "ADH_Manager"};
        product.setPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS, list);
        product.setPropertyValue(Constant.XPATH.PRODUCT_READ_ASSET_ACCESS, list);
        product.setPropertyValue(Constant.XPATH.TITLE, "product");
        product = session.createDocument(product);
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        eventService.waitForAsyncCompletion();

        DocumentModel deviceAsset = session.createDocumentModel(product.getPathAsString(), "asset",
                Constant.DOCTYPE.DEVICE_ASSET);
        deviceAsset = session.createDocument(deviceAsset);
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        
        deviceAsset = session.getDocument(deviceAsset.getRef());
        
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess("Alice", Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess("Alice", Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(deviceAsset.getACP().getAccess("Alice", Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));

        Assert.assertEquals(session.getDocument(new PathRef(Constant.PATH.DEVICES)).getId(), deviceAsset.getPropertyValue(Constant.XPATH.RELATIONS_WORKSPACE_LOCATION));
    }

}
