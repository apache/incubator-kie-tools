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
import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.AddParameterCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.RemoveParameterCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.SetKindCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.UpdateParameterNameCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.UpdateParameterTypeRefCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHasNameCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHasNameCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetTypeRefCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.GridFactoryCommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParametersPopoverView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.FunctionSupplementaryGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.mocks.EventSourceMock;
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
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class FunctionGridTest {

    private static final String PARAMETER_NAME = "name";

    private static final String NODE_UUID = "uuid";

    private static final String NAME = "name";

    private static final String NAME_NEW = "name-new";

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
    private GridWidget gridWidget;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private DMNSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Index index;

    @Mock
    private Element element;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private TranslationService translationService;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private Supplier<ExpressionEditorDefinitions> supplementaryEditorDefinitionsSupplier;

    @Mock
    private ParametersPopoverView.Presenter parametersEditor;

    @Mock
    private GridCellTuple parent;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private LiteralExpressionGrid literalExpressionEditor;

    @Mock
    private ExpressionEditorDefinition supplementaryLiteralExpressionEditorDefinition;

    @Mock
    private FunctionSupplementaryGrid supplementaryLiteralExpressionEditor;

    @Mock
    private Command onSuccess;

    @Mock
    private NameAndDataTypePopoverView.Presenter headerEditor;

    @Captor
    private ArgumentCaptor<AddParameterCommand> addParameterCommandCaptor;

    @Captor
    private ArgumentCaptor<RemoveParameterCommand> removeParameterCommandCaptor;

    @Captor
    private ArgumentCaptor<UpdateParameterNameCommand> updateParameterNameCommandCaptor;

    @Captor
    private ArgumentCaptor<UpdateParameterTypeRefCommand> updateParameterTypeRefCommandCaptor;

    @Captor
    private ArgumentCaptor<SetKindCommand> setKindCommandCaptor;

    @Captor
    private ArgumentCaptor<ClearExpressionTypeCommand> clearExpressionTypeCommandCaptor;

    @Captor
    private ArgumentCaptor<Optional<Expression>> expressionCaptor;

    @Captor
    private ArgumentCaptor<Optional<BaseExpressionGrid>> gridWidgetCaptor;

    @Captor
    private ArgumentCaptor<GridLayerRedrawManager.PrioritizedCommand> redrawCommandCaptor;

    @Captor
    private ArgumentCaptor<GridCellTuple> parentCaptor;

    @Captor
    private ArgumentCaptor<Optional<LiteralExpression>> literalExpressionCaptor;

    @Captor
    private ArgumentCaptor<Optional<Context>> contextExpressionCaptor;

    @Captor
    private ArgumentCaptor<CompositeCommand> compositeCommandCaptor;

    @Captor
    private ArgumentCaptor<DomainObjectSelectionEvent> domainObjectSelectionEventCaptor;

    private LiteralExpressionEditorDefinition literalExpressionEditorDefinition;

    private Context supplementaryLiteralExpression = new Context();

    private Decision hasExpression = new Decision();

    private Optional<FunctionDefinition> expression = Optional.empty();

    private Optional<HasName> hasName = Optional.empty();

    private InformationItem parameter = new InformationItem();

    private FunctionEditorDefinition definition;

    private FunctionGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getGridPanel()).thenReturn(gridPanel);
        when(session.getGridLayer()).thenReturn(gridLayer);
        when(session.getCellEditorControls()).thenReturn(cellEditorControls);

        definition = new FunctionEditorDefinition(definitionUtils,
                                                  sessionManager,
                                                  sessionCommandManager,
                                                  canvasCommandFactory,
                                                  editorSelectedEvent,
                                                  domainObjectSelectionEvent,
                                                  listSelector,
                                                  translationService,
                                                  expressionEditorDefinitionsSupplier,
                                                  supplementaryEditorDefinitionsSupplier,
                                                  headerEditor,
                                                  parametersEditor);
        literalExpressionEditorDefinition = spy(new LiteralExpressionEditorDefinition(definitionUtils,
                                                                                      sessionManager,
                                                                                      sessionCommandManager,
                                                                                      canvasCommandFactory,
                                                                                      editorSelectedEvent,
                                                                                      domainObjectSelectionEvent,
                                                                                      listSelector,
                                                                                      translationService,
                                                                                      headerEditor));

        expression = definition.getModelClass();
        definition.enrich(Optional.empty(), expression);
        expression.get().getFormalParameter().add(parameter);
        parameter.getName().setValue(PARAMETER_NAME);

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add((ExpressionEditorDefinition) definition);
        expressionEditorDefinitions.add((ExpressionEditorDefinition) literalExpressionEditorDefinition);
        expressionEditorDefinitions.add(supplementaryLiteralExpressionEditorDefinition);

        final Decision decision = new Decision();
        decision.setName(new Name("name"));
        hasName = Optional.of(decision);

        doReturn(expressionEditorDefinitions).when(expressionEditorDefinitionsSupplier).get();
        doReturn(expressionEditorDefinitions).when(supplementaryEditorDefinitionsSupplier).get();

        doReturn(Optional.of(supplementaryLiteralExpression)).when(supplementaryLiteralExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(supplementaryLiteralExpressionEditor)).when(supplementaryLiteralExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                                                   any(Optional.class),
                                                                                                                                   any(HasExpression.class),
                                                                                                                                   any(Optional.class),
                                                                                                                                   any(Optional.class),
                                                                                                                                   anyInt());
        when(supplementaryLiteralExpressionEditor.getLayer()).thenReturn(gridLayer);
        when(supplementaryLiteralExpressionEditor.getModel()).thenReturn(new BaseGridData(false));
        doCallRealMethod().when(supplementaryLiteralExpressionEditor).selectFirstCell();

        doReturn(canvasHandler).when(session).getCanvasHandler();

        when(gridWidget.getModel()).thenReturn(new BaseGridData(false));
        when(canvasHandler.getGraphIndex()).thenReturn(index);
        when(index.get(anyString())).thenReturn(element);
        when(element.getContent()).thenReturn(mock(Definition.class));
        when(definitionUtils.getNameIdentifier(any())).thenReturn("name");
        when(canvasCommandFactory.updatePropertyValue(any(Element.class),
                                                      anyString(),
                                                      any())).thenReturn(mock(UpdateElementPropertyCommand.class));

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(anyString());
        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).getTranslation(anyString());
    }

    private void setupGrid(final int nesting) {
        this.grid = spy((FunctionGrid) definition.getEditor(parent,
                                                            nesting == 0 ? Optional.of(NODE_UUID) : Optional.empty(),
                                                            hasExpression,
                                                            expression,
                                                            hasName,
                                                            nesting).get());

        when(parent.getGridWidget()).thenReturn(gridWidget);
        when(parent.getRowIndex()).thenReturn(0);
        when(parent.getColumnIndex()).thenReturn(0);
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
        assertTrue(dcv.getValue().get() instanceof LiteralExpressionGrid);
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
    public void testCacheable() {
        setupGrid(0);

        assertTrue(grid.isCacheable());
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
        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(ClearExpressionTypeCommand.class));
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

        grid.addParameter(onSuccess);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              addParameterCommandCaptor.capture());

        final AddParameterCommand addParameterCommand = addParameterCommandCaptor.getValue();
        addParameterCommand.execute(canvasHandler);

        verify(gridLayer).batch();
        verify(onSuccess).execute();
    }

    @Test
    public void testRemoveParameter() {
        setupGrid(0);

        grid.removeParameter(parameter,
                             onSuccess);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              removeParameterCommandCaptor.capture());

        final RemoveParameterCommand removeParameterCommand = removeParameterCommandCaptor.getValue();
        removeParameterCommand.execute(canvasHandler);

        verify(gridLayer).batch();
        verify(onSuccess).execute();
    }

    @Test
    public void testUpdateParameterName() {
        setupGrid(0);

        grid.updateParameterName(parameter,
                                 "name");

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              updateParameterNameCommandCaptor.capture());

        final UpdateParameterNameCommand updateParameterNameCommand = updateParameterNameCommandCaptor.getValue();
        updateParameterNameCommand.execute(canvasHandler);

        verify(gridLayer).batch();
    }

    @Test
    public void testUpdateParameterTypeRef() {
        setupGrid(0);

        grid.updateParameterTypeRef(parameter,
                                    new QName(QName.NULL_NS_URI,
                                              BuiltInType.DATE.getName()));

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              updateParameterTypeRefCommandCaptor.capture());

        final UpdateParameterTypeRefCommand updateParameterTypeRefCommand = updateParameterTypeRefCommandCaptor.getValue();
        updateParameterTypeRefCommand.execute(canvasHandler);

        verify(gridLayer).batch();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetKindFEEL() {
        setupGrid(0);

        reset(literalExpressionEditorDefinition);

        doReturn(Optional.of(literalExpressionEditor)).when(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                         any(Optional.class),
                                                                                                         any(HasExpression.class),
                                                                                                         any(Optional.class),
                                                                                                         any(Optional.class),
                                                                                                         anyInt());
        when(literalExpressionEditor.getLayer()).thenReturn(gridLayer);
        when(literalExpressionEditor.getParentInformation()).thenReturn(parent);
        when(literalExpressionEditor.getModel()).thenReturn(new BaseGridData(false));
        when(literalExpressionEditor.getExpression()).thenReturn(Optional.empty());
        doCallRealMethod().when(literalExpressionEditor).selectFirstCell();

        grid.setKind(FunctionDefinition.Kind.FEEL);

        verify(literalExpressionEditorDefinition).getEditor(parentCaptor.capture(),
                                                            eq(Optional.empty()),
                                                            eq(hasExpression),
                                                            literalExpressionCaptor.capture(),
                                                            eq(hasName),
                                                            eq(1));

        final Optional<LiteralExpression> literalExpression = literalExpressionCaptor.getValue();
        //Since we're using a concrete LiteralExpressionEditorDefinition we can only check the model is present
        assertTrue(literalExpression.isPresent());

        assertSetKind(literalExpressionEditorDefinition,
                      literalExpressionEditor,
                      FunctionDefinition.Kind.FEEL,
                      LiteralExpression.class,
                      LiteralExpressionGrid.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetKindJava() {
        setupGrid(0);

        doReturn(ExpressionType.FUNCTION_JAVA).when(supplementaryLiteralExpressionEditorDefinition).getType();

        grid.setKind(FunctionDefinition.Kind.JAVA);

        verify(supplementaryLiteralExpressionEditorDefinition).getEditor(parentCaptor.capture(),
                                                                         eq(Optional.empty()),
                                                                         eq(hasExpression),
                                                                         contextExpressionCaptor.capture(),
                                                                         eq(hasName),
                                                                         eq(1));

        final Optional<Context> contextExpression = contextExpressionCaptor.getValue();
        assertTrue(contextExpression.isPresent());
        assertEquals(supplementaryLiteralExpression, contextExpression.get());

        assertSetKind(supplementaryLiteralExpressionEditorDefinition,
                      supplementaryLiteralExpressionEditor,
                      FunctionDefinition.Kind.JAVA,
                      Context.class,
                      FunctionSupplementaryGrid.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetKindPMML() {
        setupGrid(0);

        doReturn(ExpressionType.FUNCTION_PMML).when(supplementaryLiteralExpressionEditorDefinition).getType();

        grid.setKind(FunctionDefinition.Kind.PMML);

        verify(supplementaryLiteralExpressionEditorDefinition).getEditor(parentCaptor.capture(),
                                                                         eq(Optional.empty()),
                                                                         eq(hasExpression),
                                                                         contextExpressionCaptor.capture(),
                                                                         eq(hasName),
                                                                         eq(1));

        final Optional<Context> contextExpression = contextExpressionCaptor.getValue();
        assertTrue(contextExpression.isPresent());
        assertEquals(supplementaryLiteralExpression, contextExpression.get());

        assertSetKind(supplementaryLiteralExpressionEditorDefinition,
                      supplementaryLiteralExpressionEditor,
                      FunctionDefinition.Kind.PMML,
                      Context.class,
                      FunctionSupplementaryGrid.class);
    }

    @SuppressWarnings("unchecked")
    private void assertSetKind(final ExpressionEditorDefinition definition,
                               final BaseExpressionGrid editor,
                               final FunctionDefinition.Kind expectedKind,
                               final Class<?> expectedExpressionType,
                               final Class<?> expectedEditorType) {
        verify(grid).doSetKind(eq(expectedKind),
                               eq(expression.get()),
                               expressionCaptor.capture(),
                               gridWidgetCaptor.capture());
        verify(definition).enrich(any(Optional.class), any(Optional.class));
        final Expression expression = expressionCaptor.getValue().get();
        final BaseExpressionGrid gridWidget = gridWidgetCaptor.getValue().get();
        assertThat(expectedExpressionType).isAssignableFrom(expression.getClass());
        assertThat(expectedEditorType).isAssignableFrom(gridWidget.getClass());

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              setKindCommandCaptor.capture());

        final SetKindCommand setKindCommand = setKindCommandCaptor.getValue();
        setKindCommand.execute(canvasHandler);

        verify(grid).resize(eq(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM));
        verify(editor).selectFirstCell();

        verify(gridLayer).batch(redrawCommandCaptor.capture());
        redrawCommandCaptor.getValue().execute();
        verify(gridLayer).draw();

        final GridCellTuple parent = parentCaptor.getValue();
        assertEquals(grid, parent.getGridWidget());
        assertEquals(0, parent.getRowIndex());
        assertEquals(0, parent.getColumnIndex());

        //Check undo operation
        reset(grid, gridLayer);
        setKindCommand.undo(canvasHandler);

        verify(grid).resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
        verify(grid).selectFirstCell();

        verify(gridLayer).batch(redrawCommandCaptor.capture());
        assertThat(redrawCommandCaptor.getAllValues()).hasSize(2);
        redrawCommandCaptor.getAllValues().get(1).execute();
        verify(gridLayer).draw();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClearExpressionType() {
        doReturn(Optional.of(literalExpressionEditor)).when(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                         any(Optional.class),
                                                                                                         any(HasExpression.class),
                                                                                                         any(Optional.class),
                                                                                                         any(Optional.class),
                                                                                                         anyInt());

        setupGrid(0);

        grid.clearExpressionType();

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              clearExpressionTypeCommandCaptor.capture());

        final ClearExpressionTypeCommand clearExpressionTypeCommand = clearExpressionTypeCommandCaptor.getValue();
        clearExpressionTypeCommand.execute(canvasHandler);

        verify(grid).resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
        verify(gridLayer).select(literalExpressionEditor);
        verify(literalExpressionEditor).selectFirstCell();
        verify(gridLayer).batch(redrawCommandCaptor.capture());
        redrawCommandCaptor.getValue().execute();
        verify(gridLayer).draw();

        //Check undo operation
        reset(grid, gridLayer);
        clearExpressionTypeCommand.undo(canvasHandler);

        //Verify Expression has been restored and UndefinedExpressionEditor resized
        assertThat(grid.getModel().getColumns().get(0).getWidth()).isEqualTo(DMNGridColumn.DEFAULT_WIDTH);
        verify(grid).resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);

        verify(grid).selectExpressionEditorFirstCell(eq(0), eq(0));
        verify(gridLayer).select(literalExpressionEditor);
        verify(literalExpressionEditor, times(2)).selectFirstCell();

        verify(gridLayer).batch(redrawCommandCaptor.capture());
        assertThat(redrawCommandCaptor.getAllValues()).hasSize(2);
        redrawCommandCaptor.getAllValues().get(1).execute();
        verify(gridLayer).draw();
    }

    @Test
    public void testResizeWhenExpressionEditorChanged() {
        setupGrid(0);

        final double literalWidth = 200.0;
        final double columnWidth = literalWidth + 2 * grid.getPadding();

        grid.getModel().getColumns().get(0).setWidth(literalWidth);

        grid.resize(BaseExpressionGrid.RESIZE_EXISTING);

        verify(parent).onResize();
        verify(parent).proposeContainingColumnWidth(eq(columnWidth), eq(BaseExpressionGrid.RESIZE_EXISTING));
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(gridLayer).batch(redrawCommandCaptor.capture());

        final GridLayerRedrawManager.PrioritizedCommand command = redrawCommandCaptor.getValue();
        command.execute();

        verify(gridLayer).draw();
    }

    @Test
    public void testGetDisplayName() {
        setupGrid(0);

        assertThat(extractHeaderMetaData().getName().getValue()).isEqualTo(NAME);
    }

    private FunctionColumnNameHeaderMetaData extractHeaderMetaData() {
        final FunctionColumn column = (FunctionColumn) grid.getModel().getColumns().get(0);
        return (FunctionColumnNameHeaderMetaData) column.getHeaderMetaData().get(0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameWithNoChange() {
        setupGrid(0);

        extractHeaderMetaData().setName(new Name(NAME));

        verify(sessionCommandManager, never()).execute(any(AbstractCanvasHandler.class),
                                                       any(org.kie.workbench.common.stunner.core.command.Command.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameWithEmptyValue() {
        setupGrid(0);

        extractHeaderMetaData().setName(new Name());

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              compositeCommandCaptor.capture());

        GridFactoryCommandUtils.assertCommands(compositeCommandCaptor.getValue(),
                                               DeleteHasNameCommand.class,
                                               UpdateElementPropertyCommand.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameWithNullValue() {
        setupGrid(0);

        extractHeaderMetaData().setName(null);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              compositeCommandCaptor.capture());

        GridFactoryCommandUtils.assertCommands(compositeCommandCaptor.getValue(),
                                               DeleteHasNameCommand.class,
                                               UpdateElementPropertyCommand.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameWithNonEmptyValue() {
        setupGrid(0);

        extractHeaderMetaData().setName(new Name(NAME_NEW));

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              compositeCommandCaptor.capture());

        GridFactoryCommandUtils.assertCommands(compositeCommandCaptor.getValue(),
                                               SetHasNameCommand.class,
                                               UpdateElementPropertyCommand.class);
    }

    @Test
    public void testGetTypeRef() {
        setupGrid(0);

        assertThat(extractHeaderMetaData().getTypeRef()).isNotNull();
    }

    @Test
    public void testSetTypeRef() {
        setupGrid(0);

        extractHeaderMetaData().setTypeRef(new QName(QName.NULL_NS_URI,
                                                     BuiltInType.DATE.getName()));

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(SetTypeRefCommand.class));
    }

    @Test
    public void testSetTypeRefWithoutChange() {
        setupGrid(0);

        extractHeaderMetaData().setTypeRef(new QName());

        verify(sessionCommandManager, never()).execute(any(AbstractCanvasHandler.class),
                                                       any(SetTypeRefCommand.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectFirstCell() {
        doReturn(Optional.of(literalExpressionEditor)).when(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                         any(Optional.class),
                                                                                                         any(HasExpression.class),
                                                                                                         any(Optional.class),
                                                                                                         any(Optional.class),
                                                                                                         anyInt());

        setupGrid(0);

        grid.selectFirstCell();

        verify(gridLayer).select(literalExpressionEditor);
        verify(literalExpressionEditor).selectFirstCell();
    }

    @Test
    public void testSelectHeaderNameRow() {
        setupGrid(0);

        grid.selectHeaderCell(0, 0, false, false);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(hasExpression);
    }

    @Test
    public void testSelectHeaderParametersRow() {
        setupGrid(0);

        grid.selectHeaderCell(1, 0, false, false);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isInstanceOf(NOPDomainObject.class);
    }

    @Test
    public void testAsDMNModelInstrumentedBase() {
        setupGrid(0);

        assertThat(extractHeaderMetaData().asDMNModelInstrumentedBase()).isInstanceOf(hasExpression.getVariable().getClass());
    }
}