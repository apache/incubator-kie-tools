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

import elemental2.promise.Promise;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.ScenarioSimulationEditorKogitoWrapper;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.EditorActivity;
import org.uberfire.mvp.PlaceRequest;

/**
 * Abstract class to be extended by concrete <b>ScenarioSimulationEditorKogitoScreen</b>s
 */
public abstract class AbstractScenarioSimulationEditorKogitoScreen extends AbstractActivity implements EditorActivity {

    public static final String TITLE = "Scenario Simulation - Kogito";

    @Inject
    protected ScenarioSimulationEditorKogitoWrapper scenarioSimulationEditorKogitoWrapper;

    @Override
    public void onStartup(final PlaceRequest place) {
        super.onStartup(place);
        scenarioSimulationEditorKogitoWrapper.onStartup(place);
    }

    public boolean mayClose() {
        return scenarioSimulationEditorKogitoWrapper.mayClose();
    }

    public String getTitleText() {
        return TITLE;
    }

    public MultiPageEditorContainerView getWidget() {
        return scenarioSimulationEditorKogitoWrapper.getWidget();
    }


    public Promise<String> getContent() {
        return scenarioSimulationEditorKogitoWrapper.getContent();
    }

    public Promise<Void> setContent(String fullPath, String value) {
        return scenarioSimulationEditorKogitoWrapper.setContent(fullPath, value);
    }

    public boolean isDirty() {
        return false;
    }

}
