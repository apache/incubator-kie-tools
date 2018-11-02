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

package org.kie.workbench.common.stunner.cm.client.command.graph;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementRemoveChildCommandTest extends CaseManagementAbstractGraphCommandTest {

    private CaseManagementRemoveChildCommand tested;

    @Before
    public void setUp() {
        super.setup();

        final Edge edge = mock(Edge.class);
        when(edge.getTargetNode()).thenReturn(candidate);
        parent.getOutEdges().add(edge);

        tested = new CaseManagementRemoveChildCommand(parent, candidate);
    }

    @Test
    public void testUndo() {
        parent.getOutEdges().clear();

        tested.undo(context);

        assertEquals(1,
                     parent.getOutEdges().size());
        assertEquals(1,
                     candidate.getInEdges().size());
        assertEquals(parent.getOutEdges().get(0),
                     candidate.getInEdges().get(0));

        final Edge edge = parent.getOutEdges().get(0);
        assertEquals(parent,
                     edge.getSourceNode());
        assertEquals(candidate,
                     edge.getTargetNode());
        assertTrue(edge.getContent() instanceof Child);
    }
}