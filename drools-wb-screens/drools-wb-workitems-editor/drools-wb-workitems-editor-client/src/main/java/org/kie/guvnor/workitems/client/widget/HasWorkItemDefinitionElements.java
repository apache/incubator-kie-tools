package org.kie.guvnor.workitems.client.widget;

import org.kie.guvnor.workitems.model.WorkItemDefinitionElements;

/**
 * Marker interface for widgets that need Work Item definition elements
 */
public interface HasWorkItemDefinitionElements {

    void setDefinitionElements( final WorkItemDefinitionElements elementDefinitions );

}
