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

package org.drools.workbench.screens.testscenario.client.page.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.assertj.core.api.Assertions;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.RuleSelectionEvent;
import org.drools.workbench.screens.testscenario.client.ScenarioParentWidget;
import org.drools.workbench.screens.testscenario.client.ScenarioWidgetComponentCreator;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Text.class, RootPanel.class})
public class ConfigWidgetTest {

    @Mock
    private ScenarioWidgetComponentCreator scenarioWidgetComponentCreator;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private Button addButton;

    @Mock
    private Button removeButton;

    @Mock
    private ListBox configuredRules;

    @Mock
    private ListBox configurationType;

    @Mock
    private ScenarioParentWidget scenarioParentWidget;

    @Mock
    private Path path;

    @Mock
    private Scenario scenario;

    @Captor
    private ArgumentCaptor<ClickHandler> clickCaptor;

    @Captor
    private ArgumentCaptor<RuleSelectionEvent> selectedRuleEventCaptor;

    @Captor
    private ArgumentCaptor<ChangeHandler> changeHandlerCaptor;

    private ConfigWidget configWidget;

    @Before
    public void setUp() throws Exception {
        configWidget = new ConfigWidget(scenarioWidgetComponentCreator,
                                        oracle,
                                        addButton,
                                        removeButton,
                                        configuredRules,
                                        configurationType);
    }

    @Test
    public void testInit() throws Exception {
        configWidget.init(scenarioParentWidget, path, scenario);

        Mockito.verify(scenarioWidgetComponentCreator).reset(scenarioParentWidget, path, oracle, scenario);
    }

    @Test
    public void testShowOneAllowedRule() {
        configWidget.init(scenarioParentWidget, path, scenario);
        when(scenario.getRules()).thenReturn(Collections.singletonList("rule 1"));
        when(scenario.isInclusive()).thenReturn(true);

        configWidget.show();

        verify(configuredRules).clear();
        verify(configuredRules).addItem("rule 1");
        verify(configurationType).setSelectedIndex(ConfigWidget.ALLOWED_INDEX);
        verify(configuredRules).setVisible(true);
        verify(addButton).setVisible(true);
        verify(removeButton).setVisible(true);
    }

    @Test
    public void testShowOnePreventedRule() {
        configWidget.init(scenarioParentWidget, path, scenario);
        when(scenario.getRules()).thenReturn(Collections.singletonList("rule 1"));
        when(scenario.isInclusive()).thenReturn(false);

        configWidget.show();

        verify(configuredRules).clear();
        verify(configuredRules).addItem("rule 1");
        verify(configurationType).setSelectedIndex(ConfigWidget.PREVENTED_INDEX);
        verify(configuredRules).setVisible(true);
        verify(addButton).setVisible(true);
        verify(removeButton).setVisible(true);
    }

    @Test
    public void testShowNoRule() {
        configWidget.init(scenarioParentWidget, path, scenario);
        when(scenario.getRules()).thenReturn(Collections.emptyList());

        configWidget.show();

        verify(configuredRules).clear();
        verify(configurationType).setSelectedIndex(2);
        verify(configuredRules).setVisible(false);
        verify(addButton).setVisible(false);
        verify(removeButton).setVisible(false);
    }

    @Test
    public void testAddButtonClickHandler() throws Exception {
        final List<String> rules = new ArrayList<>();
        when(scenario.getRules()).thenReturn(rules);

        configWidget.init(scenarioParentWidget, path, scenario);
        configWidget.setupHandlers();

        verify(addButton).addClickHandler(clickCaptor.capture());
        clickCaptor.getValue().onClick(null);
        verify(scenarioWidgetComponentCreator).getRuleSelectionWidget(selectedRuleEventCaptor.capture());
        selectedRuleEventCaptor.getValue().ruleSelected("rule 1");

        Assertions.assertThat(rules).containsExactly("rule 1");
        verify(configuredRules).addItem("rule 1");
    }

    @Test
    public void testRemoveButtonHandler() throws Exception {
        final List<String> rules = new ArrayList<String>() {{
            add("rule 1");
        }};
        when(scenario.getRules()).thenReturn(rules);
        when(configuredRules.getSelectedIndex()).thenReturn(0);
        when(configuredRules.getItemText(0)).thenReturn("rule 1");

        configWidget.init(scenarioParentWidget, path, scenario);
        configWidget.setupHandlers();

        verify(removeButton).addClickHandler(clickCaptor.capture());
        clickCaptor.getValue().onClick(null);

        Assertions.assertThat(rules).isEmpty();
        verify(configuredRules).removeItem(0);
    }

    @Test
    public void testChangeAllowedRules() throws Exception {
        when(configurationType.getSelectedIndex()).thenReturn(ConfigWidget.ALLOWED_INDEX);
        when(configurationType.getValue(ConfigWidget.ALLOWED_INDEX)).thenReturn("inc");

        configWidget.init(scenarioParentWidget, path, scenario);
        configWidget.setupHandlers();
        verify(configurationType).addChangeHandler(changeHandlerCaptor.capture());
        changeHandlerCaptor.getValue().onChange(null);

        verify(scenario).setInclusive(true);
        verify(addButton).setVisible(true);
        verify(removeButton).setVisible(true);
        verify(configuredRules).setVisible(true);
    }

    @Test
    public void testChangePreventedRules() throws Exception {
        when(configurationType.getSelectedIndex()).thenReturn(ConfigWidget.PREVENTED_INDEX);
        when(configurationType.getValue(ConfigWidget.PREVENTED_INDEX)).thenReturn("exc");

        configWidget.init(scenarioParentWidget, path, scenario);
        configWidget.setupHandlers();
        verify(configurationType).addChangeHandler(changeHandlerCaptor.capture());
        changeHandlerCaptor.getValue().onChange(null);

        verify(scenario).setInclusive(false);
        verify(addButton).setVisible(true);
        verify(removeButton).setVisible(true);
        verify(configuredRules).setVisible(true);
    }

    @Test
    public void testChangeAllowAllRules() throws Exception {
        final List<String> rules = new ArrayList<String>() {{
            add("rule 1");
        }};
        when(scenario.getRules()).thenReturn(rules);
        when(configurationType.getSelectedIndex()).thenReturn(ConfigWidget.ALL_ALLOWED_INDEX);
        when(configurationType.getValue(ConfigWidget.ALL_ALLOWED_INDEX)).thenReturn("all");

        configWidget.init(scenarioParentWidget, path, scenario);
        configWidget.setupHandlers();
        verify(configurationType).addChangeHandler(changeHandlerCaptor.capture());
        changeHandlerCaptor.getValue().onChange(null);

        Assertions.assertThat(rules).isEmpty();
        verify(addButton).setVisible(false);
        verify(removeButton).setVisible(false);
        verify(configuredRules).setVisible(false);
        verify(configuredRules).clear();
    }
}
