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

package org.kie.workbench.common.dmn.client.editors.expressions.util;

import java.util.Collections;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.GwtMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGridTheme;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.NameAndDataTypeHeaderMetaData;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class RendererUtilsTest {

    private static final String VALUE = "some text value";

    private static final double WIDTH = 200;

    private static final double HEIGHT = 80;

    private static final String TITLE = "title";

    private static final QName TYPE_REF = new QName();

    private static final int BLOCK_WIDTH = 100;

    private static final double ROW_HEIGHT = 32.0;

    private Text text;

    @Mock
    private GridRenderer gridRenderer;

    @Mock
    private GridRendererTheme gridTheme;

    @Mock
    private GridBodyCellRenderContext cellContext;

    @Mock
    private LiteralExpressionColumn uiColumn;

    @Mock
    private GridData uiModel;

    @Mock
    private Text headerText1;

    @Mock
    private Text headerText2;

    @Mock
    private Transform transform;

    @GwtMock
    @SuppressWarnings("unused")
    private Group headerGroup;

    private GridHeaderColumnRenderContext headerContext;

    private GridBodyCellRenderContext bodyContext;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        text = spy(new Text(""));
        headerContext = new GridHeaderColumnRenderContext(0,
                                                          Collections.singletonList(uiColumn),
                                                          Collections.singletonList(uiColumn),
                                                          0,
                                                          uiModel,
                                                          gridRenderer);
        bodyContext = new GridBodyCellRenderContext(0,
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

        when(cellContext.getRenderer()).thenReturn(gridRenderer);
        when(gridRenderer.getTheme()).thenReturn(gridTheme);
        when(gridTheme.getBodyText()).thenReturn(text);
        when(cellContext.getCellWidth()).thenReturn(WIDTH);
        when(cellContext.getCellHeight()).thenReturn(HEIGHT);

        when(gridTheme.getHeaderText()).thenReturn(headerText1, headerText2);
        when(headerText1.asNode()).thenReturn(mock(Node.class));
        when(headerText2.asNode()).thenReturn(mock(Node.class));
    }

    @Test
    public void testCenteredText() throws Exception {
        final BaseGridCell<String> cell = new BaseGridCell<>(new BaseGridCellValue<>(VALUE));

        RendererUtils.getCenteredCellText(cellContext, cell);

        verify(text).setText(VALUE);
        verify(text).setListening(false);
        verify(text).setX(WIDTH / 2);
        verify(text).setY(HEIGHT / 2);
    }

    @Test
    public void testLeftAlignTest() throws Exception {
        final BaseGridCell<String> cell = new BaseGridCell<>(new BaseGridCellValue<>(VALUE));

        RendererUtils.getExpressionCellText(cellContext, cell);

        verify(text).setText(VALUE);
        verify(text).setListening(false);
        verify(text).setX(5);
        verify(text).setY(5);
        verify(text).setFontFamily(BaseExpressionGridTheme.FONT_FAMILY_EXPRESSION);
        verify(text).setTextAlign(TextAlign.LEFT);
    }

    @Test
    public void testRenderHeaderContentWithNameAndDataTypeHeaderMetaData() {
        final NameAndDataTypeHeaderMetaData metaData = mock(NameAndDataTypeHeaderMetaData.class);

        when(metaData.getTitle()).thenReturn(TITLE);
        when(metaData.getTypeRef()).thenReturn(TYPE_REF);

        RendererUtils.getNameAndDataTypeText(metaData,
                                             headerContext,
                                             BLOCK_WIDTH,
                                             ROW_HEIGHT);

        assertHasNameAndDataTypeRendering();
    }

    @Test
    public void testRenderHeaderContentWithInformationItemCell() {
        final InformationItemCell.HasNameAndDataTypeCell informationItemCell = mock(InformationItemCell.HasNameAndDataTypeCell.class);
        final Name name = new Name(TITLE);

        when(informationItemCell.getName()).thenReturn(name);
        when(informationItemCell.getTypeRef()).thenReturn(TYPE_REF);

        RendererUtils.getNameAndDataTypeText(informationItemCell,
                                             bodyContext);

        assertHasNameAndDataTypeRendering();
    }

    private void assertHasNameAndDataTypeRendering() {
        verify(headerText1).setText(eq(TITLE));
        verify(headerText1).setX(BLOCK_WIDTH / 2);
        verify(headerText1).setY(ROW_HEIGHT / 2 - RendererUtils.SPACING);

        verify(headerText2).setText(eq("(" + TYPE_REF + ")"));
        verify(headerText2).setX(BLOCK_WIDTH / 2);
        verify(headerText2).setY(ROW_HEIGHT / 2 + RendererUtils.SPACING);
        verify(headerText2).setFontStyle(RendererUtils.FONT_STYLE_TYPE_REF);
        verify(headerText2).setFontSize(BaseExpressionGridTheme.FONT_SIZE - 2.0);

        verify(headerGroup).add(headerText1);
        verify(headerGroup).add(headerText2);
    }
}
