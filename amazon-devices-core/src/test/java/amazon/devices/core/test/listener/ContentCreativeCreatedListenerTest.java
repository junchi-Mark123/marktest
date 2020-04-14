package amazon.devices.core.test.listener;


import amazon.devices.core.common.Constant;
import amazon.devices.core.test.AMZDFeature;
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

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author ray.huang
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class ContentCreativeCreatedListenerTest {

    @Inject
    private CoreSession session;

    @Inject
    protected EventService eventService;

    private final static String CONTENT_CREATION = "CONTENT CREATION";

    private final static String CONTENT = "RetailExperiencePkg";
    //private final static String CONTENT = "Content - IP";
    //private final static String CONTENT = "Production";
    //private final static String CONTENT = "Brand Strategy";
    
    

    @Test
    public void testProductionCreativeContentsCreated() {
        DocumentModel contentCreation = session.createDocumentModel(Constant.PATH.DOMAIN, CONTENT_CREATION,
                Constant.DOCTYPE.CONTENT_CREATION);
        contentCreation = session.createDocument(contentCreation);
        session.save();

        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        eventService.waitForAsyncCompletion();

        DocumentModel Production = session.getChild(contentCreation.getRef(), CONTENT);
        
        String contentGroupName = CONTENT.replace(" ", "_");
        assertThat(Production.getACP().getAccess(contentGroupName, Constant.PERMISSION.VIEW_NODOWNLOAD), is(Access.GRANT));
        assertThat(Production.getACP().getAccess(contentGroupName, Constant.PERMISSION.DOWNLOAD), is(Access.GRANT));
        assertThat(Production.getACP().getAccess(contentGroupName, Constant.PERMISSION.EDIT), is(Access.GRANT));
        assertThat(Production.getACP().getAccess(contentGroupName, Constant.PERMISSION.CREATE_CONTENTS), is(Access.GRANT));
    }

}
