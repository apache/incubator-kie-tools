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

import org.drools.workbench.screens.scenariosimulation.client.events.AppendColumnEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.PrependColumnEvent;

/**
 * This class is meant to provide common methods to <b>group-specific</b> menus {@link HeaderExpectedContextMenu} and {@link HeaderGivenContextMenu}
 * It is provided to avoid code duplication in concrete implementations
 */
public abstract class AbstractHeaderGroupMenuPresenter extends AbstractHeaderMenuPresenter {

    protected String HEADERCONTEXTMENU_GROUP;
    protected String HEADERCONTEXTMENU_INSERT_COLUMN_LEFT;
    protected String HEADERCONTEXTMENU_INSERT_COLUMN_RIGHT;
    protected String HEADERCONTEXTMENU_DELETE_COLUMN;
    protected String HEADERCONTEXTMENU_LABEL;
    protected String HEADERCONTEXTMENU_I18N;


    protected AppendColumnEvent appendColumnEvent;
    protected PrependColumnEvent prependColumnEvent;

    /**
     * This method set <b>group-specific</b> menu items and common <b>SCENARIO</b> menu items
     */
    public void initMenu() {
        addMenuItem(HEADERCONTEXTMENU_GROUP, HEADERCONTEXTMENU_LABEL, HEADERCONTEXTMENU_I18N);
        addExecutableMenuItem(HEADERCONTEXTMENU_INSERT_COLUMN_LEFT, constants.insertLeftmostColumn(), "insertLeftmostColumn", prependColumnEvent);
        addExecutableMenuItem(HEADERCONTEXTMENU_INSERT_COLUMN_RIGHT, constants.insertRightmostColumn(), "insertRightmostColumn", appendColumnEvent);
        super.initMenu();
    }
}
