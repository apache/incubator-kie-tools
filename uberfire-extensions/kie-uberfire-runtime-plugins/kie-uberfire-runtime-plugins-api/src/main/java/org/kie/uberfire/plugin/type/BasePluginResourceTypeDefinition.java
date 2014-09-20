package org.kie.uberfire.plugin.type;

import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public abstract class BasePluginResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getSimpleWildcardPattern() {
        return "*/" + getSuffix();
    }

    @Override
    public boolean accept( final Path path ) {
        return path.toURI().endsWith( getSuffix() );
    }
}
