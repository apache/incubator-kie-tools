package org.drools.workbench.screens.globals.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class GlobalResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "global";
    }

    @Override
    public String getDescription() {
        return "Globals definition";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "gdrl";
    }

    @Override
    public int getPriority() {
        return 101;
    }

    @Override
    public String getSimpleWildcardPattern() {
        return "*." + getSuffix();
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().endsWith( "." + getSuffix() );
    }
}
