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

package org.drools.workbench.screens.guided.dtable.client.widget.table.themes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.verifier.api.reporting.Severity;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.GuidedDecisionTableUiModel;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.GridRendererContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderBodyGridBackgroundCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderBodyGridContentCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderBodyGridLinesCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderHeaderGridLinesCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderSelectedCellsCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RenderSelectorCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer.RendererCommand;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper.RenderingBlockInformation;
import static org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRendererHelper.RenderingInformation;

@RunWith(LienzoMockitoTestRunner.class)
public class GuidedDecisionTableRendererTest {

    @Mock
    private ModelSynchronizer synchronizer;

    @Mock
    private GridBodyRenderContext context;

    @Mock
    private BaseGridRendererHelper rendererHelper;

    @Mock
    private RenderingInformation renderingInformation;

    @Mock
    private RenderingBlockInformation bodyBlockInformation;

    @Mock
    private RenderingBlockInformation floatingBlockInformation;

    @Mock
    private Group parent;

    private GuidedDecisionTableUiModel uiModel;

    private GridColumn<?> uiColumn;

    private GuidedDecisionTableRenderer renderer;

    @Before
    public void setup() {
        this.uiModel = new GuidedDecisionTableUiModel(synchronizer);
        this.uiColumn = new RowNumberColumn();

        this.uiModel.appendColumn(uiColumn);
        this.uiModel.appendRow(new BaseGridRow());

        this.renderer = spy(new GuidedDecisionTableRenderer(uiModel,
                                                            new GuidedDecisionTable52()));

        when(renderingInformation.getVisibleRowOffsets()).thenReturn(Collections.singletonList(20.0));
        when(renderingInformation.getAllRowHeights()).thenReturn(Collections.singletonList(20.0));
        when(context.getBlockColumns()).thenReturn(Collections.singletonList(uiColumn));
        when(renderingInformation.getBodyBlockInformation()).thenReturn(bodyBlockInformation);
        when(bodyBlockInformation.getColumns()).thenReturn(Collections.singletonList(uiColumn));
        when(renderingInformation.getFloatingBlockInformation()).thenReturn(floatingBlockInformation);
        when(floatingBlockInformation.getColumns()).thenReturn(Collections.emptyList());
        when(context.getRenderer()).thenReturn(renderer);
    }

    @Test
    public void testRenderBodyWithRowHighlights() {
        renderer.highlightRows(Severity.ERROR,
                               Collections.singleton(1));

        final List<RendererCommand> commands = renderer.renderBody(uiModel,
                                                                   context,
                                                                   rendererHelper,
                                                                   renderingInformation);

        assertRenderingCommands(commands,
                                RenderBodyGridBackgroundCommand.class,
                                RenderBodyGridLinesCommand.class,
                                RenderBodyGridContentCommand.class,
                                RenderSelectedCellsCommand.class);
    }

    @Test
    public void testRenderBodyWithNoRowHighlights() {
        renderer.clearHighlights();

        final List<RendererCommand> commands = renderer.renderBody(uiModel,
                                                                   context,
                                                                   rendererHelper,
                                                                   renderingInformation);

        assertRenderingCommands(commands,
                                RenderBodyGridBackgroundCommand.class,
                                RenderBodyGridLinesCommand.class,
                                RenderBodyGridContentCommand.class);
    }

    @Test
    public void testRenderSelector() {
        final RendererCommand command = renderer.renderSelector(10.0,
                                                                20.0,
                                                                renderingInformation);
        assertRenderingCommands(Collections.singletonList(command),
                                RenderSelectorCommand.class);
    }

    @Test
    public void testRenderSelectorIsSelectionLayer() {
        final RendererCommand command = renderer.renderSelector(10.0,
                                                                20.0,
                                                                renderingInformation);

        command.execute(makeGridRendererContext(true));

        verify(parent, never()).add(any(Group.class));
    }

    @Test
    public void testRenderSelectorIsNotSelectionLayer() {
        final RendererCommand command = renderer.renderSelector(10.0,
                                                                20.0,
                                                                renderingInformation);

        command.execute(makeGridRendererContext(false));

        verify(parent).add(any());
    }

    private GridRendererContext makeGridRendererContext(final boolean isSelectionLayer) {
        return new GridRendererContext() {
            @Override
            public Group getGroup() {
                return parent;
            }

            @Override
            public boolean isSelectionLayer() {
                return isSelectionLayer;
            }
        };
    }

    @Test
    public void testRenderHeaderBodyDivider() {
        final RendererCommand command = renderer.renderHeaderBodyDivider(10.0);
        assertRenderingCommands(Collections.singletonList(command),
                                RenderHeaderGridLinesCommand.class);
    }

    @SafeVarargs
    private final void assertRenderingCommands(final List<RendererCommand> actualCommands,
                                               final Class<? extends RendererCommand>... expectedTypes) {
        assertThat(actualCommands).hasOnlyElementsOfTypes(expectedTypes);
        Arrays.asList(expectedTypes).forEach(type -> assertThat(actualCommands).filteredOn(type::isInstance).hasSize(1));
    }
}
