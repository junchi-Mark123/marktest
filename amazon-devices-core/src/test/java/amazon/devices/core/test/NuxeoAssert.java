package amazon.devices.core.test;

import static org.junit.Assert.fail;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;

public interface NuxeoAssert {

    public AssertDoc withStringProperty(String propertyName);

    /**
     * @param the name of the property
     * @return
     */
    public AssertDoc withDateProperty(String propertyName);

    /**
     * @param the name of the property
     * @return
     */
    public AssertDoc withBooleanProperty(String propertyName);

    public AssertDoc withNumberProperty(String propertyName);

    public AssertDoc withArrayProperty(String propertyName);

    public AssertDoc withArrayProperty(String propertyName, String defaultValue);

    /**
     * @param propertyName
     * @param value the test value to set
     * @return self
     */
    public AssertDoc withProperty(String propertyName, Serializable value);

    /**
     * @param key
     * @param value the test value to set
     * @return self
     */
    public AssertDoc withContextData(final String key, final Serializable value);

    /**
     * Test if the session can create the document
     *
     * @return self
     */
    public AssertDoc canBeCreated();

    /**
     * @param docType The doctype to test
     * @param name The name of the test doctype that will be created at the root of the repository
     * @param session the session that will be tested
     * @return an AssertDoc
     */
    public static AssertDoc assertThatDocType(String docType, String name, CoreSession session) {
        return assertThatDocType(docType, "/", name, session);
    }

    /**
     * @param docType The doctype to test
     * @param path The path to create the doc
     * @param name The name of the test doctype that will be created at the root of the repository
     * @param session the session that will be tested
     * @return an AssertDoc
     */
    public static AssertDoc assertThatDocType(String docType, String path, String name, CoreSession session) {
        DocumentModel doc = null;
        try {
            doc = session.createDocumentModel(path, name, docType);
        } catch (NuxeoException e) {
            fail("Unable to created docType [" + docType + "] : " + e.getMessage());
        }
        return new AssertDoc(doc, session);
    }

    /**
     * Test if the doc has a given schema
     *
     * @param the schema's name
     * @return self
     */
    public AssertDoc withSchema(String schemaName);

    /**
     * Test if the doc has given schemas
     *
     * @param schemas list of schemas to check
     * @return
     */
    public AssertDoc withSchemas(String... schemas);

    /**
     * Test if the doc has given facets
     *
     * @param facets list of facets to check
     * @return
     */
    public AssertDoc withFacet(String... facets);

    /**
     * Test if the doc has a given facet
     *
     * @param facet
     * @return
     */
    public AssertDoc withFacet(String facet);

    /**
     * Test if the doc has the given lifecycle
     * 
     * @param the expected lifecycle
     */
    public AssertDoc hasLifecycle(String lifecycleName);

    /**
     * Test if the doc is under the given parent ref
     * 
     * @param the expected parent ref
     */
    public AssertDoc isChildOf(DocumentRef parentRef);

    public AssertDoc whenItFollowsTransition(String transition);

}
