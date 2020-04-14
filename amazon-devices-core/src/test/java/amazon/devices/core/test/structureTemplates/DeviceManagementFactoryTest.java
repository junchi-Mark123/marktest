/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test.structureTemplates;

import static amazon.devices.core.test.NuxeoAssert.assertThatDocType;

import javax.inject.Inject;

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
public class DeviceManagementFactoryTest {

    private final static String DEVICE_MANAGEMENT = "Device Management";

    private final static String DEVICES = "Devices";

    private final static String CODENAMES = "Codenames";

    @Inject
    private CoreSession session;

    /**
     * Test if structure templates work properly when a new Device Management document is created
     */
    @Test
    public void testDeviceManagementCreated() {
        DocumentModel deviceManagement = session.createDocumentModel(Constant.PATH.DOMAIN, DEVICE_MANAGEMENT,
                Constant.DOCTYPE.DEVICE_MANAGEMENT);
        deviceManagement = session.createDocument(deviceManagement);
        session.save();

        DocumentModel devices = session.getChild(deviceManagement.getRef(), DEVICES);
        DocumentModel codenames = session.getChild(deviceManagement.getRef(), CODENAMES);

        assertThatDocType(devices.getType(), deviceManagement.getPathAsString(), devices.getName(),
                session).hasPermissionGranted(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);

        assertThatDocType(codenames.getType(), deviceManagement.getPathAsString(), codenames.getName(),
                session).hasPermissionGranted(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);

    }
}
