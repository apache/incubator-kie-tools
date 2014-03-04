package org.drools.workbench.screens.testscenario.client.resources.images;

import com.google.gwt.user.client.ui.Image;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;

public class TestScenarioAltedImages {

    public static final TestScenarioAltedImages INSTANCE = new TestScenarioAltedImages();

    public Image RuleAsset() {
        Image image = new Image(TestScenarioImages.INSTANCE.RuleAsset());
        image.setAltText(CommonConstants.INSTANCE.RuleAsset());
        return image;
    }

    public Image AddFieldToFact() {
        Image image = new Image(TestScenarioImages.INSTANCE.addFieldToFact());
        image.setAltText( TestScenarioConstants.INSTANCE.AddFieldToFact());
        return image;
    }

    public Image Wizard() {
        Image image = new Image(TestScenarioImages.INSTANCE.newWiz());
        image.setAltText(TestScenarioConstants.INSTANCE.Wizard());
        return image;
    }


    public Image typeTestScenario() {
        Image image = new Image(TestScenarioImages.INSTANCE.typeTestScenario());
        image.setAltText(TestScenarioConstants.INSTANCE.TestScenario());
        return image;
    }
}
