/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.grid.columns;

import java.util.List;
import java.util.function.Consumer;

import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.edit.EditorPopup;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

public class StringPopupColumn extends BaseGridColumn<String> {

    private final EditorPopup editor = new EditorPopup();

    public StringPopupColumn(final List<GridColumn.HeaderMetaData> headerMetaData,
                             final GridColumnRenderer<String> columnRenderer,
                             final double width) {
        super(headerMetaData,
              columnRenderer,
              width);
    }

    public StringPopupColumn(final GridColumn.HeaderMetaData headerMetaData,
                             final GridColumnRenderer<String> columnRenderer,
                             final double width) {
        super(headerMetaData,
              columnRenderer,
              width);
    }

    @Override
    public void edit(final GridCell<String> cell,
                     final GridBodyCellRenderContext context,
                     final Consumer<GridCellValue<String>> callback) {
        editor.edit(assertCell(cell).getValue(),
                    callback);
    }

    private GridCell<String> assertCell(final GridCell<String> cell) {
        if (cell != null) {
            return cell;
        }
        return new BaseGridCell<String>(new BaseGridCellValue<String>(""));
    }
}