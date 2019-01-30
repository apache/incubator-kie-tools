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
import org.kie.workbench.common.stunner.core.graph.command.impl.CloneNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.CloneNodeCommandTest;
import org.kie.workbench.common.stunner.core.graph.command.impl.RegisterNodeCommand;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementCloneNodeCommandTest extends CloneNodeCommandTest {

    private CaseManagementCloneNodeCommand tested;

    @Before
    public void setUp() {
        super.setUp();

        tested = new CaseManagementCloneNodeCommand(candidate,
                                                    parent.getUUID(),
                                                    position,
                                                    null,
                                                    childrenTraverseProcessorManagedInstance);
        cloneNodeCommand = tested;
    }

    @Test
    public void testCreateNodeCommands() throws Exception {
        tested.createNodeCommands(candidate, parent.getUUID(), position);

        assertEquals(2, tested.getCommands().size());
        assertTrue(RegisterNodeCommand.class.isInstance(tested.getCommands().get(0)));
        assertTrue(CaseManagementAddChildNodeGraphCommand.class.isInstance(tested.getCommands().get(1)));
    }

    @Test
    public void testCreateCloneChildCommand() throws Exception {
        final CloneNodeCommand command = tested.createCloneChildCommand(candidate,
                                                                        parent.getUUID(),
                                                                        position,
                                                                        null,
                                                                        childrenTraverseProcessorManagedInstance);

        assertTrue(CaseManagementCloneNodeCommand.class.isInstance(command));
    }
}