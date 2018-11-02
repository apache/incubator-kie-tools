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

package org.kie.workbench.common.stunner.cm.client.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.kie.workbench.common.stunner.cm.client.canvas.CaseManagementCanvasHandler;
import org.kie.workbench.common.stunner.cm.client.canvas.CaseManagementCanvasPresenter;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndex;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public abstract class CaseManagementAbstractCommandTest {

    protected static final String DEF_SET_ID = "dsid1";
    protected static final String SHAPE_SET_ID = "ssid1";
    protected static final String CANVAS_ROOT_UUID = "rootUUID1";

    @Mock
    protected CaseManagementCanvasHandler canvasHandler;

    @Mock
    protected CaseManagementCanvasPresenter canvas;

    @Mock
    protected Diagram diagram;

    @Mock
    protected Metadata metadata;

    @Mock
    protected GraphCommandExecutionContext context;

    protected Graph<String, Node> graph;
    protected Index index;

    @Mock
    protected Node node;

    @Mock
    protected Node parent;

    @Mock
    protected Node candidate;

    protected String shapeUUID;


    protected static void assertCommandSuccess(final CommandResult<? extends RuleViolation> result) {
        final List<RuleViolation> violations = new ArrayList<>();
        result.getViolations().forEach(violations::add);

        assertEquals(0,
                     violations.size());
        assertEquals(CommandResult.Type.INFO,
                     result.getType());
    }

    @SuppressWarnings("unchecked")
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.graph = new GraphImpl<>("graph",
                                     new GraphNodeStoreImpl());
        this.index = new MapIndex(graph,
                                  new HashMap<>(),
                                  new HashMap<>());

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getGraphExecutionContext()).thenReturn(context);
        when(context.getGraphIndex()).thenReturn(index);
        when(canvasHandler.getGraphIndex()).thenReturn(index);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(diagram.getGraph()).thenReturn(graph);
        when(metadata.getDefinitionSetId()).thenReturn(DEF_SET_ID);
        when(metadata.getShapeSetId()).thenReturn(SHAPE_SET_ID);
        when(metadata.getCanvasRootUUID()).thenReturn(CANVAS_ROOT_UUID);

        when(node.getUUID()).thenReturn(UUID.randomUUID().toString());
        when(parent.getUUID()).thenReturn(UUID.randomUUID().toString());
        when(candidate.getUUID()).thenReturn(UUID.randomUUID().toString());

        shapeUUID = UUID.randomUUID().toString();
    }
}
