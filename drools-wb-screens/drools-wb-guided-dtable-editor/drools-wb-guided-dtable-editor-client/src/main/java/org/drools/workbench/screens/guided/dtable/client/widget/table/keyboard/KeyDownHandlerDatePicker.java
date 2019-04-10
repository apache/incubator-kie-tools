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
package org.drools.workbench.screens.guided.dtable.client.widget.table.keyboard;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.HasSingletonDOMElementResource;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.keyboard.KeyDownHandlerCommon;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

public class KeyDownHandlerDatePicker extends KeyDownHandlerCommon {

    public KeyDownHandlerDatePicker(final GridLienzoPanel gridPanel,
                                    final GridLayer gridLayer,
                                    final GridWidget gridWidget,
                                    final HasSingletonDOMElementResource gridCell,
                                    final GridBodyCellRenderContext context) {
        super(gridPanel,
              gridLayer,
              gridWidget,
              gridCell,
              context);
    }

    @Override
    public void onKeyDown(final KeyDownEvent e) {
        final int keyCode = e.getNativeKeyCode();
        final boolean isShiftKeyDown = e.isShiftKeyDown();
        switch (keyCode) {
            case KeyCodes.KEY_TAB:
            case KeyCodes.KEY_ESCAPE:
                gridCell.destroyResources();

            case KeyCodes.KEY_ENTER:
                moveSelection(keyCode,
                              isShiftKeyDown);

                gridPanel.setFocus(true);
                gridLayer.batch();
        }

        e.stopPropagation();
    }
}