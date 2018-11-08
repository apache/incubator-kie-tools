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
 * The contextual menu of the top level <i>EXPECT</i> group.
 * It differ from <code>ExpectedContextMenu</code> because it manage column (insert/remove) in different way
 */
@Dependent
public class HeaderExpectedContextMenu extends AbstractHeaderGroupMenuPresenter {

    // This strings are used to give unique id in the final dom
    private final String HEADEREXPECTCONTEXTMENU_EXPECT = "headerexpectcontextmenu-expect";
    private final String HEADEREXPECTCONTEXTMENU_SCENARIO = "headerexpectcontextmenu-scenario";
    private final String HEADEREXPECTCONTEXTMENU_INSERT_COLUMN_LEFT = "headerexpectcontextmenu-insert-column-left";
    private final String HEADEREXPECTCONTEXTMENU_INSERT_COLUMN_RIGHT = "headerexpectcontextmenu-insert-column-right";
    private final String HEADEREXPECTCONTEXTMENU_DELETE_COLUMN = "headerexpectcontextmenu-delete-column";
    private final String HEADEREXPECTCONTEXTMENU_INSERT_ROW_ABOVE = "headerexpectcontextmenu-insert-row-above";
    private final String HEADEREXPECTCONTEXTMENU_INSERT_ROW_BELOW = "headerexpectcontextmenu-insert-row-below";

    private final AppendColumnEvent appendExpectedColumnEvent = new AppendColumnEvent("EXPECT");
    private final PrependColumnEvent prependExpectedColumnEvent = new PrependColumnEvent("EXPECT");

    @PostConstruct
    @Override
    public void initMenu() {
        HEADERCONTEXTMENU_GROUP = HEADEREXPECTCONTEXTMENU_EXPECT;
        HEADERCONTEXTMENU_SCENARIO = HEADEREXPECTCONTEXTMENU_SCENARIO;
        HEADERCONTEXTMENU_INSERT_COLUMN_LEFT = HEADEREXPECTCONTEXTMENU_INSERT_COLUMN_LEFT;
        HEADERCONTEXTMENU_INSERT_COLUMN_RIGHT = HEADEREXPECTCONTEXTMENU_INSERT_COLUMN_RIGHT;
        HEADERCONTEXTMENU_DELETE_COLUMN = HEADEREXPECTCONTEXTMENU_DELETE_COLUMN;
        HEADERCONTEXTMENU_PREPEND_ROW = HEADEREXPECTCONTEXTMENU_INSERT_ROW_ABOVE;
        HEADERCONTEXTMENU_APPEND_ROW = HEADEREXPECTCONTEXTMENU_INSERT_ROW_BELOW;
        HEADERCONTEXTMENU_LABEL = constants.expect().toUpperCase();
        HEADERCONTEXTMENU_I18N = "expect";
        appendColumnEvent = appendExpectedColumnEvent;
        prependColumnEvent = prependExpectedColumnEvent;
        super.initMenu();
    }
}
