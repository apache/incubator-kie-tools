/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid;

import java.util.Set;

import com.ait.lienzo.client.core.event.INodeXYEvent;
import com.google.gwt.user.client.Command;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.TransformMediator;

public class DMNBaseGridWidget extends BaseGridWidget {

    public DMNBaseGridWidget(final GridData model,
                             final GridRenderer renderer,
                             final GridLayer gridLayer) {
        super(model,
              getSelectionManager(gridLayer),
              getPinnedModeManager(gridLayer),
              renderer);
    }

    static GridSelectionManager getSelectionManager(final GridLayer gridLayer) {
        return new GridSelectionManager() {
            @Override
            public void select(final GridWidget selectedGridWidget) {
                gridLayer.select(selectedGridWidget);
            }

            @Override
            public void selectLinkedColumn(final GridColumn<?> selectedGridColumn) {
                gridLayer.selectLinkedColumn(selectedGridColumn);
            }

            @Override
            public Set<GridWidget> getGridWidgets() {
                return gridLayer.getGridWidgets();
            }
        };
    }

    static GridPinnedModeManager getPinnedModeManager(final GridLayer gridLayer) {
        return new GridPinnedModeManager() {
            @Override
            public void enterPinnedMode(final GridWidget gridWidget,
                                        final Command onStartCommand) {
                //Do nothing. The DMN Editor does not support interaction with a PinnedModeManager.
            }

            @Override
            public void exitPinnedMode(final Command onCompleteCommand) {
                //Do nothing. The DMN Editor does not support interaction with a PinnedModeManager.
            }

            @Override
            public void updatePinnedContext(final GridWidget gridWidget) throws IllegalStateException {
                //Do nothing. The DMN Editor does not support interaction with a PinnedModeManager.
            }

            @Override
            public void addOnEnterPinnedModeCommand(final Command command) {
                //Do nothing. The DMN Editor does not support interaction with a PinnedModeManager.
            }

            @Override
            public void addOnExitPinnedModeCommand(final Command command) {
                //Do nothing. The DMN Editor does not support interaction with a PinnedModeManager.
            }

            @Override
            public PinnedContext getPinnedContext() {
                return gridLayer.getPinnedContext();
            }

            @Override
            public TransformMediator getDefaultTransformMediator() {
                return gridLayer.getDefaultTransformMediator();
            }

            @Override
            public boolean isGridPinned() {
                return gridLayer.isGridPinned();
            }
        };
    }

    @Override
    public boolean onDragHandle(final INodeXYEvent event) {
        return false;
    }
}
