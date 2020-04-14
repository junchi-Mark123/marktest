/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
/**
 * 
 */
package amazon.devices.core.enricher;

import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher;
import org.nuxeo.ecm.core.io.registry.reflect.Priorities;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;

import com.fasterxml.jackson.core.JsonGenerator;

import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 */
@Setup(mode = SINGLETON, priority = Priorities.REFERENCE)
public class AMZDCodenameJsonEnricher extends AbstractJsonEnricher<DocumentModel> {

    private final static Log logger = LogFactory.getLog(AMZDCodenameJsonEnricher.class);

    public static final String NAME = "codename";

    private static final String USER = "user";

    private static final String GROUP = "group";

    private static final String RETAIL = "retail";

    private static final String VENDOR = "vendor";

    private static final String AGENCY = "agency";

    private static final String NAME_TYPE = "name_type";

    private static final String NAME_VALUE = "name_value";

    private static final String NAME_CODENAME = "name_codename";

    private static final String CODENAME_QUERY = "SELECT * FROM Document WHERE ecm:primaryType "
            + "= 'Codename' AND ecm:isVersion = 0 AND codename:device = '%s' AND dc:title = '%s'";

    private static final String USERACCESS_QUERY = "SELECT * FROM Document WHERE ecm:primaryType "
            + "= 'UserAccess' AND ecm:isVersion = 0 AND userAccess:email = '%s' "
            + "AND userAccess:typeOfAccess = '%s' AND userAccess:company = '%s'";

    /**
     * @param name
     */
    public AMZDCodenameJsonEnricher() {
        super(NAME);
    }

    /*
     * (non-Javadoc)
     * @see org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher#write(com.fasterxml.jackson.core.
     * JsonGenerator, java.lang.Object)
     */
    @Override
    public void write(JsonGenerator jg, DocumentModel enriched) throws IOException {
        // codename enricher should only work for product doc
        if (!Constant.DOCTYPE.PRODUCT.equals(enriched.getType())) {
            jg.writeStringField(NAME, null);
            return;
        }

        // Get current session
        CoreSession session = enriched.getCoreSession();

        // Get current user
        NuxeoPrincipal user = session.getPrincipal();

        // Write codename to enricher
        jg.writeStringField(NAME, getCodename(enriched, session, user));

    }

    /**
     * @param doc
     * @param session
     * @param user
     * @return
     */
    @SuppressWarnings("unchecked")
    private String getCodename(DocumentModel doc, CoreSession session, NuxeoPrincipal user) {
        // if GTM has passed, use public codename
        Calendar GTM = (Calendar) doc.getPropertyValue(Constant.XPATH.GTM);
        if (GTM != null && GTM.compareTo(Calendar.getInstance()) < 0) {
            return doc.getTitle();
        }

        CoreSession sysSession = CoreInstance.openCoreSessionSystem(null);
        DocumentModelList codenameList = sysSession.query(String.format(CODENAME_QUERY, doc.getId(), doc.getTitle()));
        if (codenameList == null || codenameList.size() == 0) {
            if (sysSession != null) {
                ((CloseableCoreSession) sysSession).close();
                sysSession = null;
            }
            return null;
        }

        try {
            // Logically one device should link to only one codename
            // We will use the first one if we get multiple
            DocumentModel codename = codenameList.get(0);

            // if no specific names, use public codename
            List<Map<String, Object>> specificNames = (List<Map<String, Object>>) codename.getPropertyValue(
                    Constant.XPATH.SPECIFIC_NAMES);
            if (specificNames == null || specificNames.size() == 0) {
                return codename.getTitle();
            }

            for (Map<String, Object> specificName : specificNames) {
                if (USER.equals(specificName.get(NAME_TYPE))) {
                    if (user.getName().equals(specificName.get(NAME_VALUE))) {
                        return (String) specificName.get(NAME_CODENAME);
                    }
                    continue;
                } else if (GROUP.equals(specificName.get(NAME_TYPE))) {
                    if (user.isMemberOf((String) specificName.get(NAME_VALUE))) {
                        return (String) specificName.get(NAME_CODENAME);
                    }
                    continue;
                } else if (Arrays.asList(RETAIL, AGENCY, VENDOR).contains(specificName.get(NAME_TYPE))) {
                    String result = handleExternalUserCodename(specificName, user, sysSession);
                    if (result != null) {
                        return result;
                    }
                    continue;
                } else {
                    logger.warn(specificName.get(NAME_TYPE) + " is not supported");
                    continue;
                }
            }
            return codename.getTitle();
        } finally {
            if (sysSession != null) {
                ((CloseableCoreSession) sysSession).close();
                sysSession = null;
            }
        }
    }

    private String handleExternalUserCodename(Map<String, Object> specificName, NuxeoPrincipal user,
            CoreSession session) {
        String codename = null;

        String accessType = (String) specificName.get(NAME_TYPE);
        String company = (String) specificName.get(NAME_VALUE);
        DocumentModelList userAccesses = session.query(
                String.format(USERACCESS_QUERY, user.getName(), accessType, company));
        if (userAccesses == null || userAccesses.size() == 0) {
            return null;
        }
        if (userAccesses.size() > 0) {
            codename = (String) specificName.get(NAME_CODENAME);
        }
        return codename;
    }

}
