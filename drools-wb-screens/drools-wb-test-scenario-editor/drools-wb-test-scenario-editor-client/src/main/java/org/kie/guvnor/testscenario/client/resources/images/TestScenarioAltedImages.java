package org.kie.guvnor.testscenario.client.resources.images;

import com.google.gwt.user.client.ui.Image;
import org.kie.guvnor.commons.ui.client.resources.CommonImages;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.testscenario.client.resources.i18n.TestScenarioConstants;

public class TestScenarioAltedImages {

    public static TestScenarioAltedImages INSTANCE = new TestScenarioAltedImages();

    public Image RuleAsset() {
        Image image = new Image(TestScenarioImages.INSTANCE.RuleAsset());
        image.setAltText(CommonConstants.INSTANCE.RuleAsset());
        return image;
    }

    public Image AddFieldToFact() {
        Image image = new Image(TestScenarioImages.INSTANCE.addFieldToFact());
        image.setAltText(TestScenarioConstants.INSTANCE.AddFieldToFact());
        return image;
    }

    public Image Wizard() {
        Image image = new Image(TestScenarioImages.INSTANCE.newWiz());
        image.setAltText(TestScenarioConstants.INSTANCE.Wizard());
        return image;
    }


    public Image TestScenario() {
        Image image = new Image(TestScenarioImages.INSTANCE.testManager());
        image.setAltText(TestScenarioConstants.INSTANCE.TestScenario());
        return image;
    }
}
