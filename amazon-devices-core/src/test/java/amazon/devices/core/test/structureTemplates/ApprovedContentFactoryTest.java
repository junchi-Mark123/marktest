package amazon.devices.core.test.structureTemplates;

import amazon.devices.core.common.Constant;
import amazon.devices.core.test.AMZDFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;

import static amazon.devices.core.test.NuxeoAssert.assertThatDocType;

/**
 * @author ray.huang
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class ApprovedContentFactoryTest {

    @Inject
    private CoreSession session;

    private final static String APPROVED_CONTENT = "Approved Content";

    private final static String AMZ_DEVICE_HUB_STAGING = "Amazon Device Hub Staging";
    
    private final static String PRODUCTION_APPROVED = "Production Approved";
    
    

    /**
     * Test if structure templates work properly when a new Approved Content document is created
     */
    @Test
    public void testApprovedContentCreated() {
        DocumentModel approvedContent = session.createDocumentModel(Constant.PATH.DOMAIN, APPROVED_CONTENT,
                Constant.DOCTYPE.APPROVED_CONTENT);
        approvedContent = session.createDocument(approvedContent);
        session.save();

        DocumentModel amzDeviceHubStaging = session.getChild(approvedContent.getRef(), AMZ_DEVICE_HUB_STAGING);
        DocumentModel productionapproved =  session.getChild(approvedContent.getRef(), PRODUCTION_APPROVED);

        assertThatDocType(amzDeviceHubStaging.getType(), approvedContent.getPathAsString(), amzDeviceHubStaging.getName(),
                session).hasPermissionGranted(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.DOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT)
                .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.EDIT)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.CREATE_CONTENTS)
                .hasLifecycle(Constant.LIFECYCLE.FOLDER);
        assertThatDocType(productionapproved.getType(), approvedContent.getPathAsString(), productionapproved.getName(),
                session)
                .hasPermissionGranted(Constant.GROUP.ADMINISTRATORS, Constant.PERMISSION.EVERYTHING) 
                .hasPermissionGranted(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.CXLABS_CONSUMER, Constant.PERMISSION.DOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT)
                .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                .hasLifecycle(Constant.LIFECYCLE.FOLDER);

    }

}
