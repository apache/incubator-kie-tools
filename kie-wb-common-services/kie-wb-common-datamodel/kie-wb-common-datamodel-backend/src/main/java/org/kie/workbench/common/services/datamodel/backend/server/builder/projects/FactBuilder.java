package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import org.drools.workbench.models.commons.shared.oracle.ProjectDataModelOracleImpl;

/**
 * Builder for Fact Types
 */
public interface FactBuilder {

    public ProjectDataModelOracleBuilder end();

    public void build( final ProjectDataModelOracleImpl oracle );

}
