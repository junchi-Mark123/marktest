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
public class ExternalUserManagementFactoryTest {

    @Inject
    private CoreSession session;

    private final static String EXT_USERS = "External User Management";

    private final static String RETAIL_USERS = "Retail Users";

    private final static String VENDOR_USERS = "Vendor Users";

    private final static String AGENCY_USERS = "Agency Users";

    /**
     * Test if structure templates work properly when a new ExtUsers document is created
     */
    @Test
    public void testExtUsersCreated() {
        DocumentModel extUsers = session.createDocumentModel(Constant.PATH.DOMAIN, EXT_USERS,
                Constant.DOCTYPE.EXT_USERS);
        extUsers = session.createDocument(extUsers);
        session.save();

        DocumentModel retailUsers = session.getChild(extUsers.getRef(), RETAIL_USERS);
        DocumentModel vendorUsers = session.getChild(extUsers.getRef(), VENDOR_USERS);
        DocumentModel agencyUsers = session.getChild(extUsers.getRef(), AGENCY_USERS);

        assertThatDocType(retailUsers.getType(), extUsers.getPathAsString(), retailUsers.getName(),
                session).hasPermissionGranted(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);

        assertThatDocType(vendorUsers.getType(), extUsers.getPathAsString(), vendorUsers.getName(),
                session).hasPermissionGranted(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);

        assertThatDocType(agencyUsers.getType(), extUsers.getPathAsString(), agencyUsers.getName(),
                session).hasPermissionGranted(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);
    }
}
