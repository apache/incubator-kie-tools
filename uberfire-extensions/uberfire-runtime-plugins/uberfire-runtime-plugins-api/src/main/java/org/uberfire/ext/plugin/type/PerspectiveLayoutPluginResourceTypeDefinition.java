package org.uberfire.ext.plugin.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class PerspectiveLayoutPluginResourceTypeDefinition extends BasePluginResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "perspective layout plugin";
    }

    @Override
    public String getDescription() {
        return "Perspective Layout plugin";
    }

    @Override
    public String getSuffix() {
        return "/" + PluginType.PERSPECTIVE_LAYOUT.toString().toLowerCase() + ".plugin";
    }

}
