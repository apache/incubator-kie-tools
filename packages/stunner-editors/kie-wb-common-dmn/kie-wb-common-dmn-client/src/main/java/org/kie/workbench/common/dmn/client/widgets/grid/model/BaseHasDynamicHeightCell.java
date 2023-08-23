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

import java.util.Objects;

import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;

public abstract class BaseHasDynamicHeightCell<T> extends DMNGridCell<T> implements HasDynamicHeight {

    public static final double DEFAULT_HEIGHT = 48.0;

    protected final double lineHeight;

    private double height;

    public BaseHasDynamicHeightCell(final GridCellValue<T> value,
                                    final double lineHeight) {
        super(value);
        this.lineHeight = lineHeight;
        this.height = getExpressionTextHeight();
    }

    @Override
    protected void setValue(final GridCellValue<T> value) {
        super.setValue(value);
        this.height = getExpressionTextHeight();
    }

    @Override
    public double getHeight() {
        return height;
    }

    protected double getExpressionTextHeight() {
        if (Objects.isNull(value) || Objects.isNull(value.getValue())) {
            return DEFAULT_HEIGHT;
        }
        final String asText = getValue().getValue().toString();
        if (StringUtils.isEmpty(asText)) {
            return DEFAULT_HEIGHT;
        }

        final int expressionLineCount = asText.split("\\r?\\n", -1).length;
        return expressionLineCount * lineHeight + (RendererUtils.EXPRESSION_TEXT_PADDING * 3);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseHasDynamicHeightCell)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final BaseHasDynamicHeightCell<?> that = (BaseHasDynamicHeightCell<?>) o;
        return Double.compare(that.lineHeight, lineHeight) == 0 &&
                Double.compare(that.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lineHeight, height);
    }
}
