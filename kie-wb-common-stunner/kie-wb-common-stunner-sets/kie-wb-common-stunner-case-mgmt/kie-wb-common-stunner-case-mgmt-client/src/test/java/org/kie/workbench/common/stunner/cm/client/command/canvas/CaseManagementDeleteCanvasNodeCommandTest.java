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

package org.kie.workbench.common.stunner.cm.client.command.canvas;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommandTest;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementDeleteCanvasNodeCommandTest extends AbstractCanvasCommandTest {

    private static final String C_ID = "c1";
    private static final String P_ID = "p1";

    @Mock
    private Node candidate;

    @Mock
    private Node parent;

    @Mock
    private View view;

    private final int index = 0;

    private CaseManagementDeleteCanvasNodeCommand tested;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        when(candidate.getUUID()).thenReturn(C_ID);
        when(parent.getUUID()).thenReturn(P_ID);
        when(candidate.getContent()).thenReturn(view);
        when(view.getBounds()).thenReturn(new BoundsImpl(new BoundImpl(0d, 0d), new BoundImpl(10d, 10d)));
        this.tested = new CaseManagementDeleteCanvasNodeCommand(candidate,
                                                                parent,
                                                                index);
    }

    @Test
    public void testCreateUndoCommand() {
        final AbstractCanvasCommand command = this.tested.createUndoCommand(parent, candidate, "ssid");

        assertNotNull(command);
        assertTrue(command instanceof CaseManagementAddChildNodeCanvasCommand);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUndo() {
        tested.undo(canvasHandler);

        verify(canvasHandler).register(SHAPE_SET_ID, candidate);
        verify(canvasHandler).addChild(parent, candidate, index);

        verify(canvasHandler).applyElementMutation(eq(candidate), anyObject());
        verify(canvasHandler, never()).applyElementMutation(eq(parent), anyObject());
    }
}