/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.popup;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.popup.AbstractScenarioPopupView;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dropdown.ScenarioKogitoCreationAssetsDropdown;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class ScenarioKogitoCreationPopupView extends AbstractScenarioPopupView implements ScenarioKogitoCreationPopup {

    @Inject
    @DataField("rule-button")
    protected HTMLInputElement ruleButton;

    @Inject
    @DataField("dmn-button")
    protected HTMLInputElement dmnButton;

    @Inject
    @DataField("dmn-assets")
    protected HTMLDivElement dmnAssetsDivElement;

    @Inject
    protected HTMLLabelElement dmnAssetsLabelElement;

    @Inject
    @DataField("source-type-label")
    protected HTMLLabelElement sourceTypeLabelElement;

    @Inject
    @DataField("info-rule-icon")
    protected HTMLDivElement infoRuleIconDivElement;

    protected ScenarioSimulationModel.Type selectedType = null;

    protected String selectedPath = null;

    @Inject
    protected ScenarioKogitoCreationAssetsDropdown scenarioKogitoCreationAssetsDropdown;

    @Override
    public void show(String mainTitleText, Command okCommand) {
        initialize();
        super.show(mainTitleText, ScenarioSimulationEditorConstants.INSTANCE.createButton(), okCommand);
    }

    protected void initialize() {
        okButton.setEnabled(false);
        cancelButton.setText(ScenarioSimulationEditorConstants.INSTANCE.cancelButton());
        dmnAssetsDivElement.setAttribute(ConstantHolder.HIDDEN, "");
        sourceTypeLabelElement.textContent = ScenarioSimulationEditorConstants.INSTANCE.sourceType();
        ruleButton.checked = false;
        dmnButton.checked = false;
        dmnAssetsLabelElement.textContent = ScenarioSimulationEditorConstants.INSTANCE.chooseValidDMNAsset();
        dmnAssetsDivElement.appendChild(dmnAssetsLabelElement);
        infoRuleIconDivElement.setAttribute("title", ScenarioSimulationEditorConstants.INSTANCE.currentlyNotAvailable());
        initializeDropdown();
    }

    @Override
    public ScenarioSimulationModel.Type getSelectedType() {
        return selectedType;
    }

    @Override
    public String getSelectedPath() {
        return selectedPath;
    }

    @EventHandler("dmn-button")
    public void onDmnClick(final ClickEvent event) {
        if (dmnButton.checked) {
            selectedType = ScenarioSimulationModel.Type.DMN;
            dmnAssetsDivElement.removeAttribute(ConstantHolder.HIDDEN);
            enableCreateButtonForDMNScenario();
        }
    }

    @EventHandler("rule-button")
    public void onRuleClick(final ClickEvent event) {
        if (ruleButton.checked) {
            selectedType = ScenarioSimulationModel.Type.RULE;
            dmnAssetsDivElement.setAttribute(ConstantHolder.HIDDEN, "");
            okButton.setEnabled(true);
        }
    }

    protected void initializeDropdown() {
        dmnAssetsDivElement.appendChild(scenarioKogitoCreationAssetsDropdown.getElement());
        scenarioKogitoCreationAssetsDropdown.clear();
        scenarioKogitoCreationAssetsDropdown.init();
        scenarioKogitoCreationAssetsDropdown.initializeDropdown();
        scenarioKogitoCreationAssetsDropdown.registerOnChangeHandler(() -> {
            final Optional<KieAssetsDropdownItem> value = scenarioKogitoCreationAssetsDropdown.getValue();
            selectedPath = value.map(KieAssetsDropdownItem::getValue).orElse(null);
            enableCreateButtonForDMNScenario();
        });
    }

    protected void enableCreateButtonForDMNScenario() {
        if (selectedPath != null && !selectedPath.isEmpty()) {
            okButton.setEnabled(true);
        }
    }
}
