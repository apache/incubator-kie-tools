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

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.LIElement;
import org.drools.workbench.screens.scenariosimulation.client.editor.menu.GridContextMenu;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.RunSingleScenarioEvent;

@Dependent
public class BusinessCentralGridContextMenu extends GridContextMenu {

    protected static final String GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO = "gridcontextmenu-run-single-scenario";

    protected LIElement runSingleScenarioElement;

    @PostConstruct
    @Override
    public void initMenu() {
        super.initMenu();
        runSingleScenarioElement = addExecutableMenuItem(GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO, constants.runSingleScenario(), "runSingleScenario");
    }

    @Override
    public void show(GridWidget gridWidget, int mx, int my, int rowIndex) {
        super.show(gridWidget, mx, my, rowIndex);
        if (Objects.equals(GridWidget.BACKGROUND, gridWidget) && runSingleScenarioElement != null) {
            removeMenuItem(runSingleScenarioElement);
            runSingleScenarioElement = null;
        } else if (Objects.equals(GridWidget.SIMULATION, gridWidget)) {
            if (runSingleScenarioElement == null) {
                runSingleScenarioElement = addExecutableMenuItem(GRIDCONTEXTMENU_RUN_SINGLE_SCENARIO, constants.runSingleScenario(), "runSingleScenario");
            }
            mapEvent(runSingleScenarioElement, new RunSingleScenarioEvent(rowIndex));
        }
    }
}
