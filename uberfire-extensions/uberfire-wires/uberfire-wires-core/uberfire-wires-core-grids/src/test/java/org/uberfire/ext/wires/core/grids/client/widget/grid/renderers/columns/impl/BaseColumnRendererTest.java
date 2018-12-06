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

package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.GwtMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public abstract class BaseColumnRendererTest<T, R extends GridColumnRenderer<T>> {

    private static final String TITLE = "title";

    @Mock
    private GridCell<T> cell;

    @Mock
    private GridCellValue<T> cellValue;

    @Mock
    private GridColumn gridColumn;

    @Mock
    private GridBodyCellRenderContext context;

    @Mock
    private GridHeaderColumnRenderContext headerContext;

    @Mock
    private BaseGridRendererHelper.RenderingInformation renderingInformation;

    @Mock
    private GridRenderer gridRenderer;

    @Mock
    private GridRendererTheme theme;

    @Mock
    private GridRenderer.GridRendererContext renderContext;

    @Mock
    private Text text;

    @Mock
    private Node textNode;

    @Mock
    private Text placeHolderText;

    @Mock
    private Node placeHolderTextNode;

    @GwtMock
    @SuppressWarnings("unused")
    private Group headerGroup;

    @Mock
    private Node headerGroupNode;

    @Mock
    private Group parentGroup;

    @Mock
    private Node parentGroupNode;

    @Mock
    protected GridColumn.HeaderMetaData headerMetaData;

    @Mock
    protected IPathClipper boundingBoxPathClipper;

    protected R renderer;

    protected String PLACEHOLDER = "PLACEHOLDER";

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.renderer = getRenderer();
        this.gridColumn = getGridColumn();

        final GridData uiModel = new BaseGridData();
        uiModel.appendColumn(gridColumn);

        when(context.getRenderer()).thenReturn(gridRenderer);
        when(headerContext.getRenderer()).thenReturn(gridRenderer);
        when(headerContext.getAllColumns()).thenReturn(uiModel.getColumns());
        when(headerContext.getBlockColumns()).thenReturn(uiModel.getColumns());
        when(gridRenderer.getTheme()).thenReturn(theme);
        when(theme.getBodyText()).thenReturn(text);
        when(theme.getHeaderText()).thenReturn(text);
        when(theme.getPlaceholderText()).thenReturn(placeHolderText);
        when(text.asNode()).thenReturn(textNode);
        when(placeHolderText.asNode()).thenReturn(placeHolderTextNode);
        when(headerGroup.asNode()).thenReturn(headerGroupNode);
        when(parentGroup.asNode()).thenReturn(parentGroupNode);
        when(renderContext.getGroup()).thenReturn(parentGroup);
        when(headerMetaData.getTitle()).thenReturn(TITLE);
    }

    protected abstract R getRenderer();

    protected abstract T getValueToRender();

    protected abstract GridColumn getGridColumn();

    @Test
    public void testNullCell() {
        assertNull(renderer.renderCell(null, context));
    }

    @Test
    public void testNullCellValue() {
        doReturn(null).when(cell).getValue();

        assertNull(renderer.renderCell(cell, context));
    }

    @Test
    public void testNullCellValueValue() {
        doReturn(cellValue).when(cell).getValue();
        doReturn(null).when(cellValue).getValue();

        assertNull(renderer.renderCell(cell, context));
    }

    @Test
    public void testRendering() {
        doReturn(cellValue).when(cell).getValue();
        doReturn(getValueToRender()).when(cellValue).getValue();

        final Group g = renderer.renderCell(cell, context);
        assertNotNull(g);

        assertEquals(1,
                     g.getChildNodes().size());
        assertEquals(text,
                     g.getChildNodes().get(0));
    }

    @Test
    public void testRenderingPlaceHolder() {
        doReturn(cellValue).when(cell).getValue();
        doReturn(null).when(cellValue).getValue();
        doReturn(PLACEHOLDER).when(cellValue).getPlaceHolder();

        final Group g = renderer.renderCell(cell, context);
        assertNotNull(g);

        assertEquals(1,
                     g.getChildNodes().size());
        assertEquals(placeHolderText,
                     g.getChildNodes().get(0));
        verify(placeHolderText, times(1)).setText(eq(PLACEHOLDER));
    }

    @Test
    public void testRenderHeader() {
        final List<GridRenderer.RendererCommand> commands = renderer.renderHeader(Collections.singletonList(headerMetaData),
                                                                                  headerContext,
                                                                                  renderingInformation,
                                                                                  (isSelectionLayer, gridColumn) -> true);

        assertRenderingCommands(commands,
                                GridRenderer.RenderHeaderGridLinesCommand.class, GridRenderer.RenderHeaderContentCommand.class);

        commands.stream()
                .filter(command -> command instanceof GridRenderer.RenderHeaderContentCommand)
                .findFirst()
                .ifPresent(command -> command.execute(renderContext));

        verify(text).setText(eq(TITLE));
        verify(text).setX(gridColumn.getWidth() / 2);
        verify(text).setY(0.0);

        verify(headerGroup).add(text);
    }

    @SafeVarargs
    private final void assertRenderingCommands(final List<GridRenderer.RendererCommand> actualCommands,
                                               final Class<? extends GridRenderer.RendererCommand>... expectedTypes) {
        assertThat(actualCommands).asList().hasOnlyElementsOfTypes(expectedTypes);
        Arrays.asList(expectedTypes).forEach(type -> assertThat(actualCommands).asList().filteredOn(type::isInstance).hasSize(1));
    }
}
