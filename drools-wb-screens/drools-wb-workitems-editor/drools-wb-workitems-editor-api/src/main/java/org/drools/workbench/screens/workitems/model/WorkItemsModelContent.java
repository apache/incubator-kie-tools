package org.drools.workbench.screens.workitems.model;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;

@Portable
public class WorkItemsModelContent {

    private String definition;
    private List<String> workItemImages;

    public WorkItemsModelContent() {
    }

    public WorkItemsModelContent( final String definition,
                                  final List<String> workItemImages ) {
        this.definition = PortablePreconditions.checkNotNull( "definition",
                                                              definition );
        this.workItemImages = PortablePreconditions.checkNotNull( "workItemImages",
                                                                  workItemImages );

    }

    public String getDefinition() {
        return this.definition;
    }

    public List<String> getWorkItemImages() {
        return this.workItemImages;
    }

}
