/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CloneConnectorCommandTest {

    private CloneConnectorCommand cloneConnectorCommand;

    @Mock
    private Edge candidate;

    @Mock
    private Edge clone;

    @Mock
    private AbstractCanvasHandler context;

    private static final String SOURCE_UUID = UUID.uuid();
    private static final String TARGET_UUID = UUID.uuid();
    private static final String SHAPE_SET_UUID = UUID.uuid();

    @Before
    public void setUp() {
        cloneConnectorCommand = new CloneConnectorCommand(candidate, SOURCE_UUID, TARGET_UUID, SHAPE_SET_UUID, null);
    }

    @Test
    public void newGraphCommand() {
        Command<GraphCommandExecutionContext, RuleViolation> command = cloneConnectorCommand.newGraphCommand(context);
        assertTrue(command instanceof org.kie.workbench.common.stunner.core.graph.command.impl.CloneConnectorCommand);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void newCanvasCommand() {
        Command<AbstractCanvasHandler, CanvasViolation> command = cloneConnectorCommand.newCanvasCommand(context);
        assertTrue(command instanceof CompositeCommand);

        Consumer<Edge> cloneCallback = cloneConnectorCommand.getCloneCallback();
        cloneCallback.accept(clone);

        CompositeCommand compositeCommand = (CompositeCommand) command;
        assertTrue(compositeCommand.getCommands().stream().anyMatch(c -> c instanceof AddCanvasConnectorCommand));
        assertEquals(((AddCanvasConnectorCommand) compositeCommand.getCommands().stream()
                .filter(c -> c instanceof AddCanvasConnectorCommand)
                .findFirst()
                .get()).getCandidate(), clone);
        assertTrue(compositeCommand.getCommands().stream().anyMatch(c -> c instanceof SetCanvasConnectionCommand));
        assertEquals(((SetCanvasConnectionCommand) compositeCommand.getCommands().stream()
                .filter(c -> c instanceof SetCanvasConnectionCommand)
                .findFirst()
                .get()).getEdge(), clone);
    }
}