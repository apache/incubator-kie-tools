package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import java.util.Collections;
import java.util.Map;

import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.TypeSource;

/**
 * Simple builder for Fact Types
 */
public class SimpleFactBuilder extends BaseFactBuilder {

    public SimpleFactBuilder( final ProjectDataModelOracleBuilder builder,
                              final String factType,
                              final boolean isEvent,
                              final TypeSource typeSource ) {
        super( builder,
               factType,
               false,
               isEvent,
               typeSource );
    }

    public SimpleFactBuilder addField( final ModelField field ) {
        super.addField( field );
        return this;
    }

    @Override
    public Map<String, FactBuilder> getInternalBuilders() {
        return Collections.emptyMap();
    }
}
