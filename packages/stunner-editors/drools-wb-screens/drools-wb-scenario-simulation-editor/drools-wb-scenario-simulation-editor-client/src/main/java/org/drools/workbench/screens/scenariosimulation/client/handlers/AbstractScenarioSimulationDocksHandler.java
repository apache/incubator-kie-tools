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
package org.drools.workbench.screens.scenariosimulation.client.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.CheatSheetView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.SettingsView;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsPresenter;
import org.drools.workbench.screens.scenariosimulation.client.rightpanel.TestToolsView;
import org.kie.workbench.common.widgets.client.docks.AbstractWorkbenchDocksHandler;
import org.kie.workbench.common.widgets.client.docks.AuthoringEditorDock;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

/**
 * Abstract Handler used to register docks in ScenarioSimulation. Subclasses have to be defined in
 * Business Cental and Kogito Contest
 */
public abstract class AbstractScenarioSimulationDocksHandler extends AbstractWorkbenchDocksHandler {

    public static final String SCESIMEDITOR_ID = "scesimeditorid";

    @Inject
    protected AuthoringEditorDock authoringWorkbenchDocks;
    @Inject
    protected TestToolsPresenter testToolsPresenter;
    @Inject
    protected SettingsPresenter settingsPresenter;
    @Inject
    protected CheatSheetPresenter cheatSheetPresenter;

    private UberfireDock settingsDock;
    private UberfireDock toolsDock;
    private UberfireDock cheatSheetDock;
    private String currentScesimEditorId;

    @Override
    public Collection<UberfireDock> provideDocks(final String perspectiveIdentifier) {
        List<UberfireDock> result = new ArrayList<>();
        settingsDock = new UberfireDock(UberfireDockPosition.EAST,
                                        "SLIDERS",
                                        new DefaultPlaceRequest(SettingsPresenter.IDENTIFIER)
                                        //perspectiveIdentifier
                                        );
        result.add(settingsDock.withSize(450).withLabel(ScenarioSimulationEditorConstants.INSTANCE.settings()));
        toolsDock = new UberfireDock(UberfireDockPosition.EAST,
                                     "INFO_CIRCLE",
                                     new DefaultPlaceRequest(TestToolsPresenter.IDENTIFIER)
                                     //perspectiveIdentifier
        );
        result.add(toolsDock.withSize(450).withLabel(ScenarioSimulationEditorConstants.INSTANCE.testTools()));
        cheatSheetDock = new UberfireDock(UberfireDockPosition.EAST,
                                          "FILE_TEXT",
                                          new DefaultPlaceRequest(CheatSheetPresenter.IDENTIFIER)
                                          //perspectiveIdentifier
        );
        result.add(cheatSheetDock.withSize(450).withLabel(ScenarioSimulationEditorConstants.INSTANCE.scenarioCheatSheet()));
        return result;
    }

    public void addDocks() {
        refreshDocks(true, false);
    }

    public void removeDocks() {
        refreshDocks(true, true);
    }

    /* Indirection for tests */
    @Override
    protected void refreshDocks(boolean shouldRefresh, boolean shouldDisable) {
        super.refreshDocks(shouldRefresh, shouldDisable);
    }

    public void expandToolsDock() {
        if (!testToolsPresenter.isOpen()) {
            authoringWorkbenchDocks.expandAuthoringDock(toolsDock);
        }
    }

    public void expandSettingsDock() {
        if (!settingsPresenter.isOpen()) {
            authoringWorkbenchDocks.expandAuthoringDock(settingsDock);
        }
    }

    public abstract void expandTestResultsDock();

    public void resetDocks() {
        settingsPresenter.reset();
        cheatSheetPresenter.reset();
        testToolsPresenter.reset();
    }

    public void setScesimEditorId(String scesimEditorId) {
        currentScesimEditorId = scesimEditorId;
        settingsDock.getPlaceRequest().addParameter(SCESIMEDITOR_ID, scesimEditorId);
        toolsDock.getPlaceRequest().addParameter(SCESIMEDITOR_ID, scesimEditorId);
        cheatSheetDock.getPlaceRequest().addParameter(SCESIMEDITOR_ID, scesimEditorId);
    }

    public CheatSheetView.Presenter getCheatSheetPresenter() {
        return cheatSheetPresenter;
    }

    public TestToolsView.Presenter getTestToolsPresenter() {
        return testToolsPresenter;
    }

    public SettingsView.Presenter getSettingsPresenter() {
        return settingsPresenter;
    }

    protected PlaceRequest getSettingsPlaceManager() {
        return getCurrentRightDockPlaceRequest(SettingsPresenter.IDENTIFIER);
    }

    protected PlaceRequest getTestToolsPlaceManager() {
        return getCurrentRightDockPlaceRequest(TestToolsPresenter.IDENTIFIER);
    }

    /**
     * Returns a <code>PlaceRequest</code> for the <b>status</b> of the right dock with the given <b>identifier</b>
     * relative to the current instance of <code>ScenarioSimulationEditorPresenter</code>
     * @return A <code>PlaceRequest</code> for the <b>status</b> of the requested right dock
     */
    protected PlaceRequest getCurrentRightDockPlaceRequest(String identifier) {
        PlaceRequest toReturn = new DefaultPlaceRequest(identifier);
        toReturn.addParameter(SCESIMEDITOR_ID, String.valueOf(currentScesimEditorId));
        return toReturn;
    }
}
