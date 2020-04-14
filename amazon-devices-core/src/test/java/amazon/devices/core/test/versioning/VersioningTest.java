/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test.versioning;

import java.io.IOException;
import java.io.Serializable;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PropertyException;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import amazon.devices.core.common.Constant;
import amazon.devices.core.test.AMZDFeature;
import amazon.devices.core.test.UnitTestHelper;

@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class VersioningTest {

    @Inject
    private CoreSession session;

    @Test
    public void testInitialVersionWithVersionableDoc() {
        DocumentModel asset = session.createDocumentModel(Constant.PATH.DOMAIN, "asset", "Asset");
        asset = session.createDocument(asset);
        session.save();

        Assert.assertTrue(asset.hasFacet("Versionable"));
        Assert.assertEquals("0.1",asset.getVersionLabel());
        Assert.assertEquals(new Long(0), asset.getPropertyValue("uid:major_version"));
        Assert.assertEquals(new Long(1), asset.getPropertyValue("uid:minor_version"));
    }

    @Test
    public void testInitialVersionWithNotVersionableDoc() {
        DocumentModel teamWorkspace = session.createDocumentModel(Constant.PATH.DOMAIN, "team", "TeamWorkspace");
        teamWorkspace = session.createDocument(teamWorkspace);
        session.save();

        Assert.assertFalse(teamWorkspace.hasFacet("Versionable"));
        Assert.assertEquals(new Long(0), teamWorkspace.getPropertyValue("uid:major_version"));
        Assert.assertEquals(new Long(0), teamWorkspace.getPropertyValue("uid:minor_version"));
    }

    /*
     * This test is to check when udpating the file content from null
     */
    @Test
    public void fileContentChangeUpdateVersion() throws PropertyException, IOException {
        DocumentModel asset = session.createDocumentModel(Constant.PATH.DOMAIN, "asset", "Asset");
        asset = session.createDocument(asset);
        session.save();

        asset = session.getDocument(asset.getRef());

        Assert.assertTrue(asset.hasFacet("Versionable"));
        Assert.assertEquals(new Long(0), asset.getPropertyValue("uid:major_version"));
        Assert.assertEquals(new Long(1), asset.getPropertyValue("uid:minor_version"));

        asset.setPropertyValue(Constant.XPATH.FILE_CONTENT,
                (Serializable) UnitTestHelper.getFileFromFilename("test1.txt", "text/plain"));
        asset = session.saveDocument(asset);
        session.save();

        asset = session.getDocument(asset.getRef());

        Assert.assertEquals("0.2", asset.getVersionLabel());
        Assert.assertEquals(new Long(0), asset.getPropertyValue("uid:major_version"));
        Assert.assertEquals(new Long(2), asset.getPropertyValue("uid:minor_version"));
        
        asset.setPropertyValue(Constant.XPATH.TITLE, "test1");
        asset = session.saveDocument(asset);
        session.save();
        
        asset = session.getDocument(asset.getRef());

        Assert.assertEquals("0.2+", asset.getVersionLabel());
        Assert.assertEquals(new Long(0), asset.getPropertyValue("uid:major_version"));
        Assert.assertEquals(new Long(2), asset.getPropertyValue("uid:minor_version"));
    }

    /*
     * This test is to check when udpating the file content with another file
     */
    @Test
    public void fileContentChangeUpdateVersionWithAnotherFile() throws PropertyException, IOException {
        DocumentModel asset = session.createDocumentModel(Constant.PATH.DOMAIN, "asset", "Asset");
        asset.setPropertyValue(Constant.XPATH.FILE_CONTENT,
                (Serializable) UnitTestHelper.getFileFromFilename("test1.txt", "text/plain"));
        asset = session.createDocument(asset);
        session.save();

        Assert.assertTrue(asset.hasFacet("Versionable"));
        Assert.assertEquals("0.1", asset.getVersionLabel());
        Assert.assertEquals(new Long(0), asset.getPropertyValue("uid:major_version"));
        Assert.assertEquals(new Long(1), asset.getPropertyValue("uid:minor_version"));
        
        asset.setPropertyValue(Constant.XPATH.TITLE, "test1");
        asset = session.saveDocument(asset);
        session.save();
        
        asset = session.getDocument(asset.getRef());

        Assert.assertEquals("0.1+", asset.getVersionLabel());
        Assert.assertEquals(new Long(0), asset.getPropertyValue("uid:major_version"));
        Assert.assertEquals(new Long(1), asset.getPropertyValue("uid:minor_version"));

        asset.setPropertyValue(Constant.XPATH.FILE_CONTENT,
                (Serializable) UnitTestHelper.getFileFromFilename("test2.txt", "text/plain"));
        asset = session.saveDocument(asset);
        session.save();
        Assert.assertEquals("0.2", asset.getVersionLabel());
        Assert.assertEquals(new Long(0), asset.getPropertyValue("uid:major_version"));
        Assert.assertEquals(new Long(2), asset.getPropertyValue("uid:minor_version"));
    }

    /*
     * This test is to check when udpating the file content with same file
     */
    @Test
    public void fileContentChangeUpdateVersionWithSameFile() throws PropertyException, IOException {
        DocumentModel asset = session.createDocumentModel(Constant.PATH.DOMAIN, "asset", "Asset");
        asset.setPropertyValue(Constant.XPATH.FILE_CONTENT,
                (Serializable) UnitTestHelper.getFileFromFilename("test1.txt", "text/plain"));
        asset = session.createDocument(asset);
        
        Assert.assertTrue(asset.hasFacet("Versionable"));
        Assert.assertEquals("0.1", asset.getVersionLabel());
        Assert.assertEquals(new Long(0), asset.getPropertyValue("uid:major_version"));
        Assert.assertEquals(new Long(1), asset.getPropertyValue("uid:minor_version"));

        asset.setPropertyValue(Constant.XPATH.FILE_CONTENT,
                (Serializable) UnitTestHelper.getFileFromFilename("test1.txt", "text/plain"));
        asset = session.saveDocument(asset);

        asset = session.getDocument(asset.getRef());

        Assert.assertEquals(new Long(0), asset.getPropertyValue("uid:major_version"));
        Assert.assertEquals(new Long(1), asset.getPropertyValue("uid:minor_version"));
    }

}
