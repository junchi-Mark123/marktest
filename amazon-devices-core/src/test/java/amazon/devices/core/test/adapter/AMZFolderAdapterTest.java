/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test.adapter;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import amazon.devices.core.adapter.AMZFolderAdapter;
import amazon.devices.core.common.Constant;
import amazon.devices.core.test.AMZDFeature;

/**
 * @author frank.zheng
 *
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class AMZFolderAdapterTest {
    
    @Inject
    private CoreSession session;
    
    private final static String NORMAL = "Normal";

    private final static String ACCESS_CONTROLLED = "Access Controlled";
    
    @Test
    public void testAmazonFolderUnderDevices() {
        DocumentModel deviceNormal = session.getDocument(new PathRef(Constant.PATH.DEVICES.concat("/").concat(NORMAL)));
        AMZFolderAdapter adapter = deviceNormal.getAdapter(AMZFolderAdapter.class);
        Assert.assertEquals(1, adapter.getAllowedSubtypes().size());
        
        DocumentModel deviceAccessControlled = session.getDocument(new PathRef(Constant.PATH.DEVICES.concat("/").concat(ACCESS_CONTROLLED)));
        adapter = deviceAccessControlled.getAdapter(AMZFolderAdapter.class);
        Assert.assertEquals(1, adapter.getAllowedSubtypes().size());
    }
    
    @Test
    public void testAmazonFolderUnderCodenames() {
        DocumentModel deviceNormal = session.getDocument(new PathRef(Constant.PATH.CODENAMES.concat("/").concat(NORMAL)));
        AMZFolderAdapter adapter = deviceNormal.getAdapter(AMZFolderAdapter.class);
        Assert.assertEquals(0, adapter.getAllowedSubtypes().size());
        
        DocumentModel deviceAccessControlled = session.getDocument(new PathRef(Constant.PATH.CODENAMES.concat("/").concat(ACCESS_CONTROLLED)));
        adapter = deviceAccessControlled.getAdapter(AMZFolderAdapter.class);
        Assert.assertEquals(0, adapter.getAllowedSubtypes().size());
    }

}
