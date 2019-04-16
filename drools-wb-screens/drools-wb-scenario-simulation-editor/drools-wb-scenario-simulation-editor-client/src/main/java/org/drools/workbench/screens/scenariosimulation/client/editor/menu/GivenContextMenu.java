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

/**
 * The contextual menu of a specific <i>GIVEN</i> column
 * It differ from {@link HeaderGivenContextMenu} because it manage column (insert/remove) in different way
 */
@Dependent
public class GivenContextMenu extends AbstractColumnMenuPresenter {

    // This strings are used to give unique id in the final dom
    private final String GIVENCONTEXTMENU_GIVEN = "givencontextmenu-given";
    private final String GIVENCONTEXTMENU_INSERT_COLUMN_LEFT = "givencontextmenu-insert-column-left";
    private final String GIVENCONTEXTMENU_INSERT_COLUMN_RIGHT = "givencontextmenu-insert-column-right";
    private final String GIVENCONTEXTMENU_DELETE_COLUMN = "givencontextmenu-delete-column";
    private final String GIVENCONTEXTMENU_DUPLICATE_INSTANCE = "givencontextmenu-duplicate-instance";

    @PostConstruct
    @Override
    public void initMenu() {
        // GIVEN MENU
        COLUMNCONTEXTMENU_COLUMN = GIVENCONTEXTMENU_GIVEN;
        COLUMNCONTEXTMENU_INSERT_COLUMN_LEFT = GIVENCONTEXTMENU_INSERT_COLUMN_LEFT;
        COLUMNCONTEXTMENU_INSERT_COLUMN_RIGHT = GIVENCONTEXTMENU_INSERT_COLUMN_RIGHT;
        COLUMNCONTEXTMENU_DELETE_COLUMN = GIVENCONTEXTMENU_DELETE_COLUMN;
        COLUMNCONTEXTMENU_DUPLICATE_INSTANCE = GIVENCONTEXTMENU_DUPLICATE_INSTANCE;
        COLUMNCONTEXTMENU_LABEL = constants.given().toUpperCase();
        COLUMNCONTEXTMENU_I18N = "given";
        // SCENARIO MENU
        super.initMenu();
    }
}
