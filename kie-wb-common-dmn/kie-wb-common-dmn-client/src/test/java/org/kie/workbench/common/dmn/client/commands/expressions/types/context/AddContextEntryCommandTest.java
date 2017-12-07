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

package org.kie.workbench.common.dmn.client.commands.expressions.types.context;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.NameColumn;
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
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddContextEntryCommandTest {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private NameColumn uiNameColumn;

    @Mock
    private ExpressionEditorColumn uiExpressionEditorColumn;

    @Mock
    private ExpressionEditorDefinitions expressionEditorDefinitions;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private GraphCommandExecutionContext gce;

    @Mock
    private RuleManager ruleManager;

    private Context context;

    private ContextEntry defaultResultContextEntry;

    private ContextEntry contextEntry;

    private GridData uiModel;

    private DMNGridRow uiModelRow;

    private DMNGridRow uiDefaultResultModelRow;

    private ContextUIModelMapper uiModelMapper;

    private AddContextEntryCommand command;

    public AddContextEntryCommandTest() {
    }

    @Before
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
        this.uiModelRow = new DMNGridRow();
        this.uiDefaultResultModelRow = new DMNGridRow();
        this.uiModel.appendRow(uiDefaultResultModelRow);

        this.uiModel.appendColumn(uiRowNumberColumn);
        this.uiModel.appendColumn(uiNameColumn);
        this.uiModel.appendColumn(uiExpressionEditorColumn);

        this.uiModelMapper = new ContextUIModelMapper(() -> uiModel,
                                                      () -> Optional.of(context),
                                                      () -> expressionEditorDefinitions);

        this.command = new AddContextEntryCommand(context,
                                                  contextEntry,
                                                  uiModel,
                                                  uiModelRow,
                                                  uiModelMapper,
                                                  canvasOperation);
        doReturn(ruleManager).when(handler).getRuleManager();
        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiNameColumn).getIndex();
        doReturn(2).when(uiExpressionEditorColumn).getIndex();
        doReturn(Optional.empty()).when(expressionEditorDefinitions).getExpressionEditorDefinition(any(Optional.class));
    }

    @Test
    public void testGraphCommandAllow() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecute() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(2,
                     context.getContextEntry().size());
        assertEquals(contextEntry,
                     context.getContextEntry().get(0));
        assertEquals(defaultResultContextEntry,
                     context.getContextEntry().get(1));
    }

    @Test
    public void testGraphCommandExecuteMultipleEntriesPresent() {
        final ContextEntry firstEntry = new ContextEntry() {{
            setVariable(new InformationItem() {{
                setName(new Name("old one"));
            }});
        }};
        context.getContextEntry().add(0, firstEntry);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(3,
                     context.getContextEntry().size());
        assertEquals(firstEntry,
                     context.getContextEntry().get(0));
        assertEquals(contextEntry,
                     context.getContextEntry().get(1));
        assertEquals(defaultResultContextEntry,
                     context.getContextEntry().get(2));
    }

    @Test
    public void testGraphCommandUndo() {
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
        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecute() {
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
        assertEquals(2,
                     uiModel.getRows().get(0).getCells().size());
        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals("variable",
                     uiModel.getCell(0, 1).getValue().getValue());
        assertNull(uiModel.getCell(1, 0));
        assertNull(uiModel.getCell(1, 1));

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandExecuteMultipleEntries() {
        // first row
        command.newGraphCommand(handler).execute(gce);
        final Command<AbstractCanvasHandler, CanvasViolation> firstEntryCanvasCommand = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     firstEntryCanvasCommand.execute(handler));

        // second row
        final ContextEntry secondRowEntry = new ContextEntry() {{
            setVariable(new InformationItem() {{
                setName(new Name("last entry"));
            }});
        }};
        final DMNGridRow uiSecondModelRow = new DMNGridRow();
        command = new AddContextEntryCommand(context,
                                             secondRowEntry,
                                             uiModel,
                                             uiSecondModelRow,
                                             uiModelMapper,
                                             canvasOperation);
        command.newGraphCommand(handler).execute(gce);
        final Command<AbstractCanvasHandler, CanvasViolation> secondEntryCanvasCommand = command.newCanvasCommand(handler);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     secondEntryCanvasCommand.execute(handler));

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
        assertEquals(2,
                     uiModel.getRows().get(0).getCells().size());
        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals("variable",
                     uiModel.getCell(0, 1).getValue().getValue());
        assertEquals(2,
                     uiModel.getCell(1, 0).getValue().getValue());
        assertEquals("last entry",
                     uiModel.getCell(1, 1).getValue().getValue());
        assertNull(uiModel.getCell(2, 0));
        assertNull(uiModel.getCell(2, 1));

        verify(canvasOperation, times(2)).execute();
    }

    @Test
    public void testCanvasCommandUndo() {
        //Add Graph column first as ContextUIModelMapper relies on the model being first updated
        command.newGraphCommand(handler).execute(gce);

        //Add column and then undo
        final Command<AbstractCanvasHandler, CanvasViolation> cc = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     cc.execute(handler));

        reset(canvasOperation);
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

        verify(canvasOperation).execute();
    }
}
