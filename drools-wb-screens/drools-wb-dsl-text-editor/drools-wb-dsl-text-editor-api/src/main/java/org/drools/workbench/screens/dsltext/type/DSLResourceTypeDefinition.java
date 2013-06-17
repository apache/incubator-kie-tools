package org.drools.workbench.screens.dsltext.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class DSLResourceTypeDefinition
        implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "dsl";
    }

    @Override
    public String getDescription() {
        return "Domain Specific Language definition";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "dsl";
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
