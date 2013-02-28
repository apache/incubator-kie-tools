package org.uberfire.shared.workbench.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class TextResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "text";
    }

    @Override
    public String getDescription() {
        return "Text file";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "txt";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getSimpleWildcardPattern() {
        return "*.txt";
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().endsWith( "." + getSuffix() );
    }
}
