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
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
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
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.widgets.grid.model.BaseHasDynamicHeightCell.DEFAULT_HEIGHT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DeleteRelationColumnCommandTest {

    private static final String VALUE = "value";

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private RelationColumn uiModelColumn;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private org.uberfire.mvp.Command executeCanvasOperation;

    @Mock
    private org.uberfire.mvp.Command undoCanvasOperation;

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private GraphCommandExecutionContext gce;

    @Mock
    private RuleManager ruleManager;

    private Relation relation;

    private InformationItem informationItem;

    private GridData uiModel;

    private RelationUIModelMapper uiModelMapper;

    private DeleteRelationColumnCommand command;

    @Before
    public void setup() {
        this.relation = new Relation();
        this.informationItem = new InformationItem();
        this.relation.getColumn().add(informationItem);
        this.uiModel = new BaseGridData();
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiModelColumn);

        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiModelColumn).getIndex();

        this.uiModelMapper = new RelationUIModelMapper(() -> uiModel,
                                                       () -> Optional.of(relation),
                                                       listSelector,
                                                       DEFAULT_HEIGHT);
    }

    private void makeCommand(final int uiColumnIndex) {
        this.command = spy(new DeleteRelationColumnCommand(relation,
                                                           uiModel,
                                                           uiColumnIndex,
                                                           uiModelMapper,
                                                           executeCanvasOperation,
                                                           undoCanvasOperation));
    }

    private void makeCommand() {
        makeCommand(1);
    }

    @Test
    public void testGraphCommandAllow() {
        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecuteWithRows() {
        final List rowList = new List();
        relation.getRow().add(rowList);
        relation.getRow().get(0).getExpression().add(HasExpression.wrap(rowList, new LiteralExpression()));

        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(0,
                     relation.getColumn().size());
        assertEquals(1,
                     relation.getRow().size());
        assertEquals(0,
                     relation.getRow().get(0).getExpression().size());
    }

    @Test
    public void testGraphCommandExecuteDeleteMiddleWithRows() {
        final List rowList = new List();
        uiModel.appendColumn(mock(RelationColumn.class));
        uiModel.appendColumn(mock(RelationColumn.class));
        relation.getColumn().add(new InformationItem());
        relation.getColumn().add(new InformationItem());
        relation.getRow().add(rowList);
        final LiteralExpression firstExpression = new LiteralExpression();
        final LiteralExpression lastExpression = new LiteralExpression();
        relation.getRow().get(0).getExpression().add(HasExpression.wrap(rowList, firstExpression));
        relation.getRow().get(0).getExpression().add(HasExpression.wrap(rowList, new LiteralExpression()));
        relation.getRow().get(0).getExpression().add(HasExpression.wrap(rowList, lastExpression));

        makeCommand(2);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(2,
                     relation.getColumn().size());
        assertEquals(1,
                     relation.getRow().size());
        assertEquals(2,
                     relation.getRow().get(0).getExpression().size());
        assertEquals(firstExpression,
                     relation.getRow().get(0).getExpression().get(0).getExpression());
        assertEquals(lastExpression,
                     relation.getRow().get(0).getExpression().get(1).getExpression());
    }

    @Test
    public void testGraphCommandExecuteWithNoRows() {
        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(0,
                     relation.getColumn().size());
        assertEquals(0,
                     relation.getRow().size());
    }

    @Test
    public void testGraphCommandUndoWithRows() {
        final List rowList = new List();
        relation.getRow().add(rowList);
        final LiteralExpression literalExpression = new LiteralExpression();
        literalExpression.getText().setValue(VALUE);
        relation.getRow().get(0).getExpression().add(HasExpression.wrap(rowList, literalExpression));

        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Delete column and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));
        assertEquals(1,
                     relation.getColumn().size());
        assertEquals(1,
                     relation.getRow().size());
        assertEquals(1,
                     relation.getRow().get(0).getExpression().size());
        assertEquals(VALUE,
                     ((LiteralExpression) relation.getRow().get(0).getExpression().get(0).getExpression()).getText().getValue());
    }

    @Test
    public void testGraphCommandUndoWithNoRows() {
        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Delete column and then undo
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
    public void testCanvasCommandAllow() {
        makeCommand();

        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecuteWithRows() {
        final List rowList = new List();
        relation.getRow().add(rowList);
        relation.getRow().get(0).getExpression().add(HasExpression.wrap(rowList, new LiteralExpression()));
        uiModel.appendRow(new BaseGridRow());
        uiModelMapper.fromDMNModel(0, 0);
        uiModelMapper.fromDMNModel(0, 1);

        makeCommand();

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(1,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(1,
                     uiModel.getRowCount());
        assertEquals(1,
                     uiModel.getRows().get(0).getCells().size());
        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());

        verify(command).updateParentInformation();

        verify(executeCanvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteWithNoRows() {
        makeCommand();

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(1,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(0,
                     uiModel.getRowCount());

        verify(command).updateParentInformation();

        verify(executeCanvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoWithRows() {
        final List rowList = new List();
        relation.getRow().add(rowList);
        final LiteralExpression literalExpression = new LiteralExpression();
        literalExpression.getText().setValue(VALUE);
        relation.getRow().get(0).getExpression().add(HasExpression.wrap(rowList, literalExpression));
        uiModel.appendRow(new BaseGridRow());
        uiModelMapper.fromDMNModel(0, 1);

        makeCommand();

        //Delete column and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command);
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
        assertEquals(1,
                     uiModel.getRows().get(0).getCells().size());
        assertEquals(VALUE,
                     uiModel.getCell(0, 1).getValue().getValue());

        verify(command).updateParentInformation();

        verify(undoCanvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoWithNoRows() {
        makeCommand();

        //Delete column and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(2,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiModelColumn,
                     uiModel.getColumns().get(1));
        assertEquals(0,
                     uiModel.getRowCount());

        verify(command).updateParentInformation();

        verify(undoCanvasOperation).execute();
    }

    @Test
    public void testComponentWidths() {
        makeCommand();

        when(uiModelColumn.getWidth()).thenReturn(DMNGridColumn.DEFAULT_WIDTH);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(handler);

        //Execute
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(gce));

        assertEquals(relation.getRequiredComponentWidthCount(),
                     relation.getComponentWidths().size());

        //Undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(gce));

        assertEquals(relation.getRequiredComponentWidthCount(),
                     relation.getComponentWidths().size());
        assertEquals(DMNGridColumn.DEFAULT_WIDTH,
                     relation.getComponentWidths().get(1),
                     0.0);
    }
}
