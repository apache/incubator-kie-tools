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
package org.drools.workbench.screens.scenariosimulation.client.dropdown;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.uberfire.mvp.Command;

@Dependent
@Named(SettingsScenarioSimulationDropdown.BEAN_NAME)
public class SettingsScenarioSimulationDropdown extends ScenarioSimulationDropdown {

    public static final String BEAN_NAME = "SettingsDropdown";

    protected String currentValue;
    protected Command onMissingValueHandler = () -> {/*Nothing/*/};

    @Inject
    public SettingsScenarioSimulationDropdown(@Named(SettingsScenarioSimulationDropdownView.BEAN_NAME) SettingsScenarioSimulationDropdownView view,
                                              ScenarioSimulationAssetsDropdownProvider dataProvider) {
        super(view, dataProvider);
    }

    /**
     * It loads the DMN assets and it sets currentValue variable.
     * @param currentValue
     */
    public void loadAssets(String currentValue) {
        Optional<KieAssetsDropdownItem> value = getValue();
        if (!value.isPresent() || !Objects.equals(value.get().getValue(), currentValue)) {
            this.currentValue = currentValue;
            super.loadAssets();
        }
    }

    public void registerOnMissingValueHandler(final Command onMissingValueHandler) {
        this.onMissingValueHandler = onMissingValueHandler;
    }

    @Override
    protected void assetListConsumerMethod(final List<KieAssetsDropdownItem> assetList) {
        assetList.forEach(this::addValue);
        view.refreshSelectPicker();
        if (isValuePresentInKieAssets(currentValue)) {
            ((SettingsScenarioSimulationDropdownView) view).initialize(currentValue);
        } else {
            view.initialize();
            onMissingValueHandler.execute();
        }
        this.currentValue = null;
    }

    /**
     * It navigate over the kieAssets list to check if a value is present.
     * @param value
     * @return
     */
    protected boolean isValuePresentInKieAssets(String value) {
        return kieAssets.stream().anyMatch(asset -> asset.getValue().equals(value));
    }
}