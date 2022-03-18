/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class RemoveChildrenCommandTest extends AbstractCanvasCommandTest {

    @Mock
    private Node parent;
    @Mock
    private Node candidate;

    private RemoveChildrenCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();
        when(parent.getUUID()).thenReturn("uuid1");
        when(candidate.getUUID()).thenReturn("uuid2");
        this.tested = new RemoveChildrenCommand(parent,
                                                Collections.singleton(candidate));
    }

    @Test
    public void testGetGraphCommand() {
        final org.kie.workbench.common.stunner.core.graph.command.impl.RemoveChildrenCommand graphCommand =
                (org.kie.workbench.common.stunner.core.graph.command.impl.RemoveChildrenCommand) tested.newGraphCommand(canvasHandler);
        assertNotNull(graphCommand);
        assertEquals(parent,
                     graphCommand.getParent());
        assertEquals(candidate,
                     graphCommand.getCandidates().iterator().next());
    }

    @Test
    public void testGetCanvasCommand() {
        final RemoveCanvasChildrenCommand canvasCommand =
                (RemoveCanvasChildrenCommand) tested.newCanvasCommand(canvasHandler);
        assertNotNull(canvasCommand);
        assertEquals(parent,
                     canvasCommand.getParent());
        assertEquals(candidate,
                     canvasCommand.getChildren().iterator().next());
    }
}
