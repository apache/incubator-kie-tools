package org.drools.workbench.screens.enums.type;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class EnumResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "enum";
    }

    @Override
    public String getDescription() {
        return "Enumeration definition";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "enumeration";
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
