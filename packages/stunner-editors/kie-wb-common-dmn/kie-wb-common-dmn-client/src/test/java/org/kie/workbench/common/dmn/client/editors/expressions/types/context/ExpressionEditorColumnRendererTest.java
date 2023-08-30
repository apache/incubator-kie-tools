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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.GwtMockito;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridWidgetRegistry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ExpressionEditorColumnRendererTest {

    @Mock
    private GridWidgetRegistry registry;

    @Mock
    private GridBodyCellRenderContext context;

    @Mock
    private BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper> widget;

    @Mock
    private Group renderedGroup;

    @Mock
    private Group editorGroup;

    private GridCell<Optional<BaseExpressionGrid<? extends Expression, ? extends GridData, ? extends BaseUIModelMapper>>> cell;

    private ExpressionEditorColumnRenderer renderer;

    @Before
    public void setUp() throws Exception {
        GwtMockito.useProviderForType(Group.class, aClass -> renderedGroup);

        renderer = new ExpressionEditorColumnRenderer(registry);

        doReturn(editorGroup).when(widget).setX(anyDouble());
        doReturn(editorGroup).when(editorGroup).setY(anyDouble());
    }

    @Test
    public void testRenderCellNoDMNExpression() throws Exception {
        cell = new BaseGridCell<>(new BaseGridCellValue<>(Optional.of(widget)));
        renderer.renderCell(cell, context);
        verify(renderedGroup, never()).add(any());
    }

    @Test
    public void testRenderCellDMNExpression() throws Exception {
        cell = new BaseGridCell<>(new ExpressionCellValue(Optional.of(widget)));
        renderer.renderCell(cell, context);
        verify(renderedGroup).add(editorGroup);
        verify(registry).register(widget);
    }
}
