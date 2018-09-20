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
 * The contextual menu of a specific <i>EXPECTED</i> column
 * It differ from {@link HeaderExpectedContextMenu} because it manage column (insert/remove) in different way
 */
@Dependent
public class ExpectedContextMenu extends AbstractColumnMenuPresenter {

    // This strings are used to give unique id in the final dom
    private final String EXPECTEDCONTEXTMENU_EXPECTED = "expectedcontextmenu-expected";
    private final String EXPECTEDCONTEXTMENU_SCENARIO = "expectedcontextmenu-scenario";
    private final String EXPECTEDCONTEXTMENU_INSERT_COLUMN_LEFT = "expectedcontextmenu-insert-column-left";
    private final String EXPECTEDCONTEXTMENU_INSERT_COLUMN_RIGHT = "expectedcontextmenu-insert-column-right";
    private final String EXPECTEDCONTEXTMENU_DELETE_COLUMN = "expectedcontextmenu-delete-column";
    private final String EXPECTEDCONTEXTMENU_PREPEND_ROW = "expectedcontextmenu-prepend-row";
    private final String EXPECTEDCONTEXTMENU_APPEND_ROW = "expectedcontextmenu-append-row";

    @PostConstruct
    @Override
    public void initMenu() {
        // EXPECTED MENU
        COLUMNCONTEXTMENU_COLUMN = EXPECTEDCONTEXTMENU_EXPECTED;
        COLUMNCONTEXTMENU_INSERT_COLUMN_LEFT = EXPECTEDCONTEXTMENU_INSERT_COLUMN_LEFT;
        COLUMNCONTEXTMENU_INSERT_COLUMN_RIGHT = EXPECTEDCONTEXTMENU_INSERT_COLUMN_RIGHT;
        COLUMNCONTEXTMENU_DELETE_COLUMN = EXPECTEDCONTEXTMENU_DELETE_COLUMN;
        COLUMNCONTEXTMENU_LABEL = constants.expected().toUpperCase();
        COLUMNCONTEXTMENU_I18N = "expected";
        // SCENARIO MENU
        HEADERCONTEXTMENU_SCENARIO = EXPECTEDCONTEXTMENU_SCENARIO;
        HEADERCONTEXTMENU_PREPEND_ROW = EXPECTEDCONTEXTMENU_PREPEND_ROW;
        HEADERCONTEXTMENU_APPEND_ROW = EXPECTEDCONTEXTMENU_APPEND_ROW;
        super.initMenu();
    }
}
