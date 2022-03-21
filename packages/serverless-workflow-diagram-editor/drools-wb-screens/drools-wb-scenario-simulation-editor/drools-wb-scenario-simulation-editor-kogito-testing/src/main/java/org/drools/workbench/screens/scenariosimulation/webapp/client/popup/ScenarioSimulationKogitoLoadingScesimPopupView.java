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
package org.drools.workbench.screens.scenariosimulation.webapp.client.popup;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.popup.AbstractScenarioPopupView;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.kogito.client.popup.ScenarioSimulationKogitoCreationPopup;
import org.drools.workbench.screens.scenariosimulation.webapp.client.dropdown.ScenarioSimulationKogitoLoadingScesimDropdown;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class ScenarioSimulationKogitoLoadingScesimPopupView extends AbstractScenarioPopupView implements ScenarioSimulationKogitoCreationPopup {

    @Inject
    @DataField("assets")
    protected HTMLDivElement divElement;

    protected ScenarioSimulationModel.Type selectedType = null;

    protected String selectedPath = null;

    @Inject
    protected ScenarioSimulationKogitoLoadingScesimDropdown scenarioSimulationKogitoLoadingScesimDropdown;

    @Override
    public void show(String mainTitleText, Command okCommand) {
        cancelButton.setText(ScenarioSimulationEditorConstants.INSTANCE.cancelButton());
        divElement.appendChild(scenarioSimulationKogitoLoadingScesimDropdown.getElement());
        scenarioSimulationKogitoLoadingScesimDropdown.clear();
        scenarioSimulationKogitoLoadingScesimDropdown.init();
        scenarioSimulationKogitoLoadingScesimDropdown.initializeDropdown();
        scenarioSimulationKogitoLoadingScesimDropdown.registerOnChangeHandler(() -> {
            final Optional<KieAssetsDropdownItem> value = scenarioSimulationKogitoLoadingScesimDropdown.getValue();
            selectedPath = value.map(KieAssetsDropdownItem::getValue).orElse(null);
        });
        super.show(mainTitleText, ScenarioSimulationEditorConstants.INSTANCE.importLabel(), okCommand);
    }

    @Override
    public ScenarioSimulationModel.Type getSelectedType() {
        return selectedType;
    }

    @Override
    public String getSelectedPath() {
        return selectedPath;
    }

}
