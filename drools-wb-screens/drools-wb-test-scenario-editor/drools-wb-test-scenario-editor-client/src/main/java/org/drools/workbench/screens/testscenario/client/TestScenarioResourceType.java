package org.drools.workbench.screens.testscenario.client;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.drools.workbench.screens.testscenario.type.TestScenarioResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class TestScenarioResourceType
        extends TestScenarioResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = TestScenarioAltedImages.INSTANCE.typeTestScenario();

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

}
