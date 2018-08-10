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

package org.kie.workbench.common.dmn.client.commands.general;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.commands.VetoExecutionCommand;
import org.kie.workbench.common.dmn.client.commands.VetoUndoCommand;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SetTypeRefCommandTest {

    @Mock
    private DMNGridLayer gridLayer;

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

    private HasVariable hasVariable = new Decision();

    private SetTypeRefCommand command;

    @SuppressWarnings("unchecked")
    public void setup() {
        hasVariable.getVariable().setTypeRef(oldTypeRef);

        this.command = new SetTypeRefCommand(hasVariable,
                                             typeRef,
                                             canvasOperation);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkGraphCommand() {
        setup();
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).allow(graphCommandExecutionContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeGraphCommand() {
        setup();
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).execute(graphCommandExecutionContext));
        assertEquals(typeRef,
                     hasVariable.getVariable().getTypeRef());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void undoGraphCommand() {
        setup();
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.getGraphCommand(canvasHandler).undo(graphCommandExecutionContext));
        assertEquals(oldTypeRef,
                     hasVariable.getVariable().getTypeRef());
    }

    @Test
    public void allowCanvasCommand() {
        setup();
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).allow(canvasHandler));
        verify(canvasOperation, never()).execute();
    }

    @Test
    public void executeCanvasCommand() {
        setup();
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).execute(canvasHandler));
        verify(canvasOperation).execute();
    }

    @Test
    public void undoCanvasCommand() {
        setup();
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.getCanvasCommand(canvasHandler).undo(canvasHandler));
        verify(canvasOperation).execute();
    }

    @Test
    public void checkCommandDefinition() {
        setup();
        assertTrue(command instanceof VetoExecutionCommand);
        assertTrue(command instanceof VetoUndoCommand);
    }
}
