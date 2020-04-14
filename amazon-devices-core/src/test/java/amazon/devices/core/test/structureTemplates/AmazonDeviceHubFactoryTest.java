package amazon.devices.core.test.structureTemplates;

import amazon.devices.core.common.Constant;
import amazon.devices.core.test.AMZDFeature;
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
import org.nuxeo.runtime.transaction.TransactionHelper;

import javax.inject.Inject;

import static amazon.devices.core.test.NuxeoAssert.assertThatDocType;

/**
 * @author ray.huang
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class AmazonDeviceHubFactoryTest {

    @Inject
    private CoreSession session;

    private final static String AMAZON_DEVICE_HUB = "Amazon Device Hub";

    private final static String AMAZON_DEVICE_HUB_INTERNAL = "Amazon Device Hub Internal";

    private final static String AMAZON_DEVICE_HUB_EXTERNAL = "Amazon Device Hub External";


    /**
     * Test if structure templates work properly when a new AMZDeviceHub document is created
     */
    @Test
    public void testAmazonDeviceHubCreated() {
        DocumentModel amzDeviceHub = session.createDocumentModel(Constant.PATH.DOMAIN, AMAZON_DEVICE_HUB,
                Constant.DOCTYPE.AMAZON_DEVICE_HUB);
        amzDeviceHub = session.createDocument(amzDeviceHub);
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        DocumentModel amzDeviceHubInternal = session.getChild(amzDeviceHub.getRef(), AMAZON_DEVICE_HUB_INTERNAL);

        assertThatDocType(amzDeviceHubInternal.getType(), amzDeviceHub.getPathAsString(), amzDeviceHubInternal.getName(),
                session).hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.DOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.EDIT)
                .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.CREATE_CONTENTS)
                .hasPermissionGranted(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.DOWNLOAD)

                .hasLifecycle(Constant.LIFECYCLE.FOLDER);

        DocumentModel amzDeviceHubExternal = session.getChild(amzDeviceHub.getRef(), AMAZON_DEVICE_HUB_EXTERNAL);
        assertThatDocType(amzDeviceHubExternal.getType(), amzDeviceHub.getPathAsString(), amzDeviceHubExternal.getName(),
                session).hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.DOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.EDIT)
                .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.CREATE_CONTENTS)
                .hasPermissionGranted(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.DOWNLOAD)
                .hasLifecycle(Constant.LIFECYCLE.FOLDER);
    }

}
