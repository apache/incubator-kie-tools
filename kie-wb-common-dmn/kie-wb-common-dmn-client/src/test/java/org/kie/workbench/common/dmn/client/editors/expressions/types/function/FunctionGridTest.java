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
import org.jboss.errai.ui.client.local.spi.TranslationService;
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
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.RemoveParameterCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.SetKindCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.UpdateParameterNameCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParametersEditorView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
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
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class FunctionGridTest {

    private static final String PARAMETER_NAME = "name";

    private static final int KIND_FEEL = 0;

    private static final int KIND_JAVA = 1;

    private static final int KIND_PMML = 2;

    private final static int DIVIDER = 3;

    private final static int CLEAR_EXPRESSION_TYPE = 4;

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
    private AbstractCanvasHandler handler;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private ParametersEditorView.Presenter parametersEditor;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private TranslationService translationService;

    @Mock
    private GridCellTuple parent;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private BaseExpressionGrid literalExpressionEditor;

    private LiteralExpression literalExpression = new LiteralExpression();

    @Mock
    private ExpressionEditorDefinition supplementaryLiteralExpressionEditorDefinition;

    @Mock
    private GridWidget supplementaryLiteralExpressionEditor;

    private LiteralExpression supplementaryLiteralExpression = new LiteralExpression();

    @Captor
    private ArgumentCaptor<Optional<Expression>> expressionCaptor;

    @Captor
    private ArgumentCaptor<Optional<BaseExpressionGrid>> gridWidgetCaptor;

    @Captor
    private ArgumentCaptor<GridLayerRedrawManager.PrioritizedCommand> redrawCaptor;

    private Optional<FunctionDefinition> expression = Optional.empty();

    private Optional<HasName> hasName = Optional.empty();

    private InformationItem parameter = new InformationItem();

    private FunctionEditorDefinition definition;

    private FunctionGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        definition = new FunctionEditorDefinition(gridPanel,
                                                  gridLayer,
                                                  sessionManager,
                                                  sessionCommandManager,
                                                  expressionEditorDefinitionsSupplier,
                                                  supplementaryEditorDefinitionsSupplier,
                                                  cellEditorControls,
                                                  translationService,
                                                  listSelector,
                                                  parametersEditor);

        expression = definition.getModelClass();
        expression.get().getFormalParameter().add(parameter);
        parameter.getName().setValue(PARAMETER_NAME);

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add((ExpressionEditorDefinition) definition);
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);
        expressionEditorDefinitions.add(supplementaryLiteralExpressionEditorDefinition);

        final Decision decision = new Decision();
        decision.setName(new Name("name"));
        hasName = Optional.of(decision);

        doReturn(expressionEditorDefinitions).when(expressionEditorDefinitionsSupplier).get();
        doReturn(expressionEditorDefinitions).when(supplementaryEditorDefinitionsSupplier).get();

        doReturn(ExpressionType.LITERAL_EXPRESSION).when(literalExpressionEditorDefinition).getType();
        doReturn(Optional.of(literalExpression)).when(literalExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(literalExpressionEditor)).when(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                         any(HasExpression.class),
                                                                                                         any(Optional.class),
                                                                                                         any(Optional.class),
                                                                                                         anyInt());

        doReturn(Optional.of(supplementaryLiteralExpression)).when(supplementaryLiteralExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(supplementaryLiteralExpressionEditor)).when(supplementaryLiteralExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                                                   any(HasExpression.class),
                                                                                                                                   any(Optional.class),
                                                                                                                                   any(Optional.class),
                                                                                                                                   anyInt());
        final GridData uiLiteralExpressionModel = new BaseGridData();
        doReturn(uiLiteralExpressionModel).when(literalExpressionEditor).getModel();

        doReturn(session).when(sessionManager).getCurrentSession();
        doReturn(handler).when(session).getCanvasHandler();

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(anyString());
    }

    private void setupGrid(final int nesting) {
        this.grid = spy((FunctionGrid) definition.getEditor(parent,
                                                            hasExpression,
                                                            expression,
                                                            hasName,
                                                            nesting).get());
    }

    @Test
    public void testInitialSetupFromDefinition() {
        setupGrid(0);

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
    public void testHeaderVisibilityWhenNested() {
        setupGrid(1);

        assertFalse(grid.isHeaderHidden());
    }

    @Test
    public void testHeaderVisibilityWhenNotNested() {
        setupGrid(0);

        assertFalse(grid.isHeaderHidden());
    }

    @Test
    public void testColumnMetaData() {
        setupGrid(0);

        final GridColumn<?> column = grid.getModel().getColumns().get(0);
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
        assertEquals("(" + PARAMETER_NAME + ")",
                     md2.getFormalParametersTitle());
    }

    @Test
    public void testOnItemSelectedExpressionColumnDefinedExpressionType() {
        setupGrid(0);

        //The default model from FunctionEditorDefinition has a Literal Expression at (0, 0)
        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);

        assertThat(items.size()).isEqualTo(5);
        assertDefaultListItems(items.subList(0, 3));

        assertThat(items.get(DIVIDER)).isInstanceOf(HasListSelectorControl.ListSelectorDividerItem.class);
        assertListSelectorItem(items.get(CLEAR_EXPRESSION_TYPE),
                               DMNEditorConstants.ExpressionEditor_Clear);

        ((HasListSelectorControl.ListSelectorTextItem) items.get(CLEAR_EXPRESSION_TYPE)).getCommand().execute();
        verify(cellEditorControls).hide();
        verify(sessionCommandManager).execute(eq(handler),
                                              any(org.kie.workbench.common.dmn.client.commands.general.ClearExpressionTypeCommand.class));
    }

    @Test
    public void testOnItemSelectedExpressionColumnUndefinedExpressionType() {
        setupGrid(0);

        //Clear editor for expression at (0, 0)
        grid.getModel().setCellValue(0, 0, new ExpressionCellValue(Optional.empty()));

        assertDefaultListItems(grid.getItems(0, 0));
    }

    private void assertDefaultListItems(final List<HasListSelectorControl.ListSelectorItem> items) {
        assertThat(items.size()).isEqualTo(3);
        assertListSelectorItem(items.get(KIND_FEEL),
                               DMNEditorConstants.FunctionEditor_FEEL);
        assertListSelectorItem(items.get(KIND_JAVA),
                               DMNEditorConstants.FunctionEditor_JAVA);
        assertListSelectorItem(items.get(KIND_PMML),
                               DMNEditorConstants.FunctionEditor_PMML);
    }

    private void assertListSelectorItem(final HasListSelectorControl.ListSelectorItem item,
                                        final String text) {
        assertThat(item).isInstanceOf(HasListSelectorControl.ListSelectorTextItem.class);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) item;
        assertThat(ti.getText()).isEqualTo(text);
    }

    @Test
    public void testOnItemSelected() {
        setupGrid(0);

        final Command command = mock(Command.class);
        final HasListSelectorControl.ListSelectorTextItem listSelectorItem = mock(HasListSelectorControl.ListSelectorTextItem.class);
        when(listSelectorItem.getCommand()).thenReturn(command);

        grid.onItemSelected(listSelectorItem);

        verify(command).execute();
    }

    @Test
    public void testOnItemSelectedKindFEEL() {
        setupGrid(0);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(KIND_FEEL);

        when(supplementaryLiteralExpressionEditorDefinition.getType()).thenReturn(ExpressionType.FUNCTION);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).setKind(eq(FunctionDefinition.Kind.FEEL));
    }

    @Test
    public void testOnItemSelectedKindJava() {
        setupGrid(0);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(KIND_JAVA);

        when(supplementaryLiteralExpressionEditorDefinition.getType()).thenReturn(ExpressionType.FUNCTION_JAVA);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).setKind(eq(FunctionDefinition.Kind.JAVA));
    }

    @Test
    public void testOnItemSelectedKindPMML() {
        setupGrid(0);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(KIND_PMML);

        when(supplementaryLiteralExpressionEditorDefinition.getType()).thenReturn(ExpressionType.FUNCTION_PMML);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).setKind(eq(FunctionDefinition.Kind.PMML));
    }

    @Test
    public void testGetParameters() {
        setupGrid(0);

        final List<InformationItem> parameters = grid.getParameters();

        assertEquals(1,
                     parameters.size());
        assertEquals(parameter,
                     parameters.get(0));
        assertEquals(PARAMETER_NAME,
                     parameters.get(0).getName().getValue());
    }

    @Test
    public void testAddParameter() {
        setupGrid(0);

        grid.addParameter(() -> {/*Nothing*/});

        verify(sessionCommandManager).execute(eq(handler),
                                              any(AddParameterCommand.class));
    }

    @Test
    public void testRemoveParameter() {
        setupGrid(0);

        grid.removeParameter(parameter,
                             () -> {/*Nothing*/});

        verify(sessionCommandManager).execute(eq(handler),
                                              any(RemoveParameterCommand.class));
    }

    @Test
    public void testUpdateParameterName() {
        setupGrid(0);

        grid.updateParameterName(parameter,
                                 "name");

        verify(sessionCommandManager).execute(eq(handler),
                                              any(UpdateParameterNameCommand.class));
    }

    @Test
    public void testSetKindFEEL() {
        setupGrid(0);

        grid.setKind(FunctionDefinition.Kind.FEEL);

        assertSetKind(FunctionDefinition.Kind.FEEL,
                      literalExpression,
                      literalExpressionEditor);
    }

    @Test
    public void testSetKindJava() {
        setupGrid(0);

        doReturn(ExpressionType.FUNCTION_JAVA).when(supplementaryLiteralExpressionEditorDefinition).getType();

        grid.setKind(FunctionDefinition.Kind.JAVA);

        assertSetKind(FunctionDefinition.Kind.JAVA,
                      supplementaryLiteralExpression,
                      supplementaryLiteralExpressionEditor);
    }

    @Test
    public void testSetKindPMML() {
        setupGrid(0);

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

        verify(sessionCommandManager).execute(eq(handler),
                                              any(SetKindCommand.class));
    }

    @Test
    public void testClearExpressionType() {
        setupGrid(0);

        grid.clearExpressionType();

        verify(sessionCommandManager).execute(eq(handler),
                                              any(ClearExpressionTypeCommand.class));
    }

    @Test
    public void testSynchroniseViewWhenExpressionEditorChanged() {
        setupGrid(0);

        final double literalWidth = 123.0;
        final double literalPadding = 1.0;
        final double columnWidth = literalWidth + 2 * literalPadding;

        doReturn(literalWidth).when(literalExpressionEditor).getWidth();
        doReturn(literalPadding).when(literalExpressionEditor).getPadding();

        grid.synchroniseViewWhenExpressionEditorChanged(literalExpressionEditor);

        verify(parent).onResize();
        verify(parent).proposeContainingColumnWidth(columnWidth);
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(gridLayer).batch(redrawCaptor.capture());

        final GridLayerRedrawManager.PrioritizedCommand command = redrawCaptor.getValue();
        command.execute();

        verify(gridLayer).draw();
        verify(gridLayer).select(eq(literalExpressionEditor));
    }
}
