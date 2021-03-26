/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.event.AbstractNodeMouseEvent;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class GuidedDecisionTableSortGridWidgetMouseEventHandlerTest {

    @Captor
    private ArgumentCaptor<GridColumn> gridColumnCaptor;
    @Mock
    private GridColumn gridColumn;
    @Mock
    private Consumer consumer;
    @Mock
    private GridWidget gridWidget;
    @Mock
    private BaseGridRendererHelper rendererHelper;
    @Mock
    private BaseGridRendererHelper.ColumnInformation ci;
    @Mock
    private BaseGridRendererHelper.ColumnInformation ciWithoutColumn;

    @Before
    public void setUp() throws Exception {
        doReturn(rendererHelper).when(gridWidget).getRendererHelper();
        doReturn(ci).when(rendererHelper).getColumnInformation(0);
        doReturn(ciWithoutColumn).when(rendererHelper).getColumnInformation(100);
        doReturn(gridColumn).when(ci).getColumn();
    }

    @Test
    public void testHappyPath() {
        assertTrue(new GuidedDecisionTableSortGridWidgetMouseEventHandler(consumer).handleHeaderCell(gridWidget,
                                                                                                     new Point2D(0, 0),
                                                                                                     1,
                                                                                                     1,
                                                                                                     mock(AbstractNodeMouseEvent.class)));

        verify(consumer).accept(gridColumnCaptor.capture());
        assertEquals(gridColumn, gridColumnCaptor.getValue());
    }
}