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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.client.widgets.grid.model.BaseHasDynamicHeightCell.DEFAULT_HEIGHT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class LiteralExpressionGridRowTest {

    private static final double CELL_HEIGHT = DEFAULT_HEIGHT * 2;

    private static class MockHasDynamicHeightCell<T> extends BaseGridCell<T> implements HasDynamicHeight {

        private MockHasDynamicHeightCell(final GridCellValue<T> value) {
            super(value);
        }

        @Override
        public double getHeight() {
            return CELL_HEIGHT;
        }
    }

    @Test
    public void testEmptyRow() {
        final GridRow row = new LiteralExpressionGridRow();
        assertThat(row.getHeight()).isEqualTo(DEFAULT_HEIGHT);
    }

    @Test
    public void testGetHeightWithHasDynamicHeightCell() {
        final GridRow row = spy(new LiteralExpressionGridRow());
        final Map<Integer, GridCell> cells = new HashMap<Integer, GridCell>() {{
            put(0, new MockHasDynamicHeightCell<>(new BaseGridCellValue<>("cheese")));
            put(1, new BaseGridCell<>(new BaseGridCellValue<>("cheese")));
        }};

        doReturn(cells).when(row).getCells();
        assertThat(row.getHeight()).isEqualTo(CELL_HEIGHT);
    }

    @Test
    public void testGetHeightWithoutHasDynamicHeightCell() {
        final GridRow row = spy(new LiteralExpressionGridRow());
        final Map<Integer, GridCell> cells = new HashMap<Integer, GridCell>() {{
            put(0, new BaseGridCell<>(new BaseGridCellValue<>("cheese")));
        }};

        doReturn(cells).when(row).getCells();
        assertThat(row.getHeight()).isEqualTo(DEFAULT_HEIGHT);
    }
}
