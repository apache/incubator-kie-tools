/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.testscenario.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Select.class, Option.class})
public class ScenarioWidgetComponentCreatorTest {

    @Mock
    private RuleNamesService ruleNamesService;
    private CallerMock<RuleNamesService> ruleNamesServiceCaller;

    @Mock
    private ScenarioParentWidget scenarioWidget;

    @Mock
    private Path path;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private Scenario scenario;

    @GwtMock
    private Select ruleNameSelector;

    private List<String> ruleNames = new ArrayList<>();

    private ScenarioWidgetComponentCreator creator;

    @Before
    public void setup() {
        this.ruleNamesServiceCaller = new CallerMock<>(ruleNamesService);
        this.creator = spy(new ScenarioWidgetComponentCreator(ruleNamesServiceCaller));

        when(ruleNamesService.getRuleNames(any(Path.class),
                                           anyString())).thenReturn(ruleNames);
    }

    @Test
    public void checkRuleNameSelectorWithRules() {
        ruleNames.add("rule1");
        ruleNames.add("rule2");

        creator.reset(scenarioWidget,
                      path,
                      oracle,
                      scenario);

        verify(ruleNameSelector,
               times(1)).setEnabled(eq(true));
        verify(creator,
               times(1)).makeRuleNameOption(eq("rule1"));
        verify(creator,
               times(1)).makeRuleNameOption(eq("rule2"));
    }

    @Test
    public void checkRuleNameSelectorWithNoRules() {
        creator.reset(scenarioWidget,
                      path,
                      oracle,
                      scenario);

        verify(ruleNameSelector,
               times(1)).setEnabled(eq(false));
        verify(creator,
               never()).makeRuleNameOption(anyString());
    }
}
