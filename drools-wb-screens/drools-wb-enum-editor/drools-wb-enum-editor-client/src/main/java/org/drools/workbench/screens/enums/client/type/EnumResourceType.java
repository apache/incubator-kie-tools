package org.drools.workbench.screens.enums.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.enums.client.resources.EnumEditorResources;
import org.drools.workbench.screens.enums.type.EnumResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class EnumResourceType
        extends EnumResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( EnumEditorResources.INSTANCE.images().enumsIcon() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

}
