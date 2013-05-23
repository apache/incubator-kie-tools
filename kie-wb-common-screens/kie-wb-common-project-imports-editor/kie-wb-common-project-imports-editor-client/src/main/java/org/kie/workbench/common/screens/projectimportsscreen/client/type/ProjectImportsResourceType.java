package org.kie.workbench.common.screens.projectimportsscreen.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.projectimportsscreen.type.ProjectImportsResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class ProjectImportsResourceType
        extends ProjectImportsResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return null;
    }
}
