package org.drools.workbench.screens.workitems.client.widget;

import org.drools.workbench.screens.workitems.model.WorkItemDefinitionElements;

/**
 * Marker interface for widgets that need Work Item definition elements
 */
public interface HasWorkItemDefinitionElements {

    void setDefinitionElements( final WorkItemDefinitionElements elementDefinitions );

}
