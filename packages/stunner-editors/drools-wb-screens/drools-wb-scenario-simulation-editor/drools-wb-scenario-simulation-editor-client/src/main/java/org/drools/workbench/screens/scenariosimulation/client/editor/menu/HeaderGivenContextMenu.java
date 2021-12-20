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

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.enums.GridWidget;
import org.drools.workbench.screens.scenariosimulation.client.events.AppendColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.PrependColumnEvent;

/**
 * The contextual menu of the top level <i>GIVEN</i> group.
 * It differ from <code>GivenContextMenu</code> because it manage column (insert/remove) in different way
 */
@Dependent
public class HeaderGivenContextMenu extends AbstractHeaderGroupMenuPresenter {

    protected static final String HEADERGIVENCONTEXTMENU_GIVEN = "headergivencontextmenu-given";
    protected static final String HEADERGIVENCONTEXTMENU_GRID_TITLE = "headergivencontextmenu-grid-title";
    protected static final String HEADERGIVENCONTEXTMENU_INSERT_COLUMN_LEFT = "headergivencontextmenu-insert-column-left";
    protected static final String HEADERGIVENCONTEXTMENU_INSERT_COLUMN_RIGHT = "headergivencontextmenu-insert-column-right";
    protected static final String HEADERGIVENCONTEXTMENU_DELETE_COLUMN = "headergivencontextmenu-delete-column";
    protected static final String HEADERGIVENCONTEXTMENU_INSERT_ROW_ABOVE = "headergivencontextmenu-insert-row-above";

    @PostConstruct
    @Override
    public void initMenu() {
        HEADERCONTEXTMENU_GROUP = HEADERGIVENCONTEXTMENU_GIVEN;
        HEADERCONTEXTMENU_GRID_TITLE = HEADERGIVENCONTEXTMENU_GRID_TITLE;
        HEADERCONTEXTMENU_INSERT_COLUMN_LEFT = HEADERGIVENCONTEXTMENU_INSERT_COLUMN_LEFT;
        HEADERCONTEXTMENU_INSERT_COLUMN_RIGHT = HEADERGIVENCONTEXTMENU_INSERT_COLUMN_RIGHT;
        HEADERCONTEXTMENU_DELETE_COLUMN = HEADERGIVENCONTEXTMENU_DELETE_COLUMN;
        HEADERCONTEXTMENU_PREPEND_ROW = HEADERGIVENCONTEXTMENU_INSERT_ROW_ABOVE;
        HEADERCONTEXTMENU_LABEL = constants.given().toUpperCase();
        HEADERCONTEXTMENU_I18N = "given";
        super.initMenu();
    }

    @Override
    public void show(final GridWidget gridWidget, int mx, int my) {
        super.show(gridWidget, mx, my);
        if (Objects.equals(GridWidget.BACKGROUND, gridWidget)) {
            updateMenuItemAttributes(gridTitleElement , HEADERGIVENCONTEXTMENU_GRID_TITLE, constants.background(), "background");
        } else if (Objects.equals(GridWidget.SIMULATION, gridWidget)) {
            updateMenuItemAttributes(gridTitleElement , HEADERGIVENCONTEXTMENU_GRID_TITLE, constants.scenario(), "scenario");
        }
        mapEvent(appendColumnElement, new AppendColumnEvent(gridWidget, "GIVEN"));
        mapEvent(prependColumnElement, new PrependColumnEvent(gridWidget, "GIVEN"));
    }

}
