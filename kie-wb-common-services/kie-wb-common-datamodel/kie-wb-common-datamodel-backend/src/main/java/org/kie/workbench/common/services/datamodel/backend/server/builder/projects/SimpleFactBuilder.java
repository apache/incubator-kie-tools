package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import org.guvnor.common.services.builder.model.TypeSource;
import org.kie.workbench.common.services.datamodel.model.ModelField;

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

}
