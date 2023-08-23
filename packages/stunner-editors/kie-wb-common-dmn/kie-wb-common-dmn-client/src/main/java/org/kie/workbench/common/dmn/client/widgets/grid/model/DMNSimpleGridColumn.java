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

package org.kie.workbench.common.dmn.client.widgets.grid.model;

import java.util.List;

import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseGrid;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;

public abstract class DMNSimpleGridColumn<G extends BaseGrid<? extends Expression>, T> extends DMNGridColumn<G, T> {

    public DMNSimpleGridColumn(final HeaderMetaData headerMetaData,
                               final GridColumnRenderer<T> columnRenderer,
                               final double width,
                               final G gridWidget) {
        super(headerMetaData,
              columnRenderer,
              width,
              gridWidget);
    }

    public DMNSimpleGridColumn(final List<HeaderMetaData> headerMetaData,
                               final GridColumnRenderer<T> columnRenderer,
                               final double width,
                               final G gridWidget) {
        super(headerMetaData,
              columnRenderer,
              width,
              gridWidget);
    }

    protected GridCell<T> assertCell(final GridCell<T> cell) {
        if (cell != null) {
            return cell;
        }
        return new BaseGridCell<>(makeDefaultCellValue());
    }

    protected GridCellValue<T> assertCellValue(final GridCellValue<T> cellValue) {
        if (cellValue != null) {
            if (cellValue.getValue() != null) {
                return cellValue;
            }
        }
        return makeDefaultCellValue();
    }

    protected abstract GridCellValue<T> makeDefaultCellValue();
}
