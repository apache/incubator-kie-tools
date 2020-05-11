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
package org.drools.workbench.screens.scenariosimulation.businesscentral.client.menu;

import com.google.gwt.dom.client.LIElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.Event;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenuTest;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.RunSingleScenarioEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.businesscentral.client.menu.BusinessCentralGridContextMenu.GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class BusinessCentralGridContextMenuTest extends GridContextMenuTest {

    @Mock
    private LIElement runSingleScenarioElementMock;

    private BusinessCentralGridContextMenu businessCentralGridContextMenuSpy;

    @Before
    public void setup() {
        super.setup();
        businessCentralGridContextMenuSpy = spy(new BusinessCentralGridContextMenu() {

            {
                this.insertRowAboveLIElement = insertRowAboveLIElementMock;
                this.insertRowBelowLIElement= insertRowBelowLIElementMock;
                this.duplicateRowLIElement = duplicateRowLIElementMock;
                this.deleteRowLIElement = deleteRowLIElementMock;
                this.runSingleScenarioElement = runSingleScenarioElementMock;
                this.gridTitleElement = gridTitleElementMock;
            }

            @Override
            public LIElement addExecutableMenuItem(String id, String label, String i18n) {
                return createdElementMock;
            }

            @Override
            public void mapEvent(LIElement executableMenuItem, Event toBeMapped) {
                //Do nothing
            }

            @Override
            protected void updateExecutableMenuItemAttributes(LIElement toUpdate, String id, String label, String i18n) {
                //Do nothing
            }

            @Override
            public LIElement addMenuItem(String id, String label, String i18n) {
                return createdElementMock;
            }

            @Override
            public void removeMenuItem(LIElement toRemove) {
                //Do nothing
            }

            @Override
            public void show(GridWidget gridWidget, int mx, int my) {
                //Do nothing
            }
        });
    }

    @Test
    public void initMenu() {
        super.initMenu(businessCentralGridContextMenuSpy);
        verify(businessCentralGridContextMenuSpy, times(1)).addExecutableMenuItem(eq(GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO), eq(ScenarioSimulationEditorConstants.INSTANCE.runSingleScenario()), eq("runSingleScenario"));
    }

    @Test
    public void show_Simulation_NullRunScenarioElement() {
        businessCentralGridContextMenuSpy.runSingleScenarioElement = null;
        super.show(businessCentralGridContextMenuSpy, GridWidget.SIMULATION, ScenarioSimulationEditorConstants.INSTANCE.scenario(), "scenario",0, 0, 1);
        verify(businessCentralGridContextMenuSpy, times(1)).addExecutableMenuItem(eq(GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO), eq(ScenarioSimulationEditorConstants.INSTANCE.runSingleScenario()), eq("runSingleScenario"));
        verify(businessCentralGridContextMenuSpy, times(1)).mapEvent(eq(createdElementMock), isA(RunSingleScenarioEvent.class));
    }

    @Test
    public void show_Simulation_NotNullRunScenarioElement() {
        super.show(businessCentralGridContextMenuSpy, GridWidget.SIMULATION, ScenarioSimulationEditorConstants.INSTANCE.scenario(), "scenario", 0, 0, 1);
        verify(businessCentralGridContextMenuSpy, never()).addExecutableMenuItem(eq(GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO), eq(ScenarioSimulationEditorConstants.INSTANCE.runSingleScenario()), eq("runSingleScenario"));
        verify(businessCentralGridContextMenuSpy, times(1)).mapEvent(eq(runSingleScenarioElementMock), isA(RunSingleScenarioEvent.class));
    }

    @Test
    public void show_Background_NullRunScenarioElement() {
        businessCentralGridContextMenuSpy.runSingleScenarioElement = null;
        super.show(businessCentralGridContextMenuSpy, GridWidget.BACKGROUND, ScenarioSimulationEditorConstants.INSTANCE.background(), "background", 0, 0, 1);
        verify(businessCentralGridContextMenuSpy, never()).addExecutableMenuItem(eq(GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO), eq(ScenarioSimulationEditorConstants.INSTANCE.runSingleScenario()), eq("runSingleScenario"));
        verify(businessCentralGridContextMenuSpy, never()).mapEvent(eq(createdElementMock), isA(RunSingleScenarioEvent.class));
    }

    @Test
    public void show_Background_NotNullRunScenarioElement() {
        super.show(businessCentralGridContextMenuSpy, GridWidget.BACKGROUND, ScenarioSimulationEditorConstants.INSTANCE.background(), "background", 0, 0, 1);
        verify(businessCentralGridContextMenuSpy, never()).addExecutableMenuItem(eq(GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO), eq(ScenarioSimulationEditorConstants.INSTANCE.runSingleScenario()), eq("runSingleScenario"));
        verify(businessCentralGridContextMenuSpy, never()).mapEvent(eq(createdElementMock), isA(RunSingleScenarioEvent.class));
    }
}
