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
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CanvasUndockNodeCommandTest extends AbstractCanvasCommandTest {

    private static final String P_ID = "p1";
    private static final String C_ID = "c1";

    @Mock
    private Node parent;
    @Mock
    private Node candidate;

    private CanvasUndockNodeCommand tested;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(parent.getUUID()).thenReturn(P_ID);
        when(candidate.getUUID()).thenReturn(C_ID);
        when(graphIndex.getNode(eq(P_ID))).thenReturn(parent);
        when(graphIndex.getNode(eq(C_ID))).thenReturn(candidate);
        this.tested = new CanvasUndockNodeCommand(parent,
                                                  candidate);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        final CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertNotEquals(CommandResult.Type.ERROR,
                        result.getType());
        verify(canvasHandler,
               times(0)).register(anyString(),
                                  eq(candidate));
        verify(canvasHandler,
               times(0)).addChild(any(Node.class),
                                  any(Node.class));
        verify(canvasHandler,
               times(1)).undock(eq(parent),
                                eq(candidate));
        verify(canvasHandler,
               times(1)).applyElementMutation(eq(candidate),
                                              any(MutationContext.class));
        verify(canvasHandler,
               times(1)).applyElementMutation(eq(parent),
                                              any(MutationContext.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteButCandidateHasBeenRemoved() {
        when(graphIndex.getNode(eq(C_ID))).thenReturn(null);
        final CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertNotEquals(CommandResult.Type.ERROR,
                        result.getType());
        verify(canvasHandler,
               times(0)).register(anyString(),
                                  eq(candidate));
        verify(canvasHandler,
               times(0)).addChild(any(Node.class),
                                  any(Node.class));
        verify(canvasHandler,
               times(1)).undock(eq(parent),
                                eq(candidate));
        verify(canvasHandler,
               never()).applyElementMutation(eq(candidate),
                                             any(MutationContext.class));
        verify(canvasHandler,
               times(1)).applyElementMutation(eq(parent),
                                              any(MutationContext.class));
    }
}
