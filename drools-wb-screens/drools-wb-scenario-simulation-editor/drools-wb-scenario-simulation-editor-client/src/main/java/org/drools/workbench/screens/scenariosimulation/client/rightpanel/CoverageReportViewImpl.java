/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Composite;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.drools.scenariosimulation.api.model.ScenarioSimulationModel.Type;
import static org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder.HIDDEN;

@ApplicationScoped
@Templated(stylesheet = "/org/drools/workbench/screens/scenariosimulation/client/resources/css/ScenarioSimulationEditorStyles.css")
public class CoverageReportViewImpl
        extends Composite
        implements CoverageReportView {

    protected Presenter presenter;

    @DataField
    protected HTMLElement reportAvailableLabel = (HTMLElement) DomGlobal.document.createElement("dt");

    @DataField
    protected HTMLElement reportAvailable = (HTMLElement) DomGlobal.document.createElement("dd");

    @DataField
    protected HTMLElement reportExecutedLabel = (HTMLElement) DomGlobal.document.createElement("dt");

    @DataField
    protected HTMLElement reportExecuted = (HTMLElement) DomGlobal.document.createElement("dd");

    @DataField
    protected HTMLElement reportCoverageLabel = (HTMLElement) DomGlobal.document.createElement("dt");

    @DataField
    protected HTMLElement reportCoverage = (HTMLElement) DomGlobal.document.createElement("dd");

    @DataField
    protected HTMLElement list = (HTMLElement) DomGlobal.document.createElement("dl");

    @DataField
    protected HTMLDivElement donutChart = (HTMLDivElement) DomGlobal.document.createElement("div");

    @DataField
    protected HTMLUListElement scenarioList = (HTMLUListElement) DomGlobal.document.createElement("ul");

    @DataField
    protected HTMLDivElement emptyStatus = (HTMLDivElement) DomGlobal.document.createElement("div");

    @DataField
    protected HTMLDivElement emptyStatusText = (HTMLDivElement) DomGlobal.document.createElement("div");

    @DataField
    protected HTMLDivElement summarySection = (HTMLDivElement) DomGlobal.document.createElement("div");

    @DataField
    protected HTMLDivElement listSection = (HTMLDivElement) DomGlobal.document.createElement("div");

    @DataField
    protected HTMLDivElement scenarioListSection = (HTMLDivElement) DomGlobal.document.createElement("div");

    @DataField
    protected HTMLElement numberOfTimesElementEvaluated = (HTMLElement) DomGlobal.document.createElement("span");

    @Override
    public void initText(Type type) {
        if (Type.DMN.equals(type)) {
            reportAvailableLabel.textContent = ScenarioSimulationEditorConstants.INSTANCE.reportAvailableLabel();
            reportExecutedLabel.textContent = ScenarioSimulationEditorConstants.INSTANCE.reportExecutedLabel();
            reportCoverageLabel.textContent = ScenarioSimulationEditorConstants.INSTANCE.reportCoverageLabel();
            numberOfTimesElementEvaluated.textContent = ScenarioSimulationEditorConstants.INSTANCE.numberOfTimesDecisionEvaluated();
        } else {
            reportAvailableLabel.textContent = ScenarioSimulationEditorConstants.INSTANCE.reportAvailableRuleLabel();
            reportExecutedLabel.textContent = ScenarioSimulationEditorConstants.INSTANCE.reportExecutedRuleLabel();
            reportCoverageLabel.textContent = ScenarioSimulationEditorConstants.INSTANCE.reportCoverageRuleLabel();
            numberOfTimesElementEvaluated.textContent = ScenarioSimulationEditorConstants.INSTANCE.numberOfTimesRulesFired();
        }
    }

    @Override
    public Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void reset() {
        emptyStatusText.textContent = "";
        hide();
    }

    @Override
    public void hide() {
        emptyStatus.classList.remove(HIDDEN);
        summarySection.classList.add(HIDDEN);
        listSection.classList.add(HIDDEN);
        scenarioListSection.classList.add(HIDDEN);
    }

    @Override
    public void show() {
        emptyStatus.classList.add(HIDDEN);
        summarySection.classList.remove(HIDDEN);
        listSection.classList.remove(HIDDEN);
        scenarioListSection.classList.remove(HIDDEN);
    }

    @Override
    public void setReportAvailable(String value) {
        this.reportAvailable.textContent = value;
    }

    @Override
    public void setReportExecuted(String value) {
        this.reportExecuted.textContent = value;
    }

    @Override
    public void setReportCoverage(String value) {
        this.reportCoverage.textContent = value;
    }

    @Override
    public void setEmptyStatusText(String value) {
        this.emptyStatusText.textContent = value;
    }

    @Override
    public HTMLElement getList() {
        return list;
    }

    @Override
    public HTMLDivElement getDonutChart() {
        return donutChart;
    }

    @Override
    public HTMLUListElement getScenarioList() {
        return scenarioList;
    }
}
