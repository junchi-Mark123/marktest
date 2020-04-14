/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
/**
 * 
 */
package amazon.devices.core.test.operation;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import amazon.devices.core.common.Constant;
import amazon.devices.core.operation.CreateProjectOperation;
import amazon.devices.core.test.AMZDFeature;

/**
 * @author frank.zheng
 *
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class CreateProjectOperationTest {
    
    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;
    
    @Test
    public void testCreateProject() throws OperationException {
        // create project template
        DocumentModel creativeProjectTemplate = session.createDocumentModel(Constant.PATH.TEMPLATE_LIBRARY, "template",
                Constant.DOCTYPE.CREATIVE_PROJECT_TEMPLATE);
        creativeProjectTemplate.setPropertyValue(Constant.XPATH.TITLE, "template");
        creativeProjectTemplate.setPropertyValue(Constant.XPATH.PROJECT_PRIORITY, "P1");
        creativeProjectTemplate.setPropertyValue(Constant.XPATH.DESCRIPTION, "description");
        creativeProjectTemplate = session.createDocument(creativeProjectTemplate);
        session.save();
        
        // create asset
        DocumentModel creativeAsset = session.createDocumentModel(creativeProjectTemplate.getPathAsString(), "asset",
                Constant.DOCTYPE.CREATIVE_ASSET);
        creativeAsset.setPropertyValue(Constant.XPATH.TITLE, "asset");
        creativeAsset = session.createDocument(creativeAsset);
        session.save();
        
        // create subfolder
        DocumentModel subfolder = session.createDocumentModel(creativeProjectTemplate.getPathAsString(), "subfolder",
                Constant.DOCTYPE.SUBFOLDER);
        subfolder.setPropertyValue(Constant.XPATH.TITLE, "subfolder");
        subfolder = session.createDocument(subfolder);
        session.save();
        
        // create asset under subfolder
        DocumentModel creativeAsset1 = session.createDocumentModel(subfolder.getPathAsString(), "asset1",
                Constant.DOCTYPE.CREATIVE_ASSET);
        creativeAsset1.setPropertyValue(Constant.XPATH.TITLE, "asset1");
        creativeAsset1 = session.createDocument(creativeAsset1);
        session.save();
        
        DocumentModel year = session.createDocumentModel(Constant.PATH.CREATIVE_BRAND, "2020",
                Constant.DOCTYPE.YEAR);
        year = session.createDocument(year);
        session.save();
        
        OperationContext ctx = new OperationContext(session);
        Map<String, String> params = new HashMap<>();
        params.put("template", creativeProjectTemplate.getId());
        params.put("title", "test title");
        ctx.setInput(year);
        DocumentModel res = (DocumentModel) automationService.run(ctx, CreateProjectOperation.ID, params);
        
        Assert.assertEquals("test title", res.getTitle());
        
        Assert.assertNotNull(session.getChild(res.getRef(), "asset"));
        
        DocumentModel folder = session.getChild(res.getRef(), "subfolder");
        Assert.assertNotNull(folder);
        
        Assert.assertNotNull(session.getChild(folder.getRef(), "asset1"));
    }

}
