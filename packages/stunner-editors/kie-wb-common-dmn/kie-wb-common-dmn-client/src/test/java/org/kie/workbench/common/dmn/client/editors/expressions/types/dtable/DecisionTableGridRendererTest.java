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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridRenderingTest;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DecisionTableGridRendererTest extends BaseExpressionGridRenderingTest<DecisionTableGridData> {

    @Override
    protected DecisionTableGridData getGridData() {
        return mock(DecisionTableGridData.class);
    }

    @Override
    protected GridRenderer getGridRenderer() {
        return new DecisionTableGridRenderer(gridData);
    }

    @Override
    protected double getExpectedHeaderRowHeight() {
        return DecisionTableGridRenderer.HEADER_ROW_HEIGHT;
    }

    @Override
    protected double getExpectedHeaderHeightZeroRows() {
        return DecisionTableGridRenderer.HEADER_ROW_HEIGHT * 2;
    }

    @Override
    protected double getExpectedHeaderHeightOneRow() {
        return DecisionTableGridRenderer.HEADER_ROW_HEIGHT * 2;
    }

    @Override
    protected double getExpectedHeaderHeightTwoRows() {
        return DecisionTableGridRenderer.HEADER_ROW_HEIGHT * 2;
    }
}
