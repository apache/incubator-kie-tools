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

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.promise.Promise;
import org.drools.workbench.screens.scenariosimulation.kogito.client.editor.ScenarioSimulationEditorKogitoWrapper;
import org.kie.workbench.common.kogito.client.editor.MultiPageEditorContainerView;
import org.kie.workbench.common.kogito.webapp.base.client.editor.KogitoScreen;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.lifecycle.GetContent;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.lifecycle.SetContent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

/**
 * Abstract class to be extended by concrete <b>ScenarioSimulationEditorKogitoScreen</b>s
 */
public abstract class AbstractScenarioSimulationEditorKogitoScreen implements KogitoScreen {

    public static final String TITLE = "Scenario Simulation - Kogito";

    @Inject
    protected ScenarioSimulationEditorKogitoWrapper scenarioSimulationEditorKogitoWrapper;

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
    public Promise setContent(String fullPath, String value) {
        return scenarioSimulationEditorKogitoWrapper.setContent(fullPath, value);
    }

    @IsDirty
    public boolean isDirty() {
        return scenarioSimulationEditorKogitoWrapper.isDirty();
    }

}
