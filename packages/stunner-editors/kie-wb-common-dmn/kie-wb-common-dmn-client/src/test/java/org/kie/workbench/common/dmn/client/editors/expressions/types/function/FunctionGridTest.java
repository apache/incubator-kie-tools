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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.AddParameterCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.RemoveParameterCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.SetKindCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.UpdateParameterNameCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.function.UpdateParameterTypeRefCommand;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHasValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHasValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetTypeRefCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.GridFactoryCommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector.KindPopoverView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParametersPopoverView;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.FunctionSupplementaryGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionColumn;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager.PrioritizedCommand;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
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

    private static final String PARAMETER_NAME = "parameter-name";

    private static final String PARAMETER_NAME_NEW = "parameter-name-new";

    private static final String NODE_UUID = "uuid";

    private static final String NAME = "name";

    private static final String NAME_NEW = "name-new";

    private final static int CLEAR_EXPRESSION_TYPE = 0;

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
    private DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    private DMNSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private Node node;

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
    private KindPopoverView.Presenter kindEditor;

    @Mock
    private GridWidget parentGridWidget;

    @Mock
    private GridData parentGridData;

    @Mock
    private GridColumn parentGridColumn;

    @Mock
    private GridCellTuple parent;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private LiteralExpressionGrid literalExpressionEditor;

    @Mock
    private ExpressionEditorDefinition supplementaryExpressionEditorDefinition;

    @Mock
    private FunctionSupplementaryGrid supplementaryExpressionEditor;

    @Mock
    private Command onSuccess;

    @Mock
    private ValueAndDataTypePopoverView.Presenter headerEditor;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

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
    private ArgumentCaptor<Optional<ExpressionEditorDefinition<Expression>>> expressionDefinitionCaptor;

    @Captor
    private ArgumentCaptor<PrioritizedCommand> redrawCommandCaptor;

    @Captor
    private ArgumentCaptor<GridCellTuple> parentCaptor;

    @Captor
    private ArgumentCaptor<CompositeCommand> compositeCommandCaptor;

    @Captor
    private ArgumentCaptor<DomainObjectSelectionEvent> domainObjectSelectionEventCaptor;

    private LiteralExpressionEditorDefinition literalExpressionEditorDefinition;

    private Context supplementaryExpression = new Context();

    private Decision hasExpression = new Decision();

    private Optional<FunctionDefinition> expression = Optional.empty();

    private Optional<HasName> hasName = Optional.empty();

    private InformationItem parameter = new InformationItem();

    private FunctionEditorDefinition definition;

    private FunctionGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(parent.getGridWidget()).thenReturn(parentGridWidget);
        when(parentGridWidget.getModel()).thenReturn(parentGridData);
        when(parentGridData.getColumns()).thenReturn(Collections.singletonList(parentGridColumn));

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getGridPanel()).thenReturn(gridPanel);
        when(session.getGridLayer()).thenReturn(gridLayer);
        when(session.getCellEditorControls()).thenReturn(cellEditorControls);

        definition = new FunctionEditorDefinition(definitionUtils,
                                                  sessionManager,
                                                  sessionCommandManager,
                                                  canvasCommandFactory,
                                                  editorSelectedEvent,
                                                  refreshFormPropertiesEvent,
                                                  domainObjectSelectionEvent,
                                                  listSelector,
                                                  translationService,
                                                  expressionEditorDefinitionsSupplier,
                                                  supplementaryEditorDefinitionsSupplier,
                                                  headerEditor,
                                                  parametersEditor,
                                                  kindEditor,
                                                  readOnlyProvider);
        literalExpressionEditorDefinition = spy(new LiteralExpressionEditorDefinition(definitionUtils,
                                                                                      sessionManager,
                                                                                      sessionCommandManager,
                                                                                      canvasCommandFactory,
                                                                                      editorSelectedEvent,
                                                                                      refreshFormPropertiesEvent,
                                                                                      domainObjectSelectionEvent,
                                                                                      listSelector,
                                                                                      translationService,
                                                                                      headerEditor,
                                                                                      readOnlyProvider));

        expression = definition.getModelClass();
        definition.enrich(Optional.empty(), hasExpression, expression);
        expression.get().getFormalParameter().add(parameter);
        parameter.getName().setValue(PARAMETER_NAME);

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add((ExpressionEditorDefinition) definition);
        expressionEditorDefinitions.add((ExpressionEditorDefinition) literalExpressionEditorDefinition);
        expressionEditorDefinitions.add(supplementaryExpressionEditorDefinition);

        final Decision decision = new Decision();
        decision.setName(new Name("name"));
        hasName = Optional.of(decision);

        when(expressionEditorDefinitionsSupplier.get()).thenReturn(expressionEditorDefinitions);
        when(supplementaryEditorDefinitionsSupplier.get()).thenReturn(expressionEditorDefinitions);

        //Setup LiteralExpression definition and editor
        doReturn(Optional.of(literalExpressionEditor)).when(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                         any(Optional.class),
                                                                                                         any(HasExpression.class),
                                                                                                         any(Optional.class),
                                                                                                         anyBoolean(),
                                                                                                         anyInt());
        when(literalExpressionEditor.getGridPanel()).thenReturn(gridPanel);
        when(literalExpressionEditor.getLayer()).thenReturn(gridLayer);
        when(literalExpressionEditor.getModel()).thenReturn(new BaseGridData(false));
        doCallRealMethod().when(literalExpressionEditor).resize(any(Function.class));
        doCallRealMethod().when(literalExpressionEditor).doResize(any(PrioritizedCommand.class), any(Function.class));
        doCallRealMethod().when(literalExpressionEditor).selectFirstCell();
        when(literalExpressionEditor.getParentInformation()).thenReturn(parent);

        //Setup Supplementary expression definition and editor
        when(supplementaryExpressionEditorDefinition.getModelClass()).thenReturn(Optional.of(supplementaryExpression));
        when(supplementaryExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                               any(Optional.class),
                                                               any(HasExpression.class),
                                                               any(Optional.class),
                                                               anyBoolean(),
                                                               anyInt())).thenReturn(Optional.of(supplementaryExpressionEditor));
        when(supplementaryExpressionEditor.getGridPanel()).thenReturn(gridPanel);
        when(supplementaryExpressionEditor.getLayer()).thenReturn(gridLayer);
        when(supplementaryExpressionEditor.getModel()).thenReturn(new BaseGridData(false));
        doCallRealMethod().when(supplementaryExpressionEditor).resize(any(Function.class));
        doCallRealMethod().when(supplementaryExpressionEditor).doResize(any(PrioritizedCommand.class), any(Function.class));
        doCallRealMethod().when(supplementaryExpressionEditor).selectFirstCell();
        when(supplementaryExpressionEditor.getParentInformation()).thenReturn(parent);

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(gridWidget.getModel()).thenReturn(new BaseGridData(false));

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(Collections.singletonList(node));

        when(canvasHandler.getGraphIndex()).thenReturn(index);
        when(index.get(Mockito.<String>any())).thenReturn(element);
        when(element.getContent()).thenReturn(mock(Definition.class));
        when(definitionUtils.getNameIdentifier(any())).thenReturn("name");
        when(canvasCommandFactory.updatePropertyValue(any(Element.class),
                                                      Mockito.<String>any(),
                                                      any())).thenReturn(mock(UpdateElementPropertyCommand.class));
        when(canvasHandler.getGraphExecutionContext()).thenReturn(graphCommandExecutionContext);

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(Mockito.<String>any());
        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).getTranslation(Mockito.<String>any());
    }

    private void setupGrid(final int nesting) {
        this.hasExpression.setExpression(expression.get());
        this.grid = spy((FunctionGrid) definition.getEditor(parent,
                                                            nesting == 0 ? Optional.of(NODE_UUID) : Optional.empty(),
                                                            hasExpression,
                                                            hasName,
                                                            false,
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

        assertEquals(2,
                     uiModel.getColumnCount());
        assertTrue(uiModel.getColumns().get(1) instanceof ExpressionEditorColumn);

        assertEquals(1,
                     uiModel.getRowCount());

        assertTrue(uiModel.getCell(0, 1).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv = (ExpressionCellValue) uiModel.getCell(0, 1).getValue();
        assertTrue(dcv.getValue().get() instanceof LiteralExpressionGrid);
    }

    @Test
    public void testInitialColumnWidthsFromDefinition() {
        setupGrid(0);

        assertComponentWidths(EmptyColumn.DEFAULT_WIDTH,
                              UndefinedExpressionColumn.DEFAULT_WIDTH);
    }

    @Test
    public void testInitialColumnWidthsFromExpression() {
        final List<Double> componentWidths = expression.get().getComponentWidths();
        componentWidths.set(0, 100.0);
        componentWidths.set(1, 200.0);

        setupGrid(0);

        assertComponentWidths(100.0,
                              200.0);
    }

    private void assertComponentWidths(final double... widths) {
        final GridData uiModel = grid.getModel();
        IntStream.range(0, widths.length).forEach(i -> assertEquals(widths[i], uiModel.getColumns().get(i).getWidth(), 0.0));
    }

    @Test
    public void testCacheable() {
        setupGrid(0);

        assertTrue(grid.isCacheable());
    }

    @Test
    public void testColumnMetaData() {
        setupGrid(0);

        final GridColumn<?> column = grid.getModel().getColumns().get(1);
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
    public void testColumnMetaDataWhenNested() {
        setupGrid(1);

        final GridColumn<?> column = grid.getModel().getColumns().get(1);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof FunctionColumnParametersHeaderMetaData);

        final FunctionColumnParametersHeaderMetaData md1 = (FunctionColumnParametersHeaderMetaData) header.get(0);

        assertEquals("F",
                     md1.getExpressionLanguageTitle());
        assertEquals("(" + PARAMETER_NAME + ")",
                     md1.getFormalParametersTitle());
    }

    @Test
    public void testOnItemSelectedExpressionColumnDefinedExpressionType() {
        setupGrid(0);

        //The default model from FunctionEditorDefinition has a Literal Expression at (0, 1)
        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 1);

        //Only the "Clear" item
        assertThat(items.size()).isEqualTo(1);

        assertListSelectorItem(items.get(CLEAR_EXPRESSION_TYPE),
                               DMNEditorConstants.ExpressionEditor_Clear);

        ((HasListSelectorControl.ListSelectorTextItem) items.get(CLEAR_EXPRESSION_TYPE)).getCommand().execute();
        verify(cellEditorControls).hide();
        verify(gridPanel).setFocus(true);
        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(ClearExpressionTypeCommand.class));
    }

    @Test
    public void testOnItemSelectedExpressionColumnUndefinedExpressionType() {
        setupGrid(0);

        //Clear editor for expression at (0, 0)
        grid.getModel().setCellValue(0, 0, new ExpressionCellValue(Optional.empty()));

        assertNoListItems(grid.getItems(0, 0));
    }

    private void assertNoListItems(final List<HasListSelectorControl.ListSelectorItem> items) {
        assertThat(items.size()).isEqualTo(0);
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

        final Command command = mock(Command.class);

        grid.updateParameterName(parameter,
                                 PARAMETER_NAME_NEW,
                                 command);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              updateParameterNameCommandCaptor.capture());

        final UpdateParameterNameCommand updateParameterNameCommand = updateParameterNameCommandCaptor.getValue();
        updateParameterNameCommand.execute(canvasHandler);

        assertEquals(PARAMETER_NAME_NEW, parameter.getName().getValue());
        verify(gridLayer).batch();
        verify(command).execute();
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

        grid.setKind(FunctionDefinition.Kind.FEEL);

        assertSetKind(literalExpressionEditorDefinition,
                      literalExpressionEditor,
                      FunctionDefinition.Kind.FEEL,
                      LiteralExpression.class,
                      LiteralExpressionGrid.class);

        verify(literalExpressionEditorDefinition).getEditor(parentCaptor.capture(),
                                                            eq(Optional.empty()),
                                                            eq(expression.get()),
                                                            eq(hasName),
                                                            eq(false),
                                                            eq(1));

        final GridCellTuple parent = parentCaptor.getValue();
        assertEquals(grid, parent.getGridWidget());
        assertEquals(0, parent.getRowIndex());
        assertEquals(1, parent.getColumnIndex());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetKindJava() {
        setupGrid(0);

        doReturn(ExpressionType.FUNCTION_JAVA).when(supplementaryExpressionEditorDefinition).getType();

        grid.setKind(FunctionDefinition.Kind.JAVA);

        assertSetKind(supplementaryExpressionEditorDefinition,
                      supplementaryExpressionEditor,
                      FunctionDefinition.Kind.JAVA,
                      Context.class,
                      FunctionSupplementaryGrid.class);

        verify(supplementaryExpressionEditorDefinition).getEditor(parentCaptor.capture(),
                                                                  eq(Optional.empty()),
                                                                  eq(expression.get()),
                                                                  eq(hasName),
                                                                  eq(false),
                                                                  eq(1));

        final GridCellTuple parent = parentCaptor.getValue();
        assertEquals(grid, parent.getGridWidget());
        assertEquals(0, parent.getRowIndex());
        assertEquals(1, parent.getColumnIndex());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetKindPMML() {
        setupGrid(0);

        doReturn(ExpressionType.FUNCTION_PMML).when(supplementaryExpressionEditorDefinition).getType();

        grid.setKind(FunctionDefinition.Kind.PMML);

        assertSetKind(supplementaryExpressionEditorDefinition,
                      supplementaryExpressionEditor,
                      FunctionDefinition.Kind.PMML,
                      Context.class,
                      FunctionSupplementaryGrid.class);

        verify(supplementaryExpressionEditorDefinition).getEditor(parentCaptor.capture(),
                                                                  eq(Optional.empty()),
                                                                  eq(expression.get()),
                                                                  eq(hasName),
                                                                  eq(false),
                                                                  eq(1));

        final GridCellTuple parent = parentCaptor.getValue();
        assertEquals(grid, parent.getGridWidget());
        assertEquals(0, parent.getRowIndex());
        assertEquals(1, parent.getColumnIndex());
    }

    @SuppressWarnings("unchecked")
    private void assertSetKind(final ExpressionEditorDefinition definition,
                               final BaseExpressionGrid editor,
                               final FunctionDefinition.Kind expectedKind,
                               final Class<?> expectedExpressionType,
                               final Class<?> expectedEditorType) {
        verify(grid).doSetKind(eq(expectedKind),
                               eq(expression.get()),
                               expressionDefinitionCaptor.capture());
        verify(definition).enrich(any(Optional.class), eq(hasExpression), any(Optional.class));
        final ExpressionEditorDefinition<Expression> expressionDefinition = expressionDefinitionCaptor.getValue().get();
        assertThat(expectedExpressionType).isAssignableFrom(expressionDefinition.getModelClass().get().getClass());

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              setKindCommandCaptor.capture());

        final SetKindCommand setKindCommand = setKindCommandCaptor.getValue();
        setKindCommand.execute(canvasHandler);

        final GridCellValue<?> gcv = grid.getModel().getCell(0, 1).getValue();
        assertTrue(gcv instanceof ExpressionCellValue);
        final ExpressionCellValue ecv = (ExpressionCellValue) gcv;
        assertThat(expectedEditorType).isAssignableFrom(ecv.getValue().get().getClass());

        verify(editor).resize(eq(BaseExpressionGrid.RESIZE_EXISTING));
        verify(editor).selectFirstCell();

        verify(gridLayer).batch(redrawCommandCaptor.capture());
        redrawCommandCaptor.getValue().execute();
        verify(gridLayer).draw();

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
                                                                                                         anyBoolean(),
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
        assertThat(grid.getModel().getColumns().get(1).getWidth()).isEqualTo(UndefinedExpressionColumn.DEFAULT_WIDTH);
        verify(grid).resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);

        verify(grid).selectExpressionEditorFirstCell(eq(0), eq(1));
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
        final double changeKindColumnWidth = 150.0;
        final double columnWidth = changeKindColumnWidth + literalWidth + 2 * grid.getPadding();

        grid.getModel().getColumns().get(0).setWidth(literalWidth);

        grid.resize(BaseExpressionGrid.RESIZE_EXISTING);

        verify(parent).onResize();
        verify(parent).proposeContainingColumnWidth(eq(columnWidth), eq(BaseExpressionGrid.RESIZE_EXISTING));
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(gridLayer).batch(redrawCommandCaptor.capture());

        final PrioritizedCommand command = redrawCommandCaptor.getValue();
        command.execute();

        verify(gridLayer).draw();
    }

    @Test
    public void testGetDisplayName() {
        setupGrid(0);

        assertThat(extractHeaderMetaData().getValue().getValue()).isEqualTo(NAME);
    }

    private FunctionColumnNameHeaderMetaData extractHeaderMetaData() {
        final FunctionColumn column = (FunctionColumn) grid.getModel().getColumns().get(1);
        return (FunctionColumnNameHeaderMetaData) column.getHeaderMetaData().get(0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameWithNoChange() {
        setupGrid(0);

        extractHeaderMetaData().setValue(new Name(NAME));

        verify(sessionCommandManager, never()).execute(any(AbstractCanvasHandler.class),
                                                       any(org.kie.workbench.common.stunner.core.command.Command.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameWithEmptyValue() {
        setupGrid(0);

        extractHeaderMetaData().setValue(new Name());

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              compositeCommandCaptor.capture());

        GridFactoryCommandUtils.assertCommands(compositeCommandCaptor.getValue(),
                                               DeleteHasValueCommand.class,
                                               UpdateElementPropertyCommand.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameWithNullValue() {
        setupGrid(0);

        extractHeaderMetaData().setValue(null);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              compositeCommandCaptor.capture());

        GridFactoryCommandUtils.assertCommands(compositeCommandCaptor.getValue(),
                                               DeleteHasValueCommand.class,
                                               UpdateElementPropertyCommand.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameWithNonEmptyValue() {
        setupGrid(0);

        extractHeaderMetaData().setValue(new Name(NAME_NEW));

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              compositeCommandCaptor.capture());

        GridFactoryCommandUtils.assertCommands(compositeCommandCaptor.getValue(),
                                               SetHasValueCommand.class,
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
                                                                                                         anyBoolean(),
                                                                                                         anyInt());

        setupGrid(0);

        grid.selectFirstCell();

        verify(grid).selectCell(eq(0),
                                eq(0),
                                eq(false),
                                eq(false));

        assertNOPDomainObjectSelection();
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

        grid.selectHeaderCell(1, 1, false, false);

        assertNOPDomainObjectSelection();
    }

    @Test
    public void testSelectHeaderParametersRowWhenNested() {
        setupGrid(1);

        grid.selectHeaderCell(0, 0, false, false);

        assertNOPDomainObjectSelection();
    }

    private void assertNOPDomainObjectSelection() {
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