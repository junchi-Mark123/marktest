/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;

/**
 * @author frank.zheng
 */
public class UserDeletedListener implements EventListener {

    private final static Log logger = LogFactory.getLog(UserDeletedListener.class);

    private final static String ID_PROPERTY_KEY = "id";

    private final static String USER_ACCESS_QUERY_BY_USER_ID = "SELECT * FROM Document WHERE ecm:primaryType = 'UserAccess' AND ecm:isVersion = 0 AND userAccess:related_User = '%s' AND ecm:isTrashed = 0";

    /*
     * (non-Javadoc)
     * @see org.nuxeo.ecm.core.event.EventListener#handleEvent(org.nuxeo.ecm.core.event.Event)
     */
    @Override
    public void handleEvent(Event event) {
        EventContext ectx = event.getContext();
        // Get user id
        String userId = (String) ectx.getProperty(ID_PROPERTY_KEY);

        CloseableCoreSession session = CoreInstance.openCoreSessionSystem(null);

        try {

            // Get User Access document by userId
            DocumentModelList userAccessList = session.query(String.format(USER_ACCESS_QUERY_BY_USER_ID, userId));

            if (userAccessList == null || userAccessList.size() == 0) {
                logger.warn(String.format("No User Access Document found by userId: %s", userId));
                return;
            }

            // It should have one-to-one relationship between user and userAccess
            // However, to make sure we keep this relationship, when doing delete process
            // we will clean up all the UserAccess documents with this user to avoid future confusion
            for (DocumentModel userAccess : userAccessList) {
                session.removeDocument(userAccess.getRef());
            }
        } catch (Exception e) {
            logger.error(
                    String.format("Error happened when trying to delete User Access document with user: %s", userId),
                    e);
        } finally {
            session.close();
        }

    }

}
