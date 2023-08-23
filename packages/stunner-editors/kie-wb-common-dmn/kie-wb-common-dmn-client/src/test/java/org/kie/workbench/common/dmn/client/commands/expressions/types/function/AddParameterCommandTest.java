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

package org.kie.workbench.common.dmn.client.commands.expressions.types.function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.FunctionDefaultValueUtilities;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AddParameterCommandTest {

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private GraphCommandExecutionContext gce;

    @Mock
    private RuleManager ruleManager;

    private FunctionDefinition function;

    private InformationItem parameter;

    private AddParameterCommand command;

    @Before
    public void setup() {
        this.function = new FunctionDefinition();
        this.parameter = new InformationItem();
        this.command = new AddParameterCommand(function,
                                               parameter,
                                               canvasOperation);
        doReturn(ruleManager).when(handler).getRuleManager();
    }

    @Test
    public void testGraphCommandAllow() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecuteWithParameters() {
        final InformationItem otherParameter = new InformationItem();
        function.getFormalParameter().add(otherParameter);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));

        assertFormalParameters(2,
                               otherParameter, parameter);

        assertEquals(function,
                     parameter.getParent());
    }

    @Test
    public void testGraphCommandExecuteWithNoParameters() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));

        assertFormalParameters(1,
                               parameter);

        assertEquals(function,
                     parameter.getParent());
    }

    @Test
    public void testGraphCommandUndoWithParameters() {
        final InformationItem otherParameter = new InformationItem();
        function.getFormalParameter().add(otherParameter);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add parameter and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));

        assertFormalParameters(1,
                               otherParameter);
    }

    @Test
    public void testGraphCommandUndoWithNoParameters() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add parameter and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));

        assertFormalParameters(0);
    }

    private void assertFormalParameters(final int expectedCount,
                                        final InformationItem... parameters) {
        assertEquals(expectedCount,
                     function.getFormalParameter().size());
        for (int i = 0; i < expectedCount; i++) {
            assertEquals(parameters[i],
                         function.getFormalParameter().get(i));
        }

        assertEquals(FunctionDefaultValueUtilities.PREFIX + "1",
                     parameter.getName().getValue());
    }

    @Test
    public void testCanvasCommandAllow() {
        //There are no Canvas mutations by the command
        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecute() {
        //There are no Canvas mutations by the command
        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.execute(handler));

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndo() {
        //There are no Canvas mutations by the command
        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.undo(handler));

        verify(canvasOperation).execute();
    }
}
