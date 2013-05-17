package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import org.kie.workbench.common.services.datamodel.oracle.ProjectDataModelOracleImpl;

/**
 * Builder for Fact Types
 */
public interface FactBuilder {

    public ProjectDataModelOracleBuilder end();

    public void build( final ProjectDataModelOracleImpl oracle );

}
