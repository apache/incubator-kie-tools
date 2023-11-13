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
package org.kie.workbench.common.dmn.client.commands.expressions.types.list;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
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
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DeleteListRowCommandTest {

    private static final String VALUE = "value";

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private ExpressionEditorColumn uiExpressionEditorColumn;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private GraphCommandExecutionContext gce;

    @Mock
    private RuleManager ruleManager;

    private List list;

    private GridData uiModel;

    private DeleteListRowCommand command;

    @Before
    public void setup() {
        this.list = new List();
        this.uiModel = new BaseGridData();
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiExpressionEditorColumn);

        this.uiModel.appendRow(new BaseGridRow());
        this.list.getExpression().add(HasExpression.wrap(list, new LiteralExpression()));

        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiExpressionEditorColumn).getIndex();
    }

    private void makeCommand(final int uiRowIndex) {
        this.command = spy(new DeleteListRowCommand(list,
                                                    uiModel,
                                                    uiRowIndex,
                                                    canvasOperation));
    }

    @Test
    public void testGraphCommandAllow() {
        makeCommand(0);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecute() {
        makeCommand(0);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(0,
                     list.getExpression().size());
    }

    @Test
    public void testGraphCommandExecuteRemoveMiddle() {
        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());
        final HasExpression firstRow = HasExpression.wrap(list, new LiteralExpression());
        final HasExpression lastRow = HasExpression.wrap(list, new LiteralExpression());
        list.getExpression().add(0, firstRow);
        list.getExpression().add(lastRow);

        makeCommand(1);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(2,
                     list.getExpression().size());
        assertEquals(firstRow,
                     list.getExpression().get(0));
        assertEquals(lastRow,
                     list.getExpression().get(1));
    }

    @Test
    public void testGraphCommandUndo() {
        final LiteralExpression literalExpression = new LiteralExpression();
        literalExpression.getText().setValue(VALUE);
        list.getExpression().add(HasExpression.wrap(list, literalExpression));

        makeCommand(0);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Delete row and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));
        assertEquals(2,
                     list.getExpression().size());
        assertEquals(VALUE,
                     ((LiteralExpression) list.getExpression().get(1).getExpression()).getText().getValue());
    }

    @Test
    public void testCanvasCommandAllow() {
        makeCommand(0);

        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecute() {
        makeCommand(0);

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(0,
                     uiModel.getRowCount());

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteRemoveMiddle() {
        final GridRow firstRow = new BaseGridRow();
        final GridRow lastRow = new BaseGridRow();
        uiModel.insertRow(0, firstRow);
        uiModel.appendRow(lastRow);
        list.getExpression().add(0, HasExpression.wrap(list, null));
        list.getExpression().add(HasExpression.wrap(list, null));

        makeCommand(1);

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(2,
                     uiModel.getRowCount());
        assertEquals(firstRow,
                     uiModel.getRow(0));
        assertEquals(lastRow,
                     uiModel.getRow(1));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndo() {
        final GridRow existingGridRow = uiModel.getRow(0);

        makeCommand(0);

        //Delete row and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command, canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(1,
                     uiModel.getRowCount());
        assertEquals(existingGridRow,
                     uiModel.getRow(0));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }
}
