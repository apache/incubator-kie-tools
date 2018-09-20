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
 * The contextual menu of the top level <i>EXPECTED</i> group.
 * It differ from <code>ExpectedContextMenu</code> because it manage column (insert/remove) in different way
 */
@Dependent
public class HeaderExpectedContextMenu extends AbstractHeaderGroupMenuPresenter {

    // This strings are used to give unique id in the final dom
    private final String HEADEREXPECTEDCONTEXTMENU_EXPECTED = "headerexpectedcontextmenu-expected";
    private final String HEADEREXPECTEDCONTEXTMENU_SCENARIO = "headerexpectedcontextmenu-scenario";
    private final String HEADEREXPECTEDCONTEXTMENU_INSERT_COLUMN_LEFT = "headerexpectedcontextmenu-insert-column-left";
    private final String HEADEREXPECTEDCONTEXTMENU_INSERT_COLUMN_RIGHT = "headerexpectedcontextmenu-insert-column-right";
    private final String HEADEREXPECTEDCONTEXTMENU_DELETE_COLUMN = "headerexpectedcontextmenu-delete-column";
    private final String HEADEREXPECTEDCONTEXTMENU_INSERT_ROW_ABOVE = "headerexpectedcontextmenu-insert-row-above";
    private final String HEADEREXPECTEDCONTEXTMENU_INSERT_ROW_BELOW = "headerexpectedcontextmenu-insert-row-below";

    private final AppendColumnEvent appendExpectedColumnEvent = new AppendColumnEvent("EXPECTED");
    private final PrependColumnEvent prependExpectedColumnEvent = new PrependColumnEvent("EXPECTED");

    @PostConstruct
    @Override
    public void initMenu() {
        HEADERCONTEXTMENU_GROUP = HEADEREXPECTEDCONTEXTMENU_EXPECTED;
        HEADERCONTEXTMENU_SCENARIO = HEADEREXPECTEDCONTEXTMENU_SCENARIO;
        HEADERCONTEXTMENU_INSERT_COLUMN_LEFT = HEADEREXPECTEDCONTEXTMENU_INSERT_COLUMN_LEFT;
        HEADERCONTEXTMENU_INSERT_COLUMN_RIGHT = HEADEREXPECTEDCONTEXTMENU_INSERT_COLUMN_RIGHT;
        HEADERCONTEXTMENU_DELETE_COLUMN = HEADEREXPECTEDCONTEXTMENU_DELETE_COLUMN;
        HEADERCONTEXTMENU_PREPEND_ROW = HEADEREXPECTEDCONTEXTMENU_INSERT_ROW_ABOVE;
        HEADERCONTEXTMENU_APPEND_ROW = HEADEREXPECTEDCONTEXTMENU_INSERT_ROW_BELOW;
        HEADERCONTEXTMENU_LABEL = constants.expected().toUpperCase();
        HEADERCONTEXTMENU_I18N = "expected";
        appendColumnEvent = appendExpectedColumnEvent;
        prependColumnEvent = prependExpectedColumnEvent;
        super.initMenu();
    }
}
