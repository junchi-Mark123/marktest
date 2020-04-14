/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test.endpoint;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
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
import org.nuxeo.runtime.test.runner.ServletContainerFeature;
import org.nuxeo.runtime.transaction.TransactionHelper;

import amazon.devices.core.common.Constant;
import amazon.devices.core.endpoint.RegistrationRequestEndpoint;
import amazon.devices.core.test.AMZDFeature;

/**
 * @author frank.zheng
 */
@RunWith(FeaturesRunner.class)
@Features(AMZDFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
public class RegistrationRequestEndpointTest {

    public static final String BASE_URL = "http://localhost";

    private static final String RELATIVE_RETAIL_USER_PATH = "Retail Users";

    @Inject
    protected ServletContainerFeature servletContainerFeature;

    @Inject
    private CoreSession session;

    @Test
    public void testReceiveValideRegistrationCall() throws IOException {
        int port = servletContainerFeature.getPort();
        String url = BASE_URL + ":" + port + "/" + RegistrationRequestEndpoint.PATH;
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        post.setEntity(new InputStreamEntity(getClass().getResourceAsStream("/user.json")));
        HttpResponse response = client.execute(post);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testReceiveExistedEmailRegistrationCall() throws IOException {
        this.createUserAccessDocument();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();
         
        int port = servletContainerFeature.getPort();
        String url = BASE_URL + ":" + port + "/" + RegistrationRequestEndpoint.PATH;
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        post.setEntity(new InputStreamEntity(getClass().getResourceAsStream("/user.json")));
        HttpResponse response = client.execute(post);
        
        Assert.assertEquals(409, response.getStatusLine().getStatusCode());
        
    }

    private void createUserAccessDocument() {
        DocumentModel company = session.createDocumentModel(
                Constant.PATH.EXTERNAL_USER_MANAGEMENT.concat(RELATIVE_RETAIL_USER_PATH), "test",
                Constant.DOCTYPE.COMPANY);
        company = session.createDocument(company);

        DocumentModel userAccess = session.createDocumentModel(company.getPathAsString(), "test",
                Constant.DOCTYPE.USER_ACCESS);
        userAccess.setPropertyValue(Constant.XPATH.TITLE, "test test - test@gmail.com");
        userAccess.setPropertyValue("fName", "test");
        userAccess.setPropertyValue("lName", "test");
        userAccess.setPropertyValue("email", "test@gmail.com");
        userAccess.setPropertyValue("company", "test");
        userAccess.setPropertyValue("password", "123456$e");
        userAccess.setPropertyValue("typeOfAccess", "retail");
        userAccess = session.createDocument(userAccess);
        session.save();
    }

}
