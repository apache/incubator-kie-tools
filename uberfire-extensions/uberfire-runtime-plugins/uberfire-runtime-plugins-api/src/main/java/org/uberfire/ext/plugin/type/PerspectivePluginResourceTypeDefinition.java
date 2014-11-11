package org.uberfire.ext.plugin.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class PerspectivePluginResourceTypeDefinition extends BasePluginResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "perspective plugin";
    }

    @Override
    public String getDescription() {
        return "Perspective plugin";
    }

    @Override
    public String getSuffix() {
        return "/" + PluginType.PERSPECTIVE.toString().toLowerCase() + ".plugin";
    }

}
