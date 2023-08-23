/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.grid.keyboard;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gwt.event.dom.client.KeyCodes;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseKeyboardOperation;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class KeyboardOperationEscapeGridCell extends BaseKeyboardOperation {

    public KeyboardOperationEscapeGridCell(final GridLayer gridLayer) {
        super(gridLayer);
    }

    @Override
    public int getKeyCode() {
        return KeyCodes.KEY_ESCAPE;
    }

    @Override
    public boolean isExecutable(final GridWidget gridWidget) {
        return gridWidget instanceof BaseExpressionGrid;
    }

    @Override
    public boolean perform(final GridWidget gridWidget,
                           final boolean isShiftKeyDown,
                           final boolean isControlKeyDown) {

        final AtomicBoolean needToRedraw = new AtomicBoolean(false);
        final Optional<BaseExpressionGrid> oParent = ((BaseExpressionGrid) gridWidget).findParentGrid();
        oParent.ifPresent(parentWidget -> {
            gridLayer.select(parentWidget);
            parentWidget.selectFirstCell();
            needToRedraw.set(true);
        });

        return needToRedraw.get();
    }
}
