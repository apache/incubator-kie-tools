/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.client.factories;

import com.google.gwt.user.client.Command;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioMenuItem;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.workbench.model.menu.MenuItem;

public class ScenarioMenuItemFactory {

    private ScenarioMenuItemFactory() {
        /* Class with static methods - not instantiable */
    }

    public static MenuItem getRunScenarioMenuItem(final Command command) {
        return new ScenarioMenuItem(IconType.PLAY, command);
    }

    public static MenuItem getUndoMenuItem(final Command command) {
        return new ScenarioMenuItem(IconType.UNDO, command);
    }

    public static MenuItem getRedoMenuItem(final Command command) {
        return new ScenarioMenuItem(IconType.REPEAT, command);
    }

    public static MenuItem getDownloadMenuItem(final Command command) {
        return new ScenarioMenuItem(IconType.DOWNLOAD, command);
    }

    public static MenuItem getExportToCsvMenuItem(final Command command) {
        return new ScenarioMenuItem(ScenarioSimulationEditorConstants.INSTANCE.export(), command);
    }

    public static MenuItem getImportMenuItem(final Command command) {
        return new ScenarioMenuItem(ScenarioSimulationEditorConstants.INSTANCE.importLabel(), command);
    }
}
