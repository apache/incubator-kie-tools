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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

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
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.AddParameterCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.SetKindCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
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
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class FunctionGridTest {

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private ClientSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier;

    @Mock
    private EventSourceMock<ExpressionEditorSelectedEvent> editorSelectedEvent;

    @Mock
    private ManagedInstance<FunctionGridControls> controlsProvider;

    @Mock
    private FunctionGridControls controls;

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private GridWidget literalExpressionEditor;

    private LiteralExpression literalExpression = new LiteralExpression();

    @Mock
    private ExpressionEditorDefinition supplementaryLiteralExpressionEditorDefinition;

    @Mock
    private GridWidget supplementaryLiteralExpressionEditor;

    private LiteralExpression supplementaryLiteralExpression = new LiteralExpression();

    @Mock
    private GridWidgetDnDHandlersState dndHandlersState;

    @Captor
    private ArgumentCaptor<Optional<Expression>> expressionCaptor;

    @Captor
    private ArgumentCaptor<Optional<GridWidget>> gridWidgetCaptor;

    @Captor
    private ArgumentCaptor<GridLayerRedrawManager.PrioritizedCommand> redrawCaptor;

    private Optional<FunctionDefinition> expression;

    private FunctionGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        final FunctionEditorDefinition definition = new FunctionEditorDefinition(gridPanel,
                                                                                 gridLayer,
                                                                                 sessionManager,
                                                                                 sessionCommandManager,
                                                                                 expressionEditorDefinitionsSupplier,
                                                                                 supplementaryEditorDefinitionsSupplier,
                                                                                 editorSelectedEvent,
                                                                                 controlsProvider);

        expression = definition.getModelClass();
        expression.get().getFormalParameter().add(new InformationItem() {{
            setName(new Name("p0"));
        }});

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add((ExpressionEditorDefinition) definition);
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);
        expressionEditorDefinitions.add(supplementaryLiteralExpressionEditorDefinition);

        final Decision decision = new Decision();
        decision.setName(new Name("name"));
        final Optional<HasName> hasName = Optional.of(decision);

        doReturn(controls).when(controlsProvider).get();
        doReturn(expressionEditorDefinitions).when(expressionEditorDefinitionsSupplier).get();
        doReturn(expressionEditorDefinitions).when(supplementaryEditorDefinitionsSupplier).get();

        doReturn(ExpressionType.LITERAL_EXPRESSION).when(literalExpressionEditorDefinition).getType();
        doReturn(Optional.of(literalExpression)).when(literalExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(literalExpressionEditor)).when(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                         any(HasExpression.class),
                                                                                                         any(Optional.class),
                                                                                                         any(Optional.class),
                                                                                                         anyBoolean());

        doReturn(Optional.of(supplementaryLiteralExpression)).when(supplementaryLiteralExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(supplementaryLiteralExpressionEditor)).when(supplementaryLiteralExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                                                   any(HasExpression.class),
                                                                                                                                   any(Optional.class),
                                                                                                                                   any(Optional.class),
                                                                                                                                   anyBoolean());
        final GridData uiLiteralExpressionModel = new BaseGridData();
        doReturn(uiLiteralExpressionModel).when(literalExpressionEditor).getModel();

        doReturn(session).when(sessionManager).getCurrentSession();
        doReturn(canvasHandler).when(session).getCanvasHandler();

        this.grid = spy((FunctionGrid) definition.getEditor(parent,
                                                            hasExpression,
                                                            expression,
                                                            hasName,
                                                            false).get());
    }

    @Test
    public void testInitialSetupFromDefinition() {
        final GridData uiModel = grid.getModel();
        assertTrue(uiModel instanceof DMNGridData);

        assertEquals(1,
                     uiModel.getColumnCount());
        assertTrue(uiModel.getColumns().get(0) instanceof ExpressionEditorColumn);

        assertEquals(1,
                     uiModel.getRowCount());

        assertTrue(uiModel.getCell(0, 0).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv = (ExpressionCellValue) uiModel.getCell(0, 0).getValue();
        assertEquals(literalExpressionEditor,
                     dcv.getValue().get());
    }

    @Test
    public void testGetEditorControls() {
        grid.getEditorControls();

        verify(controls).initSelectedKind(eq(FunctionDefinition.Kind.FEEL));
        verify(controls).enableKind(eq(true));
        verify(controls).initSelectedExpressionType(eq(ExpressionType.LITERAL_EXPRESSION));
        verify(controls).enableExpressionType(eq(true));
    }

    @Test
    public void testColumnMetaData() {
        final GridColumn column = grid.getModel().getColumns().get(0);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(2,
                     header.size());
        assertTrue(header.get(0) instanceof FunctionColumnNameHeaderMetaData);
        assertTrue(header.get(1) instanceof FunctionColumnParametersHeaderMetaData);

        final FunctionColumnNameHeaderMetaData md1 = (FunctionColumnNameHeaderMetaData) header.get(0);
        final FunctionColumnParametersHeaderMetaData md2 = (FunctionColumnParametersHeaderMetaData) header.get(1);

        assertEquals("name",
                     md1.getTitle());
        assertEquals("F",
                     md2.getExpressionLanguageTitle());
        assertEquals("(p0)",
                     md2.getFormalParametersTitle());
    }

    @Test
    public void testAddFormalParameter() {
        grid.addFormalParameter();

        verify(sessionCommandManager).execute(any(AbstractCanvasHandler.class),
                                              any(AddParameterCommand.class));
    }

    @Test
    public void testSetKindFEEL() {
        grid.setKind(FunctionDefinition.Kind.FEEL);

        assertSetKind(FunctionDefinition.Kind.FEEL,
                      literalExpression,
                      literalExpressionEditor);
    }

    @Test
    public void testSetKindJava() {
        doReturn(ExpressionType.FUNCTION_JAVA).when(supplementaryLiteralExpressionEditorDefinition).getType();

        grid.setKind(FunctionDefinition.Kind.JAVA);

        assertSetKind(FunctionDefinition.Kind.JAVA,
                      supplementaryLiteralExpression,
                      supplementaryLiteralExpressionEditor);
    }

    @Test
    public void testSetKindPMML() {
        doReturn(ExpressionType.FUNCTION_PMML).when(supplementaryLiteralExpressionEditorDefinition).getType();

        grid.setKind(FunctionDefinition.Kind.PMML);

        assertSetKind(FunctionDefinition.Kind.PMML,
                      supplementaryLiteralExpression,
                      supplementaryLiteralExpressionEditor);
    }

    private void assertSetKind(final FunctionDefinition.Kind expectedKind,
                               final Expression expectedExpression,
                               final GridWidget expectedEditor) {
        verify(grid).doSetKind(eq(expectedKind),
                               eq(expression.get()),
                               expressionCaptor.capture(),
                               gridWidgetCaptor.capture());
        assertEquals(expectedExpression,
                     expressionCaptor.getValue().get());
        assertEquals(expectedEditor,
                     gridWidgetCaptor.getValue().get());

        verify(sessionCommandManager).execute(any(AbstractCanvasHandler.class),
                                              any(SetKindCommand.class));
    }

    @Test
    public void testSetExpressionType() {
        grid.setExpressionType(ExpressionType.LITERAL_EXPRESSION);

        verify(sessionCommandManager).execute(any(AbstractCanvasHandler.class),
                                              any(SetCellValueCommand.class));
    }

    @Test
    public void testSynchroniseViewWhenExpressionEditorChanged() {
        grid.synchroniseViewWhenExpressionEditorChanged(Optional.of(literalExpressionEditor));

        verify(parent).onResize();
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(gridLayer).batch(redrawCaptor.capture());
        verify(grid).getEditorControls();

        final GridLayerRedrawManager.PrioritizedCommand command = redrawCaptor.getValue();
        command.execute();

        verify(gridLayer).draw();
        verify(gridLayer).select(eq(literalExpressionEditor));
    }
}
