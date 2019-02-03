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

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.command.impl.DeleteElementsCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementDeleteElementsCommandTest extends CaseManagementAbstractGraphCommandTest {

    private CaseManagementDeleteElementsCommand tested;

    @Before
    public void setUp() {
        super.setup();
        tested = new CaseManagementDeleteElementsCommand(() -> Collections.singletonList(node),
                                                         null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateSafeDeleteNodeCommand() {
        final CaseManagementSafeDeleteNodeCommand command =
                tested.createSafeDeleteNodeCommand(node,
                                                   SafeDeleteNodeCommand.Options.defaults(),
                                                   new DeleteElementsCommand.DeleteCallback() {
                                                   });

        assertEquals(node, command.getNode());
    }
}