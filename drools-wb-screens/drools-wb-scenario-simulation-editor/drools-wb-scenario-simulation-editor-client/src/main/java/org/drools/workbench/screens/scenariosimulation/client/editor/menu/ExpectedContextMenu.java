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
 * The contextual menu of a specific <i>EXPECT</i> column
 * It differ from {@link HeaderExpectedContextMenu} because it manage column (insert/remove) in different way
 */
@Dependent
public class ExpectedContextMenu extends AbstractColumnMenuPresenter {

    // This strings are used to give unique id in the final dom
    private final String EXPECTCONTEXTMENU_EXPECT = "expectcontextmenu-expect";
    private final String EXPECTCONTEXTMENU_SCENARIO = "expectcontextmenu-scenario";
    private final String EXPECTCONTEXTMENU_INSERT_COLUMN_LEFT = "expectcontextmenu-insert-column-left";
    private final String EXPECTCONTEXTMENU_INSERT_COLUMN_RIGHT = "expectcontextmenu-insert-column-right";
    private final String EXPECTCONTEXTMENU_DELETE_COLUMN = "expectcontextmenu-delete-column";
    private final String EXPECTCONTEXTMENU_PREPEND_ROW = "expectcontextmenu-prepend-row";
    private final String EXPECTCONTEXTMENU_APPEND_ROW = "expectcontextmenu-append-row";

    @PostConstruct
    @Override
    public void initMenu() {
        // EXPECT MENU
        COLUMNCONTEXTMENU_COLUMN = EXPECTCONTEXTMENU_EXPECT;
        COLUMNCONTEXTMENU_INSERT_COLUMN_LEFT = EXPECTCONTEXTMENU_INSERT_COLUMN_LEFT;
        COLUMNCONTEXTMENU_INSERT_COLUMN_RIGHT = EXPECTCONTEXTMENU_INSERT_COLUMN_RIGHT;
        COLUMNCONTEXTMENU_DELETE_COLUMN = EXPECTCONTEXTMENU_DELETE_COLUMN;
        COLUMNCONTEXTMENU_LABEL = constants.expect().toUpperCase();
        COLUMNCONTEXTMENU_I18N = "expect";
        // SCENARIO MENU
        HEADERCONTEXTMENU_SCENARIO = EXPECTCONTEXTMENU_SCENARIO;
        HEADERCONTEXTMENU_PREPEND_ROW = EXPECTCONTEXTMENU_PREPEND_ROW;
        HEADERCONTEXTMENU_APPEND_ROW = EXPECTCONTEXTMENU_APPEND_ROW;
        super.initMenu();
    }
}
