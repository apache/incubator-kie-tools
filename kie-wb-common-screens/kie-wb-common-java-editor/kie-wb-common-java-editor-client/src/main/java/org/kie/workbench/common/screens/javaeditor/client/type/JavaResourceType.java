package org.kie.workbench.common.screens.javaeditor.client.type;

import org.kie.workbench.common.screens.javaeditor.type.JavaResourceTypeDefinition;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.workbench.type.ClientResourceType;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JavaResourceType
        extends JavaResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return null;
    }
}
