/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test.docTypes;

import static amazon.devices.core.test.NuxeoAssert.assertThatDocType;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
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
public class CompanyTest {

    @Inject
    private CoreSession session;

    private final static String TEST_COMPANY = "Nuxeo";

    /**
     * Test if company document can be created with correct info
     */
    @Test
    public void testCompanyCreated() {
        assertThatDocType(Constant.DOCTYPE.COMPANY, Constant.PATH.DOMAIN, TEST_COMPANY,
                session).canBeCreated().hasLifecycle(Constant.LIFECYCLE.FOLDER);
    }
}
