/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test.operation;

import static amazon.devices.core.test.NuxeoAssert.assertThatDocType;

import javax.inject.Inject;

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
 * @author frank.zheng This is the test for "javascript.eh_company_created"
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class AddPermissionForCompanyOperationTest {

    @Inject
    private CoreSession session;

    @Test
    public void testCreatingCompanyUnderRetailUsers() {
        DocumentModel company = session.createDocumentModel(Constant.PATH.RETAIL_USERS, "company",
                Constant.DOCTYPE.COMPANY);
        company = session.createDocument(company);
        session.save();

        assertThatDocType(Constant.DOCTYPE.COMPANY, Constant.PATH.RETAIL_USERS, "company",
                session).hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT)
                        .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasPermissionGranted(Constant.GROUP.ADH_MANAGER, Constant.PERMISSION.EDIT);
    }

    @Test
    public void testCreatingCompanyUnderVendorUsers() {
        DocumentModel company = session.createDocumentModel(Constant.PATH.VENDOR_USERS, "company",
                Constant.DOCTYPE.COMPANY);
        company = session.createDocument(company);
        session.save();

        assertThatDocType(Constant.DOCTYPE.COMPANY, Constant.PATH.VENDOR_USERS, "company",
                session).hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT);
    }

    @Test
    public void testCreatingCompanyUnderAgencyUsers() {
        DocumentModel company = session.createDocumentModel(Constant.PATH.AGENCY_USERS, "company",
                Constant.DOCTYPE.COMPANY);
        company = session.createDocument(company);
        session.save();

        assertThatDocType(Constant.DOCTYPE.COMPANY, Constant.PATH.AGENCY_USERS, "company",
                session).hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.VIEW_NODOWNLOAD)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.CREATE_CONTENTS)
                        .hasPermissionGranted(Constant.GROUP.LIBRARIAN, Constant.PERMISSION.EDIT);
    }
}
