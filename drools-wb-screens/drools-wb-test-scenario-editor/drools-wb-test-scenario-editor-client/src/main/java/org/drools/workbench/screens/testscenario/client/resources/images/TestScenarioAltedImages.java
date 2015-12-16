/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
