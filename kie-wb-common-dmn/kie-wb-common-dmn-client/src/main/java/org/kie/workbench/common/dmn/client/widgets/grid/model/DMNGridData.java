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

package org.kie.workbench.common.dmn.client.widgets.grid.model;

import java.util.function.Supplier;

import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;

public class DMNGridData extends BaseGridData {

    public DMNGridData() {
        super(false);
    }

    @Override
    public Range setCell(final int rowIndex,
                         final int columnIndex,
                         final Supplier<GridCell<?>> cellSupplier) {
        final Range r = super.setCell(rowIndex,
                                      columnIndex,
                                      cellSupplier);
        resizeColumnIfRequired(columnIndex);

        return r;
    }

    @Override
    public Range setCellValue(final int rowIndex,
                              final int columnIndex,
                              final GridCellValue<?> value) {
        final Range r = super.setCellValue(rowIndex,
                                           columnIndex,
                                           value);
        resizeColumnIfRequired(columnIndex);

        return r;
    }

    @Override
    public Range deleteCell(final int rowIndex,
                            final int columnIndex) {
        final Range r = super.deleteCell(rowIndex,
                                         columnIndex);
        resizeColumnIfRequired(columnIndex);

        return r;
    }

    private void resizeColumnIfRequired(final int columnIndex) {
        final GridColumn column = getColumns().get(columnIndex);
        if (column instanceof RequiresResize) {
            ((RequiresResize) column).onResize();
        }
    }
}
