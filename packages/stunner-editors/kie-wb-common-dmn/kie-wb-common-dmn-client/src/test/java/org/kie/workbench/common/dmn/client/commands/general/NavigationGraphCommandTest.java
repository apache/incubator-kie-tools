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

package org.kie.workbench.common.dmn.client.commands.general;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class NavigationGraphCommandTest {

    @Mock
    protected GraphCommandExecutionContext graphCommandExecutionContext;

    @Test
    public void checkNOPGraphCommand() {
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     BaseNavigateCommand.NOP_GRAPH_COMMAND.execute(graphCommandExecutionContext));
    }

    @Test
    public void executeNOPGraphCommand() {
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     BaseNavigateCommand.NOP_GRAPH_COMMAND.execute(graphCommandExecutionContext));
    }

    @Test
    public void undoNOPGraphCommand() {
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     BaseNavigateCommand.NOP_GRAPH_COMMAND.execute(graphCommandExecutionContext));
    }
}
