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

package org.kie.workbench.common.dmn.client.commands.expressions.types.relation;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
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
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.widgets.grid.model.BaseHasDynamicHeightCell.DEFAULT_HEIGHT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AddRelationRowCommandTest {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private RelationColumn uiModelColumn;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private GraphCommandExecutionContext gce;

    @Mock
    private RuleManager ruleManager;

    private Relation relation;

    private List row;

    private GridData uiModel;

    private GridRow uiModelRow;

    private RelationUIModelMapper uiModelMapper;

    private AddRelationRowCommand command;

    @Before
    public void setup() {
        this.relation = new Relation();
        this.row = new List();
        this.uiModelRow = new BaseGridRow();
        this.uiModel = new BaseGridData();
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModelMapper = new RelationUIModelMapper(() -> uiModel,
                                                       () -> Optional.of(relation),
                                                       listSelector,
                                                       DEFAULT_HEIGHT);

        makeCommand(0);

        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiModelColumn).getIndex();
    }

    private void makeCommand(final int uiRowIndex) {
        command = spy(new AddRelationRowCommand(relation,
                                                row,
                                                uiModel,
                                                uiModelRow,
                                                uiRowIndex,
                                                uiModelMapper,
                                                canvasOperation));
    }

    @Test
    public void testGraphCommandAllow() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecuteWithColumns() {
        relation.getColumn().add(new InformationItem());

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(1,
                     relation.getRow().size());
        assertEquals(row,
                     relation.getRow().get(0));
        assertEquals(1,
                     relation.getColumn().size());
        assertEquals(1,
                     relation.getRow().get(0).getExpression().size());
        assertTrue(relation.getRow().get(0).getExpression().get(0).getExpression() instanceof LiteralExpression);

        assertEquals(relation,
                     row.getParent());
        assertEquals(relation.getRow().get(0),
                     relation.getRow().get(0).getExpression().get(0).getExpression().getParent());
    }

    @Test
    public void testGraphCommandExecuteInsertMiddleWithColumns() {
        relation.getRow().add(new List());
        relation.getRow().add(new List());
        relation.getColumn().add(new InformationItem());

        makeCommand(1);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(3,
                     relation.getRow().size());
        assertEquals(row,
                     relation.getRow().get(1));
        assertEquals(1,
                     relation.getColumn().size());
        assertEquals(1,
                     relation.getRow().get(1).getExpression().size());
        assertTrue(relation.getRow().get(1).getExpression().get(0).getExpression() instanceof LiteralExpression);

        assertEquals(relation,
                     row.getParent());
        assertEquals(relation.getRow().get(1),
                     relation.getRow().get(1).getExpression().get(0).getExpression().getParent());
    }

    @Test
    public void testGraphCommandExecuteWithNoColumns() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(1,
                     relation.getRow().size());
        assertEquals(row,
                     relation.getRow().get(0));

        assertEquals(relation,
                     row.getParent());
    }

    @Test
    public void testGraphCommandUndoWithColumns() {
        relation.getColumn().add(new InformationItem());

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add column and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));
        assertEquals(1,
                     relation.getColumn().size());
        assertEquals(0,
                     relation.getRow().size());
    }

    @Test
    public void testGraphCommandUndoWithNoColumns() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add column and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));
        assertEquals(0,
                     relation.getColumn().size());
        assertEquals(0,
                     relation.getRow().size());
    }

    @Test
    public void testCanvasCommandAllow() {
        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecuteWithColumns() {
        relation.getRow().add(new List());
        relation.getRow().add(new List());

        relation.getColumn().add(new InformationItem());
        uiModel.appendColumn(uiModelColumn);

        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());

        makeCommand(1);

        //Add Graph row first as RelationUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(3,
                     uiModel.getRowCount());
        assertEquals(uiModelRow,
                     uiModel.getRows().get(1));
        assertEquals(2,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));

        // checking just the row added by command
        assertEquals(uiModelColumn,
                     uiModel.getColumns().get(1));
        assertEquals(2,
                     uiModel.getRows().get(1).getCells().size());
        assertEquals(2,
                     uiModel.getCell(1, 0).getValue().getValue());
        assertEquals("", uiModel.getCell(1, 1).getValue().getValue());

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteWithNoColumns() {
        //Add Graph column first as RelationUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(1,
                     uiModel.getRowCount());
        assertEquals(uiModelRow,
                     uiModel.getRows().get(0));
        assertEquals(1,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(1,
                     uiModel.getRows().get(0).getCells().size());
        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoWithColumns() {
        relation.getColumn().add(new InformationItem());
        relation.getRow().add(new org.kie.workbench.common.dmn.api.definition.model.List());
        uiModel.appendColumn(uiModelColumn);
        uiModel.appendRow(new BaseGridRow());
        uiModelMapper.fromDMNModel(0, 0);

        //Add Graph column first as RelationUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        //Add column and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command, canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(2,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiModelColumn,
                     uiModel.getColumns().get(1));
        assertEquals(1,
                     uiModel.getRowCount());

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoWithNoColumns() {
        //Add Graph column first as RelationUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        //Add column and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command, canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(1,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(0,
                     uiModel.getRowCount());

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }
}
