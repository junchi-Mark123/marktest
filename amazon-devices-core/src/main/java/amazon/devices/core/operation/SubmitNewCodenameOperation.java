package amazon.devices.core.operation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.automation.core.util.StringList;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Iterator;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.util.StringList;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.query.sql.NXQL;

/**
 * submit a new codename for the selected device
 */
@Operation(id = SubmitNewCodenameOperation.ID, category = Constants.CAT_DOCUMENT, label = "AMZD.SubmitNewCodenameOperation", description = "User can submit a new codenameMark222.")
public class SubmitNewCodenameOperation {

    public static final String ID = "AMZD.SubmitNewCodenameOperation";

    protected static final Log log = LogFactory.getLog(SubmitNewCodenameOperation.class);

    @Param(name = "device_name", required = true)
    protected String devicename;

    @Param(name = "name_type", required = true)
    protected String nametype;

    @Param(name = "name_value", required = true)
    protected String namevalue;

    @Param(name = "name_codename", required = true)
    protected String namecodename;

    @Context
    protected CoreSession session;

    @Context
    protected OperationContext ctx;

    @OperationMethod
    public DocumentModel run(DocumentModel document) throws Exception {
        // 1.Get the document(type:codename)by property "device"
        System.out.println(devicename);
        DocumentModelList codename = (DocumentModelList) session.query(
                "SELECT * FROM Document WHERE codename:device=" + "'" + devicename + "'"

                        + "AND ecm:primaryType = 'Codename'");
        System.out.println(codename);
        // if the device is not related to any codename

        if (codename == null) {
               log.error("The codename/devicen is null");
            
        }

        DocumentModel doc = codename.get(0);
        // 2.put the value to the params
        List<Map<String, Object>> specific_names = new ArrayList<Map<String, Object>>();

        Map<String, Object> newspecific_names = new HashMap<String, Object>();
        newspecific_names.put("name_type", nametype);
        newspecific_names.put("name_value", namevalue);
        newspecific_names.put("name_codename", namecodename);
        // check if the specific_names hava value
        if (doc.getProperty("specific_names") != null && doc.getProperty("specific_names").size() != 0) {
            specific_names = (List<Map<String, Object>>) doc.getProperty("specific_names").getValue();

        }
        // 3.add the new specific name to the old specific_name properties
        specific_names.add(newspecific_names);

        // 4.add the property to the specific_names
        doc.setPropertyValue("specific_names", (Serializable) specific_names);

        // 5. save the document;
        doc = session.saveDocument(doc);

        // codename.set(0, doc);

        return doc;

    }

}
