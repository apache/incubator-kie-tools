package org.kie.workbench.common.screens.projectimportsscreen.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.shared.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class ProjectImportsResourceTypeDefinition
        implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "project imports";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getPrefix() {
        return "project";
    }

    @Override
    public String getSuffix() {
        return "imports";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getSimpleWildcardPattern() {
        return getPrefix() + "." + getSuffix();
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().endsWith( getPrefix() + "." + getSuffix() );
    }
}
