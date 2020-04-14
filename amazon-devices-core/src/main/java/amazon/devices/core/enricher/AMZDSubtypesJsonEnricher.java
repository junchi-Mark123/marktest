/*

(C) Copyright 2020 Amazon Devices.
Contributors:
Frank Zheng fzheng@ext.nuxeo.com
*/
package amazon.devices.core.enricher;

import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;

import java.io.IOException;
import java.util.Collection;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher;
import org.nuxeo.ecm.core.io.registry.reflect.Priorities;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.runtime.api.Framework;

import com.fasterxml.jackson.core.JsonGenerator;

import amazon.devices.core.adapter.AMZDAdapter;

/**
 * @author frank.zheng
 */
@Setup(mode = SINGLETON, priority = Priorities.OVERRIDE_REFERENCE)
public class AMZDSubtypesJsonEnricher extends AbstractJsonEnricher<DocumentModel> {

    public static final String NAME = "subtypes";
    
    /**
     * @param name
     */
    public AMZDSubtypesJsonEnricher() {
        super(NAME);
    }

    /*
     * (non-Javadoc)
     * @see org.nuxeo.ecm.core.io.marshallers.json.enrichers.AbstractJsonEnricher#write(com.fasterxml.jackson.core.
     * JsonGenerator, java.lang.Object)
     */
    @Override
    public void write(JsonGenerator jg, DocumentModel enriched) throws IOException {
        SchemaManager schemaManager = Framework.getService(SchemaManager.class);
        // Any subtype enricher should be added in the adapter
        // If not overriden the getAllowedSubtypes method, then default behavior will be applied
        Collection<String> subtypes = enriched.getAdapter(AMZDAdapter.class).getAllowedSubtypes();
        jg.writeFieldName(NAME);
        jg.writeStartArray();
        for (String subtype : subtypes) {
            jg.writeStartObject();
            jg.writeStringField("type", subtype);
            jg.writeArrayFieldStart("facets");
            for (String facet : schemaManager.getDocumentType(subtype).getFacets()) {
                jg.writeString(facet);
            }
            jg.writeEndArray();
            jg.writeEndObject();
        }
        jg.writeEndArray();
    }

}
