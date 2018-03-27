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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

public abstract class AbstractCanvasCommandTest {

    protected static final String DEF_SET_ID = "dsid1";
    protected static final String SHAPE_SET_ID = "ssid1";
    protected static final String CANVAS_ROOT_UUID = "rootUUID1";

    @Mock
    protected AbstractCanvasHandler canvasHandler;
    @Mock
    protected AbstractCanvas canvas;
    @Mock
    protected Diagram diagram;
    @Mock
    protected Graph graph;
    @Mock
    protected Metadata metadata;
    @Mock
    protected Index<?, ?> graphIndex;
    @Mock
    protected CanvasCommandFactory canvasCommandFactory;

    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getGraphIndex()).thenReturn(graphIndex);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(diagram.getGraph()).thenReturn(graph);
        when(graphIndex.getGraph()).thenReturn(graph);
        when(metadata.getDefinitionSetId()).thenReturn(DEF_SET_ID);
        when(metadata.getShapeSetId()).thenReturn(SHAPE_SET_ID);
        when(metadata.getCanvasRootUUID()).thenReturn(CANVAS_ROOT_UUID);
    }
}
