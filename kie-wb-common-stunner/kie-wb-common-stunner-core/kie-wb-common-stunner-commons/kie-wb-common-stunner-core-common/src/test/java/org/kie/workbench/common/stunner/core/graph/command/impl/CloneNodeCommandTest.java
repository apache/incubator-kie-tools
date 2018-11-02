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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Objects;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.definition.clone.ClonePolicy;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CloneNodeCommandTest extends AbstractCloneCommandTest {

    protected CloneNodeCommand cloneNodeCommand;

    protected Node<View, Edge> candidate;

    protected Node<View, Edge> parent;

    protected Point2D position;

    @Captor
    protected ArgumentCaptor<Bounds> boundsArgumentCaptor;

    @Before
    public void setUp() {
        super.setUp();

        candidate = graphInstance.containerNode;
        parent = graphInstance.parentNode;
        candidate.setContent(candidateContent);
        this.position = new Point2D(1, 1);
        this.cloneNodeCommand = new CloneNodeCommand(candidate, parent.getUUID(), position, null, childrenTraverseProcessorManagedInstance);
    }

    @Test
    public void testInitialize() throws Exception {

        cloneNodeCommand.initialize(graphCommandExecutionContext);

        Node<View, Edge> clone = cloneNodeCommand.getClone();
        assertEquals(clone, this.clone);

        RegisterNodeCommand registerNodeCommand = (RegisterNodeCommand) cloneNodeCommand.getCommands().stream().filter(command -> command instanceof RegisterNodeCommand).findFirst().get();
        assertNotNull(registerNodeCommand);
        assertEquals(registerNodeCommand.getCandidate(), clone);

        AddChildNodeCommand addChildCommand = (AddChildNodeCommand) cloneNodeCommand.getCommands().stream().filter(command -> command instanceof AddChildNodeCommand).findFirst().get();
        assertNotNull(addChildCommand);
        assertEquals(addChildCommand.getCandidate(), clone);
        assertEquals(addChildCommand.getParent(graphCommandExecutionContext), parent);
        assertEquals(addChildCommand.getLocation().getX(), position.getX(), 0);
        assertEquals(addChildCommand.getLocation().getY(), position.getY(), 0);
    }

    @Test
    public void testExecute() {
        cloneNodeCommand.execute(graphCommandExecutionContext);

        Function<Object, Boolean> cloneNodeInstanceFunction = command -> command instanceof CloneNodeCommand;
        assertTrue(checkCloneChildrenElement(cloneNodeInstanceFunction, getCheckChildrenNodeFunction(graphInstance.startNode)));
        assertTrue(checkCloneChildrenElement(cloneNodeInstanceFunction, getCheckChildrenNodeFunction(graphInstance.intermNode)));
        assertTrue(checkCloneChildrenElement(cloneNodeInstanceFunction, getCheckChildrenNodeFunction(graphInstance.endNode)));

        Function<Object, Boolean> cloneConnectorInstanceFunction = command -> command instanceof CloneConnectorCommand;
        assertTrue(checkCloneChildrenElement(cloneConnectorInstanceFunction, getCheckChildrenEdgeFunction(graphInstance.edge1)));
        assertTrue(checkCloneChildrenElement(cloneConnectorInstanceFunction, getCheckChildrenEdgeFunction(graphInstance.edge2)));
    }

    private Function<CloneNodeCommand, Boolean> getCheckChildrenNodeFunction(Node node) {
        return command -> Objects.equals(command.getCandidate(), node);
    }

    private Function<CloneConnectorCommand, Boolean> getCheckChildrenEdgeFunction(Edge edge) {
        return command -> Objects.equals(command.getCandidate(), edge);
    }

    private <T> boolean checkCloneChildrenElement(Function<Object, Boolean> commandFilter, Function<T, Boolean> candidateFilter) {
        return cloneNodeCommand.getChildrenCommands().stream()
                .filter(command -> commandFilter.apply(command))
                .map(command -> (T) command)
                .filter(command -> candidateFilter.apply(command))
                .findFirst()
                .isPresent();
    }

    @Test
    public void testDelegateRulesContextToChildren() throws Exception {
        assertFalse(cloneNodeCommand.delegateRulesContextToChildren());
    }

    @Test
    public void testUndo() throws Exception {
        testInitialize();
        cloneNodeCommand.undo(graphCommandExecutionContext);
        verify(graphIndex, times(1)).removeNode(clone);
    }

    @Test
    public void testCloneNodeContentWithProperties() {

        final View cloneContent = mock(View.class);
        final DefinitionManager definitionManager = mock(DefinitionManager.class);
        final CloneManager cloneManager = mock(CloneManager.class);
        final Object clonedDefinition = mock(Object.class);

        cloneNodeCommand.setClone(clone);

        when(clone.getContent()).thenReturn(cloneContent);

        when(graphCommandExecutionContext.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.cloneManager()).thenReturn(cloneManager);
        when(cloneManager.clone(candidateContent.getDefinition(), ClonePolicy.ALL)).thenReturn(clonedDefinition);

        cloneNodeCommand.cloneNodeContentWithProperties(graphCommandExecutionContext);

        verify(cloneContent).setBounds(boundsArgumentCaptor.capture());
        verify(cloneContent).setDefinition(clonedDefinition);

        final Bounds clonedBounds = boundsArgumentCaptor.getValue();

        assertEquals(candidateBounds, clonedBounds);
        assertNotSame(candidateBounds, clonedBounds);
    }
}
