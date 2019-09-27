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

package org.kie.workbench.common.dmn.client.commands.factory.canvas;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNDeleteNodeCommandTest {

    @Test
    public void testNewGraphCommand() {
        final Node candidate = mock(Node.class);
        when(candidate.getUUID()).thenReturn("uuid");
        final DMNDeleteNodeCommand cmd = new DMNDeleteNodeCommand(candidate);
        final Command<GraphCommandExecutionContext, RuleViolation> actual = cmd.newGraphCommand(null);

        assertTrue(actual instanceof DMNSafeDeleteNodeCommand);
        final DMNSafeDeleteNodeCommand safeCmd = (DMNSafeDeleteNodeCommand) actual;
        assertEquals(cmd.getCandidate(), safeCmd.getNode());
        assertEquals(cmd.getDeleteProcessor(), safeCmd.getSafeDeleteCallback().get());
        assertEquals(cmd.getOptions(), safeCmd.getOptions());
    }
}