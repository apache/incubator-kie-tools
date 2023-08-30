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

package org.kie.workbench.common.dmn.client.commands.general;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SetTypeRefCommandTest {

    @Mock
    private QName typeRef;

    @Mock
    private QName oldTypeRef;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    private HasTypeRef hasTypeRef = new InformationItem();

    private SetTypeRefCommand command;

    @Before
    public void setup() {
        hasTypeRef.setTypeRef(oldTypeRef);

        this.command = new SetTypeRefCommand(hasTypeRef,
                                             typeRef,
                                             canvasOperation);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkGraphCommand() {
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).allow(graphCommandExecutionContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeGraphCommand() {
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).execute(graphCommandExecutionContext));
        assertEquals(typeRef,
                     hasTypeRef.getTypeRef());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void undoGraphCommand() {
        //Execute and then undo...
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).execute(graphCommandExecutionContext));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).undo(graphCommandExecutionContext));
        assertEquals(oldTypeRef,
                     hasTypeRef.getTypeRef());
    }

    @Test
    public void allowCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).allow(canvasHandler));
        verify(canvasOperation, never()).execute();
    }

    @Test
    public void executeCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));
        verify(canvasOperation).execute();
    }

    @Test
    public void undoCanvasCommand() {
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));
        verify(canvasOperation).execute();
    }

    @Test
    public void checkCommandDefinition() {
        assertTrue(command instanceof VetoExecutionCommand);
        assertTrue(command instanceof VetoUndoCommand);
    }
}
