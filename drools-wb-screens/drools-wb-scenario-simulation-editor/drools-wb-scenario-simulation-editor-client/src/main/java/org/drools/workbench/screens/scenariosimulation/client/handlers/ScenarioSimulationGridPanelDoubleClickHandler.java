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
package org.drools.workbench.screens.scenariosimulation.client.handlers;

import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetMouseDoubleClickHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

public class ScenarioSimulationGridPanelDoubleClickHandler extends BaseGridWidgetMouseDoubleClickHandler {

    public ScenarioSimulationGridPanelDoubleClickHandler(final GridWidget gridWidget,
                                                         final GridSelectionManager selectionManager,
                                                         final GridPinnedModeManager pinnedModeManager,
                                                         final GridRenderer renderer) {
        super(gridWidget,
              selectionManager,
              pinnedModeManager,
              renderer);
    }

    @Override
    protected boolean handleHeaderCellDoubleClick(final NodeMouseDoubleClickEvent event) {
        return true;
    }
}
