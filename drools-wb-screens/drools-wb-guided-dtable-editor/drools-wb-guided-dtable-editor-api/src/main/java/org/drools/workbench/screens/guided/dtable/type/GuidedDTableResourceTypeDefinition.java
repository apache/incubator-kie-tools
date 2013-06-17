package org.drools.workbench.screens.guided.dtable.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class GuidedDTableResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "guided dtable";
    }

    @Override
    public String getDescription() {
        return "Guided Decision Table";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "gdst";
    }

    @Override
    public int getPriority() {
        return 0;
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
