package org.drools.workbench.screens.testscenario.client.resources.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface TestScenarioImages
        extends ClientBundle {

    public static TestScenarioImages INSTANCE = GWT.create(TestScenarioImages.class);

    @Source("rule_asset.gif")
    public ImageResource RuleAsset();

    @Source("add_field_to_fact.gif")
    ImageResource addFieldToFact();

    @Source("new_wiz.gif")
    ImageResource newWiz();

    @Source("execution_trace.gif")
    ImageResource executionTrace();

    @Source("test_passed.png")
    ImageResource testPassed();

    @Source("BPM_FileIcons_testscenario.png")
    ImageResource typeTestScenario();
}
