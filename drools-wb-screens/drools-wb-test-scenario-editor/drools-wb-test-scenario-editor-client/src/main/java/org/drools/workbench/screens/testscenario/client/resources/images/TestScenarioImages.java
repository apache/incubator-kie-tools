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
