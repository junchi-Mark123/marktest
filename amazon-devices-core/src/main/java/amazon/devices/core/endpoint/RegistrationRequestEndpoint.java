/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;

/**
 * @author frank.zheng
 *
 */
@Path("/selfregistration")
@WebObject(type = "selfregistration")
public class RegistrationRequestEndpoint extends ModuleRoot {

    protected static final Log log = LogFactory.getLog(RegistrationRequestEndpoint.class);
    
    public static final String PATH = "selfregistration";
    
    private static final String USER_ACCESS_QUERY = "SELECT * FROM Document WHERE ecm:primaryType "
            + "= 'UserAccess' AND ecm:isVersion = 0 AND userAccess:email = '%s'";

    @Path("/")
    @POST
    public Object doPost(@Context HttpServletRequest request) throws IOException {
        InputStream in = request.getInputStream();
        String json = IOUtils.toString(in, Charset.defaultCharset());
        AutomationService as = Framework.getService(AutomationService.class);
        CoreSession session = CoreInstance.openCoreSessionSystem(null);
        
        //Check if email exists in system already
        String email = (String) new ObjectMapper().readValue(json, Map.class).get("email");
        DocumentModelList userAccessDocuments = session.query(String.format(USER_ACCESS_QUERY, email));
        if(userAccessDocuments != null && userAccessDocuments.size() > 0) {
            if (session != null) {
                ((CloseableCoreSession) session).close();
                session = null;
            }
            return Response.status(Response.Status.CONFLICT).build();
        }
        try {
            OperationContext ctx = new OperationContext();
            ctx.setCoreSession(session);
            OperationChain chain = new OperationChain("processUserRegistrationRequest");
            chain.add("AMZD.CreateUserAccessOperation").
                    set("request",json);
            as.run(ctx, chain);
            return Response.status(Response.Status.OK).build();
        } catch (OperationException e) {
            throw new NuxeoException(e);
        } finally {
            if (session != null) {
                ((CloseableCoreSession) session).close();
                session = null;
            }
        }
    }

}
