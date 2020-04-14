/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.operation;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 */
@Operation(id = CreateUserAccessOperation.ID, category = Constants.CAT_DOCUMENT, label = "AMZD.CreateUserAccessOperation", description = "Create User Access Document")
public class CreateUserAccessOperation {

    public static final String ID = "AMZD.CreateUserAccessOperation";
    
    protected static final Log log = LogFactory.getLog(CreateUserAccessOperation.class);

    @Param(name = "request", required = true)
    protected String request;

    @Context
    protected CoreSession session;

    @OperationMethod
    public DocumentModel run() throws Exception {

        @SuppressWarnings("unchecked")
        Map<String, Serializable> userAccessMap = new ObjectMapper().readValue(request, Map.class);

        String accessType = (String) userAccessMap.get("accesstype");

        String company = (String) userAccessMap.get("company");

        String firstName = (String) userAccessMap.get("firstname");

        String lastName = (String) userAccessMap.get("lastname");

        String email = (String) userAccessMap.get("email");

        String password = (String) userAccessMap.get("password");
        
        // check company document exists or not
        DocumentModel companyDoc = session.createDocumentModel(
                Constant.PATH.EXTERNAL_USER_MANAGEMENT.concat(accessType).concat(" Users"), company,
                Constant.DOCTYPE.COMPANY);
        session.getOrCreateDocument(companyDoc);
        session.save();
        
     // Calculate location
        String location = Constant.PATH.EXTERNAL_USER_MANAGEMENT.concat(accessType).concat(" Users/").concat(company);

        // Calculate title
        String title = String.format("%s %s - %s", firstName, lastName, email);

        DocumentModel userAccess = session.createDocumentModel(location, title, Constant.DOCTYPE.USER_ACCESS);
        userAccess.setPropertyValue("title", title);
        userAccess.setPropertyValue("fName", firstName);
        userAccess.setPropertyValue("lName", lastName);
        userAccess.setPropertyValue("email", email.toLowerCase());
        userAccess.setPropertyValue("company", company);
        userAccess.setPropertyValue("password", password);
        userAccess.setPropertyValue("typeOfAccess", accessType.toLowerCase());

        userAccess = session.createDocument(userAccess);
        session.save();

        return userAccess;

    }
}
