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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.context.AddContextEntryCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState.GridWidgetHandlersOperation;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ContextGridTest {

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSession session;

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private EventSourceMock<ExpressionEditorSelectedEvent> editorSelectedEvent;

    @Mock
    private ManagedInstance<ContextGridControls> controlsProvider;

    @Mock
    private ContextGridControls controls;

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private BaseExpressionGrid literalExpressionEditor;

    @Mock
    private GridWidgetDnDHandlersState dndHandlersState;

    @Captor
    private ArgumentCaptor<AddContextEntryCommand> addContextEntryCommandCaptor;

    private LiteralExpression literalExpression = new LiteralExpression();

    private Optional<HasName> hasName = Optional.empty();

    private ContextGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        final ContextEditorDefinition definition = new ContextEditorDefinition(gridPanel,
                                                                               gridLayer,
                                                                               sessionManager,
                                                                               sessionCommandManager,
                                                                               expressionEditorDefinitionsSupplier,
                                                                               editorSelectedEvent,
                                                                               controlsProvider);

        final Optional<Context> expression = definition.getModelClass();
        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add((ExpressionEditorDefinition) definition);
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);

        doReturn(controls).when(controlsProvider).get();
        doReturn(expressionEditorDefinitions).when(expressionEditorDefinitionsSupplier).get();
        doReturn(Optional.of(literalExpression)).when(literalExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(literalExpressionEditor)).when(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                         any(HasExpression.class),
                                                                                                         any(Optional.class),
                                                                                                         any(Optional.class),
                                                                                                         anyBoolean());

        doReturn(session).when(sessionManager).getCurrentSession();
        doReturn(handler).when(session).getCanvasHandler();

        this.grid = (ContextGrid) definition.getEditor(parent,
                                                       hasExpression,
                                                       expression,
                                                       hasName,
                                                       false).get();
    }

    @Test
    public void testInitialSetupFromDefinition() {
        final GridData uiModel = grid.getModel();
        assertTrue(uiModel instanceof ContextGridData);

        assertEquals(3,
                     uiModel.getColumnCount());
        assertTrue(uiModel.getColumns().get(0) instanceof RowNumberColumn);
        assertTrue(uiModel.getColumns().get(1) instanceof NameColumn);
        assertTrue(uiModel.getColumns().get(2) instanceof ExpressionEditorColumn);

        assertEquals(2,
                     uiModel.getRowCount());

        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals(Name.defaultValue,
                     uiModel.getCell(0, 1).getValue().getValue());
        assertNull(uiModel.getCell(0, 2));

        assertNull(uiModel.getCell(1, 0));
        assertEquals(ContextUIModelMapper.DEFAULT_ROW_CAPTION,
                     uiModel.getCell(1, 1).getValue().getValue());
        assertTrue(uiModel.getCell(1, 2).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv = (ExpressionCellValue) uiModel.getCell(1, 2).getValue();
        assertEquals(literalExpressionEditor,
                     dcv.getValue().get());
    }

    @Test
    public void testRowDragPermittedNotPendingRowMove() {
        doReturn(GridWidgetHandlersOperation.NONE).when(dndHandlersState).getOperation();
        assertTrue(grid.isRowDragPermitted(dndHandlersState));
    }

    @Test
    public void testRowDragPermittedPendingRowMoveNotLastRow() {
        assertRowDragPermitted(0,
                               true);
    }

    @Test
    public void testRowDragPermittedPendingRowMoveLastRow() {
        assertRowDragPermitted(1,
                               false);
    }

    private void assertRowDragPermitted(final int uiModelRowIndex,
                                        final boolean isPermitted) {
        final List<GridRow> rows = new ArrayList<>();
        rows.add(grid.getModel().getRow(uiModelRowIndex));

        doReturn(GridWidgetHandlersOperation.ROW_MOVE_PENDING).when(dndHandlersState).getOperation();
        doReturn(rows).when(dndHandlersState).getActiveGridRows();

        assertEquals(isPermitted,
                     grid.isRowDragPermitted(dndHandlersState));
    }

    @Test
    public void testAddContextEntry() {
        grid.addContextEntry();

        verify(sessionCommandManager).execute(eq(handler),
                                              addContextEntryCommandCaptor.capture());

        final AddContextEntryCommand addContextEntryCommand = addContextEntryCommandCaptor.getValue();
        addContextEntryCommand.execute(handler);

        verify(parent).onResize();
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(gridLayer).batch();
    }
}
