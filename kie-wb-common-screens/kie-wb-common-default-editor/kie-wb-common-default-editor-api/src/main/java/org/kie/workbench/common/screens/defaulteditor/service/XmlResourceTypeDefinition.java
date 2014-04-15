package org.kie.workbench.common.screens.defaulteditor.service;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class XmlResourceTypeDefinition implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "xml file";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "xml";
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
