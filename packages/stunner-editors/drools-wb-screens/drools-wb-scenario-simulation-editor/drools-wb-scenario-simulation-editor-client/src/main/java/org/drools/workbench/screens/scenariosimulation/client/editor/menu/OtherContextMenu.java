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
 * The contextual menu of the <i>OTHER</i> group (both top level and specific column).
 */
@Dependent
public class OtherContextMenu extends AbstractHeaderMenuPresenter {

    // This strings are used to give unique id in the final dom
    private static final String OTHERCONTEXTMENU_GRID_TITLE = "othercontextmenu-grid-title";
    private static final String OTHERCONTEXTMENU_INSERT_ROW_ABOVE = "othercontextmenu-insert-row-above";

    @PostConstruct
    @Override
    public void initMenu() {
        HEADERCONTEXTMENU_GRID_TITLE = OTHERCONTEXTMENU_GRID_TITLE;
        HEADERCONTEXTMENU_PREPEND_ROW = OTHERCONTEXTMENU_INSERT_ROW_ABOVE;
        super.initMenu();
    }
}
