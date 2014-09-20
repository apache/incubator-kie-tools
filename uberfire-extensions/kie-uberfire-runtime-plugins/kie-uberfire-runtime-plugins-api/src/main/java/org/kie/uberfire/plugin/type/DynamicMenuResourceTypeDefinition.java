package org.kie.uberfire.plugin.type;

import javax.enterprise.context.ApplicationScoped;

import org.kie.uberfire.plugin.model.PluginType;

@ApplicationScoped
public class DynamicMenuResourceTypeDefinition extends BasePluginResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "dynamic menu";
    }

    @Override
    public String getDescription() {
        return "Dynamic Menu";
    }

    @Override
    public String getSuffix() {
        return "/" + PluginType.DYNAMIC_MENU.toString().toLowerCase() + ".plugin";
    }
}
