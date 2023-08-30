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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwtmockito.GwtMock;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.BaseGridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseNameAndDataTypeColumnRendererTest<R extends BaseGridColumnRenderer, C> {

    protected static final String TITLE = "title";

    protected static final String PLACE_HOLDER = "placeHolder";

    protected static final QName TYPE_REF = new QName();

    protected static final double BLOCK_WIDTH = 100;

    protected static final double ROW_HEIGHT = 32.0;

    @Mock
    protected LiteralExpressionColumn uiColumn;

    @Mock
    protected GridData uiModel;

    @Mock
    protected GridRenderer gridRenderer;

    @Mock
    protected GridRendererTheme gridRendererTheme;

    @Mock
    protected Text text1;

    @Mock
    protected Text text2;

    @Mock
    protected Transform transform;

    @GwtMock
    @SuppressWarnings("unused")
    protected Group headerGroup;

    protected GridBodyCellRenderContext bodyContext;

    protected GridHeaderColumnRenderContext headerContext;

    protected List<GridColumn.HeaderMetaData> headerMetaData;

    protected GridCell<C> cell;

    protected R renderer;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.renderer = getColumnRenderer();
        this.headerContext = new GridHeaderColumnRenderContext(0,
                                                               Collections.singletonList(uiColumn),
                                                               Collections.singletonList(uiColumn),
                                                               0,
                                                               uiModel,
                                                               gridRenderer);
        this.bodyContext = new GridBodyCellRenderContext(0,
                                                         0,
                                                         BLOCK_WIDTH,
                                                         ROW_HEIGHT,
                                                         0,
                                                         0,
                                                         0,
                                                         0,
                                                         false,
                                                         transform,
                                                         gridRenderer);

        when(gridRenderer.getTheme()).thenReturn(gridRendererTheme);
        when(gridRendererTheme.getHeaderText()).thenReturn(text1, text2);
        when(gridRendererTheme.getBodyText()).thenReturn(text1);
        when(gridRendererTheme.getPlaceholderText()).thenReturn(text1);
        when(text1.asNode()).thenReturn(mock(Node.class));
        when(text2.asNode()).thenReturn(mock(Node.class));
    }

    protected abstract R getColumnRenderer();

    @Test
    public void testRenderHeaderContentWithNormalColumnHeaderMetaData() {
        final GridColumn.HeaderMetaData metaData = mock(GridColumn.HeaderMetaData.class);
        this.headerMetaData = Collections.singletonList(metaData);

        when(metaData.getTitle()).thenReturn(TITLE);

        renderer.renderHeaderContent(headerMetaData,
                                     headerContext,
                                     0,
                                     BLOCK_WIDTH,
                                     ROW_HEIGHT);

        verify(text1).setText(eq(TITLE));
        verify(text1).setX(BLOCK_WIDTH / 2);
        verify(text1).setY(ROW_HEIGHT / 2);

        verify(headerGroup).add(text1);
        verify(headerGroup, never()).add(text2);
    }

    @Test
    public void testRenderHeaderContentWithNameAndDataTypeHeaderMetaData() {
        final ValueAndDataTypeHeaderMetaData metaData = mock(ValueAndDataTypeHeaderMetaData.class);
        when(metaData.render(any(GridHeaderColumnRenderContext.class), anyDouble(), anyDouble())).thenCallRealMethod();
        this.headerMetaData = Collections.singletonList(metaData);

        when(metaData.getTitle()).thenReturn(TITLE);
        when(metaData.getTypeRef()).thenReturn(TYPE_REF);

        renderer.renderHeaderContent(headerMetaData,
                                     headerContext,
                                     0,
                                     BLOCK_WIDTH,
                                     ROW_HEIGHT);

        verify(text1).setText(eq(TITLE));
        verify(text1).setX(BLOCK_WIDTH / 2);
        verify(text1).setY(ROW_HEIGHT / 2 - RendererUtils.SPACING);

        verify(text2).setText(eq("(" + TYPE_REF + ")"));
        verify(text2).setX(BLOCK_WIDTH / 2);
        verify(text2).setY(ROW_HEIGHT / 2 + RendererUtils.SPACING);
        verify(text2).setFontStyle(RendererUtils.FONT_STYLE_TYPE_REF);
        verify(text2).setFontSize(BaseExpressionGridTheme.FONT_SIZE - 2.0);

        verify(headerGroup).add(text1);
        verify(headerGroup).add(text2);
    }

    @Test
    public void testRenderHeaderContentWithNameAndDataTypeHeaderMetaDataWithPlaceHolder() {
        final ValueAndDataTypeHeaderMetaData metaData = mock(ValueAndDataTypeHeaderMetaData.class);
        when(metaData.renderPlaceHolder(any(GridHeaderColumnRenderContext.class), anyDouble(), anyDouble())).thenCallRealMethod();
        when(metaData.getPlaceHolder()).thenReturn(Optional.of(PLACE_HOLDER));
        this.headerMetaData = Collections.singletonList(metaData);

        renderer.renderHeaderContent(headerMetaData,
                                     headerContext,
                                     0,
                                     BLOCK_WIDTH,
                                     ROW_HEIGHT);

        verify(text1).setText(eq(PLACE_HOLDER));
        verify(text1).setX(BLOCK_WIDTH / 2);
        verify(text1).setY(ROW_HEIGHT / 2);

        verify(headerGroup).add(text1);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRenderNullCell() {
        assertThat(renderer.renderCell(null, bodyContext)).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRenderNullCellValue() {
        this.cell = new BaseGridCell<>(null);

        assertThat(renderer.renderCell(cell, bodyContext)).isNull();
    }
}
