package org.kie.guvnor.testscenario.client;

import javax.enterprise.context.ApplicationScoped;

import org.kie.guvnor.testscenario.type.TestScenarioResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

import com.google.gwt.user.client.ui.IsWidget;

@ApplicationScoped
public class TestScenarioResourceType
        extends TestScenarioResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return null;
    }
}
