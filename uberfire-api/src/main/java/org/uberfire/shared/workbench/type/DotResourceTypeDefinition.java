package org.uberfire.shared.workbench.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class DotResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "meta data";
    }

    @Override
    public String getDescription() {
        return "Dot file";
    }

    @Override
    public String getPrefix() {
        return ".";
    }

    @Override
    public String getSuffix() {
        return "";
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().startsWith( getPrefix() );
    }
}
