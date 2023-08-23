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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector.UndefinedExpressionSelectorPopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;

public class UndefinedExpressionColumn extends DMNGridColumn<UndefinedExpressionGrid, String> {

    public static final double DEFAULT_WIDTH = 150.0;

    private final CellEditorControlsView.Presenter cellEditorControls;
    private final UndefinedExpressionSelectorPopoverView.Presenter undefinedExpressionSelector;

    public UndefinedExpressionColumn(final double width,
                                     final UndefinedExpressionGrid gridWidget,
                                     final CellEditorControlsView.Presenter cellEditorControls,
                                     final UndefinedExpressionSelectorPopoverView.Presenter undefinedExpressionSelector) {
        this(Collections.emptyList(),
             width,
             gridWidget,
             cellEditorControls,
             undefinedExpressionSelector);
    }

    public UndefinedExpressionColumn(final List<HeaderMetaData> headerMetaData,
                                     final double width,
                                     final UndefinedExpressionGrid gridWidget,
                                     final CellEditorControlsView.Presenter cellEditorControls,
                                     final UndefinedExpressionSelectorPopoverView.Presenter undefinedExpressionSelector) {
        super(headerMetaData,
              new UndefinedExpressionColumnRenderer(),
              width,
              gridWidget);
        this.cellEditorControls = cellEditorControls;
        this.undefinedExpressionSelector = undefinedExpressionSelector;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void edit(final GridCell<String> cell,
                     final GridBodyCellEditContext context,
                     final Consumer<GridCellValue<String>> callback) {
        final int uiRowIndex = context.getRowIndex();
        final int uiColumnIndex = context.getColumnIndex();
        final double absoluteCellX = context.getAbsoluteCellX();
        final double absoluteCellY = context.getAbsoluteCellY();

        undefinedExpressionSelector.bind(gridWidget,
                                         uiRowIndex,
                                         uiColumnIndex);
        final double[] dxy = {absoluteCellX, absoluteCellY};
        final Optional<com.ait.lienzo.client.core.types.Point2D> rx = context.getRelativeLocation();
        if (!rx.isPresent()) {
            dxy[0] += gridWidget.getWidth() / 2.0;
            dxy[1] += gridWidget.getHeight() / 2.0;
        } else {
            dxy[0] = rx.get().getX();
            dxy[1] = rx.get().getY();
        }

        cellEditorControls.show(undefinedExpressionSelector,
                                (int) (dxy[0]),
                                (int) (dxy[1]));
    }

    @Override
    public void setWidth(final double width) {
        super.setWidth(width);
        updateWidthOfPeers();
    }

    @Override
    public void destroyResources() {
        super.destroyResources();
        undefinedExpressionSelector.hide();
    }
}
