package org.uberfire.workbench.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class AnyResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "any";
    }

    @Override
    public String getDescription() {
        return "Others";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "";
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public String getSimpleWildcardPattern() {
        return "*";
    }

    @Override
    public boolean accept( final Path path ) {
        return true;
    }
}
