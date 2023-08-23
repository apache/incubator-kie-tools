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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.Optional;

import com.ait.lienzo.client.core.types.Point2D;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;

public abstract class EditablePopupHeaderMetaData<G, E extends HasCellEditorControls.Editor<G>> implements EditableHeaderMetaData {

    protected CellEditorControlsView.Presenter cellEditorControls;
    protected E editor;

    public EditablePopupHeaderMetaData(final CellEditorControlsView.Presenter cellEditorControls,
                                       final E editor) {
        this.cellEditorControls = cellEditorControls;
        this.editor = editor;
    }

    protected abstract G getPresenter();

    @Override
    public void setColumnGroup(final String columnGroup) {
        throw new UnsupportedOperationException("Group cannot be set.");
    }

    @Override
    public void setTitle(final String title) {
        throw new UnsupportedOperationException("Title is derived and cannot be set on the HeaderMetaData.");
    }

    @Override
    public void edit(final GridBodyCellEditContext context) {
        final int uiRowIndex = context.getRowIndex();
        final int uiColumnIndex = context.getColumnIndex();
        final double cellWidth = context.getCellWidth();
        final double cellHeight = context.getCellHeight();
        final double absoluteCellX = context.getAbsoluteCellX();
        final double absoluteCellY = context.getAbsoluteCellY();

        editor.bind(getPresenter(),
                    uiRowIndex,
                    uiColumnIndex);
        final double[] dxy = {absoluteCellX + cellWidth / 2, absoluteCellY + cellHeight / 2};
        final Optional<Point2D> rx = context.getRelativeLocation();
        rx.ifPresent(r -> {
            dxy[0] = r.getX();
            dxy[1] = r.getY();
        });
        cellEditorControls.show(editor,
                                (int) (dxy[0]),
                                (int) (dxy[1]));
    }

    @Override
    public void destroyResources() {
        editor.hide();
    }
}
