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
package org.drools.workbench.screens.scenariosimulation.client.editor.menu;

import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.PrependRowEvent;
import org.drools.workbench.screens.scenariosimulation.client.models.ScenarioGridModel;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;

/**
 * This is the first <i>ScenarioSimulation</i> specific abstract class - i.e. it is bound to a specific use case. Not every implementation
 * would need this. Menu initialization may be done in other different ways. It is provided to avoid code duplication in concrete implementations
 */
public abstract class AbstractHeaderMenuPresenter extends BaseMenu implements HeaderMenuPresenter {

    protected ScenarioSimulationEditorConstants constants = ScenarioSimulationEditorConstants.INSTANCE;

    protected ScenarioGridModel model;

    protected String HEADERCONTEXTMENU_GRID_TITLE;
    protected String HEADERCONTEXTMENU_PREPEND_ROW;

    /**
     * The <b>Insert row below</b> menu element in the <b>header</b> contextual menu
     */
    protected LIElement insertRowBelowElement;
    protected LIElement gridTitleElement;

    public void setEventBus(EventBus eventBus) {
        this.executableMenuItemPresenter.setEventBus(eventBus);
    }

    /**
     * This method set common <b>SCENARIO</b> menu items
     */
    public void initMenu() {
        // SCENARIO
        gridTitleElement = addMenuItem(HEADERCONTEXTMENU_GRID_TITLE, constants.scenario(), "scenario");
        insertRowBelowElement = addExecutableMenuItem(HEADERCONTEXTMENU_PREPEND_ROW, constants.insertRowBelow(), "insertRowBelow");
    }

    public void show(final GridWidget gridWidget, final int mx, final int my) {
        super.show(mx, my);
        mapEvent(insertRowBelowElement, new PrependRowEvent(gridWidget));
    }
}
