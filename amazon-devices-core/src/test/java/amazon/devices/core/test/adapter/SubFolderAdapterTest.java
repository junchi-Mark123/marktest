/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
/**
 * 
 */
package amazon.devices.core.test.adapter;

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

import amazon.devices.core.adapter.AMZDAdapter;
import amazon.devices.core.common.Constant;
import amazon.devices.core.test.AMZDFeature;

/**
 * @author frank.zheng
 *
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class SubFolderAdapterTest {
    
    @Inject
    private CoreSession session;
    
    @Test
    public void testSubFolderSubtype() {
        DocumentModel projectTemplate = session.createDocumentModel(Constant.PATH.TEMPLATE_LIBRARY, "Project Template", Constant.DOCTYPE.CREATIVE_PROJECT_TEMPLATE);
        projectTemplate = session.createDocument(projectTemplate);
        session.save();
        
        DocumentModel subFolder = session.createDocumentModel(projectTemplate.getPathAsString(), "SubFolder", Constant.DOCTYPE.SUBFOLDER);
        subFolder = session.createDocument(subFolder);
        session.save();
        
        AMZDAdapter adapter = subFolder.getAdapter(AMZDAdapter.class);
        Assert.assertTrue(adapter.getAllowedSubtypes().size() == 1);
        Assert.assertTrue(adapter.getAllowedSubtypes().contains(Constant.DOCTYPE.CREATIVE_ASSET));
        
        // Create year
        DocumentModel year = session.createDocumentModel(Constant.PATH.CREATIVE_BRAND, "2020",
                Constant.DOCTYPE.YEAR);
        year = session.createDocument(year);
        session.save();
        
        // Create project
        DocumentModel project = session.createDocumentModel(year.getPathAsString(), "Project", Constant.DOCTYPE.CREATIVE_PROJECT);
        project = session.createDocument(project);
        session.save();
        
        // Create subfolder under project
        DocumentModel subFolder1 = session.createDocumentModel(project.getPathAsString(), "SubFolder", Constant.DOCTYPE.SUBFOLDER);
        subFolder1 = session.createDocument(subFolder1);
        session.save();
        
        AMZDAdapter adapter1 = subFolder1.getAdapter(AMZDAdapter.class);
        Assert.assertTrue(adapter1.getAllowedSubtypes().size() == 1);
        Assert.assertTrue(adapter1.getAllowedSubtypes().contains(Constant.DOCTYPE.CREATIVE_ASSET));
        
    }

}
