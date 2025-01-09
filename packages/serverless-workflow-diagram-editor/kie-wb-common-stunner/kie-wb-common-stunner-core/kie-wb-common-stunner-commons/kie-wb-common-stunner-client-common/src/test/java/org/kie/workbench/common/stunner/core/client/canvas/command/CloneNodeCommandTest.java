/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CloneNodeCommandTest extends AbstractCanvasCommandTest {

    @Mock
    private Node candidate;

    @Mock
    private View candidateContent;

    private static final String NODE_UUID = UUID.uuid();

    private static final String PARENT_UUID = UUID.uuid();

    private CloneNodeCommand cloneNodeCommand;

    @Mock
    private ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessorManagedInstance;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(candidate.getUUID()).thenReturn(NODE_UUID);
        when(candidate.getContent()).thenReturn(candidateContent);
        when(childrenTraverseProcessorManagedInstance.get()).thenReturn(new ChildrenTraverseProcessorImpl(new TreeWalkTraverseProcessorImpl()));

        Point2D position = new Point2D(1, 1);
        this.cloneNodeCommand = new CloneNodeCommand(candidate, PARENT_UUID, position, null, childrenTraverseProcessorManagedInstance);
    }

    @Test
    public void testNewGraphCommand() {
        Command<GraphCommandExecutionContext, RuleViolation> graphCommand = cloneNodeCommand.newGraphCommand(canvasHandler);
        assertTrue(graphCommand instanceof org.kie.workbench.common.stunner.core.graph.command.impl.CloneNodeCommand);
    }

    @Test
    public void testNewCanvasCommand() {
        Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = cloneNodeCommand.newCanvasCommand(canvasHandler);
        assertTrue(canvasCommand instanceof CompositeCommand);
    }
}
