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

package org.kie.workbench.common.dmn.client.commands.expressions.types.dtable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeleteDecisionRuleCommandTest {

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    private DecisionTable dtable;

    private DecisionRule rule;

    private GridData uiModel;

    private GridRow uiModelRow;

    private DeleteDecisionRuleCommand command;

    @Before
    public void setup() {
        this.dtable = new DecisionTable();
        this.rule = new DecisionRule();
        this.dtable.getRule().add(rule);

        this.uiModel = new DMNGridData();
        this.uiModelRow = new BaseGridRow();
        this.uiModel.appendRow(uiModelRow);
    }

    private void makeCommand(final int index) {
        this.command = spy(new DeleteDecisionRuleCommand(dtable,
                                                         uiModel,
                                                         index,
                                                         canvasOperation));
    }

    @Test
    public void testGraphCommandAllow() throws Exception {
        makeCommand(0);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandCheck() throws Exception {
        makeCommand(0);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandExecute() {
        makeCommand(0);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));
        assertEquals(0,
                     dtable.getRule().size());
    }

    @Test
    public void testGraphCommandExecuteRemoveFromMiddle() throws Exception {
        final DecisionRule firstRule = mock(DecisionRule.class);
        final DecisionRule lastRule = mock(DecisionRule.class);

        dtable.getRule().add(0, firstRule);
        dtable.getRule().add(lastRule);

        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());

        makeCommand(1);

        assertEquals(3, dtable.getRule().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertEquals(2, dtable.getRule().size());
        assertEquals(firstRule, dtable.getRule().get(0));
        assertEquals(lastRule, dtable.getRule().get(1));
    }

    @Test
    public void testGraphCommandExecuteAndThenUndo() {
        makeCommand(0);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);

        assertEquals(0,
                     dtable.getRule().size());

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(graphCommandExecutionContext));

        assertEquals(1,
                     dtable.getRule().size());
        assertEquals(rule,
                     dtable.getRule().get(0));
    }

    @Test
    public void testCanvasCommandAllow() throws Exception {
        makeCommand(0);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(canvasHandler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.allow(canvasHandler));
    }

    @Test
    public void testCanvasCommandExecute() throws Exception {
        makeCommand(0);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddRuleCommand = command.newCanvasCommand(canvasHandler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasAddRuleCommand.execute(canvasHandler));

        assertEquals(0,
                     uiModel.getRowCount());

        verify(canvasOperation).execute();
        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();
    }

    @Test
    public void testCanvasCommandExecuteAndThenUndo() throws Exception {
        makeCommand(0);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddRuleCommand = command.newCanvasCommand(canvasHandler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasAddRuleCommand.execute(canvasHandler));

        assertEquals(0,
                     uiModel.getRowCount());

        reset(canvasOperation, command);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasAddRuleCommand.undo(canvasHandler));
        assertEquals(1,
                     uiModel.getRowCount());
        assertEquals(uiModelRow,
                     uiModel.getRow(0));

        verify(canvasOperation).execute();
        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();
    }
}
