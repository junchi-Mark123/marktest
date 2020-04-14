/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
/**
 * 
 */
package amazon.devices.core.test.listener;

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
public class CreativeAssetAboutToCreateListenerTest {

    @Inject
    private CoreSession session;

    @Test
    public void testCreativeAssetCreation() {
        DocumentModel creativeProjectTemplate = session.createDocumentModel(Constant.PATH.TEMPLATE_LIBRARY, "template",
                Constant.DOCTYPE.CREATIVE_PROJECT_TEMPLATE);
        creativeProjectTemplate.setPropertyValue(Constant.XPATH.TITLE, "template");
        creativeProjectTemplate.setPropertyValue(Constant.XPATH.PROJECT_PRIORITY, "P1");
        creativeProjectTemplate.setPropertyValue(Constant.XPATH.DESCRIPTION, "description");
        creativeProjectTemplate = session.createDocument(creativeProjectTemplate);
        session.save();

        DocumentModel creativeAsset = session.createDocumentModel(creativeProjectTemplate.getPathAsString(), "asset",
                Constant.DOCTYPE.CREATIVE_ASSET);
        creativeAsset.setPropertyValue(Constant.XPATH.TITLE, "asset");
        creativeAsset = session.createDocument(creativeAsset);
        session.save();

        Assert.assertEquals("asset", creativeAsset.getPropertyValue(Constant.XPATH.TITLE));
        Assert.assertEquals("description", creativeAsset.getPropertyValue(Constant.XPATH.DESCRIPTION));
        Assert.assertEquals(creativeProjectTemplate.getPropertyValue(Constant.XPATH.RELATIONS_WORKSPACE_LOCATION),
                creativeAsset.getPropertyValue(Constant.XPATH.RELATIONS_WORKSPACE_LOCATION));
        
        DocumentModel folder = session.createDocumentModel(creativeProjectTemplate.getPathAsString(), "folder",
                Constant.DOCTYPE.SUBFOLDER);
        folder = session.createDocument(folder);
        session.save();
        
        DocumentModel creativeAsset1 = session.createDocumentModel(folder.getPathAsString(), "asset1",
                Constant.DOCTYPE.CREATIVE_ASSET);
        creativeAsset1.setPropertyValue(Constant.XPATH.TITLE, "asset1");
        creativeAsset1 = session.createDocument(creativeAsset1);
        session.save();
        
        Assert.assertEquals("asset1", creativeAsset1.getPropertyValue(Constant.XPATH.TITLE));
        Assert.assertEquals("description", creativeAsset1.getPropertyValue(Constant.XPATH.DESCRIPTION));
        Assert.assertEquals(creativeProjectTemplate.getPropertyValue(Constant.XPATH.RELATIONS_WORKSPACE_LOCATION),
                creativeAsset1.getPropertyValue(Constant.XPATH.RELATIONS_WORKSPACE_LOCATION));
    }
}
