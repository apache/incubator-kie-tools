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

package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl;

import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.StringPopupColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.GridRendererContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.SelectionsTransformer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.BlueTheme;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public abstract class BaseGridRendererTest {

    protected static final double WIDTH = 100.0;

    protected static final double HEIGHT = 200.0;

    @Mock
    protected GridColumnRenderer<String> columnRenderer;

    @Mock
    protected GridBodyRenderContext context;

    @Mock
    protected BaseGridRendererHelper rendererHelper;

    @Mock
    protected Group parent;

    @Mock
    protected GridRendererContext rc;

    @Captor
    protected ArgumentCaptor<List<GridColumn<?>>> columnsCaptor;

    @Captor
    protected ArgumentCaptor<SelectedRange> selectedRangeCaptor;

    protected GridData model;

    protected GridColumn<String> column;

    protected GridRendererTheme theme = new BlueTheme();

    protected BaseGridRenderer renderer;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        final BaseGridRenderer wrapped = new BaseGridRenderer(theme);
        this.renderer = spy(wrapped);

        this.column = makeGridColumn(100.0);

        this.model = new BaseGridData();
        this.model.appendColumn(column);
        this.model.appendRow(new BaseGridRow());
        this.model.appendRow(new BaseGridRow());
        this.model.appendRow(new BaseGridRow());

        setupSelectionContext();

        doCallRealMethod().when(rendererHelper).getWidth(anyList());

        when(rc.getGroup()).thenReturn(parent);
        when(rc.isSelectionLayer()).thenReturn(isSelectionLayer());
    }

    protected GridColumn<String> makeGridColumn(final double width) {
        return new StringPopupColumn(new BaseHeaderMetaData("title"),
                                     columnRenderer,
                                     width);
    }

    protected void setupSelectionContext() {
        final SelectionsTransformer selectionsTransformer = new DefaultSelectionsTransformer(model,
                                                                                             model.getColumns());

        when(context.getBlockColumns()).thenReturn(model.getColumns());
        when(context.getTransformer()).thenReturn(selectionsTransformer);
    }

    protected abstract boolean isSelectionLayer();
}
