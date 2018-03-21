/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GridCellTupleTest {

    @Mock
    private GridWidget gridWidget;

    @Mock
    private GridColumn gridColumn;

    private GridData gridData;

    private GridCellTuple tuple;

    @Before
    public void setup() {
        tuple = new GridCellTuple(0, 0, gridWidget);
        gridData = new BaseGridData(false);
        gridData.appendColumn(gridColumn);

        when(gridWidget.getModel()).thenReturn(gridData);
    }

    @Test
    public void testProposeContainingColumnWidthWhenLargerThanExisting() {
        when(gridColumn.getWidth()).thenReturn(100.0);

        tuple.proposeContainingColumnWidth(200.0);

        verify(gridColumn).setWidth(200.0);
    }

    @Test
    public void testProposeContainingColumnWidthWhenSmallerThanExisting() {
        when(gridColumn.getWidth()).thenReturn(100.0);

        tuple.proposeContainingColumnWidth(50.0);

        verify(gridColumn).setWidth(100.0);
    }

    @Test
    public void testOnResizeSetsColumnWidth() {
        when(gridColumn.getWidth()).thenReturn(100.0);

        tuple.onResize();

        verify(gridColumn).setWidth(100.0);
    }
}
