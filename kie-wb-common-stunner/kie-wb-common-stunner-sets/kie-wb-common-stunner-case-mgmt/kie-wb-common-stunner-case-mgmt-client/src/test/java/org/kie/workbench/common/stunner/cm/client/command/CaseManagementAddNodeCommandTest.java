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
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementAddNodeCommandTest extends CaseManagementAbstractCommandTest {




    private CaseManagementAddNodeCommand tested;

    @Before
    public void setUp() {
        super.setup();

        tested = new CaseManagementAddNodeCommand(candidate, shapeUUID);
    }

    @Test
    public void testNewGraphCommand() {
        final Command<GraphCommandExecutionContext, RuleViolation> command = tested.newGraphCommand(canvasHandler);

        assertNotNull(command);
        assertTrue(command instanceof org.kie.workbench.common.stunner.cm.client.command.graph.CaseManagementAddNodeCommand);
        assertEquals(candidate,
                     ((org.kie.workbench.common.stunner.cm.client.command.graph.CaseManagementAddNodeCommand) command).getCandidate());
    }
}