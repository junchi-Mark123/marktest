/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
/**
 * 
 */
package amazon.devices.core.test.listener;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
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
public class CodenameBeforeModificationListenerTest {

    @Inject
    private CoreSession session;

    @Inject
    private EventService eventService;

    @Test
    public void testUpdateCodeWithTitleChanged() throws InterruptedException {
        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        product.setPropertyValue(Constant.XPATH.TITLE, "product");
        product = session.createDocument(product);
        session.save();

        // Wait until all async works finish, codename should be created as well
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        eventService.waitForAsyncCompletion();

        // Get codename and update the title
        DocumentModel codename = session.getDocument(
                new PathRef(Constant.PATH.CODENAME_NORMAL.concat("/").concat(product.getTitle())));
        codename.setPropertyValue(Constant.XPATH.TITLE, "codename");
        session.saveDocument(codename);
        session.save();
        
        product = session.getDocument(product.getRef());
        Assert.assertEquals("codename", product.getPropertyValue(Constant.XPATH.TITLE));
    }
    
    @Test
    public void testUpdateCodeWithoutTitleChanged() throws InterruptedException {
        // Create Product
        DocumentModel product = session.createDocumentModel(Constant.PATH.DEVICES_NORMAL, "product",
                Constant.DOCTYPE.PRODUCT);
        product.setPropertyValue(Constant.XPATH.TITLE, "product");
        product = session.createDocument(product);
        session.save();

        // Wait until all async works finish, codename should be created as well
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        eventService.waitForAsyncCompletion();

        // Get codename and update the title
        DocumentModel codename = session.getDocument(
                new PathRef(Constant.PATH.CODENAME_NORMAL.concat("/").concat(product.getTitle())));
        codename.setPropertyValue(Constant.XPATH.MARKET_NAME, "codename");
        session.saveDocument(codename);
        session.save();
        
        product = session.getDocument(product.getRef());
        Assert.assertEquals("product", product.getPropertyValue(Constant.XPATH.TITLE));
    }
    
}
