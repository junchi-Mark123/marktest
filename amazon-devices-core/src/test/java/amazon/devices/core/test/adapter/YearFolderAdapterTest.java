package amazon.devices.core.test.adapter;

import amazon.devices.core.adapter.YearFolderAdapter;
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
import javax.inject.Inject;

/**
 * @author ray.huang
 *
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class YearFolderAdapterTest {

    @Inject
    private CoreSession session;

    @Test
    public void testYearFolderSubTypesUnderDeviceHubStaging() {
        DocumentModel year = session.createDocumentModel(Constant.PATH.DEVICE_HUB_STAGING, "2020",
                Constant.DOCTYPE.YEAR);
        year = session.createDocument(year);
        session.save();

        YearFolderAdapter adapter = year.getAdapter(YearFolderAdapter.class);
        Assert.assertTrue(adapter.getAllowedSubtypes().contains("SubFolder"));
    }
}
