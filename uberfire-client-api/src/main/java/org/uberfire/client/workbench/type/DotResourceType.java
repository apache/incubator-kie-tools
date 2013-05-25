package org.uberfire.client.workbench.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.workbench.type.DotResourceTypeDefinition;

@ApplicationScoped
public class DotResourceType
        extends DotResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return null;
    }
}
