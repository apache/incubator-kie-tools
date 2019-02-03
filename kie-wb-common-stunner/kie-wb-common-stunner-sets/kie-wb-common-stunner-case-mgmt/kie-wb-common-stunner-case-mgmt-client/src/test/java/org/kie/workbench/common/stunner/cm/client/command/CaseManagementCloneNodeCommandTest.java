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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.cm.client.command.canvas.CaseManagementCloneCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementCloneNodeCommandTest extends CaseManagementAbstractCommandTest {

    @Mock
    private Node clone;

    @Mock
    private Point2D clonePosition;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    private CaseManagementCloneNodeCommand tested;

    @Before
    public void setUp() {
        super.setup();

        tested = new CaseManagementCloneNodeCommand(candidate, parent.getUUID(), clonePosition, null, null);
    }

    @Test
    public void testNewGraphCommand() throws Exception {
        final Command<GraphCommandExecutionContext, RuleViolation> command = tested.newGraphCommand(canvasHandler);

        assertNotNull(command);
        assertTrue(command instanceof org.kie.workbench.common.stunner.cm.client.command.graph.CaseManagementCloneNodeCommand);
        assertEquals(candidate,
                     ((org.kie.workbench.common.stunner.cm.client.command.graph.CaseManagementCloneNodeCommand) command).getCandidate());
    }

    @Test
    public void testGetCloneCanvasNodeCommand() {
        final CaseManagementCloneCanvasNodeCommand command = tested.getCloneCanvasNodeCommand(parent, clone, shapeUUID);

        assertEquals(parent, command.getParent());
        assertEquals(clone, command.getCandidate());
        assertEquals(shapeUUID, command.getShapeSetId());
    }
}