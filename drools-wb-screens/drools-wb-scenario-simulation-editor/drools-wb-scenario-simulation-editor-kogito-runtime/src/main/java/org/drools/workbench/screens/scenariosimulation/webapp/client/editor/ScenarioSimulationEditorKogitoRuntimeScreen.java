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

import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.annotations.WorkbenchClientEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.GetContent;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.lifecycle.SetContent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.drools.workbench.screens.scenariosimulation.webapp.client.editor.ScenarioSimulationEditorKogitoRuntimeScreen.IDENTIFIER;

/**
 * It represents the KogitoScreen implementation for Scenario Simulation runtime. In other words, this is the editor
 * entry point. Please note, the <code>IDENTIFIED</code> is the unique key to refer this editor. Please use the same
 * (eg in kogito-tooling project) to include it in external project or when calling .setContent method (refer to readme.md
 * for further information */
@ApplicationScoped
@WorkbenchClientEditor(identifier = IDENTIFIER)
public class ScenarioSimulationEditorKogitoRuntimeScreen extends AbstractScenarioSimulationEditorKogitoScreen {

    protected static final String NEW_FILE_NAME = "new-file.scesim";
    protected static final PlaceRequest SCENARIO_SIMULATION_KOGITO_RUNTIME_SCREEN_DEFAULT_REQUEST = new DefaultPlaceRequest(IDENTIFIER);

    protected PlaceManager placeManager;

    public ScenarioSimulationEditorKogitoRuntimeScreen() {
        //CDI proxy
    }

    @Inject
    public ScenarioSimulationEditorKogitoRuntimeScreen(final PlaceManager placeManager) {
        this.placeManager = placeManager;
    }

    @Override
    public PlaceRequest getPlaceRequest() {
        return SCENARIO_SIMULATION_KOGITO_RUNTIME_SCREEN_DEFAULT_REQUEST;
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        scenarioSimulationEditorKogitoWrapper.onStartup(place);
    }

    @OnMayClose
    public boolean mayClose() {
        return scenarioSimulationEditorKogitoWrapper.mayClose();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return TITLE;
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return scenarioSimulationEditorKogitoWrapper.getTitle();
    }

    @WorkbenchPartView
    public MultiPageEditorContainerView getWidget() {
        return scenarioSimulationEditorKogitoWrapper.getWidget();
    }

    @WorkbenchMenu
    public void setMenus(final Consumer<Menus> menusConsumer) {
        scenarioSimulationEditorKogitoWrapper.setMenus(menusConsumer);
    }

    @GetContent
    public Promise getContent() {
        return scenarioSimulationEditorKogitoWrapper.getContent();
    }

    @SetContent
    public void setContent(String fullPath, String value) {
        /* Retrieving file name and its related path */
        String finalName = NEW_FILE_NAME;
        String pathString = "/";
        if (fullPath != null && !fullPath.isEmpty()) {
            int idx = fullPath.replaceAll("\\\\", "/").lastIndexOf('/');
            finalName = idx >= 0 ? fullPath.substring(idx + 1) : fullPath;
            pathString = idx >= 0 ? fullPath.substring(0, idx + 1) : pathString;
        }
        final Path path = PathFactory.newPath(finalName, pathString);

        if (value == null || value.isEmpty()) {
            newFile(path);
        } else {
            scenarioSimulationEditorKogitoWrapper.gotoPath(path);
            scenarioSimulationEditorKogitoWrapper.setContent(path.toURI() + path.getFileName(), value);
            scenarioKogitoCreationPopupPresenter.hide();
        }
    }

    @IsDirty
    public boolean isDirty() {
        return scenarioSimulationEditorKogitoWrapper.isDirty();
    }

}
