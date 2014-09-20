package org.kie.uberfire.plugin.type;

import javax.enterprise.context.ApplicationScoped;

import org.kie.uberfire.plugin.model.PluginType;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class ScreenPluginResourceTypeDefinition extends BasePluginResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "screen plugin";
    }

    @Override
    public String getDescription() {
        return "Screen plugin";
    }

    @Override
    public String getSuffix() {
        return "/" + PluginType.SCREEN.toString().toLowerCase() + ".plugin";
    }

}
