package org.uberfire.client.workbench.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.resources.UberfireResources;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;

@ApplicationScoped
public class AnyResourceType
        extends AnyResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( UberfireResources.INSTANCE.images().typeGenericFile() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

}
