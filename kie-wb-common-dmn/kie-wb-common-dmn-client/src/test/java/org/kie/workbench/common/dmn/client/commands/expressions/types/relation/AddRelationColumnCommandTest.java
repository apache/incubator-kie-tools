/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.commands.expressions.types.relation;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.List;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridRow;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddRelationColumnCommandTest {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private RelationColumn uiModelColumn;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

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
                                                       () -> Optional.of(relation));

        this.command = new AddRelationColumnCommand(relation,
                                                    informationItem,
                                                    uiModel,
                                                    uiModelColumn,
                                                    uiModelMapper,
                                                    canvasOperation);
        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiModelColumn).getIndex();
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
        assertEquals(1,
                     relation.getRow().size());
        assertEquals(1,
                     relation.getRow().get(0).getExpression().size());
        assertTrue(relation.getRow().get(0).getExpression().get(0) instanceof LiteralExpression);
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
        assertEquals(0,
                     relation.getRow().size());
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
        uiModel.appendRow(new DMNGridRow());
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
        assertEquals("",
                     uiModel.getCell(0, 1).getValue().getValue());

        verify(canvasOperation).execute();
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

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoWithRows() {
        relation.getRow().add(new List());
        uiModel.appendRow(new DMNGridRow());
        uiModelMapper.fromDMNModel(0, 0);

        //Add Graph column first as RelationUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        //Add column and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(canvasOperation);
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

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndoWithNoRows() {
        //Add Graph column first as RelationUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        //Add column and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(1,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(0,
                     uiModel.getRowCount());

        verify(canvasOperation).execute();
    }
}
