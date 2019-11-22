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
package org.drools.workbench.screens.guided.dtable.client.widget.table;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.enterprise.event.Event;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.GwtMockito;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.drools.workbench.screens.guided.dtable.client.widget.table.themes.GuidedDecisionTableTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class GuidedDecisionTableViewImplTest {

    private static final double HEADER_ROW_HEIGHT = 32.0;

    @Mock
    private GridRenderer renderer;

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private EventSourceMock<NotificationEvent> notificationEvent;

    @Mock
    private ModelSynchronizer modelSynchronizer;

    @Mock
    private GridColumn<?> uiRowNumberColumn;

    @Mock
    private GridColumn<?> uiDescriptionColumn;

    @Mock
    private GridColumn<?> uiColumn1;

    @Mock
    private GridColumn<?> uiColumn2;

    @Mock
    private GuidedDecisionTableTheme theme;

    @Mock
    private Rectangle rectangle;

    @Mock
    private MultiPath border;

    @Mock
    private Text caption;

    @Mock
    private IPathClipper clipper;

    @Mock
    private Group container;

    @Mock
    private Group header;

    @Mock
    private Group floatingHeader;

    private GridData uiModel;

    private GuidedDecisionTable52 model;

    private TestGuidedDecisionTableViewImpl view;

    private List<GridColumn<?>> allColumns;

    @Before
    public void setup() {
        when(renderer.getTheme()).thenReturn(theme);
        when(theme.getBodyGridLine()).thenReturn(border);
        when(theme.getHeaderText()).thenReturn(caption);
        when(theme.getBaseRectangle(any(GuidedDecisionTableTheme.ModelColumnType.class))).thenReturn(rectangle);
        GwtMockito.useProviderForType(Group.class, clazz -> container);

        this.uiModel = new GuidedDecisionTableUiModel(modelSynchronizer);
        this.model = new GuidedDecisionTable52();
        this.view = new TestGuidedDecisionTableViewImpl(uiModel,
                                                        renderer,
                                                        presenter,
                                                        model,
                                                        notificationEvent) {
        };
        this.allColumns = Arrays.asList(uiRowNumberColumn, uiDescriptionColumn, uiColumn1, uiColumn2);
    }

    @Test
    public void testDrawHeaderWithFloatingColumn() {
        final BaseGridRendererHelper.RenderingInformation ri = makeRenderingInformation(Arrays.asList(uiDescriptionColumn, uiColumn1, uiColumn2),
                                                                                        Collections.singletonList(uiRowNumberColumn));

        view.drawHeader(ri);

        view.executeRenderQueueCommands(true);

        verify(header, never()).add(any(Group.class));
        verify(floatingHeader).add(eq(container));
    }

    @Test
    public void testDrawHeaderWithoutFloatingColumn() {
        final BaseGridRendererHelper.RenderingInformation ri = makeRenderingInformation(Arrays.asList(uiRowNumberColumn, uiDescriptionColumn, uiColumn1, uiColumn2),
                                                                                        Collections.emptyList());

        view.drawHeader(ri);

        view.executeRenderQueueCommands(true);

        verify(header).add(eq(container));
        verify(floatingHeader, never()).add(any(Group.class));
    }

    @Test
    public void testDrawHeaderWithoutFloatingColumnOrRowNumberOrDescriptionColumns() {
        final BaseGridRendererHelper.RenderingInformation ri = makeRenderingInformation(Arrays.asList(uiColumn1, uiColumn2),
                                                                                        Collections.emptyList());

        view.drawHeader(ri);

        view.executeRenderQueueCommands(true);

        verify(header, never()).add(any(Group.class));
        verify(floatingHeader, never()).add(any(Group.class));
    }

    private BaseGridRendererHelper.RenderingInformation makeRenderingInformation(final List<GridColumn<?>> bodyColumns,
                                                                                 final List<GridColumn<?>> floatingColumns) {
        view.setAllColumns(allColumns);
        view.setBodyColumns(bodyColumns);
        view.setFloatingColumns(floatingColumns);
        return new BaseGridRendererHelper.RenderingInformation(new BaseBounds(0, 0, 1000, 2000),
                                                               allColumns,
                                                               makeRenderingBlockInformation(bodyColumns),
                                                               makeRenderingBlockInformation(floatingColumns),
                                                               0,
                                                               1,
                                                               Collections.singletonList(0.0),
                                                               Collections.singletonList(0.0),
                                                               false,
                                                               false,
                                                               1,
                                                               HEADER_ROW_HEIGHT,
                                                               HEADER_ROW_HEIGHT,
                                                               0.0);
    }

    private BaseGridRendererHelper.RenderingBlockInformation makeRenderingBlockInformation(final List<GridColumn<?>> columns) {
        return new BaseGridRendererHelper.RenderingBlockInformation(columns,
                                                                    0.0,
                                                                    0.0,
                                                                    HEADER_ROW_HEIGHT,
                                                                    columns.stream().mapToDouble(GridColumn::getWidth).sum());
    }

    private class TestGuidedDecisionTableViewImpl extends GuidedDecisionTableViewImpl {

        public TestGuidedDecisionTableViewImpl(final GridData uiModel,
                                               final GridRenderer renderer,
                                               final Presenter presenter,
                                               final GuidedDecisionTable52 model,
                                               final Event<NotificationEvent> notificationEvent) {
            super(uiModel,
                  renderer,
                  presenter,
                  model,
                  notificationEvent);
            this.header = GuidedDecisionTableViewImplTest.this.header;
            this.floatingHeader = GuidedDecisionTableViewImplTest.this.floatingHeader;
        }

        @Override
        IPathClipper getPathClipper(final BoundingBox bb) {
            return clipper;
        }

        @Override
        public void executeRenderQueueCommands(final boolean isSelectionLayer) {
            super.executeRenderQueueCommands(isSelectionLayer);
        }

        void setAllColumns(final List<GridColumn<?>> allColumns) {
            this.allColumns.clear();
            this.allColumns.addAll(allColumns);
        }

        void setBodyColumns(final List<GridColumn<?>> bodyColumns) {
            this.bodyColumns.clear();
            this.bodyColumns.addAll(bodyColumns);
        }

        void setFloatingColumns(final List<GridColumn<?>> floatingColumns) {
            this.floatingColumns.clear();
            this.floatingColumns.addAll(floatingColumns);
        }
    }
}
