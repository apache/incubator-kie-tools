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


package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CloneConnectorCommandTest extends AbstractCloneCommandTest {

    private CloneConnectorCommand cloneConnectorCommand;

    Edge candidate;
    String sourceNodeUUID;
    String targetNodeUUID;

    @Before
    public void setUp() {
        super.setUp();

        candidate = graphInstance.edge1;
        sourceNodeUUID = graphInstance.startNode.getUUID();
        targetNodeUUID = graphInstance.intermNode.getUUID();
        this.cloneConnectorCommand = new CloneConnectorCommand(candidate, sourceNodeUUID, targetNodeUUID);
    }

    @Test
    public void initialize() throws Exception {
        cloneConnectorCommand.initialize(graphCommandExecutionContext);
        AddConnectorCommand addConnectorCommand =
                getExecutedCommand(command -> command instanceof AddConnectorCommand);

        assertEquals(addConnectorCommand.getEdge(), cloneEdge);
        assertEquals(addConnectorCommand.getSourceNode().getUUID(), sourceNodeUUID);
        assertEquals(addConnectorCommand.getConnection(), sourceConnection);

        SetConnectionTargetNodeCommand setConnectionTargetNodeCommand =
                getExecutedCommand(command -> command instanceof SetConnectionTargetNodeCommand);

        assertEquals(setConnectionTargetNodeCommand.getTargetNode().getUUID(), targetNodeUUID);
        assertEquals(setConnectionTargetNodeCommand.getEdge(), cloneEdge);
        assertEquals(setConnectionTargetNodeCommand.getConnection(), targetConnection);

        verify(graphIndex, times(1)).addEdge(cloneEdge);
    }

    public <T> T getExecutedCommand(Function<Command, Boolean> filter) {
        return cloneConnectorCommand.getCommands().stream()
                .filter(command -> filter.apply(command))
                .map(command -> (T) command)
                .findFirst()
                .get();
    }

    @Test
    public void undo() throws Exception {
        cloneConnectorCommand.execute(graphCommandExecutionContext);
        cloneConnectorCommand.undo(graphCommandExecutionContext);
        verify(graphIndex, times(1)).removeEdge(cloneEdge);
    }
}