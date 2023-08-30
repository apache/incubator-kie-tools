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

package org.kie.workbench.common.dmn.client.widgets.grid;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BaseExpressionGridRenderingTest<D extends GridData> {

    protected D gridData;

    @Before
    public void setup() {
        this.gridData = getGridData();
    }

    @SuppressWarnings("unchecked")
    protected D getGridData() {
        return (D) spy(new BaseGridData());
    }

    protected GridRenderer getGridRenderer() {
        return new BaseExpressionGridRenderer(gridData);
    }

    protected double getExpectedHeaderRowHeight() {
        return BaseExpressionGridRenderer.HEADER_ROW_HEIGHT;
    }

    protected double getExpectedHeaderHeightZeroRows() {
        return 0.0;
    }

    protected double getExpectedHeaderHeightOneRow() {
        return BaseExpressionGridRenderer.HEADER_ROW_HEIGHT;
    }

    protected double getExpectedHeaderHeightTwoRows() {
        return BaseExpressionGridRenderer.HEADER_ROW_HEIGHT * 2;
    }

    @Test
    public void testHeaderDimensionsWhenHeaderHasZeroRows() {
        when(gridData.getHeaderRowCount()).thenReturn(0);

        final GridRenderer renderer = getGridRenderer();
        assertEquals(getExpectedHeaderHeightZeroRows(),
                     renderer.getHeaderHeight(),
                     0.0);
        assertEquals(getExpectedHeaderRowHeight(),
                     renderer.getHeaderRowHeight(),
                     0.0);
    }

    @Test
    public void testHeaderDimensionsWhenHeaderHasOneRow() {
        when(gridData.getHeaderRowCount()).thenReturn(1);

        final GridRenderer renderer = getGridRenderer();
        assertEquals(getExpectedHeaderHeightOneRow(),
                     renderer.getHeaderHeight(),
                     0.0);
        assertEquals(getExpectedHeaderRowHeight(),
                     renderer.getHeaderRowHeight(),
                     0.0);
    }

    @Test
    public void testHeaderDimensionsWhenHeaderHasTwoRows() {
        when(gridData.getHeaderRowCount()).thenReturn(2);

        final GridRenderer renderer = getGridRenderer();
        assertEquals(getExpectedHeaderHeightTwoRows(),
                     renderer.getHeaderHeight(),
                     0.0);
        assertEquals(getExpectedHeaderRowHeight(),
                     renderer.getHeaderRowHeight(),
                     0.0);
    }
}
