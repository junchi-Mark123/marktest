/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test.structureTemplates;

import static amazon.devices.core.test.NuxeoAssert.assertThatDocType;

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
public class DomainFactoryTest {

    @Inject
    private CoreSession session;

    private final static String TEST_DOMAIN = "test-domain";

    private final static String EXT_USERS = "External User Management";
    
    private final static String DEVICE_MANAGEMENT = "Device Management";

    private final static String APPROVED_CONTENT = "Approved Content";

    private final static String AMAZON_DEVICE_HUB = "Amazon Device Hub";

    /**
     * Test if structure templates work properly when a new Domain is created
     */
    @Test
    public void testDomainCreated() {
        DocumentModel domain = session.createDocumentModel(Constant.PATH.ROOT, TEST_DOMAIN, Constant.DOCTYPE.DOMAIN);
        domain = session.createDocument(domain);
        session.save();

        Assert.assertEquals(true, session.hasChild(domain.getRef(), EXT_USERS));

        DocumentModel extUsers = session.getChild(domain.getRef(), EXT_USERS);
        
        DocumentModel deviceManagement = session.getChild(domain.getRef(), DEVICE_MANAGEMENT);

        DocumentModel approvedContent = session.getChild(domain.getRef(), APPROVED_CONTENT);

        DocumentModel amzDeviceHub = session.getChild(domain.getRef(), AMAZON_DEVICE_HUB);

        assertThatDocType(domain.getType(), Constant.PATH.ROOT, domain.getName(),
                session).hasPermissionGranted(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD);

        assertThatDocType(extUsers.getType(), domain.getPathAsString(), extUsers.getName(),
                session).hasPermissionGranted(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);
        
        assertThatDocType(deviceManagement.getType(), domain.getPathAsString(), deviceManagement.getName(),
                session).hasPermissionGranted(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);

        assertThatDocType(approvedContent.getType(), domain.getPathAsString(), approvedContent.getName(),
                session).hasPermissionGranted(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasLifecycle(Constant.LIFECYCLE.FOLDER);

        assertThatDocType(amzDeviceHub.getType(), domain.getPathAsString(), amzDeviceHub.getName(),
                session).hasPermissionGranted(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.RETAIL, Constant.PERMISSION.VIEW_NODOWNLOAD);
    }
}
