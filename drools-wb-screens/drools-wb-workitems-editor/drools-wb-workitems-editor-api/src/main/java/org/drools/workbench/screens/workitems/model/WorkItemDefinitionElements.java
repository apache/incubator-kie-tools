package org.drools.workbench.screens.workitems.model;

import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;

@Portable
public class WorkItemDefinitionElements {

    private Map<String, String> workItemDefinitionElements;

    public WorkItemDefinitionElements() {
    }

    public WorkItemDefinitionElements( final Map<String, String> workItemDefinitionElements ) {
        this.workItemDefinitionElements = PortablePreconditions.checkNotNull( "workItemDefinitionElements",
                                                                              workItemDefinitionElements );
    }

    public Map<String, String> getDefinitionElements() {
        return this.workItemDefinitionElements;
    }

}
