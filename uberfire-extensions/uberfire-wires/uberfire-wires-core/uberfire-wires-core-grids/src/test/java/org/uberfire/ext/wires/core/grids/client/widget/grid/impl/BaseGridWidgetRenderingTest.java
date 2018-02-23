/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import java.util.Collections;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBoundaryRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.multiple.impl.CheckBoxDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.BooleanDOMElementColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.CellSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.GridPinnedModeManager;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetRenderingTestUtils.ROW_HEIGHT;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidgetRenderingTestUtils.makeRenderingInformation;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseGridWidgetRenderingTest {

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    @Mock
    private DefaultGridLayer gridLayer;

    @Mock
    private GridSelectionManager selectionManager;

    @Mock
    private CellSelectionManager cellSelectionManager;

    @Mock
    private GridPinnedModeManager pinnedModeManager;

    @Mock
    private GridRenderer renderer;

    @Mock
    private BaseGridRendererHelper rendererHelper;

    @Mock
    private Group header;

    @Mock
    private Group body;

    @Mock
    private Group selections;

    @Mock
    private Group boundary;

    private BaseGridWidget gridWidget;

    private GridData model;

    @Before
    public void setup() {
        this.model = new BaseGridData();
        final BaseGridWidget wrapped = new BaseGridWidget(model,
                                                          selectionManager,
                                                          pinnedModeManager,
                                                          renderer) {
            @Override
            protected CellSelectionManager getCellSelectionManager() {
                return BaseGridWidgetRenderingTest.this.cellSelectionManager;
            }

            @Override
            protected BaseGridRendererHelper getBaseGridRendererHelper() {
                return BaseGridWidgetRenderingTest.this.rendererHelper;
            }
        };
        gridWidget = spy(wrapped);

        mockCanvas();
        mockHeader();
        mockBody();
        mockSelections();
        mockBoundary();
    }

    private void mockCanvas() {
        when(gridWidget.getLayer()).thenReturn(gridLayer);
        when(gridWidget.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(transform);
    }

    @SuppressWarnings("unchecked")
    private void mockHeader() {
        when(header.asNode()).thenReturn(mock(Node.class));
        when(renderer.renderHeader(any(GridData.class),
                                   any(GridHeaderRenderContext.class),
                                   eq(rendererHelper),
                                   any(BaseGridRendererHelper.RenderingInformation.class))).thenReturn(Collections.singletonList((rc) -> rc.getGroup().add(header)));
    }

    @SuppressWarnings("unchecked")
    private void mockBody() {
        when(body.asNode()).thenReturn(mock(Node.class));
        when(renderer.renderBody(any(GridData.class),
                                 any(GridBodyRenderContext.class),
                                 eq(rendererHelper),
                                 any(BaseGridRendererHelper.RenderingInformation.class))).thenReturn(Collections.singletonList((rc) -> rc.getGroup().add(body)));
    }

    @SuppressWarnings("unchecked")
    private void mockSelections() {
        when(selections.asNode()).thenReturn(mock(Node.class));
        when(renderer.renderSelectedCells(any(GridData.class),
                                          any(GridBodyRenderContext.class),
                                          eq(rendererHelper))).thenReturn((rc) -> rc.getGroup().add(selections));
    }

    @SuppressWarnings("unchecked")
    private void mockBoundary() {
        when(boundary.asNode()).thenReturn(mock(Node.class));
        when(renderer.renderGridBoundary(any(GridBoundaryRenderContext.class))).thenReturn((rc) -> rc.getGroup().add(boundary));
    }

    @Test
    public void renderingWithDOMElementColumnsAndRows() {
        final BaseGridRendererHelper.RenderingInformation ri = makeRenderingInformation(model,
                                                                                        Collections.singletonList(0d));
        when(rendererHelper.getRenderingInformation()).thenReturn(ri);

        final BooleanDOMElementColumn column = spy(new BooleanDOMElementColumn(new BaseHeaderMetaData("col1"),
                                                                               new CheckBoxDOMElementFactory(gridLayer,
                                                                                                             gridWidget),
                                                                               100.0));

        model.appendColumn(column);
        model.appendRow(new BaseGridRow(ROW_HEIGHT));

        final Context2D context2D = mock(Context2D.class);
        final BoundingBox boundingBox = mock(BoundingBox.class);

        gridWidget.drawWithTransforms(context2D,
                                      1.0,
                                      boundingBox);

        verify(column,
               times(1)).initialiseResources();
        verify(column,
               times(1)).freeUnusedResources();
        verify(gridWidget,
               times(1)).drawHeader(eq(ri));
        verify(gridWidget,
               times(1)).drawBody(eq(ri));
    }

    @Test
    public void renderingWithDOMElementColumnsAndWithoutRows() {
        final BaseGridRendererHelper.RenderingInformation ri = makeRenderingInformation(model,
                                                                                        Collections.emptyList());
        when(rendererHelper.getRenderingInformation()).thenReturn(ri);

        final BooleanDOMElementColumn column = spy(new BooleanDOMElementColumn(new BaseHeaderMetaData("col1"),
                                                                               new CheckBoxDOMElementFactory(gridLayer,
                                                                                                             gridWidget),
                                                                               100.0));

        model.appendColumn(column);

        final Context2D context2D = mock(Context2D.class);
        final BoundingBox boundingBox = mock(BoundingBox.class);

        gridWidget.drawWithTransforms(context2D,
                                      1.0,
                                      boundingBox);

        verify(column,
               times(1)).initialiseResources();
        verify(column,
               times(1)).freeUnusedResources();
        verify(gridWidget,
               times(1)).drawHeader(eq(ri));
        verify(gridWidget,
               never()).drawBody(any(BaseGridRendererHelper.RenderingInformation.class));
    }
}
