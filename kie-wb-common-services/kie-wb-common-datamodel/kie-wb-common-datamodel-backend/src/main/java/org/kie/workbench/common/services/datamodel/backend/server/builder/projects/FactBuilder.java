package org.kie.workbench.common.services.datamodel.backend.server.builder.projects;

import java.util.Map;

import org.drools.workbench.models.commons.backend.oracle.ProjectDataModelOracleImpl;

/**
 * Builder for Fact Types
 */
public interface FactBuilder {

    public ProjectDataModelOracleBuilder end();

    public Map<String, FactBuilder> getInternalBuilders();

    public void build( final ProjectDataModelOracleImpl oracle );


}
