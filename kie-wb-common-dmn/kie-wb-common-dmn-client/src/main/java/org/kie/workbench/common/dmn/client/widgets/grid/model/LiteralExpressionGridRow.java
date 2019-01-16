/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

public class LiteralExpressionGridRow extends BaseGridRow {

    public static final double DEFAULT_HEIGHT = 48.0;

    private final double lineHeight;

    public LiteralExpressionGridRow(final double lineHeight) {
        super(DEFAULT_HEIGHT);
        this.lineHeight = lineHeight;
    }

    @Override
    public double getHeight() {
        final double defaultHeight = super.getHeight();
        final double requiredHeight = getExpressionTextHeight();
        return Math.max(defaultHeight, requiredHeight);
    }

    private double getExpressionTextHeight() {
        final int maxExpressionLineCount = getMaxExpressionLineCount();
        return maxExpressionLineCount * lineHeight + (RendererUtils.EXPRESSION_TEXT_PADDING * 3);
    }

    private int getMaxExpressionLineCount() {
        return this.getCells()
                .values()
                .stream()
                .filter(cell -> cell != null && cell.getValue() != null)
                .map(GridCell::getValue)
                .filter(value -> value instanceof BaseGridCellValue)
                .map(value -> (BaseGridCellValue) value)
                .filter(value -> value.getValue() != null)
                .map(value -> value.getValue().toString())
                .map(value -> value.split("\\r?\\n", -1).length)
                .reduce(Integer::max)
                .orElse(0);
    }
}
