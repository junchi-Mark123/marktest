/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.security.Access;

/**
 * This class can be used to test a doctype for given properties : * schemas * facets * properties with a fluent API
 * <code>
 * assertThatDocType("MyDocType", "thetestname", session)
 *           .withSchemas("common","dublincore","uid")
 *           .withFacet("Commentable","Versionable","Indexable")
 *           .withProperty("myschema:myprop")
 *           .canBeCreated();
 * </code>
 *
 * @author dmetzler
 */
public class AssertDoc implements NuxeoAssert {

    protected DocumentModel doc;

    protected final CoreSession session;

    /**
     * Constructor : use {@link AssertDoc#assertThatDocType(String, String, CoreSession)}
     *
     * @param doc current doc
     * @param session opened CoreSession
     */
    protected AssertDoc(DocumentModel doc, CoreSession session) {
        this.doc = doc;
        this.session = session;
    }

    /**
     * @param propertyName of the property
     * @return
     */
    public AssertDoc withStringProperty(String propertyName) {
        return withProperty(propertyName, "dummyValue");
    }

    /**
     * @param propertyName of the property
     * @return
     */
    public AssertDoc withDateProperty(String propertyName) {
        return withProperty(propertyName, GregorianCalendar.from(ZonedDateTime.now()));
    }

    /**
     * @param propertyName of the property
     * @return
     */
    public AssertDoc withBooleanProperty(String propertyName) {
        return withProperty(propertyName, "true");
    }

    public AssertDoc withNumberProperty(String propertyName) {
        return withProperty(propertyName, 2);
    }

    public AssertDoc withArrayProperty(String propertyName) {
        return withProperty(propertyName, new String[0]);
    }

    public AssertDoc withArrayProperty(String propertyName, String defaultValue) {
        return withProperty(propertyName, new String[] { defaultValue });
    }

    /**
     * @param propertyName of the property
     * @param value the test value to set
     * @return self
     */
    public AssertDoc withProperty(String propertyName, Serializable value) {
        try {
            doc.setPropertyValue(propertyName, value);
        } catch (NuxeoException e) {
            fail("Unable to set property [" + propertyName + "] with value " + value + " : " + e.getMessage());
        }
        return this;
    }

    /**
     * @param key
     * @param value the test value to set
     * @return self
     */
    public AssertDoc withContextData(final String key, final Serializable value) {
        doc.putContextData(key, value);
        return this;
    }

    /**
     * Test if the session can create the document
     *
     * @return self
     */
    public AssertDoc canBeCreated() {
        try {
            doc = session.createDocument(doc);
        } catch (RuntimeException e) {
            fail("Unable to create document : " + e.getMessage());
        }
        return this;
    }

    /**
     * Test if the doc has a given schema
     *
     * @param schemaName schema's name
     * @return self
     */
    public AssertDoc withSchema(String schemaName) {
        try {
            if (!doc.hasSchema(schemaName)) {
                fail("Document doesn't have schema [" + schemaName + "]");
            }
        } catch (NuxeoException e) {
            fail("Document doesn't have schema [" + schemaName + "] : " + e.getMessage());
        }
        return this;
    }

    /**
     * Test if the doc has given schemas
     *
     * @param schemas list of schemas to check
     * @return
     */
    public AssertDoc withSchemas(String... schemas) {
        for (String schema : schemas) {
            withSchema(schema);
        }
        return this;
    }

    /**
     * Test if the doc has given facets
     *
     * @param facets list of facets to check
     * @return
     */
    public AssertDoc withFacet(String... facets) {
        for (String facet : facets) {
            withFacet(facet);
        }
        return this;
    }

    /**
     * Test if the doc has a given facet
     *
     * @param facet
     * @return
     */
    public AssertDoc withFacet(String facet) {
        if (!doc.hasFacet(facet)) {
            fail("Document doesn't have facet [" + facet + "]");
        }
        return this;
    }

    /**
     * Test if the doc has the given lifecycle
     * 
     * @param lifecycleName expected lifecycle
     */
    public AssertDoc hasLifecycle(String lifecycleName) {
        try {
            assertThat(doc.getLifeCyclePolicy(), is(lifecycleName));
        } catch (NuxeoException e) {
            fail("Document doesn't have any lifecycle associated. Try to call canBeCreated() before : "
                    + e.getMessage());
        }
        return this;

    }

    /**
     * Test if the doc has the default property value
     * 
     * @param lifecycleName expected lifecycle
     * @param propertyName property name
     */
    public AssertDoc hasPropertyValue(String propertyName, Serializable expectedValue) {
        assertThat(doc.getPropertyValue(propertyName), is(expectedValue));
        return this;
    }

    /**
     * Test if a principal has been granted a given permission
     * 
     * @param principalId username pr group id to check permission
     * @param permissionName expected granted permission
     */
    public AssertDoc hasPermissionGranted(String principalId, String permissionName) {
        assertThat(doc.getACP().getAccess(principalId, permissionName), is(Access.GRANT));
        return this;
    }

    @Override
    public AssertDoc isChildOf(DocumentRef parentRef) {
        try {
            assertTrue(session.exists(parentRef));
            assertTrue(session.hasChild(parentRef, doc.getName()));
        } catch (RuntimeException e) {
            fail("Unable to get parent : " + e.getMessage());
        }
        return this;
    }

    @Override
    public AssertDoc whenItFollowsTransition(String transition) {
        try {
            doc.followTransition(transition);
        } catch (Exception e) {
            fail("Unable to follow transistion '" + transition + "' : " + e.getMessage());
        }
        return this;
    }

}
