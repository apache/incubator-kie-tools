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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class UndefinedExpressionColumnTest {

    @Mock
    private GridColumn.HeaderMetaData headerMetaData;

    @Mock
    private UndefinedExpressionGrid gridWidget;

    @Mock
    private GridCellTuple parent;

    private UndefinedExpressionColumn column;

    @Before
    public void setup() {
        this.column = spy(new UndefinedExpressionColumn(headerMetaData,
                                                        gridWidget));

        doReturn(parent).when(gridWidget).getParentInformation();
    }

    @Test
    public void testRenderer() {
        assertThat(column.getColumnRenderer()).isInstanceOf(UndefinedExpressionColumnRenderer.class);
    }

    @Test
    public void testSetWidth() {
        column.setWidth(200.0);

        assertThat(column.getWidth()).isEqualTo(200.0);
        verify(column).updateWidthOfPeers();
    }
}
