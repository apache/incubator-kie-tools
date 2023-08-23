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

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
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
        final Map<Integer, GridCell<?>> cells = Stream.of(
                new AbstractMap.SimpleEntry<>(0, new BaseGridCell<>(new ExpressionCellValue(Optional.of(view)))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        when(row.getCells()).thenReturn(cells);
        assertThat(row.getHeight()).isBetween(0D, DEFAULT_HEIGHT);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRowHigherThanDefault() {
        when(view.getHeight()).thenReturn(DEFAULT_HEIGHT + 1);

        final GridRow row = spy(ExpressionEditorGridRow.class);
        final Map<Integer, GridCell<?>> cells = Stream.of(
                        new AbstractMap.SimpleEntry<>(0, new BaseGridCell<>(new ExpressionCellValue(Optional.of(view)))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        when(row.getCells()).thenReturn(cells);
        assertThat(row.getHeight()).isGreaterThan(DEFAULT_HEIGHT);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRowHigherThanDefaultWithNullCell() {
        when(view.getHeight()).thenReturn(DEFAULT_HEIGHT + 1);

        final GridRow row = spy(ExpressionEditorGridRow.class);
        final Map<Integer, GridCell<?>> cells = new HashMap<>();
        cells.put(0, new BaseGridCell<>(new ExpressionCellValue(Optional.of(view))));
        cells.put(1, null);

        when(row.getCells()).thenReturn(cells);
        assertThat(row.getHeight()).isGreaterThan(DEFAULT_HEIGHT);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRowHigherThanDefaultWithNullCellValue() {
        when(view.getHeight()).thenReturn(DEFAULT_HEIGHT + 1);

        final GridRow row = spy(ExpressionEditorGridRow.class);
        final Map<Integer, GridCell<?>> cells = Stream.of(
                        new AbstractMap.SimpleEntry<>(0, new BaseGridCell<>(new ExpressionCellValue(Optional.of(view)))),
                        new AbstractMap.SimpleEntry<>(1, new BaseGridCell<>(null)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        when(row.getCells()).thenReturn(cells);
        assertThat(row.getHeight()).isGreaterThan(DEFAULT_HEIGHT);
    }
}
