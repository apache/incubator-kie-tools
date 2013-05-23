package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import org.kie.workbench.common.services.datamodel.model.ModelField;
import org.kie.workbench.common.services.shared.builder.model.TypeSource;

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
