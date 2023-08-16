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
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AddCanvasChildNodeCommandTest extends AbstractCanvasCommandTest {

    @Mock
    private Node<View<?>, Edge> parent;

    @Mock
    private View parentContent;

    @Mock
    private Node<View<?>, Edge> candidate;

    @Mock
    private View candidateContent;

    private AddCanvasChildNodeCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();
        when(candidate.getContent()).thenReturn(candidateContent);
        when(parent.getContent()).thenReturn(parentContent);
        when(candidateContent.getBounds()).thenReturn(Bounds.create(0d, 0d, 10d, 10d));
        when(parentContent.getBounds()).thenReturn(Bounds.create(0d, 0d, 10d, 10d));
        this.tested = new AddCanvasChildNodeCommand(parent,
                                                    candidate,
                                                    SHAPE_SET_ID);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        final CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertNotEquals(CommandResult.Type.ERROR,
                        result.getType());
        verify(canvasHandler,
               times(1)).register(eq(SHAPE_SET_ID),
                                  eq(candidate));
        verify(canvasHandler,
               times(1)).addChild(eq(parent),
                                  eq(candidate));
        verify(canvasHandler,
               times(1)).applyElementMutation(eq(candidate),
                                              any(MutationContext.class));
        verify(canvasHandler,
               times(1)).applyElementMutation(eq(parent),
                                              any(MutationContext.class));
    }
}
