/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
/**
 * 
 */
package amazon.devices.core.operation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.CoreSession.CopyOption;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;

import amazon.devices.core.common.Constant;

/**
 * @author frank.zheng
 */
@Operation(id = CreateProjectOperation.ID, category = Constants.CAT_DOCUMENT, label = "AMZD.CreateProjectOperation", description = "Create Project Document based on Project Template Document")
public class CreateProjectOperation {

    public static final String ID = "AMZD.CreateProjectOperation";

    protected static final Log logger = LogFactory.getLog(CreateProjectOperation.class);

    @Param(name = "template", required = true)
    protected String projectTemplate;

    @Param(name = "title", required = true)
    protected String title;

    @Context
    protected CoreSession session;

    @OperationMethod
    public DocumentModel run(DocumentModel documentModel) throws Exception {
        // Create the project with given title
        DocumentModel project = session.createDocumentModel(documentModel.getPathAsString(), title,
                Constant.DOCTYPE.CREATIVE_PROJECT);
        project.setPropertyValue(Constant.XPATH.TITLE, title);
        project = session.createDocument(project);
        session.save();
        
        // Get the template by doc id
        DocumentModel template = session.getDocument(new IdRef(projectTemplate));
        if(template == null) {
            logger.warn(String.format("Template cannot be find with id: %s", template));
            return project;
        }
        
        // Get the children of template
        DocumentModelList children = session.getChildren(template.getRef());
        
        // Copy children to project
        for(DocumentModel child : children) {
            session.copy(child.getRef(), project.getRef(), null, CopyOption.RESET_LIFE_CYCLE, CopyOption.RESET_CREATOR);
        }
        
        return project;
    }
}
