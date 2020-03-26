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
package org.drools.workbench.screens.scenariosimulation.webapp.client.editor;

import javax.inject.Inject;

import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.KogitoScenarioSimulationBuilder;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.ScenarioSimulationEditorKogitoWrapper;
import org.drools.workbench.screens.scenariosimulation.webapp.client.popup.ScenarioKogitoCreationPopupPresenter;
import org.gwtbootstrap3.client.ui.Popover;
import org.kie.workbench.common.kogito.webapp.base.client.editor.KogitoScreen;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;

/**
 * Abstract class to be extended by concrete <b>ScenarioSimulationEditorKogitoScreen</b>s
 */
public abstract class AbstractScenarioSimulationEditorKogitoScreen implements KogitoScreen {

    public static final String IDENTIFIER = "ScenarioSimulationEditor";
    public static final String TITLE = "Scenario Simulation - Kogito";

    @Inject
    protected ScenarioSimulationEditorKogitoWrapper scenarioSimulationEditorKogitoWrapper;
    @Inject
    protected KogitoScenarioSimulationBuilder scenarioSimulationBuilder;
    @Inject
    protected ScenarioKogitoCreationPopupPresenter scenarioKogitoCreationPopupPresenter;

    /**
     *
     * @param path the path into which the file will be saved
     */
    protected void newFile(Path path) {
        Command createCommand = () -> {
            final ScenarioSimulationModel.Type selectedType = scenarioKogitoCreationPopupPresenter.getSelectedType();
            if (selectedType == null) {
                showPopover("ERROR", "Missing selected type");
                return;
            }
            String value = "";
            if (ScenarioSimulationModel.Type.DMN.equals(selectedType)) {
                value = scenarioKogitoCreationPopupPresenter.getSelectedPath();
                if (value == null || value.isEmpty()) {
                    showPopover("ERROR", "Missing dmn path");
                    return;
                }
            }
            scenarioSimulationBuilder.populateScenarioSimulationModel(new ScenarioSimulationModel(), selectedType, value, content -> {
                saveFile(path, content);
                scenarioSimulationEditorKogitoWrapper.gotoPath(path);
                scenarioSimulationEditorKogitoWrapper.setContent(path.toURI() + path.getFileName(), content);
            });
        };
        scenarioKogitoCreationPopupPresenter.show(ScenarioSimulationEditorConstants.INSTANCE.addScenarioSimulation(),
                                                  createCommand);
    }

    protected void showPopover(String title, String content) {
        new Popover(title, content).show();
    }

    protected void saveFile(final Path path, final String content) {
        // TO BE OVERRIDDEN
    }


}
