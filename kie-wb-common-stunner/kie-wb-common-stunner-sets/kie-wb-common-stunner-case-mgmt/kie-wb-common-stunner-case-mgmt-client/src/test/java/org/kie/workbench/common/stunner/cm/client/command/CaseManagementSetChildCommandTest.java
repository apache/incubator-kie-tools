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

package org.kie.workbench.common.stunner.cm.client.command;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class CaseManagementSetChildCommandTest extends CaseManagementAbstractCommandTest {

    private Node<View<?>, Edge> parent;

    private Node<View<?>, Edge> candidate;

    private CaseManagementSetChildCommand command;

    private int index;

    @Before
    public void setUp() throws Exception {
        super.setup();

        this.parent = CommandTestUtils.makeNode("uuid1",
                                                "parent",
                                                10.0,
                                                20.0,
                                                50.0,
                                                50.0);

        this.candidate = CommandTestUtils.makeNode("uuid2",
                                                   "candidate",
                                                   10.0,
                                                   20.0,
                                                   50.0,
                                                   50.0);

        this.command = new CaseManagementSetChildCommand(this.parent,
                                                         this.candidate);

        this.index = parent.getOutEdges().size();
    }

    @Test
    public void testNewGraphCommand() throws Exception {
        assertCommandSuccess(command.execute(canvasHandler));

        assertEquals(1,
                     parent.getOutEdges().size());
        assertEquals(1,
                     candidate.getInEdges().size());
        assertEquals(parent.getOutEdges().get(index),
                     candidate.getInEdges().get(0));

        final Edge edge = parent.getOutEdges().get(index);
        assertEquals(parent,
                     edge.getSourceNode());
        assertEquals(candidate,
                     edge.getTargetNode());
        assertTrue(edge.getContent() instanceof Child);
    }

    @Test
    public void testNewCanvasCommand() throws Exception {
        assertCommandSuccess(command.execute(canvasHandler));

        verify(canvasHandler).addChild(eq(parent),
                                       eq(candidate),
                                       eq(index));
    }
}