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

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteElementsCommandTest {

    private static final String SHAPE_SET_ID = "ss1";

    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private AbstractCanvas canvas;
    @Mock
    private Diagram diagram;
    @Mock
    private Metadata metadata;

    private TestingGraphInstanceBuilder.TestGraph2 graphHolder;
    private DeleteElementsCommand tested;

    @Before
    public void setup() throws Exception {
        TestingGraphMockHandler graphHandler = new TestingGraphMockHandler();
        this.graphHolder = TestingGraphInstanceBuilder.newGraph2(graphHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvasHandler.getGraphIndex()).thenReturn(graphHandler.graphIndex);
        when(canvasHandler.getGraphExecutionContext()).thenReturn(graphHandler.graphCommandExecutionContext);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(diagram.getGraph()).thenReturn(graphHandler.graph);
        when(metadata.getDefinitionSetId()).thenReturn(TestingGraphMockHandler.DEF_SET_ID);
        when(metadata.getShapeSetId()).thenReturn(SHAPE_SET_ID);
        when(metadata.getCanvasRootUUID()).thenReturn(graphHolder.parentNode.getUUID());
        this.tested = new DeleteElementsCommand(Arrays.asList(graphHolder.startNode,
                                                              graphHolder.intermNode,
                                                              graphHolder.endNode));
    }

    @Test
    public void testDeleteElements() {
        final CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertEquals(CommandResult.Type.INFO, result.getType());
        verify(canvasHandler, times(2)).removeChild(eq(graphHolder.parentNode),
                                                    eq(graphHolder.startNode));
        verify(canvasHandler, times(1)).deregister(eq(graphHolder.startNode));
        verify(canvasHandler, times(2)).removeChild(eq(graphHolder.parentNode),
                                                    eq(graphHolder.intermNode));
        verify(canvasHandler, times(1)).deregister(eq(graphHolder.intermNode));
        verify(canvasHandler, times(2)).removeChild(eq(graphHolder.parentNode),
                                                    eq(graphHolder.endNode));
        verify(canvasHandler, times(1)).deregister(eq(graphHolder.endNode));
    }
}
