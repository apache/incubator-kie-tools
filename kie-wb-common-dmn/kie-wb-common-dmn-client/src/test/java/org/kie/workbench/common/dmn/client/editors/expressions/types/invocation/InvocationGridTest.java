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

package org.kie.workbench.common.dmn.client.editors.expressions.types.invocation;

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
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.invocation.AddParameterBindingCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.NameColumnHeaderMetaData;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
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
public class InvocationGridTest {

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSession session;

    @Mock
    private AbstractCanvasHandler canvasContext;

    @Mock
    private GraphCommandExecutionContext graphContext;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private EventSourceMock<ExpressionEditorSelectedEvent> editorSelectedEvent;

    @Mock
    private ManagedInstance<InvocationGridControls> controlsProvider;

    @Mock
    private InvocationGridControls controls;

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
    private ArgumentCaptor<AddParameterBindingCommand> addParameterBindingCommandCaptor;

    private LiteralExpression literalExpression = new LiteralExpression();

    private InvocationGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        final InvocationEditorDefinition definition = new InvocationEditorDefinition(gridPanel,
                                                                                     gridLayer,
                                                                                     sessionManager,
                                                                                     sessionCommandManager,
                                                                                     expressionEditorDefinitionsSupplier,
                                                                                     editorSelectedEvent,
                                                                                     controlsProvider);

        final Optional<Invocation> expression = definition.getModelClass();
        expression.ifPresent(invocation -> ((LiteralExpression) invocation.getExpression()).setText("invocation-expression"));
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
        doReturn(canvasContext).when(session).getCanvasHandler();
        doReturn(graphContext).when(canvasContext).getGraphExecutionContext();

        final Decision decision = new Decision();
        decision.setName(new Name("name"));
        final Optional<HasName> hasName = Optional.of(decision);

        this.grid = (InvocationGrid) definition.getEditor(parent,
                                                          hasExpression,
                                                          expression,
                                                          hasName,
                                                          false).get();
    }

    @Test
    public void testInitialSetupFromDefinition() {
        final GridData uiModel = grid.getModel();
        assertTrue(uiModel instanceof InvocationGridData);

        assertEquals(3,
                     uiModel.getColumnCount());
        assertTrue(uiModel.getColumns().get(0) instanceof RowNumberColumn);
        assertTrue(uiModel.getColumns().get(1) instanceof NameColumn);
        assertTrue(uiModel.getColumns().get(2) instanceof ExpressionEditorColumn);

        assertEquals(1,
                     uiModel.getRowCount());

        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals("p0",
                     uiModel.getCell(0, 1).getValue().getValue());
        assertNull(uiModel.getCell(0, 2));
    }

    @Test
    public void testNameColumnMetaData() {
        final GridColumn<?> column = grid.getModel().getColumns().get(InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(2,
                     header.size());
        assertTrue(header.get(0) instanceof NameColumnHeaderMetaData);
        assertTrue(header.get(1) instanceof InvocationColumnExpressionHeaderMetaData);

        final NameColumnHeaderMetaData md1 = (NameColumnHeaderMetaData) header.get(0);
        final InvocationColumnExpressionHeaderMetaData md2 = (InvocationColumnExpressionHeaderMetaData) header.get(1);

        assertEquals("name",
                     md1.getTitle());
        assertEquals("invocation-expression",
                     md2.getTitle());
    }

    @Test
    public void testExpressionColumnMetaData() {
        final GridColumn<?> column = grid.getModel().getColumns().get(InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(2,
                     header.size());
        assertTrue(header.get(0) instanceof BaseHeaderMetaData);
        assertTrue(header.get(1) instanceof InvocationColumnExpressionHeaderMetaData);

        final BaseHeaderMetaData md1 = (BaseHeaderMetaData) header.get(0);
        final InvocationColumnExpressionHeaderMetaData md2 = (InvocationColumnExpressionHeaderMetaData) header.get(1);

        assertEquals("",
                     md1.getTitle());
        assertEquals("invocation-expression",
                     md2.getTitle());
    }

    @Test
    public void testAddParameterBinding() {
        grid.addParameterBinding();

        verify(sessionCommandManager).execute(eq(canvasContext),
                                              addParameterBindingCommandCaptor.capture());

        final AddParameterBindingCommand addParameterBindingCommand = addParameterBindingCommandCaptor.getValue();
        addParameterBindingCommand.execute(canvasContext);

        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(gridLayer).batch();
    }
}
