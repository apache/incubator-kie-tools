package org.drools.workbench.screens.guided.scorecard.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class GuidedScoreCardResourceTypeDefinition
        implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "guided score card";
    }

    @Override
    public String getDescription() {
        return "Guided Score Card";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "scgd";
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
