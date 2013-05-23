package org.kie.workbench.common.screens.projecteditor.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.projecteditor.type.KModuleResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class KModuleResourceType
        extends KModuleResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return null;
    }
}
