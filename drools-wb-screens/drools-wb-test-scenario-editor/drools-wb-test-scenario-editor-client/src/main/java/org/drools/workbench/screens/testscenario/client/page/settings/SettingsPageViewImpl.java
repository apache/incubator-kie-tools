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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.ScenarioParentWidget;
import org.drools.workbench.screens.testscenario.client.utils.ScenarioUtils;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.backend.vfs.Path;

@Templated
@Dependent
public class SettingsPageViewImpl implements SettingsPage.SettingsPageView {

    private Elemental2DomUtil elemental2DomUtil;

    @DataField("root")
    private HTMLDivElement root;

    @DataField("session-configuration")
    private HTMLDivElement sessionConfigurationDiv;

    @DataField("allowed-rules-configuration")
    private HTMLDivElement allowedRulesConfigurationDiv;

    @DataField("globals-configuration")
    private HTMLDivElement globalsConfigurationDiv;

    @DataField("scorecard-selector")
    private HTMLDivElement scoreCardSelectorDiv;

    private SettingsPage presenter;

    private ScenarioKSessionSelector scenarioKSessionSelector;

    private ConfigWidget configWidget;

    private ScorecardSelector scorecardSelector;
    private ExecutionWidget executionWidget;

    @Inject
    public SettingsPageViewImpl(final HTMLDivElement root,
                                final HTMLDivElement sessionConfigurationDiv,
                                final HTMLDivElement allowedRulesConfigurationDiv,
                                final HTMLDivElement globalsConfigurationDiv,
                                final HTMLDivElement scoreCardSelectorDiv,
                                final Elemental2DomUtil elemental2DomUtil,
                                final ScenarioKSessionSelector scenarioKSessionSelector,
                                final ConfigWidget configWidget,
                                final ScorecardSelector scorecardSelector,
                                final ExecutionWidget executionWidget) {
        this.root = root;
        this.sessionConfigurationDiv = sessionConfigurationDiv;
        this.allowedRulesConfigurationDiv = allowedRulesConfigurationDiv;
        this.globalsConfigurationDiv = globalsConfigurationDiv;
        this.scoreCardSelectorDiv = scoreCardSelectorDiv;
        this.elemental2DomUtil = elemental2DomUtil;
        this.scenarioKSessionSelector = scenarioKSessionSelector;
        this.configWidget = configWidget;
        this.scorecardSelector = scorecardSelector;
        this.executionWidget = executionWidget;

        this.elemental2DomUtil.appendWidgetToElement(sessionConfigurationDiv, scenarioKSessionSelector.asWidget());
        this.elemental2DomUtil.appendWidgetToElement(allowedRulesConfigurationDiv, configWidget.asWidget());
        this.elemental2DomUtil.appendWidgetToElement(globalsConfigurationDiv, executionWidget.asWidget());
        this.elemental2DomUtil.appendWidgetToElement(scoreCardSelectorDiv, scorecardSelector.asWidget());
    }

    @Override
    public HTMLElement getElement() {
        return root;
    }

    @Override
    public void init(SettingsPage presenter) {
        this.presenter = presenter;
    }

    @Override
    public void refresh(final ScenarioParentWidget scenarioParentWidget, final Path path, final Scenario scenario) {
        scenarioKSessionSelector.show(path, scenario);
        configWidget.init(scenarioParentWidget, path, scenario);
        configWidget.show();
        executionWidget.show(ScenarioUtils.findExecutionTrace(scenario));
        scorecardSelector.init(path, scenario);
    }
}
