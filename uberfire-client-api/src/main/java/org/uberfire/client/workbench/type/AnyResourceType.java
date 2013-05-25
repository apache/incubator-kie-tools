package org.uberfire.client.workbench.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;

@ApplicationScoped
public class AnyResourceType
        extends AnyResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return null;
    }

}
