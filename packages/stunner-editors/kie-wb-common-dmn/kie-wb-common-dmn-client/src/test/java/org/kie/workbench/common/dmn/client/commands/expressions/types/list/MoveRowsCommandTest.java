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

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
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
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MoveRowsCommandTest {

    private static final String TEXT1 = "text1";
    private static final String TEXT2 = "text2";
    private static final String TEXT3 = "text3";

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

    private DMNGridData uiModel;

    private MoveRowsCommand command;

    @Before
    public void setup() {
        this.list = new List();
        this.uiModel = new DMNGridData();
        doReturn(ruleManager).when(handler).getRuleManager();
        when(uiRowNumberColumn.getIndex()).thenReturn(0);
        when(uiExpressionEditorColumn.getIndex()).thenReturn(1);

        addHasExpression(TEXT1);
        addHasExpression(TEXT2);

        addUiModelColumn(uiRowNumberColumn);
        addUiModelColumn(uiExpressionEditorColumn);
        addUiModelRow(0);
        addUiModelRow(1);
    }

    private void addHasExpression(final String expressionText) {
        list.getExpression().add(HasExpression.wrap(list,
                                                    new LiteralExpression() {{
                                                        getText().setValue(makeLiteralExpression(expressionText,
                                                                                                 list.getExpression().size()));
                                                    }}));
    }

    private String makeLiteralExpression(final String base,
                                         final int index) {
        return base + "e" + index;
    }

    protected void addUiModelColumn(final GridColumn<?> uiColumn) {
        uiModel.appendColumn(uiColumn);
    }

    protected void addUiModelRow(final int rowIndex) {
        final GridRow uiRow = new BaseGridRow();
        uiModel.appendRow(uiRow);
        uiModel.setCellValue(rowIndex, 0, new BaseGridCellValue<>(rowIndex + 1));
        uiModel.setCellValue(rowIndex, 1, new BaseGridCellValue<>("editor" + rowIndex));
    }

    private void setupCommand(final int index,
                              final GridRow uiModelRow) {
        this.command = spy(new MoveRowsCommand(list,
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

        assertHasExpressionDefinitions(new int[]{1, 0});
    }

    @Test
    public void testGraphCommandExecuteMoveUpThreeRows() {
        // add third row
        addHasExpression(TEXT3);
        addUiModelRow(2);

        setupCommand(0,
                     uiModel.getRow(2));

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.newGraphCommand(handler).execute(gce));

        assertHasExpressionDefinitions(new int[]{1, 2, 0});
    }

    @Test
    public void testGraphCommandExecuteMoveDown() {
        setupCommand(1,
                     uiModel.getRow(0));

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.newGraphCommand(handler).execute(gce));

        assertHasExpressionDefinitions(new int[]{1, 0});
    }

    @Test
    public void testGraphCommandExecuteMoveDownThreeRows() {
        // add third row
        addHasExpression(TEXT3);
        addUiModelRow(2);

        setupCommand(2,
                     uiModel.getRow(0));

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.newGraphCommand(handler).execute(gce));

        assertHasExpressionDefinitions(new int[]{2, 0, 1});
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

        assertHasExpressionDefinitions(new int[]{0, 1});
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

        assertHasExpressionDefinitions(new int[]{0, 1});
    }

    private void assertHasExpressionDefinitions(final int[] iiRowIndexes) {
        final String[] rowValueBases = {TEXT1, TEXT2, TEXT3};

        int iiRowIndexIterator = 0;
        for (final int iiRowIndex : iiRowIndexes) {
            assertEquals(makeLiteralExpression(rowValueBases[iiRowIndexIterator], iiRowIndexIterator),
                         ((LiteralExpression) list.getExpression().get(iiRowIndex).getExpression()).getText().getValue());
            iiRowIndexIterator++;
        }
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
    public void testCanvasCommandExecuteMoveUpThreeRows() {
        // add third row
        addHasExpression(TEXT3);
        addUiModelRow(2);

        setupCommand(0,
                     uiModel.getRow(2));

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.newCanvasCommand(handler).execute(handler));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        assertUiModelDefinition(new int[]{2, 0, 1});
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
    public void testCanvasCommandExecuteMoveDownThreeRows() {
        // add third row
        addHasExpression(TEXT3);
        addUiModelRow(2);

        setupCommand(2,
                     uiModel.getRow(0));

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     command.newCanvasCommand(handler).execute(handler));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        assertUiModelDefinition(new int[]{1, 2, 0});
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
        int rowIndexesIterator = 0;
        for (int rowIndex : rowIndexes) {
            assertEquals(rowIndexesIterator + 1,
                         uiModel.getCell(rowIndexesIterator, 0).getValue().getValue());
            assertEquals("editor" + rowIndexes[rowIndexesIterator],
                         uiModel.getCell(rowIndexesIterator, 1).getValue().getValue());

            rowIndexesIterator++;
        }
    }
}
