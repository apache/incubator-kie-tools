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

package org.kie.workbench.common.dmn.client.editors.expressions.util;

import java.util.Collections;
import java.util.Optional;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
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
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.ValueAndDataTypeHeaderMetaData;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class RendererUtilsTest {

    private static final String VALUE = "some text value";

    private static final String PLACE_HOLDER = "place holder text";

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
    private Text placeHolderText;

    @Mock
    private Transform transform;

    @Mock
    private EditableHeaderMetaData headerMetaData;

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
        when(gridTheme.getPlaceholderText()).thenReturn(placeHolderText);
        when(headerText1.asNode()).thenReturn(mock(Node.class));
        when(headerText2.asNode()).thenReturn(mock(Node.class));
        when(placeHolderText.asNode()).thenReturn(mock(Node.class));
    }

    @Test
    public void testRenderCenteredText() throws Exception {
        final BaseGridCell<String> cell = new BaseGridCell<>(new BaseGridCellValue<>(VALUE));

        RendererUtils.getCenteredCellText(cellContext, cell);

        assertCenteredRendering(text);
        assertNotRenderedPlaceHolder(placeHolderText);
    }

    @Test
    public void testRenderCenteredEmptyTextWithPlaceHolder() throws Exception {
        final BaseGridCell<String> cell = new BaseGridCell<>(new BaseGridCellValue<>(null, PLACE_HOLDER));

        RendererUtils.getCenteredCellText(cellContext, cell);

        assertCenteredRenderingPlaceholder(placeHolderText);
    }

    @Test
    public void testRenderExpressionCellText() throws Exception {
        final BaseGridCell<String> cell = new BaseGridCell<>(new BaseGridCellValue<>(VALUE));

        RendererUtils.getExpressionCellText(cellContext, cell);

        assertExpressionRendering();
    }

    @Test
    public void testRenderExpressionHeaderText() throws Exception {
        when(headerMetaData.getTitle()).thenReturn(VALUE);

        RendererUtils.getExpressionHeaderText(headerMetaData, headerContext);

        assertExpressionRendering();
    }

    @Test
    public void testRenderHeaderContentWithNameAndDataTypeHeaderMetaData() {
        final ValueAndDataTypeHeaderMetaData metaData = mock(ValueAndDataTypeHeaderMetaData.class);

        when(metaData.getTitle()).thenReturn(TITLE);
        when(metaData.getTypeRef()).thenReturn(TYPE_REF);

        RendererUtils.getValueAndDataTypeHeaderText(metaData,
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
        when(informationItemCell.hasData()).thenReturn(true);

        RendererUtils.getNameAndDataTypeCellText(informationItemCell,
                                                 bodyContext);

        assertHasNameAndDataTypeRendering();
    }

    @Test
    public void testRenderEditableHeaderText() {
        when(headerMetaData.getTitle()).thenReturn(VALUE);

        RendererUtils.getEditableHeaderText(headerMetaData, headerContext, WIDTH, HEIGHT);

        assertCenteredRendering(headerText1);
    }

    @Test
    public void testRenderEditableHeaderPlaceHolderText() {
        when(headerMetaData.getPlaceHolder()).thenReturn(Optional.of(VALUE));

        RendererUtils.getEditableHeaderPlaceHolderText(headerMetaData, headerContext, WIDTH, HEIGHT);

        assertCenteredRendering(placeHolderText);
    }

    @Test
    public void testRenderEditableHeaderPlaceHolderTextWhenEmpty() {
        when(headerMetaData.getPlaceHolder()).thenReturn(Optional.empty());

        RendererUtils.getEditableHeaderPlaceHolderText(headerMetaData, headerContext, WIDTH, HEIGHT);

        verify(headerGroup, never()).add(any(IPrimitive.class));
    }

    private void assertNotRenderedPlaceHolder(final Text placeHolderText) {
        verify(placeHolderText, never()).setText(PLACE_HOLDER);
    }

    private void assertCenteredRendering(final Text text) {
        verify(text).setText(VALUE);
        verify(text).setListening(false);
        verify(text).setX(WIDTH / 2);
        verify(text).setY(HEIGHT / 2);
    }

    private void assertCenteredRenderingPlaceholder(final Text text) {
        verify(text).setText(PLACE_HOLDER);
        verify(text).setListening(false);
        verify(text).setX(WIDTH / 2);
        verify(text).setY(HEIGHT / 2);
    }

    private void assertExpressionRendering() {
        verify(text).setText(VALUE);
        verify(text).setListening(false);
        verify(text).setX(5);
        verify(text).setY(5);
        verify(text).setFontFamily(BaseExpressionGridTheme.FONT_FAMILY_EXPRESSION);
        verify(text).setTextAlign(TextAlign.LEFT);
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
