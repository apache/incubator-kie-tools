/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.presenters;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.widgets.canvas.view.LienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.canvas.wires.WiresCanvasPresenter;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.mockito.Mock;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public abstract class AbstractCanvasHandlerViewerTest {

    protected final static Bounds GRAPH_BOUNDS = new BoundsImpl(new BoundImpl(0d,
                                                                              0d),
                                                                new BoundImpl(100d,
                                                                              100d));

    @Mock
    protected WiresCanvasPresenter canvas;
    @Mock
    protected WiresCanvas.View canvasView;
    @Mock
    protected Widget canvasViewWidget;
    @Mock
    protected LienzoPanel lienzoPanel;
    @Mock
    protected AbstractCanvasHandler canvasHandler;
    @Mock
    protected Diagram diagram;
    @Mock
    protected Metadata metadata;
    @Mock
    protected Graph graph;
    @Mock
    protected DefinitionSet graphContent;

    protected Diagram canvasHandlerDiagram;

    @SuppressWarnings("unchecked")
    public void init() throws Exception {
        this.canvasHandlerDiagram = null;
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.asWidget()).thenReturn(canvasViewWidget);
        when(canvas.getLienzoPanel()).thenReturn(lienzoPanel);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getAbstractCanvas()).thenReturn(canvas);
        // The different viewer/editors tested reply on the canvas handler to obtain
        // the diagram instance, so applying mocked answers for CanvasHandler #open,
        // #clear and #destroy to handle the right diagram instance.
        doAnswer(invocationOnMock -> {
            final Diagram d = (Diagram) invocationOnMock.getArguments()[0];
            final ParameterizedCommand<CommandResult<?>> c = (ParameterizedCommand) invocationOnMock.getArguments()[1];
            AbstractCanvasHandlerViewerTest.this.canvasHandlerDiagram = d;
            when(canvasHandler.getDiagram()).thenReturn(canvasHandlerDiagram);
            c.execute(CanvasCommandResultBuilder.SUCCESS);
            return null;
        }).when(canvasHandler).draw(any(Diagram.class),
                                    any(ParameterizedCommand.class));
        doAnswer(invocationOnMock -> {
            AbstractCanvasHandlerViewerTest.this.canvasHandlerDiagram = null;
            when(canvasHandler.getDiagram()).thenReturn(canvasHandlerDiagram);
            return null;
        }).when(canvasHandler).clear();
        doAnswer(invocationOnMock -> {
            AbstractCanvasHandlerViewerTest.this.canvasHandlerDiagram = null;
            when(canvasHandler.getDiagram()).thenReturn(canvasHandlerDiagram);
            return null;
        }).when(canvasHandler).destroy();
        when(diagram.getMetadata()).thenReturn(metadata);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.getContent()).thenReturn(graphContent);
        when(graphContent.getBounds()).thenReturn(GRAPH_BOUNDS);
    }
}
