package org.drools.workbench.screens.workitems.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.workitems.client.resources.WorkItemsEditorResources;
import org.drools.workbench.screens.workitems.type.WorkItemsTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class WorkItemsResourceType
        extends WorkItemsTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( WorkItemsEditorResources.INSTANCE.images().workitemIcon() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

}
