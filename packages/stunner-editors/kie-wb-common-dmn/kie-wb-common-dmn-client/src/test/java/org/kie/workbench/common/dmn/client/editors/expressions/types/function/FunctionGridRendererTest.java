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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridRenderingTest;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class FunctionGridRendererTest extends BaseExpressionGridRenderingTest<DMNGridData> {

    @Override
    protected DMNGridData getGridData() {
        return mock(DMNGridData.class);
    }

    @Override
    protected GridRenderer getGridRenderer() {
        return new FunctionGridRenderer(gridData);
    }

    @Override
    protected double getExpectedHeaderRowHeight() {
        return FunctionGridRenderer.HEADER_ROW_HEIGHT;
    }

    @Override
    protected double getExpectedHeaderHeightOneRow() {
        return FunctionGridRenderer.HEADER_ROW_HEIGHT;
    }

    @Override
    protected double getExpectedHeaderHeightTwoRows() {
        return FunctionGridRenderer.HEADER_ROW_HEIGHT * 2;
    }
}
