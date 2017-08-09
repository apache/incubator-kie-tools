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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.Command;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNBaseGridWidgetTest {

    @Mock
    private GridLayer gridLayer;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private GridColumn gridColumn;

    @Mock
    private Command command;

    @Test
    public void checkSelectionManager() {
        final GridSelectionManager selectionManager = DMNBaseGridWidget.getSelectionManager(gridLayer);

        selectionManager.select(gridWidget);
        verify(gridLayer).select(eq(gridWidget));

        selectionManager.selectLinkedColumn(gridColumn);
        verify(gridLayer).selectLinkedColumn(eq(gridColumn));

        selectionManager.getGridWidgets();
        verify(gridLayer).getGridWidgets();
    }

    @Test
    public void checkPinnedModeManager() {
        final GridPinnedModeManager pinnedModeManager = DMNBaseGridWidget.getPinnedModeManager(gridLayer);

        pinnedModeManager.enterPinnedMode(gridWidget,
                                          command);
        verify(gridLayer,
               never()).enterPinnedMode(any(GridWidget.class),
                                        any(Command.class));

        pinnedModeManager.exitPinnedMode(command);
        verify(gridLayer,
               never()).exitPinnedMode(any(Command.class));

        pinnedModeManager.updatePinnedContext(gridWidget);
        verify(gridLayer,
               never()).updatePinnedContext(any(GridWidget.class));

        pinnedModeManager.getPinnedContext();
        verify(gridLayer).getPinnedContext();

        pinnedModeManager.getDefaultTransformMediator();
        verify(gridLayer).getDefaultTransformMediator();

        pinnedModeManager.isGridPinned();
        verify(gridLayer).isGridPinned();
    }
}
