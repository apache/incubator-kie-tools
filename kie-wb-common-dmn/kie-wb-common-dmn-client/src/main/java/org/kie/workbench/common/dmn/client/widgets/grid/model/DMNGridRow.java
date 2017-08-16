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

import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

public class DMNGridRow extends BaseGridRow {

    private static final double DEFAULT_HEIGHT = 100.0;

    public DMNGridRow() {
        super(DEFAULT_HEIGHT);
    }

    @Override
    public double getHeight() {
        return this.getCells()
                .values()
                .stream()
                .filter(cell -> cell != null && cell.getValue() != null)
                .map(GridCell::getValue)
                .filter(value -> value instanceof DMNExpressionCellValue)
                .map(value -> (DMNExpressionCellValue) value)
                .filter(value -> value.getValue().isPresent())
                .map(value -> value.getValue().get())
                .map(editor -> editor.getHeight() + DMNGridColumn.PADDING * 2)
                .reduce(Double::max)
                .orElse(DEFAULT_HEIGHT);
    }
}
