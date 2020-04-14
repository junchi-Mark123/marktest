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
public class DeviceTypeFactoryTest {

    @Inject
    private CoreSession session;

    private final static String DEVICES = "Devices";

    private final static String NORMAL = "Normal";

    private final static String ACCESS_CONTROLLED = "Access Controlled";

    /**
     * Test if structure templates work properly when a new DeviceType document is created
     */
    @Test
    public void testDeviceTypeCreated() {
        DocumentModel deviceType = session.createDocumentModel(Constant.PATH.DEVICE_MANAGEMENT, DEVICES,
                Constant.DOCTYPE.DEVICE_TYPE);
        deviceType = session.createDocument(deviceType);
        session.save();

        DocumentModel normal = session.getChild(deviceType.getRef(), NORMAL);
        DocumentModel accessControlled = session.getChild(deviceType.getRef(), ACCESS_CONTROLLED);

        assertThatDocType(normal.getType(), deviceType.getPathAsString(), normal.getName(),
                session).hasPermissionGranted(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasPermissionGranted(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);

        assertThatDocType(accessControlled.getType(), deviceType.getPathAsString(), accessControlled.getName(),
                session).hasPermissionGranted(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasPermissionGranted(Constant.GROUP.DEVICE_MANAGER, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);

    }
}
