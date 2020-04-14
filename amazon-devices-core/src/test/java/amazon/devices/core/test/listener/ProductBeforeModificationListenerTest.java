/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test.listener;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
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
public class ProductBeforeModificationListenerTest {

    @Inject
    private CoreSession session;
    
    @Inject
    private EventService eventService;

    @Test
    public void TestUpdateProductWithNullGTM() {
        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_MONTH, 8);
        product.setPropertyValue(Constant.XPATH.GTM, date);
        product = session.createDocument(product);
        session.save();
        Assert.assertEquals(new Long(1), product.getPropertyValue(Constant.XPATH.T_WEEK));
        
        // Update Product
        product.setPropertyValue(Constant.XPATH.GTM, null);
        product = session.saveDocument(product);
        session.save();

        Assert.assertNull(product.getPropertyValue(Constant.XPATH.T_WEEK));
    }
    
    @Test
    public void TestUpdateProductWithGTM() {
        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        product = session.createDocument(product);
        session.save();
        Assert.assertNull(product.getPropertyValue(Constant.XPATH.T_WEEK));
        
        // Update Product
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_MONTH, 8);
        product.setPropertyValue(Constant.XPATH.GTM, date);
        product = session.saveDocument(product);
        session.save();

        Assert.assertEquals(new Long(1), product.getPropertyValue(Constant.XPATH.T_WEEK));
    }
    
    @Test
    public void TestUpdateProductWithCreateList() throws InterruptedException {
        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_MONTH, 8);
        product.setPropertyValue(Constant.XPATH.GTM, date);
        product = session.createDocument(product);
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        
        Assert.assertEquals(new Long(1), product.getPropertyValue(Constant.XPATH.T_WEEK));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        
        // Update Product
        product.setPropertyValue(Constant.XPATH.GTM, null);
        String[] list = {"Alice", "ADH_Manager"};
        product.setPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS, list);
        product = session.saveDocument(product);
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        eventService.waitForAsyncCompletion();
        
        product = session.getDocument(product.getRef());

        Assert.assertNull(product.getPropertyValue(Constant.XPATH.T_WEEK));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess("Alice", Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        
        String[] newList = {"ADH_Manager"};
        product.setPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS, newList);
        product = session.saveDocument(product);
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        eventService.waitForAsyncCompletion();
        
        product = session.getDocument(product.getRef());
        
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess("Alice", Constant.PERMISSION.CREATE_CONTENTS), is(Access.DENY));
    }
    
    @Test
    public void TestUpdateProductWithReadList() throws InterruptedException {
        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_MONTH, 8);
        product.setPropertyValue(Constant.XPATH.GTM, date);
        product = session.createDocument(product);
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        
        DocumentModel asset1 = session.createDocumentModel(product.getPathAsString(), "asset1", Constant.DOCTYPE.DEVICE_ASSET);
        DocumentModel asset2 = session.createDocumentModel(product.getPathAsString(), "asset2", Constant.DOCTYPE.DEVICE_ASSET);
        asset1 = session.createDocument(asset1);
        asset2 = session.createDocument(asset2);
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        
        Assert.assertEquals(new Long(1), product.getPropertyValue(Constant.XPATH.T_WEEK));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        
        // Update Product
        product.setPropertyValue(Constant.XPATH.GTM, null);
        String[] list = {"Alice", "ADH_Manager"};
        product.setPropertyValue(Constant.XPATH.PRODUCT_READ_ASSET_ACCESS, list);
        product = session.saveDocument(product);
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        eventService.waitForAsyncCompletion();
        
        product = session.getDocument(product.getRef());

        Assert.assertNull(product.getPropertyValue(Constant.XPATH.T_WEEK));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        
        asset1 = session.getDocument(asset1.getRef());
        assertThat(asset1.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess("Alice", Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        
        asset2 = session.getDocument(asset2.getRef());
        assertThat(asset2.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess("Alice", Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        
        
        String[] newList = {"ADH_Manager"};
        product.setPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS, newList);
        product.setPropertyValue(Constant.XPATH.PRODUCT_READ_ASSET_ACCESS, newList);
        product = session.saveDocument(product);
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        eventService.waitForAsyncCompletion();
        
        product = session.getDocument(product.getRef());
        
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(product.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(product.getACP().getAccess("Alice", Constant.PERMISSION.CREATE_CONTENTS), is(Access.DENY));
        
        asset1 = session.getDocument(asset1.getRef());
        assertThat(asset1.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset1.getACP().getAccess("Alice", Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(asset1.getACP().getAccess("Alice", Constant.PERMISSION.EDIT), is(Access.DENY));
        assertThat(asset1.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        
        asset2 = session.getDocument(asset2.getRef());
        assertThat(asset2.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(asset2.getACP().getAccess("Alice", Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(asset2.getACP().getAccess("Alice", Constant.PERMISSION.EDIT), is(Access.DENY));
        assertThat(asset2.getACP().getAccess("ADH_Manager", Constant.PERMISSION.EDIT), is(Access.GRANT));
    }

}
