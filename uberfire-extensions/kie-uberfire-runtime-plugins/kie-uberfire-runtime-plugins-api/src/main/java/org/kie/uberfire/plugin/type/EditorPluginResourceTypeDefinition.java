package org.kie.uberfire.plugin.type;

import javax.enterprise.context.ApplicationScoped;

import org.kie.uberfire.plugin.model.PluginType;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class EditorPluginResourceTypeDefinition extends BasePluginResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "editor plugin";
    }

    @Override
    public String getDescription() {
        return "Editor plugin";
    }

    @Override
    public String getSuffix() {
        return "/" + PluginType.EDITOR.toString().toLowerCase() + ".plugin";
    }

}
