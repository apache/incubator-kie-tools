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

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextEntryDefaultValueUtilities;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.NameColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
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
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AddContextEntryCommandTest {

    @Mock
    private GridWidget gridWidget;

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private NameColumn uiNameColumn;

    @Mock
    private ExpressionEditorColumn uiExpressionEditorColumn;

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

    @Mock
    private GridCellTuple parent;

    @Mock
    private UndefinedExpressionEditorDefinition undefinedExpressionEditorDefinition;

    @Mock
    private BaseExpressionGrid undefinedExpressionEditor;

    private Context context;

    private ContextEntry defaultResultContextEntry;

    private ContextEntry contextEntry;

    private GridData uiModel;

    private GridRow uiModelRow;

    private GridRow uiDefaultResultModelRow;

    private ContextUIModelMapper uiModelMapper;

    private AddContextEntryCommand command;

    public AddContextEntryCommandTest() {
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.context = new Context();
        this.contextEntry = new ContextEntry() {{
            setVariable(new InformationItem() {{
                setName(new Name("variable"));
            }});
        }};
        this.defaultResultContextEntry = new ContextEntry();
        this.context.getContextEntry().add(defaultResultContextEntry);
        this.uiModel = new BaseGridData();
        this.uiModelRow = new BaseGridRow();
        this.uiDefaultResultModelRow = new BaseGridRow();
        this.uiModel.appendRow(uiDefaultResultModelRow);

        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiNameColumn);
        this.uiModel.appendColumn(uiExpressionEditorColumn);

        doReturn(uiModel).when(gridWidget).getModel();
        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiNameColumn).getIndex();
        doReturn(2).when(uiExpressionEditorColumn).getIndex();

        this.uiModel.setCellValue(0,
                                  2,
                                  new ExpressionCellValue(Optional.of(undefinedExpressionEditor)));

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(undefinedExpressionEditorDefinition);

        doReturn(parent).when(undefinedExpressionEditor).getParentInformation();
        doReturn(Optional.empty()).when(undefinedExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(undefinedExpressionEditor)).when(undefinedExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                             any(Optional.class),
                                                                                                             any(HasExpression.class),
                                                                                                             any(Optional.class),
                                                                                                             anyBoolean(),
                                                                                                             anyInt());
        this.uiModelMapper = new ContextUIModelMapper(gridWidget,
                                                      () -> uiModel,
                                                      () -> Optional.of(context),
                                                      () -> false,
                                                      () -> expressionEditorDefinitions,
                                                      listSelector,
                                                      0);
    }

    private void makeCommand() {
        this.command = spy(new AddContextEntryCommand(context,
                                                      contextEntry,
                                                      uiModel,
                                                      uiModelRow,
                                                      context.getContextEntry().size() - 1,
                                                      uiModelMapper,
                                                      canvasOperation));
    }

    @Test
    public void testGraphCommandAllow() {
        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecute() {
        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(2,
                     context.getContextEntry().size());
        assertEquals(contextEntry,
                     context.getContextEntry().get(0));
        assertEquals(ContextEntryDefaultValueUtilities.PREFIX + "1",
                     contextEntry.getVariable().getName().getValue());
        assertEquals(defaultResultContextEntry,
                     context.getContextEntry().get(1));

        assertEquals(context,
                     contextEntry.getParent());
        assertEquals(contextEntry,
                     contextEntry.getVariable().getParent());
    }

    @Test
    public void testGraphCommandExecuteMultipleEntriesPresent() {
        final String EXISTING_ENTRY_NAME = "old one";
        final ContextEntry firstEntry = new ContextEntry() {{
            setVariable(new InformationItem() {{
                setName(new Name(EXISTING_ENTRY_NAME));
            }});
        }};
        context.getContextEntry().add(0, firstEntry);

        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(3,
                     context.getContextEntry().size());
        assertEquals(firstEntry,
                     context.getContextEntry().get(0));
        assertEquals(EXISTING_ENTRY_NAME,
                     firstEntry.getVariable().getName().getValue());
        assertEquals(contextEntry,
                     context.getContextEntry().get(1));
        assertEquals(ContextEntryDefaultValueUtilities.PREFIX + "1",
                     contextEntry.getVariable().getName().getValue());
        assertEquals(defaultResultContextEntry,
                     context.getContextEntry().get(2));

        assertEquals(context,
                     contextEntry.getParent());
        assertEquals(contextEntry,
                     contextEntry.getVariable().getParent());
    }

    @Test
    public void testGraphCommandUndo() {
        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add column and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));
        assertEquals(1,
                     context.getContextEntry().size());
        assertEquals(defaultResultContextEntry,
                     context.getContextEntry().get(0));
    }

    @Test
    public void testGraphCommandUndoMultipleEntriesPresent() {
        final ContextEntry firstEntry = new ContextEntry() {{
            setVariable(new InformationItem() {{
                setName(new Name("old one"));
            }});
        }};
        context.getContextEntry().add(0, firstEntry);

        makeCommand();

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Add column and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));
        assertEquals(2,
                     context.getContextEntry().size());
        assertEquals(firstEntry,
                     context.getContextEntry().get(0));
        assertEquals(defaultResultContextEntry,
                     context.getContextEntry().get(1));
    }

    @Test
    public void testCanvasCommandAllow() {
        makeCommand();

        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecute() {
        makeCommand();

        //Add Graph column first as ContextUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));
        assertEquals(2,
                     uiModel.getRowCount());
        assertEquals(uiModelRow,
                     uiModel.getRows().get(0));
        assertEquals(uiDefaultResultModelRow,
                     uiModel.getRows().get(1));
        assertEquals(3,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiNameColumn,
                     uiModel.getColumns().get(1));
        assertEquals(uiExpressionEditorColumn,
                     uiModel.getColumns().get(2));

        assertEquals(3,
                     uiModel.getRows().get(0).getCells().size());
        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals(ContextEntryDefaultValueUtilities.PREFIX + "1",
                     ((InformationItemCell.HasNameAndDataTypeCell) uiModel.getCell(0, 1).getValue().getValue()).getName().getValue());
        assertTrue(uiModel.getCell(0, 2).getValue() instanceof ExpressionCellValue);

        //Default row
        assertEquals(1,
                     uiModel.getRows().get(1).getCells().size());
        assertTrue(uiModel.getCell(1, 2).getValue() instanceof ExpressionCellValue);

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteMultipleEntries() {
        makeCommand();

        // first row
        command.newGraphCommand(handler).execute(gce);
        final Command<AbstractCanvasHandler, CanvasViolation> firstEntryCanvasCommand = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     firstEntryCanvasCommand.execute(handler));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        // second row
        final ContextEntry secondRowEntry = new ContextEntry() {{
            setVariable(new InformationItem());
        }};
        final GridRow uiSecondModelRow = new BaseGridRow();
        command = spy(new AddContextEntryCommand(context,
                                                 secondRowEntry,
                                                 uiModel,
                                                 uiSecondModelRow,
                                                 context.getContextEntry().size() - 1,
                                                 uiModelMapper,
                                                 canvasOperation));
        command.newGraphCommand(handler).execute(gce);
        final Command<AbstractCanvasHandler, CanvasViolation> secondEntryCanvasCommand = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     secondEntryCanvasCommand.execute(handler));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        assertEquals(3,
                     uiModel.getRowCount());
        assertEquals(uiModelRow,
                     uiModel.getRows().get(0));
        assertEquals(uiSecondModelRow,
                     uiModel.getRows().get(1));
        assertEquals(uiDefaultResultModelRow,
                     uiModel.getRows().get(2));
        assertEquals(3,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiNameColumn,
                     uiModel.getColumns().get(1));
        assertEquals(uiExpressionEditorColumn,
                     uiModel.getColumns().get(2));

        assertEquals(3,
                     uiModel.getRows().get(0).getCells().size());
        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals(ContextEntryDefaultValueUtilities.PREFIX + "1",
                     ((InformationItemCell.HasNameAndDataTypeCell) uiModel.getCell(0, 1).getValue().getValue()).getName().getValue());
        assertTrue(uiModel.getCell(0, 2).getValue() instanceof ExpressionCellValue);

        assertEquals(3,
                     uiModel.getRows().get(1).getCells().size());
        assertEquals(2,
                     uiModel.getCell(1, 0).getValue().getValue());
        assertEquals(ContextEntryDefaultValueUtilities.PREFIX + "2",
                     ((InformationItemCell.HasNameAndDataTypeCell) uiModel.getCell(1, 1).getValue().getValue()).getName().getValue());
        assertTrue(uiModel.getCell(1, 2).getValue() instanceof ExpressionCellValue);

        //Default row
        assertEquals(1,
                     uiModel.getRows().get(2).getCells().size());
        assertTrue(uiModel.getCell(2, 2).getValue() instanceof ExpressionCellValue);

        verify(canvasOperation, times(2)).execute();
    }

    @Test
    public void testCanvasCommandUndo() {
        makeCommand();

        //Add Graph column first as ContextUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        //Add column and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(command, canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.undo(handler));

        assertEquals(3,
                     uiModel.getColumnCount());
        assertEquals(uiRowNumberColumn,
                     uiModel.getColumns().get(0));
        assertEquals(uiNameColumn,
                     uiModel.getColumns().get(1));
        assertEquals(uiExpressionEditorColumn,
                     uiModel.getColumns().get(2));
        assertEquals(1,
                     uiModel.getRowCount());
        assertNull(uiModel.getCell(0, 0));
        assertNull(uiModel.getCell(0, 1));

        verify(command).updateRowNumbers();
        verify(command).updateParentInformation();

        verify(canvasOperation).execute();
    }
}
