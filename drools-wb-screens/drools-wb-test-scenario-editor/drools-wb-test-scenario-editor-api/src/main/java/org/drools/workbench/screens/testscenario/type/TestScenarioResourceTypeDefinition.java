package org.drools.workbench.screens.testscenario.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class TestScenarioResourceTypeDefinition
        implements ResourceTypeDefinition {

    @Override
    public String getShortName() {
        return "test scenario";
    }

    @Override
    public String getDescription() {
        return "Test Scenario";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "scenario";
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
