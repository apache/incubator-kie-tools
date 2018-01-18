/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.Collections;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.ruleselector.RuleSelector;
import org.mockito.Mock;
import org.mockito.Mockito;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({GuidedRuleEditorImages508.class, FlexTable.class})
public class RuleModellerTest {

    private RuleModel model;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private ModellerWidgetFactory widgetFactory;

    @Mock
    private EventBus eventBus;

    @Mock
    private RuleSelector ruleSelector;

    private RuleModeller ruleModeller;

    @Before
    public void setUp() throws Exception {
        model = new RuleModel();
        GwtMockito.useProviderForType(RuleSelector.class, aClass -> ruleSelector);

        ruleModeller = new RuleModeller(model,
                                        Collections.emptyList(),
                                        oracle,
                                        widgetFactory,
                                        eventBus,
                                        false,
                                        false);
    }

    @Test
    public void testSetRuleNamesForPackage() throws Exception {
        model.name = "rule 1";
        ruleModeller.setRuleNamesForPackage(Collections.singleton("rule 2"));

        Mockito.verify(ruleSelector).setRuleNames(Collections.singleton("rule 2"), "rule 1");
    }
}
