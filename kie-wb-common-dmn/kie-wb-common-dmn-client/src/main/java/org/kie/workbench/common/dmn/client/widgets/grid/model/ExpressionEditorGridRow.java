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

import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

public class ExpressionEditorGridRow extends BaseGridRow {

    public static final double DEFAULT_HEIGHT = 48.0;

    private final double defaultHeight;

    public ExpressionEditorGridRow() {
        this(DEFAULT_HEIGHT);
    }

    public ExpressionEditorGridRow(final double height) {
        super(height);
        this.defaultHeight = height;
    }

    @Override
    public double getHeight() {
        return this.getCells()
                .values()
                .stream()
                .filter(cell -> cell != null && cell.getValue() != null)
                .map(GridCell::getValue)
                .filter(value -> value instanceof ExpressionCellValue)
                .map(value -> (ExpressionCellValue) value)
                .filter(value -> value.getValue().isPresent())
                .map(value -> value.getValue().get())
                .map(editor -> editor.getHeight() + editor.getPadding() * 2)
                .reduce(Double::max)
                .orElse(defaultHeight);
    }
}
