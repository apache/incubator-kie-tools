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
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationDefaultValueUtilities;
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
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.widgets.grid.model.BaseHasDynamicHeightCell.DEFAULT_HEIGHT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AddRelationColumnCommandTest {

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

    private AddRelationColumnCommand command;

    @Before
    public void setup() {
        this.relation = new Relation();
        this.informationItem = new InformationItem();
        this.uiModel = new BaseGridData();
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModelMapper = new RelationUIModelMapper(() -> uiModel,
                                                       () -> Optional.of(relation),
                                                       listSelector,
                                                       DEFAULT_HEIGHT);

        makeCommand(1);

        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiModelColumn).getIndex();
    }

    private void makeCommand(final int uiColumnIndex) {
        command = spy(new AddRelationColumnCommand(relation,
                                                   informationItem,
                                                   uiModel,
                                                   () -> uiModelColumn,
                                                   uiColumnIndex,
                                                   uiModelMapper,
                                                   executeCanvasOperation,
                                                   undoCanvasOperation));
    }

    @Test
    public void testGraphCommandAllow() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecuteWithRows() {
        relation.getRow().add(new List());

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(1,
                     relation.getColumn().size());
        assertEquals(informationItem,
                     relation.getColumn().get(0));
        assertEquals(RelationDefaultValueUtilities.PREFIX + "1",
                     informationItem.getName().getValue());
        assertEquals(1,
                     relation.getRow().size());
        assertEquals(1,
                     relation.getRow().get(0).getExpression().size());
        assertTrue(relation.getRow().get(0).getExpression().get(0).getExpression() instanceof LiteralExpression);

        assertEquals(relation,
                     informationItem.getParent());
        assertEquals(relation.getRow().get(0),
                     relation.getRow().get(0).getExpression().get(0).getExpression().getParent());
    }

    @Test
    public void testGraphCommandExecuteWithExistingColumn_InsertBefore() {
        final InformationItem existingInformationItem = new InformationItem();
        relation.getColumn().add(existingInformationItem);

        final List row = new List();
        relation.getRow().add(row);

        final LiteralExpression existingLiteralExpression = new LiteralExpression();
        row.getExpression().add(0, HasExpression.wrap(row, existingLiteralExpression));

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(2,
                     relation.getColumn().size());
        assertEquals(informationItem,
                     relation.getColumn().get(0));
        assertEquals(RelationDefaultValueUtilities.PREFIX + "1",
                     informationItem.getName().getValue());
        assertEquals(existingInformationItem,
                     relation.getColumn().get(1));
        assertEquals(1,
                     relation.getRow().size());
        assertEquals(2,
                     relation.getRow().get(0).getExpression().size());
        assertTrue(relation.getRow().get(0).getExpression().get(0).getExpression() instanceof LiteralExpression);
        assertEquals(existingLiteralExpression,
                     relation.getRow().get(0).getExpression().get(1).getExpression());

        assertEquals(relation,
                     informationItem.getParent());
        assertEquals(relation.getRow().get(0),
                     relation.getRow().get(0).getExpression().get(0).getExpression().getParent());
    }

    @Test
    public void testGraphCommandExecuteWithExistingColumn_InsertMiddle() {
        makeCommand(2);

        final InformationItem existingInformationItemFirst = new InformationItem();
        relation.getColumn().add(existingInformationItemFirst);

        final InformationItem existingInformationItemLast = new InformationItem();
        relation.getColumn().add(existingInformationItemLast);

        final List row = new List();
        relation.getRow().add(row);

        final LiteralExpression existingLiteralExpressionFirst = new LiteralExpression();
        final LiteralExpression existingLiteralExpressionLast = new LiteralExpression();
        row.getExpression().add(HasExpression.wrap(row, existingLiteralExpressionFirst));
        row.getExpression().add(HasExpression.wrap(row, existingLiteralExpressionLast));

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(3,
                     relation.getColumn().size());
        assertEquals(existingInformationItemFirst,
                     relation.getColumn().get(0));
        assertEquals(informationItem,
                     relation.getColumn().get(1));
        assertEquals(RelationDefaultValueUtilities.PREFIX + "1",
                     informationItem.getName().getValue());
        assertEquals(existingInformationItemLast,
                     relation.getColumn().get(2));
        assertEquals(1,
                     relation.getRow().size());
        assertEquals(3,
                     relation.getRow().get(0).getExpression().size());
        assertEquals(existingLiteralExpressionFirst,
                     relation.getRow().get(0).getExpression().get(0).getExpression());
        assertTrue(relation.getRow().get(0).getExpression().get(1).getExpression() instanceof LiteralExpression);
        assertEquals(existingLiteralExpressionLast,
                     relation.getRow().get(0).getExpression().get(2).getExpression());

        assertEquals(relation,
                     informationItem.getParent());
        assertEquals(relation.getRow().get(0),
                     relation.getRow().get(0).getExpression().get(1).getExpression().getParent());
    }

    @Test
    public void testGraphCommandExecuteWithNoRows() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(1,
                     relation.getColumn().size());
        assertEquals(informationItem,
                     relation.getColumn().get(0));
        assertEquals(RelationDefaultValueUtilities.PREFIX + "1",
                     informationItem.getName().getValue());
        assertEquals(0,
                     relation.getRow().size());

        assertEquals(relation,
                     informationItem.getParent());
    }

    @Test
    public void testGraphCommandUndoWithRows() {
        relation.getRow().add(new List());

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add column and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));
        assertEquals(0,
                     relation.getColumn().size());
        assertEquals(1,
                     relation.getRow().size());
        assertEquals(0,
                     relation.getRow().get(0).getExpression().size());
    }

    @Test
    public void testGraphCommandUndoWithNoRows() {
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
    public void testCanvasCommandExecuteWithRows() {
        relation.getRow().add(new List());
        uiModel.appendRow(new BaseGridRow());
        uiModelMapper.fromDMNModel(0, 0);

        //Add Graph column first as RelationUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(2,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiModelColumn,
                     uiModel.getColumns().get(1));
        assertEquals(1,
                     uiModel.getRowCount());
        assertEquals(2,
                     uiModel.getRows().get(0).getCells().size());
        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals("", uiModel.getCell(0, 1).getValue().getValue());

        verify(command).updateParentInformation();

        verify(executeCanvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteWithRowsAddColumnMiddle() {
        makeCommand(2);

        final InformationItem existingInformationItemFirst = new InformationItem();
        relation.getColumn().add(existingInformationItemFirst);

        final InformationItem existingInformationItemLast = new InformationItem();
        relation.getColumn().add(existingInformationItemLast);

        uiModel.appendColumn(mock(RelationColumn.class));
        uiModel.appendColumn(mock(RelationColumn.class));
        uiModel.appendRow(new BaseGridRow());

        //Add Graph column first as RelationUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(4,
                     uiModel.getColumnCount());
        assertEquals(uiModelColumn,
                     uiModel.getColumns().get(2));
    }

    @Test
    public void testCanvasCommandExecuteWithNoRows() {
        //Add Graph column first as RelationUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(2,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiModelColumn,
                     uiModel.getColumns().get(1));
        assertEquals(0,
                     uiModel.getRowCount());

        verify(command).updateParentInformation();

        verify(executeCanvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoWithRows() {
        relation.getRow().add(new List());
        uiModel.appendRow(new BaseGridRow());
        uiModelMapper.fromDMNModel(0, 0);

        //Add Graph column first as RelationUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        //Add column and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

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

        verify(undoCanvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoWithNoRows() {
        //Add Graph column first as RelationUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        //Add column and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(1,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(0,
                     uiModel.getRowCount());

        verify(command).updateParentInformation();

        verify(undoCanvasOperation).execute();
    }

    @Test
    public void testComponentWidths() {
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(handler);
        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(handler);

        //Execute
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(gce));
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.execute(handler));

        assertEquals(relation.getRequiredComponentWidthCount(),
                     relation.getComponentWidths().size());
        assertNull(relation.getComponentWidths().get(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT));

        //Undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(gce));
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.undo(handler));

        assertEquals(relation.getRequiredComponentWidthCount(),
                     relation.getComponentWidths().size());
    }
}
