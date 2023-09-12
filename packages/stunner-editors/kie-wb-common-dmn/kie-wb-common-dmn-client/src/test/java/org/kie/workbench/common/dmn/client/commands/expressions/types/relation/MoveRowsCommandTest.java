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

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MoveRowsCommandTest extends BaseMoveCommandsTest<MoveRowsCommand> {

    @Before
    public void setup() {
        this.relation = new Relation();
        this.uiModel = new DMNGridData();
        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiModelColumn1).getIndex();

        addRelationColumn(II1);
        addRelationColumn(II2);
        addRelationRow(II1);
        addRelationRow(II2);

        addUiModelColumn(uiRowNumberColumn);
        addUiModelColumn(uiModelColumn1);
        addUiModelRow(0);
        addUiModelRow(1);
    }

    @Override
    protected void addUiModelRow(final int rowIndex) {
        final GridRow uiRow = new BaseGridRow();
        uiModel.appendRow(uiRow);
        uiModel.setCellValue(rowIndex, 0, new BaseGridCellValue<>(rowIndex + 1));
        uiModel.setCellValue(rowIndex, 1, new BaseGridCellValue<>("value" + rowIndex));
    }

    private void setupCommand(final int index,
                              final GridRow uiModelRow) {
        this.command = spy(new MoveRowsCommand(relation,
                                               uiModel,
                                               index,
                                               Collections.singletonList(uiModelRow),
                                               canvasOperation));
    }

    @Test
    public void testGraphCommandAllow() {
        //Arbitrary command setUp
        setupCommand(0,
                     uiModel.getRow(0));

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecuteMoveUp() {
        setupCommand(0,
                     uiModel.getRow(1));

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.newGraphCommand(handler).execute(gce));

        assertRelationDefinition(1, 0);
    }

    @Test
    public void testGraphCommandExecuteMoveDown() {
        setupCommand(1,
                     uiModel.getRow(0));

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.newGraphCommand(handler).execute(gce));

        assertRelationDefinition(1, 0);
    }

    @Test
    public void testGraphCommandUndoMoveUp() {
        setupCommand(0,
                     uiModel.getRow(1));

        //Move row and then undo
        final Command<GraphCommandExecutionContext, RuleViolation> gc = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     gc.execute(gce));
        //Move UI rows as MoveRowsCommand.undo() relies on the UiModel being updated
        command.newCanvasCommand(handler).execute(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     gc.undo(gce));

        assertRelationDefinition(0, 1);
    }

    @Test
    public void testGraphCommandUndoMoveDown() {
        setupCommand(1,
                     uiModel.getRow(0));

        //Move row and then undo
        final Command<GraphCommandExecutionContext, RuleViolation> gc = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     gc.execute(gce));
        //Move UI rows as MoveRowsCommand.undo() relies on the UiModel being updated
        command.newCanvasCommand(handler).execute(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     gc.undo(gce));

        assertRelationDefinition(0, 1);
    }

    private void assertRelationDefinition(final int ii1RowIndex,
                                          final int ii2RowIndex) {
        assertEquals(makeIdentifier(II1, 0),
                     relation.getRow().get(ii1RowIndex).getExpression().get(0).getExpression().getId().getValue());
        assertEquals(makeIdentifier(II1, 1),
                     relation.getRow().get(ii1RowIndex).getExpression().get(1).getExpression().getId().getValue());
        assertEquals(makeIdentifier(II2, 0),
                     relation.getRow().get(ii2RowIndex).getExpression().get(0).getExpression().getId().getValue());
        assertEquals(makeIdentifier(II2, 1),
                     relation.getRow().get(ii2RowIndex).getExpression().get(1).getExpression().getId().getValue());
    }

    @Test
    public void testCanvasCommandAllow() {
        //Arbitrary command setUp
        setupCommand(0,
                     uiModel.getRow(0));

        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecuteMoveUp() {
        setupCommand(0,
                     uiModel.getRow(1));

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.newCanvasCommand(handler).execute(handler));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        assertUiModelDefinition(new int[]{1, 0});
    }

    @Test
    public void testCanvasCommandExecuteMoveDown() {
        setupCommand(1,
                     uiModel.getRow(0));

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.newCanvasCommand(handler).execute(handler));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        assertUiModelDefinition(new int[]{1, 0});
    }

    @Test
    public void testCanvasCommandUndoMoveUp() {
        setupCommand(0,
                     uiModel.getRow(1));

        //Move row and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        reset(command);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        assertUiModelDefinition(new int[]{0, 1});
    }

    @Test
    public void testCanvasCommandUndoMoveDown() {
        setupCommand(1,
                     uiModel.getRow(0));

        //Move row and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        reset(command);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        assertUiModelDefinition(new int[]{0, 1});
    }

    private void assertUiModelDefinition(final int[] rowIndexes) {
        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals("value" + rowIndexes[0],
                     uiModel.getCell(0, 1).getValue().getValue());
        assertEquals(2,
                     uiModel.getCell(1, 0).getValue().getValue());
        assertEquals("value" + rowIndexes[1],
                     uiModel.getCell(1, 1).getValue().getValue());
    }
}
