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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.GwtMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class NameAndDataTypeColumnRendererTest {

    private static final String TITLE = "title";

    private static final QName TYPE_REF = new QName();

    private static final int BLOCK_WIDTH = 100;

    private static final double ROW_HEIGHT = 32.0;

    @Mock
    private TextAreaSingletonDOMElementFactory factory;

    @Mock
    private LiteralExpressionColumn uiColumn;

    @Mock
    private GridData uiModel;

    @Mock
    private GridRenderer gridRenderer;

    @Mock
    private GridRendererTheme gridRendererTheme;

    @Mock
    private Text headerText1;

    @Mock
    private Text headerText2;

    @GwtMock
    @SuppressWarnings("unused")
    private Group headerGroup;

    private GridHeaderColumnRenderContext context;

    private List<GridColumn.HeaderMetaData> headerMetaData;

    private NameAndDataTypeColumnRenderer renderer;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.renderer = new NameAndDataTypeColumnRenderer(factory);
        this.context = new GridHeaderColumnRenderContext(0,
                                                         Collections.singletonList(uiColumn),
                                                         Collections.singletonList(uiColumn),
                                                         0,
                                                         uiModel,
                                                         gridRenderer);

        when(gridRenderer.getTheme()).thenReturn(gridRendererTheme);
        when(gridRendererTheme.getHeaderText()).thenReturn(headerText1, headerText2);
        when(headerText1.asNode()).thenReturn(mock(Node.class));
        when(headerText2.asNode()).thenReturn(mock(Node.class));
    }

    @Test
    public void testRenderHeaderContentWithNormalColumnHeaderMetaData() {
        final GridColumn.HeaderMetaData metaData = mock(GridColumn.HeaderMetaData.class);
        this.headerMetaData = Collections.singletonList(metaData);

        when(metaData.getTitle()).thenReturn(TITLE);

        renderer.renderHeaderContent(context,
                                     headerMetaData,
                                     0,
                                     BLOCK_WIDTH,
                                     ROW_HEIGHT);

        verify(headerText1).setText(eq(TITLE));
        verify(headerText1).setX(BLOCK_WIDTH / 2);
        verify(headerText1).setY(ROW_HEIGHT / 2);

        verify(headerGroup).add(headerText1);
        verify(headerGroup, never()).add(headerText2);
    }

    @Test
    public void testRenderHeaderContentWithNameAndDataTypeHeaderMetaData() {
        final NameAndDataTypeHeaderMetaData metaData = mock(NameAndDataTypeHeaderMetaData.class);
        this.headerMetaData = Collections.singletonList(metaData);

        when(metaData.getTitle()).thenReturn(TITLE);
        when(metaData.getTypeRef()).thenReturn(TYPE_REF);

        renderer.renderHeaderContent(context,
                                     headerMetaData,
                                     0,
                                     BLOCK_WIDTH,
                                     ROW_HEIGHT);

        verify(headerText1).setText(eq(TITLE));
        verify(headerText1).setX(BLOCK_WIDTH / 2);
        verify(headerText1).setY(ROW_HEIGHT / 2 - NameAndDataTypeColumnRenderer.SPACING);

        verify(headerText2).setText(eq("(" + TYPE_REF + ")"));
        verify(headerText2).setX(BLOCK_WIDTH / 2);
        verify(headerText2).setY(ROW_HEIGHT / 2 + NameAndDataTypeColumnRenderer.SPACING);
        verify(headerText2).setFontStyle(NameAndDataTypeColumnRenderer.FONT_STYLE_TYPE_REF);
        verify(headerText2).setFontSize(BaseExpressionGridTheme.FONT_SIZE - 2.0);

        verify(headerGroup).add(headerText1);
        verify(headerGroup).add(headerText2);
    }
}
