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

package org.kie.workbench.common.dmn.client.commands.expressions.types.context;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.NameColumn;
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

@RunWith(MockitoJUnitRunner.Silent.class)
public class MoveRowsCommandTest {

    protected static final String II1 = "ii1";
    protected static final String II2 = "ii2";
    protected static final String II3 = "ii3";

    @Mock
    protected RowNumberColumn uiRowNumberColumn;

    @Mock
    protected NameColumn uiNameColumn;

    @Mock
    protected ExpressionEditorColumn uiExpressionEditorColumn;

    @Mock
    protected org.uberfire.mvp.Command canvasOperation;

    @Mock
    protected AbstractCanvasHandler handler;

    @Mock
    protected GraphCommandExecutionContext gce;

    @Mock
    protected RuleManager ruleManager;

    protected Context context;

    protected DMNGridData uiModel;

    protected MoveRowsCommand command;

    @Before
    public void setup() {
        this.context = new Context();
        this.uiModel = new DMNGridData();
        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiNameColumn).getIndex();
        doReturn(2).when(uiExpressionEditorColumn).getIndex();

        addContextEntry(II1);
        addContextEntry(II2);

        addUiModelColumn(uiRowNumberColumn);
        addUiModelColumn(uiNameColumn);
        addUiModelColumn(uiExpressionEditorColumn);
        addUiModelRow(0);
        addUiModelRow(1);
    }

    protected void addContextEntry(final String variable) {
        context.getContextEntry().add(new ContextEntry() {{
            setVariable(new InformationItem() {{
                setName(new Name(variable));
            }});
            setExpression(new LiteralExpression() {{
                getText().setValue(makeLiteralExpression(variable,
                                                         context.getContextEntry().size()));
            }});
        }});
    }

    protected String makeLiteralExpression(final String base,
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
        uiModel.setCellValue(rowIndex, 1, new BaseGridCellValue<>("name" + rowIndex));
        uiModel.setCellValue(rowIndex, 2, new BaseGridCellValue<>("editor" + rowIndex));
    }

    private void setupCommand(final int index,
                              final GridRow uiModelRow) {
        this.command = spy(new MoveRowsCommand(context,
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

        assertContextEntryDefinitions(new int[]{1, 0});
    }

    @Test
    public void testGraphCommandExecuteMoveUpThreeRows() {
        // add third row
        addContextEntry(II3);
        addUiModelRow(2);

        setupCommand(0,
                     uiModel.getRow(2));

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.newGraphCommand(handler).execute(gce));

        assertContextEntryDefinitions(new int[]{1, 2, 0});
    }

    @Test
    public void testGraphCommandExecuteMoveDown() {
        setupCommand(1,
                     uiModel.getRow(0));

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.newGraphCommand(handler).execute(gce));

        assertContextEntryDefinitions(new int[]{1, 0});
    }

    @Test
    public void testGraphCommandExecuteMoveDownThreeRows() {
        // add third row
        addContextEntry(II3);
        addUiModelRow(2);

        setupCommand(2,
                     uiModel.getRow(0));

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     command.newGraphCommand(handler).execute(gce));

        assertContextEntryDefinitions(new int[]{2, 0, 1});
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

        assertContextEntryDefinitions(new int[]{0, 1});
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

        assertContextEntryDefinitions(new int[]{0, 1});
    }

    private void assertContextEntryDefinitions(final int[] iiRowIndexes) {
        final String[] rowValueBases = {II1, II2, II3};

        int iiRowIndexIterator = 0;
        for (final int iiRowIndex : iiRowIndexes) {
            assertEquals(rowValueBases[iiRowIndexIterator],
                         context.getContextEntry().get(iiRowIndex).getVariable().getName().getValue());
            assertEquals(makeLiteralExpression(rowValueBases[iiRowIndexIterator], iiRowIndexIterator),
                         ((LiteralExpression) context.getContextEntry().get(iiRowIndex).getExpression()).getText().getValue());
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
        addContextEntry(II3);
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
        addContextEntry(II3);
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
            // row number not updated for last row
            if (rowIndex != rowIndexes[rowIndexes.length - 1]) {
                assertEquals(rowIndexesIterator + 1,
                             uiModel.getCell(rowIndexesIterator, 0).getValue().getValue());
            }
            assertEquals("name" + rowIndexes[rowIndexesIterator],
                         uiModel.getCell(rowIndexesIterator, 1).getValue().getValue());
            assertEquals("editor" + rowIndexes[rowIndexesIterator],
                         uiModel.getCell(rowIndexesIterator, 2).getValue().getValue());

            rowIndexesIterator++;
        }
    }
}
