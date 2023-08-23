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

package org.kie.workbench.common.dmn.client.widgets.grid.keyboard;

import java.util.Optional;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KeyboardOperationEscapeGridCellTest {

    @Mock
    private GridLayer gridLayer;

    private KeyboardOperationEscapeGridCell operation;

    @Before
    public void setUp() {
        operation = new KeyboardOperationEscapeGridCell(gridLayer);
    }

    @Test
    public void testReactsOnKey() {
        assertThat(operation.getKeyCode()).isEqualTo(KeyCodes.KEY_ESCAPE);
    }

    @Test
    public void testNonBaseExpressionGrid() {
        assertThat(operation.isExecutable(mock(GridWidget.class)))
                .as("Widget have to be BaseExpressionWidget instance")
                .isFalse();
    }

    @Test
    public void testBaseExpressionGrid() {
        assertThat(operation.isExecutable(mock(BaseExpressionGrid.class)))
                .as("Widget can be BaseExpressionWidget instance")
                .isTrue();
    }

    @Test
    public void testEscapeFromNotChildCell() {
        final Optional<BaseExpressionGrid> oParent = Optional.empty();
        final BaseExpressionGrid gridWidget = mock(BaseExpressionGrid.class);
        when(gridWidget.findParentGrid()).thenReturn(oParent);

        assertThat(operation.perform(gridWidget, false, false))
                .as("No need to redraw")
                .isFalse();
    }

    @Test
    public void testEscapeFromChildCell() {
        final BaseExpressionGrid parent = mock(BaseExpressionGrid.class);
        final Optional<BaseExpressionGrid> oParent = Optional.of(parent);
        final BaseExpressionGrid gridWidget = mock(BaseExpressionGrid.class);
        when(gridWidget.findParentGrid()).thenReturn(oParent);

        assertThat(operation.perform(gridWidget, false, false))
                .as("Need to redraw")
                .isTrue();

        verify(gridLayer).select(parent);
        verify(parent).selectFirstCell();
    }
}
