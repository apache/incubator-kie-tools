package org.drools.workbench.screens.testscenario.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.drools.workbench.screens.testscenario.type.TestScenarioResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class TestScenarioResourceType
        extends TestScenarioResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return TestScenarioAltedImages.INSTANCE.typeTestScenario();
    }

    @Override
    public String getDescription() {
        String desc = TestScenarioConstants.INSTANCE.testScenarioResourceTypeDescription();
        if (desc == null || desc.isEmpty()) {
            return super.getDescription();
        }
        return desc;
    }
}
