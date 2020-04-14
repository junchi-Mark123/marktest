package amazon.devices.core.test.operation;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.blob.BlobInfo;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.PartialDeploy;
import org.nuxeo.runtime.test.runner.TargetExtensions;
import org.nuxeo.runtime.transaction.TransactionHelper;

import amazon.devices.core.common.Constant;
import amazon.devices.core.operation.SubmitNewCodenameOperation;
import amazon.devices.core.test.AMZDFeature;

@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class SubmitNewCodenameOperationTest {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Inject
    protected EventService eventService;

    @Test
    public void testwithemptycodename() throws OperationException {

        // create a device
        DocumentModel device = session.createDocumentModel(Constant.PATH.DEVICES_ACCESS_CONTROLLED, "testDevice",
                Constant.DOCTYPE.DEVICE);
        device = session.createDocument(device);
        session.save();
        // wait for the device creation
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
        eventService.waitForAsyncCompletion();
        
        OperationContext ctx = new OperationContext(session);
        Map<String, Object> params = new HashMap<>();
        params.put("device_name", device.getId());
        params.put("name_type", "group");
        params.put("name_value", "a");
        params.put("name_codename", "ae");
        ctx.setInput(device);
        // run the operation to get the document
        DocumentModel result = (DocumentModel) automationService.run(ctx, SubmitNewCodenameOperation.ID, params);
        // check if the new properties have been add to the "specifi_name"property
        Assert.assertEquals("group", result.getProperty("specific_names").get(0).get("name_type").getValue());

    }

}
