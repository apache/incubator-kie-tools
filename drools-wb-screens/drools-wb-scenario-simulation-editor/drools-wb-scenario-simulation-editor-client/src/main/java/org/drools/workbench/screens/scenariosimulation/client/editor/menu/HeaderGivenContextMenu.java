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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.scenariosimulation.client.events.AppendColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.PrependColumnEvent;

/**
 * The contextual menu of the top level <i>GIVEN</i> group.
 * It differ from <code>GivenContextMenu</code> because it manage column (insert/remove) in different way
 */
@Dependent
public class HeaderGivenContextMenu extends AbstractHeaderGroupMenuPresenter {

    private final String HEADERGIVENCONTEXTMENU_GIVEN = "headergivencontextmenu-given";
    private final String HEADERGIVENCONTEXTMENU_SCENARIO = "headergivencontextmenu-scenario";
    private final String HEADERGIVENCONTEXTMENU_INSERT_COLUMN_LEFT = "headergivencontextmenu-insert-column-left";
    private final String HEADERGIVENCONTEXTMENU_INSERT_COLUMN_RIGHT = "headergivencontextmenu-insert-column-right";
    private final String HEADERGIVENCONTEXTMENU_DELETE_COLUMN = "headergivencontextmenu-delete-column";
    private final String HEADERGIVENCONTEXTMENU_INSERT_ROW_ABOVE = "headergivencontextmenu-insert-row-above";


    @PostConstruct
    @Override
    public void initMenu() {
        HEADERCONTEXTMENU_GROUP = HEADERGIVENCONTEXTMENU_GIVEN;
        HEADERCONTEXTMENU_SCENARIO = HEADERGIVENCONTEXTMENU_SCENARIO;
        HEADERCONTEXTMENU_INSERT_COLUMN_LEFT = HEADERGIVENCONTEXTMENU_INSERT_COLUMN_LEFT;
        HEADERCONTEXTMENU_INSERT_COLUMN_RIGHT = HEADERGIVENCONTEXTMENU_INSERT_COLUMN_RIGHT;
        HEADERCONTEXTMENU_DELETE_COLUMN = HEADERGIVENCONTEXTMENU_DELETE_COLUMN;
        HEADERCONTEXTMENU_PREPEND_ROW = HEADERGIVENCONTEXTMENU_INSERT_ROW_ABOVE;
        HEADERCONTEXTMENU_LABEL = constants.given().toUpperCase();
        HEADERCONTEXTMENU_I18N = "given";
        appendColumnEvent = new AppendColumnEvent("GIVEN");
        prependColumnEvent = new PrependColumnEvent("GIVEN");
        super.initMenu();
    }
}
