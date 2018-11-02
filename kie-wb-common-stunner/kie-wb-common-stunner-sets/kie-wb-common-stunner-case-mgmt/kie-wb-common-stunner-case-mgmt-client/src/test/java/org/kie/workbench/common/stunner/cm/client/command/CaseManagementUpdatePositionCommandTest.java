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

package org.kie.workbench.common.stunner.cm.client.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.cm.client.command.canvas.CaseManagementUpdatePositionCanvasCommand;
import org.kie.workbench.common.stunner.cm.client.command.graph.CaseManagementUpdatePositionGraphCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementUpdatePositionCommandTest extends CaseManagementAbstractCommandTest {

    private Node<View<?>, Edge> candidate;

    private CaseManagementUpdatePositionCommand command;

    @Before
    public void setup() {
        super.setup();
        this.candidate = CommandTestUtils.makeNode("uuid",
                                                   "content",
                                                   10.0,
                                                   20.0,
                                                   50.0,
                                                   50.0);

        this.command = new CaseManagementUpdatePositionCommand(candidate,
                                                               new Point2D(100.0, 200.0));
    }

    @Test
    public void testGraphCommand() {
        final CaseManagementUpdatePositionGraphCommand graphCommand;
        graphCommand = (CaseManagementUpdatePositionGraphCommand) command.newGraphCommand(canvasHandler);
        assertNotNull(graphCommand);

        assertCommandSuccess(graphCommand.execute(context));
        assertPositionNotUpdated();
    }

    @Test
    public void testCanvasCommand() {
        final CaseManagementUpdatePositionCanvasCommand canvasCommand;
        canvasCommand = (CaseManagementUpdatePositionCanvasCommand) command.newCanvasCommand(canvasHandler);
        assertNotNull(canvasCommand);

        assertCommandSuccess(canvasCommand.execute(canvasHandler));
        assertPositionNotUpdated();
    }

    private void assertPositionNotUpdated() {
        assertEquals(10.0,
                     candidate.getContent().getBounds().getUpperLeft().getX(),
                     0.0);
        assertEquals(70.0,
                     candidate.getContent().getBounds().getLowerRight().getY(),
                     0.0);
    }
}
