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
public class ContentCreationFactoryTest {

    @Inject
    private CoreSession session;

    private final static String CONTENT_CREATION = "CONTENT CREATION";

    private final static String CONTENT = "Content - Writer,Edit,Strategy";

    private final static String CREATIVE_GTM = "Creative GTM";

    private final static String CREATIVE_Brand = "Creative Brand";

    private final static String CREATIVE_RE = "Creative RE";

    private final static String CREATIVE_PRODUCTION_DESIGN = "Creative Production Design";

    private final static String CUSTOMER_INSIGHTS = "Customer Insights";

    private final static String EMPTY_TITLE = "Empty Project";
    
    private final static String TEMPLATE_LIBRARY = "Template Library";

    /**
     * Test if structure templates work properly when a new Creative Content document is created
     */
    @Test
    public void testContentCreationCreated() {
        DocumentModel contentCreation = session.createDocumentModel(Constant.PATH.DOMAIN, CONTENT_CREATION,
                Constant.DOCTYPE.CONTENT_CREATION);
        contentCreation = session.createDocument(contentCreation);
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        DocumentModel content = session.getChild(contentCreation.getRef(), CONTENT);

        assertThatDocType(content.getType(), contentCreation.getPathAsString(), content.getName(),
                session).hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.DOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.EDIT)
                        .hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);

        DocumentModel templateLibrary = session.getChildren(content.getRef()).get(0);
        Assert.assertNotNull(templateLibrary);
        Assert.assertEquals(Constant.LIFECYCLE.FOLDER, templateLibrary.getLifeCyclePolicy());
        Assert.assertEquals(Constant.DOCTYPE.TEMPLATE_LIBRARY, templateLibrary.getType());
        Assert.assertEquals(TEMPLATE_LIBRARY, templateLibrary.getTitle());
        DocumentModel initialTemplate = session.getChildren(templateLibrary.getRef()).get(0);
        Assert.assertNotNull(initialTemplate);
        Assert.assertEquals(Constant.LIFECYCLE.GENERAL_OBJECT, initialTemplate.getLifeCyclePolicy());
        Assert.assertEquals(Constant.DOCTYPE.CREATIVE_PROJECT_TEMPLATE, initialTemplate.getType());
        Assert.assertEquals(EMPTY_TITLE, initialTemplate.getTitle());
        

        DocumentModel creativeGTM = session.getChild(contentCreation.getRef(), CREATIVE_GTM);
        assertThatDocType(creativeGTM.getType(), contentCreation.getPathAsString(), creativeGTM.getName(),
                session).hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.DOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.EDIT)
                        .hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);
        templateLibrary = session.getChildren(creativeGTM.getRef()).get(0);
        Assert.assertNotNull(templateLibrary);
        Assert.assertEquals(Constant.LIFECYCLE.FOLDER, templateLibrary.getLifeCyclePolicy());
        Assert.assertEquals(Constant.DOCTYPE.TEMPLATE_LIBRARY, templateLibrary.getType());
        Assert.assertEquals(TEMPLATE_LIBRARY, templateLibrary.getTitle());
        initialTemplate = session.getChildren(templateLibrary.getRef()).get(0);
        Assert.assertNotNull(initialTemplate);
        Assert.assertEquals(Constant.LIFECYCLE.GENERAL_OBJECT, initialTemplate.getLifeCyclePolicy());
        Assert.assertEquals(Constant.DOCTYPE.CREATIVE_PROJECT_TEMPLATE, initialTemplate.getType());
        Assert.assertEquals(EMPTY_TITLE, initialTemplate.getTitle());

        DocumentModel creativeBrand = session.getChild(contentCreation.getRef(), CREATIVE_Brand);
        assertThatDocType(creativeBrand.getType(), contentCreation.getPathAsString(), creativeBrand.getName(),
                session).hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.DOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.EDIT)
                        .hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);
        templateLibrary = session.getChildren(creativeBrand.getRef()).get(0);
        Assert.assertNotNull(templateLibrary);
        Assert.assertEquals(Constant.LIFECYCLE.FOLDER, templateLibrary.getLifeCyclePolicy());
        Assert.assertEquals(Constant.DOCTYPE.TEMPLATE_LIBRARY, templateLibrary.getType());
        Assert.assertEquals(TEMPLATE_LIBRARY, templateLibrary.getTitle());
        initialTemplate = session.getChildren(templateLibrary.getRef()).get(0);
        Assert.assertNotNull(initialTemplate);
        Assert.assertEquals(Constant.LIFECYCLE.GENERAL_OBJECT, initialTemplate.getLifeCyclePolicy());
        Assert.assertEquals(Constant.DOCTYPE.CREATIVE_PROJECT_TEMPLATE, initialTemplate.getType());
        Assert.assertEquals(EMPTY_TITLE, initialTemplate.getTitle());

        DocumentModel creativeRE = session.getChild(contentCreation.getRef(), CREATIVE_RE);
        assertThatDocType(creativeRE.getType(), contentCreation.getPathAsString(), creativeRE.getName(),
                session).hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.DOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.EDIT)
                        .hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);
        templateLibrary = session.getChildren(creativeRE.getRef()).get(0);
        Assert.assertNotNull(templateLibrary);
        Assert.assertEquals(Constant.LIFECYCLE.FOLDER, templateLibrary.getLifeCyclePolicy());
        Assert.assertEquals(Constant.DOCTYPE.TEMPLATE_LIBRARY, templateLibrary.getType());
        Assert.assertEquals(TEMPLATE_LIBRARY, templateLibrary.getTitle());
        initialTemplate = session.getChildren(templateLibrary.getRef()).get(0);
        Assert.assertNotNull(initialTemplate);
        Assert.assertEquals(Constant.LIFECYCLE.GENERAL_OBJECT, initialTemplate.getLifeCyclePolicy());
        Assert.assertEquals(Constant.DOCTYPE.CREATIVE_PROJECT_TEMPLATE, initialTemplate.getType());
        Assert.assertEquals(EMPTY_TITLE, initialTemplate.getTitle());

        DocumentModel creativeProdDesign = session.getChild(contentCreation.getRef(), CREATIVE_PRODUCTION_DESIGN);
        assertThatDocType(creativeProdDesign.getType(), contentCreation.getPathAsString(), creativeProdDesign.getName(),
                session).hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.DOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.EDIT)
                        .hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);
        templateLibrary = session.getChildren(creativeProdDesign.getRef()).get(0);
        Assert.assertNotNull(templateLibrary);
        Assert.assertEquals(Constant.LIFECYCLE.FOLDER, templateLibrary.getLifeCyclePolicy());
        Assert.assertEquals(Constant.DOCTYPE.TEMPLATE_LIBRARY, templateLibrary.getType());
        Assert.assertEquals(TEMPLATE_LIBRARY, templateLibrary.getTitle());
        initialTemplate = session.getChildren(templateLibrary.getRef()).get(0);
        Assert.assertNotNull(initialTemplate);
        Assert.assertEquals(Constant.LIFECYCLE.GENERAL_OBJECT, initialTemplate.getLifeCyclePolicy());
        Assert.assertEquals(Constant.DOCTYPE.CREATIVE_PROJECT_TEMPLATE, initialTemplate.getType());
        Assert.assertEquals(EMPTY_TITLE, initialTemplate.getTitle());

        DocumentModel customerInsights = session.getChild(contentCreation.getRef(), CUSTOMER_INSIGHTS);
        assertThatDocType(customerInsights.getType(), contentCreation.getPathAsString(), customerInsights.getName(),
                session).hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.DOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.EDIT)
                        .hasPermissionGranted(Constant.GROUP.CXPM, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.DOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasLifecycle(Constant.LIFECYCLE.FOLDER);
        templateLibrary = session.getChildren(customerInsights.getRef()).get(0);
        Assert.assertNotNull(templateLibrary);
        Assert.assertEquals(Constant.LIFECYCLE.FOLDER, templateLibrary.getLifeCyclePolicy());
        Assert.assertEquals(Constant.DOCTYPE.TEMPLATE_LIBRARY, templateLibrary.getType());
        Assert.assertEquals(TEMPLATE_LIBRARY, templateLibrary.getTitle());
        initialTemplate = session.getChildren(templateLibrary.getRef()).get(0);
        Assert.assertNotNull(initialTemplate);
        Assert.assertEquals(Constant.LIFECYCLE.GENERAL_OBJECT, initialTemplate.getLifeCyclePolicy());
        Assert.assertEquals(Constant.DOCTYPE.CREATIVE_PROJECT_TEMPLATE, initialTemplate.getType());
        Assert.assertEquals(EMPTY_TITLE, initialTemplate.getTitle());

    }

}
