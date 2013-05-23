package org.kie.workbench.common.screens.projecteditor.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.projecteditor.type.POMResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class POMResourceType
        extends POMResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return null;
    }
}
