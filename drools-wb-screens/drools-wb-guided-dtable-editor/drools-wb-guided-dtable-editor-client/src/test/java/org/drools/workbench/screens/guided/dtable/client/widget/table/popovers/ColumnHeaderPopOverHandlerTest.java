/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.popovers;

import java.util.ArrayList;
import java.util.HashSet;

import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ColumnHeaderPopOverHandlerTest {

    @Mock
    private GuidedDecisionTableModellerView.Presenter modellerPresenter;

    @Mock
    private ColumnHeaderPopOver columnPopOverPresenter;

    @Mock
    private Group header;

    @Mock
    private Viewport viewport;

    @Mock
    private DefaultGridLayer layer;

    @Mock
    private GuidedDecisionTableView.Presenter dtPresenter1;

    @Mock
    private GuidedDecisionTableView.Presenter dtPresenter2;

    @Mock
    private GuidedDecisionTableView dtView1;

    @Mock
    private GuidedDecisionTableView dtView2;

    @Mock
    private GridData uiModel;

    @Mock
    private GridColumn uiColumn;

    @Mock
    private GridRenderer renderer;

    @Mock
    private BaseGridRendererHelper helper;

    @Mock
    private BaseGridRendererHelper.ColumnInformation columnInformation;

    @Mock
    private NodeMouseMoveEvent event;

    private ColumnHeaderPopOverHandler handler;

    @Before
    public void setup() {
        when(renderer.getHeaderHeight()).thenReturn(64.0);
        when(renderer.getHeaderRowHeight()).thenReturn(32.0);
        when(helper.getColumnInformation(any(Double.class))).thenReturn(columnInformation);
        when(columnInformation.getColumn()).thenReturn(uiColumn);
        when(uiModel.getHeaderRowCount()).thenReturn(2);
        when(uiModel.getColumnCount()).thenReturn(1);
        when(uiModel.getColumns()).thenReturn(new ArrayList<GridColumn<?>>() {{
            add(uiColumn);
        }});
        when(uiColumn.getWidth()).thenReturn(100.0);

        setupDecisionTable(dtPresenter1,
                           dtView1);
        setupDecisionTable(dtPresenter2,
                           dtView2);

        this.handler = new ColumnHeaderPopOverHandler(modellerPresenter,
                                                      columnPopOverPresenter);
    }

    private void setupDecisionTable(final GuidedDecisionTableView.Presenter dtPresenter,
                                    final GuidedDecisionTableView dtView) {
        when(dtPresenter.getView()).thenReturn(dtView);
        when(dtView.isVisible()).thenReturn(true);
        when(dtView.getViewport()).thenReturn(viewport);
        when(dtView.getModel()).thenReturn(uiModel);
        when(dtView.getRenderer()).thenReturn(renderer);
        when(dtView.getRendererHelper()).thenReturn(helper);
        when(dtView.getLayer()).thenReturn(layer);
        when(dtView.getHeader()).thenReturn(header);
        when(dtView.getWidth()).thenReturn(100.0);
    }

    @Test
    public void popOverHiddenOnMouseMoveEvent() {
        handler.onNodeMouseMove(event);

        verify(columnPopOverPresenter,
               times(1)).hide();
    }

    @Test
    public void noPopOverWhenNoDecisionTables() {
        handler.onNodeMouseMove(event);

        verify(columnPopOverPresenter,
               never()).show(any(GuidedDecisionTableModellerView.class),
                             any(GuidedDecisionTableView.Presenter.class),
                             any(Integer.class));
    }

    @Test
    public void noPopOverWhenEventNotOverDecisionTableHeader() {
        when(modellerPresenter.getAvailableDecisionTables()).thenReturn(new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(dtPresenter1);
        }});
        when(event.getX()).thenReturn(50);
        when(event.getY()).thenReturn(100);
        when(dtView1.getComputedLocation()).thenReturn(new Point2D(0,
                                                                   0));

        handler.onNodeMouseMove(event);

        verify(columnPopOverPresenter,
               never()).show(any(GuidedDecisionTableModellerView.class),
                             any(GuidedDecisionTableView.Presenter.class),
                             any(Integer.class));
    }

    @Test
    public void noPopOverWhenEventNotOverDecisionTableColumn() {
        when(modellerPresenter.getAvailableDecisionTables()).thenReturn(new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(dtPresenter1);
        }});
        when(event.getX()).thenReturn(150);
        when(event.getY()).thenReturn(50);
        when(dtView1.getComputedLocation()).thenReturn(new Point2D(0,
                                                                   0));

        handler.onNodeMouseMove(event);

        verify(columnPopOverPresenter,
               never()).show(any(GuidedDecisionTableModellerView.class),
                             any(GuidedDecisionTableView.Presenter.class),
                             any(Integer.class));
    }

    @Test
    public void popOverWhenEventOverDecisionTableHeaderAndColumn() {
        when(modellerPresenter.getAvailableDecisionTables()).thenReturn(new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(dtPresenter1);
        }});
        when(event.getX()).thenReturn(50);
        when(event.getY()).thenReturn(50);
        when(dtView1.getComputedLocation()).thenReturn(new Point2D(0,
                                                                   0));

        handler.onNodeMouseMove(event);

        verify(columnPopOverPresenter,
               times(1)).show(any(),
                              eq(dtPresenter1),
                              eq(0));
    }

    @Test
    public void popOverWhenEventOverDecisionTableHeaderAndColumnWithMultipleTables() {
        when(modellerPresenter.getAvailableDecisionTables()).thenReturn(new HashSet<GuidedDecisionTableView.Presenter>() {{
            add(dtPresenter1);
            add(dtPresenter2);
        }});
        when(dtView1.getComputedLocation()).thenReturn(new Point2D(0,
                                                                   0));
        when(dtView2.getComputedLocation()).thenReturn(new Point2D(200,
                                                                   200));

        when(event.getX()).thenReturn(50);
        when(event.getY()).thenReturn(50);
        handler.onNodeMouseMove(event);

        verify(columnPopOverPresenter,
               times(1)).show(any(),
                              eq(dtPresenter1),
                              eq(0));

        when(event.getX()).thenReturn(250);
        when(event.getY()).thenReturn(250);
        handler.onNodeMouseMove(event);

        verify(columnPopOverPresenter,
               times(1)).show(any(),
                              eq(dtPresenter2),
                              eq(0));
    }
}
