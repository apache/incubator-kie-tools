package org.drools.workbench.screens.globals.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.globals.client.resources.GlobalsEditorResources;
import org.drools.workbench.screens.globals.client.resources.i18n.GlobalsEditorConstants;
import org.drools.workbench.screens.globals.type.GlobalResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class GlobalResourceType
        extends GlobalResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( GlobalsEditorResources.INSTANCE.images().typeGlobalVariable() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = GlobalsEditorConstants.INSTANCE.globalsResourceTypeDescription();
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}
