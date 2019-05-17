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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.TestingGraphInstanceBuilder;
import org.kie.workbench.common.stunner.core.TestingGraphMockHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MorphCanvasNodeCommandTest extends AbstractCanvasCommandTest {

    protected static final String NEW_SHAPE_SET_ID = "ssid2";

    private MorphCanvasNodeCommand tested;

    @Mock
    private MorphDefinition morphDefinition;

    private TestingGraphInstanceBuilder.TestGraph4 graphInstance;

    @Mock
    private ViewConnector viewConnector;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        final TestingGraphMockHandler graphTestHandler = new TestingGraphMockHandler();
        graphInstance = TestingGraphInstanceBuilder.newGraph4(graphTestHandler);
        when(diagram.getGraph()).thenReturn(graphInstance.graph);
        when(graphIndex.getGraph()).thenReturn(graphInstance.graph);

        //mocking shapes
        StreamSupport.<Node>stream(graphInstance.graph.nodes().spliterator(), true)
                .map(node -> ((Node) node).getUUID())
                .forEach(uuid -> when(canvas.getShape((String) uuid)).thenReturn(mock(Shape.class)));
        when(canvas.getShape(graphInstance.edge1.getUUID())).thenReturn(mock(EdgeShape.class));
        when(canvas.getShape(graphInstance.edge2.getUUID())).thenReturn(mock(EdgeShape.class));
    }

    @Test
    public void executeDockedNode() {
        this.tested = new MorphCanvasNodeCommand(graphInstance.dockedNode, morphDefinition, NEW_SHAPE_SET_ID);
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        verify(canvasHandler).deregister(graphInstance.dockedNode);
        verify(canvasHandler).register(NEW_SHAPE_SET_ID, graphInstance.dockedNode);
        verify(canvasHandler).applyElementMutation(graphInstance.dockedNode, MutationContext.STATIC);
        verify(canvasHandler).undock(graphInstance.intermNode, graphInstance.dockedNode);
        verify(canvasHandler).dock(graphInstance.intermNode, graphInstance.dockedNode);
    }

    @Test
    public void executeIntermediateNode() {
        this.tested = new MorphCanvasNodeCommand(graphInstance.intermNode, morphDefinition, NEW_SHAPE_SET_ID);
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertFalse(CommandUtils.isError(result));
        verify(canvasHandler).deregister(graphInstance.intermNode);
        verify(canvasHandler).register(NEW_SHAPE_SET_ID, graphInstance.intermNode);
        verify(canvasHandler).applyElementMutation(graphInstance.intermNode, MutationContext.STATIC);
        verify(canvasHandler).removeChild(graphInstance.parentNode, graphInstance.intermNode);
        verify(canvasHandler).addChild(graphInstance.parentNode, graphInstance.intermNode);
    }
}