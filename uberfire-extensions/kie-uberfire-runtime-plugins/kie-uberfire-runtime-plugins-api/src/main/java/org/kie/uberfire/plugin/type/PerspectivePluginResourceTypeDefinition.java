package org.kie.uberfire.plugin.type;

import javax.enterprise.context.ApplicationScoped;

import org.kie.uberfire.plugin.model.PluginType;
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
