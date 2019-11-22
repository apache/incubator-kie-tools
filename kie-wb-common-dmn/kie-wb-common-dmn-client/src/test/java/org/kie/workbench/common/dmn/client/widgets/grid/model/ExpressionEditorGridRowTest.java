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

import java.util.Map;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorGridRow.DEFAULT_HEIGHT;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionEditorGridRowTest {

    @Mock
    private BaseExpressionGrid view;

    @Test
    public void testEmptyRow() {
        final GridRow row = new ExpressionEditorGridRow();
        assertThat(row.getHeight()).isEqualTo(DEFAULT_HEIGHT);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRowLowerThanDefault() {
        when(view.getHeight()).thenReturn(DEFAULT_HEIGHT - 1);

        final GridRow row = spy(ExpressionEditorGridRow.class);
        final Map<Integer, GridCell<?>> cells = new Maps.Builder<Integer, GridCell<?>>()
                .put(0, new BaseGridCell<>(new ExpressionCellValue(Optional.of(view))))
                .build();

        when(row.getCells()).thenReturn(cells);
        assertThat(row.getHeight()).isBetween(0D, DEFAULT_HEIGHT);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRowHigherThanDefault() {
        when(view.getHeight()).thenReturn(DEFAULT_HEIGHT + 1);

        final GridRow row = spy(ExpressionEditorGridRow.class);
        final Map<Integer, GridCell<?>> cells = new Maps.Builder<Integer, GridCell<?>>()
                .put(0, new BaseGridCell<>(new ExpressionCellValue(Optional.of(view))))
                .build();

        when(row.getCells()).thenReturn(cells);
        assertThat(row.getHeight()).isGreaterThan(DEFAULT_HEIGHT);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRowHigherThanDefaultWithNullCell() {
        when(view.getHeight()).thenReturn(DEFAULT_HEIGHT + 1);

        final GridRow row = spy(ExpressionEditorGridRow.class);
        final Map<Integer, GridCell<?>> cells = new Maps.Builder<Integer, GridCell<?>>()
                .put(0, new BaseGridCell<>(new ExpressionCellValue(Optional.of(view))))
                .put(1, null)
                .build();

        when(row.getCells()).thenReturn(cells);
        assertThat(row.getHeight()).isGreaterThan(DEFAULT_HEIGHT);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRowHigherThanDefaultWithNullCellValue() {
        when(view.getHeight()).thenReturn(DEFAULT_HEIGHT + 1);

        final GridRow row = spy(ExpressionEditorGridRow.class);
        final Map<Integer, GridCell<?>> cells = new Maps.Builder<Integer, GridCell<?>>()
                .put(0, new BaseGridCell<>(new ExpressionCellValue(Optional.of(view))))
                .put(1, new BaseGridCell<>(null))
                .build();

        when(row.getCells()).thenReturn(cells);
        assertThat(row.getHeight()).isGreaterThan(DEFAULT_HEIGHT);
    }
}
