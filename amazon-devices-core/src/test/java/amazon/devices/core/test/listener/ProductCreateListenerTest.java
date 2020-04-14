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
public class ProductCreateListenerTest {

    @Inject
    private CoreSession session;
    
    @Inject
    protected EventService eventService;

    @Test
    public void TestCreateProductWithNullGTMUnderNormal() throws InterruptedException {
        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        product.setPropertyValue(Constant.XPATH.TITLE, "product");
        product = session.createDocument(product);
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
        
        DocumentModel codename = session.getDocument(new PathRef(Constant.PATH.CODENAME_NORMAL.concat("/").concat(product.getTitle())));
        Assert.assertNotNull(codename);
        Assert.assertEquals("product", codename.getPropertyValue(Constant.XPATH.TITLE));
        Assert.assertEquals(product.getId(), codename.getPropertyValue(Constant.XPATH.CODENAME_DEVICE));
        assertThat(codename.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(codename.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(codename.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(codename.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(codename.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
    }

    @Test
    public void TestCreateProductWithNullGTMUnderAccessControlled() throws InterruptedException {
        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_ACCESS_CONTROLLED, "product",
                Constant.DOCTYPE.PRODUCT);
        product = session.createDocument(product);
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
        assertThat(product.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(product.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(product.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(product.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(product.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        
        DocumentModel codename = session.getDocument(new PathRef(Constant.PATH.CODENAME_ACCESS_CONTROLLED.concat("/").concat(product.getTitle())));
        Assert.assertEquals(product.getId(), codename.getPropertyValue(Constant.XPATH.CODENAME_DEVICE));
        Assert.assertNotNull(codename);
        Assert.assertEquals("product", codename.getPropertyValue(Constant.XPATH.TITLE));
        assertThat(codename.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(codename.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(codename.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(codename.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(codename.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
    }
    
    @Test
    public void TestCreateProductWithGTM() {
        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DAY_OF_MONTH, 8);
        product.setPropertyValue(Constant.XPATH.GTM, date);
        product = session.createDocument(product);
        session.save();

        Assert.assertEquals(new Long(1), product.getPropertyValue(Constant.XPATH.T_WEEK));
    }
    
    @Test
    public void TestCreateProductWithNullGTMUnderNormalWithCreateAccess() throws InterruptedException {
        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        product.setPropertyValue(Constant.XPATH.TITLE, "product");
        String[] list = {"Alice", "ADH_Manager"};
        product.setPropertyValue(Constant.XPATH.PRODUCT_CREATE_ASSET_ACCESS, list);
        product = session.createDocument(product);
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
        
        DocumentModel codename = session.getDocument(new PathRef(Constant.PATH.CODENAME_NORMAL.concat("/").concat(product.getTitle())));
        Assert.assertNotNull(codename);
        Assert.assertEquals("product", codename.getPropertyValue(Constant.XPATH.TITLE));
        Assert.assertEquals(product.getId(), codename.getPropertyValue(Constant.XPATH.CODENAME_DEVICE));
        assertThat(codename.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING), is(Access.GRANT));
        assertThat(codename.getACP().getAccess(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(codename.getACP().getAccess(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(codename.getACP().getAccess(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(codename.getACP().getAccess(Constant.GROUP.VENDOR, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
        assertThat(codename.getACP().getAccess(Constant.GROUP.AGENCY, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.DENY));
    }

}
