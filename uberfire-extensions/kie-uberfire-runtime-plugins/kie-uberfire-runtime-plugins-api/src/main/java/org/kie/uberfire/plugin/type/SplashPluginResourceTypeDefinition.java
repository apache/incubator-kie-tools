package org.kie.uberfire.plugin.type;

import javax.enterprise.context.ApplicationScoped;

import org.kie.uberfire.plugin.model.PluginType;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class SplashPluginResourceTypeDefinition extends BasePluginResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "splash plugin";
    }

    @Override
    public String getDescription() {
        return "SplashScreen plugin";
    }

    @Override
    public String getSuffix() {
        return "/" + PluginType.SPLASH.toString().toLowerCase() + ".plugin";
    }

}
