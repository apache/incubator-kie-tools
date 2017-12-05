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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow.DEFAULT_HEIGHT;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNGridRowTest {

    @Mock
    private GridWidget view;

    @Test
    public void testEmptyRow() throws Exception {
        final DMNGridRow row = new DMNGridRow();
        Assertions.assertThat(row.getHeight()).isEqualTo(DEFAULT_HEIGHT);
    }

    @Test
    public void testRowNoHigherThanDefault() throws Exception {
        final DMNGridRow row = Mockito.spy(DMNGridRow.class);
        final Map<Integer, GridCell> cells = new HashMap<Integer, GridCell>() {{
            Mockito.doReturn(DEFAULT_HEIGHT - 1).when(view).getHeight();
            put(0, new BaseGridCell<>(new ExpressionCellValue(Optional.of(view))));
        }};

        Mockito.doReturn(cells).when(row).getCells();
        Assertions.assertThat(row.getHeight()).isBetween(0D, DEFAULT_HEIGHT);
    }

    @Test
    public void testRowHigherThanDefault() throws Exception {
        final DMNGridRow row = Mockito.spy(DMNGridRow.class);
        final Map<Integer, GridCell> cells = new HashMap<Integer, GridCell>() {{
            Mockito.doReturn(DEFAULT_HEIGHT + 1).when(view).getHeight();
            put(0, new BaseGridCell<>(new ExpressionCellValue(Optional.of(view))));
        }};

        Mockito.doReturn(cells).when(row).getCells();
        Assertions.assertThat(row.getHeight()).isGreaterThan(DEFAULT_HEIGHT);
    }
}
