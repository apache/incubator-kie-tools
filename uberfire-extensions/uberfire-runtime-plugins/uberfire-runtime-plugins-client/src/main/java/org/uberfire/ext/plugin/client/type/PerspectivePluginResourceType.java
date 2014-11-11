package org.uberfire.ext.plugin.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.ext.plugin.type.PerspectivePluginResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class PerspectivePluginResourceType
        extends PerspectivePluginResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return new Icon( IconType.TH_LARGE );
    }
}
