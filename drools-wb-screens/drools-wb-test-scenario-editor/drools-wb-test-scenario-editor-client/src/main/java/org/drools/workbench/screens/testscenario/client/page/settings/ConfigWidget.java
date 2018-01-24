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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.ScenarioParentWidget;
import org.drools.workbench.screens.testscenario.client.ScenarioWidgetComponentCreator;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

@Dependent
public class ConfigWidget extends HorizontalPanel {

    public static final int ALLOWED_INDEX = 0;
    public static final int PREVENTED_INDEX = 1;
    public static final int ALL_ALLOWED_INDEX = 2;

    private final ScenarioWidgetComponentCreator scenarioWidgetComponentCreator;

    private final AsyncPackageDataModelOracle oracle;

    private final Button addButton;

    private final Button removeButton;

    private final ListBox configuredRules;

    private final ListBox configurationType;

    private Scenario scenario;

    @Inject
    public ConfigWidget(final ScenarioWidgetComponentCreator scenarioWidgetComponentCreator,
                        final AsyncPackageDataModelOracle oracle,
                        final Button addButton,
                        final Button removeButton,
                        final ListBox configuredRules,
                        final ListBox configurationType) {
        this.scenarioWidgetComponentCreator = scenarioWidgetComponentCreator;
        this.oracle = oracle;
        this.addButton = addButton;
        this.removeButton = removeButton;
        this.configuredRules = configuredRules;
        this.configurationType = configurationType;

        configuredRules.setMultipleSelect(true);

        addButton.setIcon(IconType.PLUS);
        addButton.setType(ButtonType.PRIMARY);
        addButton.setTitle(TestScenarioConstants.INSTANCE.AddANewRule());

        removeButton.setIcon(IconType.TRASH);
        removeButton.setType(ButtonType.DANGER);
        removeButton.setTitle(TestScenarioConstants.INSTANCE.RemoveSelectedRule());

        configurationType.addItem(TestScenarioConstants.INSTANCE.AllowTheseRulesToFire(),
                                  "inc"); //NON-NLS
        configurationType.addItem(TestScenarioConstants.INSTANCE.PreventTheseRulesFromFiring(),
                                  "exc"); //NON-NLS
        configurationType.addItem(TestScenarioConstants.INSTANCE.AllRulesMayFire());

        final VerticalPanel actions = new VerticalPanel();
        actions.add(addButton);
        actions.add(removeButton);

        add(configurationType);
        add(configuredRules);
        add(actions);
    }

    @PostConstruct
    public void setupHandlers() {
        addButton.addClickHandler(
                event -> {
                    final FormStylePopup pop = new FormStylePopup(TestScenarioAltedImages.INSTANCE.RuleAsset(),
                                                                  TestScenarioConstants.INSTANCE.SelectRule());

                    final Widget ruleSelector = scenarioWidgetComponentCreator.getRuleSelectionWidget(
                            selectedRule -> {
                                scenario.getRules().add(selectedRule);
                                configuredRules.addItem(selectedRule);
                                pop.hide();
                            });

                    pop.addRow(ruleSelector);
                    pop.show();
                }
        );

        removeButton.addClickHandler(
                event -> {
                    if (configuredRules.getSelectedIndex() == -1) {
                        Window.alert(TestScenarioConstants.INSTANCE.PleaseChooseARuleToRemove());
                    } else {
                        final String rule = configuredRules.getItemText(configuredRules.getSelectedIndex());
                        scenario.getRules().remove(rule);
                        configuredRules.removeItem(configuredRules.getSelectedIndex());
                    }
                }
        );

        configurationType.addChangeHandler(
                event -> {
                    final String selectedType = configurationType.getValue(configurationType.getSelectedIndex());
                    if (selectedType.equals("inc")) { //NON-NLS
                        scenario.setInclusive(true);
                        addButton.setVisible(true);
                        removeButton.setVisible(true);
                        configuredRules.setVisible(true);
                    } else if (selectedType.equals("exc")) { //NON-NLS
                        scenario.setInclusive(false);
                        addButton.setVisible(true);
                        removeButton.setVisible(true);
                        configuredRules.setVisible(true);
                    } else {
                        scenario.getRules().clear();
                        configuredRules.clear();
                        configuredRules.setVisible(false);
                        addButton.setVisible(false);
                        removeButton.setVisible(false);
                    }
                }
        );
    }

    public void init(final ScenarioParentWidget scenarioParentWidget, final Path path, final Scenario scenario) {
        this.scenario = scenario;
        scenarioWidgetComponentCreator.reset(scenarioParentWidget, path, oracle, scenario);
    }

    public void show() {
        configuredRules.clear();
        for (int i = 0; i < scenario.getRules().size(); i++) {
            configuredRules.addItem(scenario.getRules().get(i));
        }

        if (scenario.getRules().size() > 0) {
            configurationType.setSelectedIndex((scenario.isInclusive()) ? ALLOWED_INDEX : PREVENTED_INDEX);
            configuredRules.setVisible(true);
            addButton.setVisible(true);
            removeButton.setVisible(true);
        } else {
            configurationType.setSelectedIndex(ALL_ALLOWED_INDEX);
            configuredRules.setVisible(false);
            addButton.setVisible(false);
            removeButton.setVisible(false);
        }
    }
}
