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

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.KeyboardOperation;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseDynamicReadOnlyKeyboardOperationTest<O extends KeyboardOperation> {

    @Mock
    protected GridLayer gridLayer;

    @Mock
    protected BaseExpressionGrid gridWidget;

    @Mock
    protected GridData uiModel;

    private O operation;

    @Before
    public void setup() {
        this.operation = getOperation();

        when(gridWidget.getModel()).thenReturn(uiModel);
        when(uiModel.getSelectedCells()).thenReturn(Collections.singletonList(mock(GridData.SelectedCell.class)));
    }

    protected abstract O getOperation();

    @Test
    public void testIsExecutable() {
        when(gridWidget.isOnlyVisualChangeAllowed()).thenReturn(false);

        assertThat(operation.isExecutable(gridWidget)).isTrue();
    }

    @Test
    public void testIsExecutableWhenOnlyVisualChangeAllowed() {
        when(gridWidget.isOnlyVisualChangeAllowed()).thenReturn(true);

        assertThat(operation.isExecutable(gridWidget)).isFalse();
    }
}
