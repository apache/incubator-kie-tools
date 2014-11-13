package org.kie.workbench.common.screens.defaulteditor.service;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class PackageNameWhiteListResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "Package Name White List";
    }

    @Override
    public String getDescription() {
        return "Package Name White List";
    }

    @Override
    public String getPrefix() {
        return "package-names-white-list";
    }

    @Override
    public String getSuffix() {
        return null;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getSimpleWildcardPattern() {
        return getPrefix();
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().equals( getPrefix() );
    }
}
