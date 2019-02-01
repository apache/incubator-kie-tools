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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.Invocation;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.commands.expressions.types.invocation.AddParameterBindingCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.invocation.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.invocation.DeleteParameterBindingCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHasNameCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHeaderValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHasNameCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHeaderValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetTypeRefCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.GridFactoryCommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.types.HasNameAndTypeRef;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
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
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetDnDHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class InvocationGridTest {

    private final static int INSERT_PARAMETER_ABOVE = 0;

    private final static int INSERT_PARAMETER_BELOW = 1;

    private final static int DELETE_PARAMETER = 2;

    private final static int DIVIDER = 3;

    private final static int CLEAR_EXPRESSION_TYPE = 4;

    private static final String NODE_UUID = "uuid";

    private static final String NAME = "name";

    private static final String NAME_NEW = "name-new";

    private static final String EXPRESSION_TEXT_NEW = "invocation-expression-new";

    private GridCellTuple tupleWithoutValue;

    private GridCellValueTuple tupleWithValue;

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private AbsolutePanel gridLayerDomElementContainer;

    @Mock
    private GridWidget gridWidget;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DMNSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

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
    private GraphCommandExecutionContext graphContext;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private TranslationService translationService;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private NameAndDataTypePopoverView.Presenter headerEditor;

    @Mock
    private GridWidget parentGridWidget;

    @Mock
    private GridData parentGridData;

    @Mock
    private GridColumn parentGridColumn;

    @Mock
    private GridCellTuple parent;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private BaseExpressionGrid literalExpressionEditor;

    @Mock
    private UndefinedExpressionEditorDefinition undefinedExpressionEditorDefinition;

    @Mock
    private UndefinedExpressionGrid undefinedExpressionEditor;

    @Mock
    private GridWidgetDnDHandlersState dndHandlersState;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Captor
    private ArgumentCaptor<AddParameterBindingCommand> addParameterBindingCommandCaptor;

    @Captor
    private ArgumentCaptor<DeleteParameterBindingCommand> deleteParameterBindingCommandCaptor;

    @Captor
    private ArgumentCaptor<ClearExpressionTypeCommand> clearExpressionTypeCommandCaptor;

    @Captor
    private ArgumentCaptor<GridLayerRedrawManager.PrioritizedCommand> redrawCommandCaptor;

    @Captor
    private ArgumentCaptor<CompositeCommand> compositeCommandCaptor;

    @Captor
    private ArgumentCaptor<DomainObjectSelectionEvent> domainObjectSelectionEventCaptor;

    private Decision hasExpression = new Decision();

    private LiteralExpression literalExpression = new LiteralExpression();

    private Optional<Invocation> expression = Optional.empty();

    private Optional<HasName> hasName = Optional.empty();

    private InvocationEditorDefinition definition;

    private InvocationGrid grid;

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

        tupleWithoutValue = new GridCellTuple(0, 1, gridWidget);
        tupleWithValue = new GridCellValueTuple<>(0, 1, gridWidget, new BaseGridCellValue<>("value"));

        definition = new InvocationEditorDefinition(definitionUtils,
                                                    sessionManager,
                                                    sessionCommandManager,
                                                    canvasCommandFactory,
                                                    editorSelectedEvent,
                                                    refreshFormPropertiesEvent,
                                                    domainObjectSelectionEvent,
                                                    listSelector,
                                                    translationService,
                                                    expressionEditorDefinitionsSupplier,
                                                    headerEditor);

        expression = definition.getModelClass();
        definition.enrich(Optional.empty(), hasExpression, expression);
        expression.ifPresent(invocation -> ((LiteralExpression) invocation.getExpression()).getText().setValue("invocation-expression"));
        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add((ExpressionEditorDefinition) definition);
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);
        expressionEditorDefinitions.add(undefinedExpressionEditorDefinition);

        doReturn(expressionEditorDefinitions).when(expressionEditorDefinitionsSupplier).get();
        doReturn(Optional.of(literalExpression)).when(literalExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(literalExpressionEditor)).when(literalExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                         any(Optional.class),
                                                                                                         any(HasExpression.class),
                                                                                                         any(Optional.class),
                                                                                                         any(Optional.class),
                                                                                                         anyInt());

        doReturn(parent).when(undefinedExpressionEditor).getParentInformation();
        doReturn(Optional.empty()).when(undefinedExpressionEditorDefinition).getModelClass();
        doReturn(Optional.of(undefinedExpressionEditor)).when(undefinedExpressionEditorDefinition).getEditor(any(GridCellTuple.class),
                                                                                                             any(Optional.class),
                                                                                                             any(HasExpression.class),
                                                                                                             any(Optional.class),
                                                                                                             any(Optional.class),
                                                                                                             anyInt());

        doReturn(session).when(sessionManager).getCurrentSession();
        doReturn(canvasHandler).when(session).getCanvasHandler();
        doReturn(graphContext).when(canvasHandler).getGraphExecutionContext();

        final Decision decision = new Decision();
        decision.setName(new Name("name"));
        hasName = Optional.of(decision);

        when(gridWidget.getModel()).thenReturn(new BaseGridData(false));
        when(gridLayer.getDomElementContainer()).thenReturn(gridLayerDomElementContainer);
        when(gridLayerDomElementContainer.iterator()).thenReturn(mock(Iterator.class));
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, 100, 200));
        when(gridLayer.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(transform);

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(Collections.singletonList(node));

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
        this.grid = spy((InvocationGrid) definition.getEditor(parent,
                                                              nesting == 0 ? Optional.of(NODE_UUID) : Optional.empty(),
                                                              hasExpression,
                                                              expression,
                                                              hasName,
                                                              nesting).get());

        when(parent.getGridWidget()).thenReturn(gridWidget);
        when(parent.getRowIndex()).thenReturn(0);
        when(parent.getColumnIndex()).thenReturn(2);
    }

    @Test
    public void testInitialSetupFromDefinition() {
        setupGrid(0);

        final GridData uiModel = grid.getModel();
        assertTrue(uiModel instanceof InvocationGridData);

        assertEquals(3,
                     uiModel.getColumnCount());
        assertTrue(uiModel.getColumns().get(0) instanceof RowNumberColumn);
        assertTrue(uiModel.getColumns().get(1) instanceof InvocationParameterColumn);
        assertTrue(uiModel.getColumns().get(2) instanceof ExpressionEditorColumn);

        assertEquals(1,
                     uiModel.getRowCount());

        assertEquals(1,
                     uiModel.getCell(0, 0).getValue().getValue());
        assertEquals(InvocationDefaultValueUtilities.PREFIX + "1",
                     ((InformationItemCell.HasNameAndDataTypeCell) uiModel.getCell(0, 1).getValue().getValue()).getName().getValue());
        assertTrue(uiModel.getCell(0, 2).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv0 = (ExpressionCellValue) uiModel.getCell(0, 2).getValue();
        assertEquals(undefinedExpressionEditor,
                     dcv0.getValue().get());
    }

    @Test
    public void testCacheable() {
        setupGrid(0);

        assertTrue(grid.isCacheable());
    }

    @Test
    public void testNameColumnMetaData() {
        setupGrid(0);

        final GridColumn<?> column = grid.getModel().getColumns().get(InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(2,
                     header.size());
        assertTrue(header.get(0) instanceof InvocationColumnHeaderMetaData);
        assertTrue(header.get(1) instanceof InvocationColumnExpressionHeaderMetaData);

        final InvocationColumnHeaderMetaData md1 = (InvocationColumnHeaderMetaData) header.get(0);
        final InvocationColumnExpressionHeaderMetaData md2 = (InvocationColumnExpressionHeaderMetaData) header.get(1);

        assertEquals("name",
                     md1.getTitle());
        assertEquals("invocation-expression",
                     md2.getTitle());
    }

    @Test
    public void testNameColumnMetaDataWhenNested() {
        setupGrid(1);

        final GridColumn<?> column = grid.getModel().getColumns().get(InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof InvocationColumnExpressionHeaderMetaData);

        final InvocationColumnExpressionHeaderMetaData md1 = (InvocationColumnExpressionHeaderMetaData) header.get(0);

        assertEquals("invocation-expression",
                     md1.getTitle());
    }

    @Test
    public void testExpressionColumnMetaData() {
        setupGrid(0);

        final GridColumn<?> column = grid.getModel().getColumns().get(InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(2,
                     header.size());
        assertTrue(header.get(0) instanceof InvocationColumnHeaderMetaData);
        assertTrue(header.get(1) instanceof InvocationColumnExpressionHeaderMetaData);

        final InvocationColumnHeaderMetaData md1 = (InvocationColumnHeaderMetaData) header.get(0);
        final InvocationColumnExpressionHeaderMetaData md2 = (InvocationColumnExpressionHeaderMetaData) header.get(1);

        assertEquals("name",
                     md1.getTitle());
        assertEquals("invocation-expression",
                     md2.getTitle());
    }

    @Test
    public void testExpressionColumnMetaDataWhenNested() {
        setupGrid(1);

        final GridColumn<?> column = grid.getModel().getColumns().get(InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();

        assertEquals(1,
                     header.size());
        assertTrue(header.get(0) instanceof InvocationColumnExpressionHeaderMetaData);

        final InvocationColumnExpressionHeaderMetaData md1 = (InvocationColumnExpressionHeaderMetaData) header.get(0);

        assertEquals("invocation-expression",
                     md1.getTitle());
    }

    @Test
    public void testExpressionColumnMetaDataSetExpressionText() {
        setupGrid(0);

        final GridColumn<?> column = grid.getModel().getColumns().get(InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX);
        final List<GridColumn.HeaderMetaData> header = column.getHeaderMetaData();
        final InvocationColumnExpressionHeaderMetaData md2 = (InvocationColumnExpressionHeaderMetaData) header.get(1);

        md2.setTitle(EXPRESSION_TEXT_NEW);

        assertEquals(EXPRESSION_TEXT_NEW,
                     ((LiteralExpression) expression.get().getExpression()).getText().getValue());
    }

    @Test
    public void testGetItemsRowNumberColumn() {
        setupGrid(0);

        assertDefaultListItems(grid.getItems(0, 0), true);
    }

    @Test
    public void testOnItemSelectedNameColumn() {
        setupGrid(0);

        assertDefaultListItems(grid.getItems(0, 1), true);
    }

    @Test
    public void testOnItemSelectedExpressionColumnUndefinedExpressionType() {
        setupGrid(0);

        //The default model from ContextEditorDefinition has an undefined expression at (0, 2)
        assertDefaultListItems(grid.getItems(0, 2), true);
    }

    @Test
    public void testOnItemSelectedExpressionColumnDefinedExpressionType() {
        setupGrid(0);

        //Set an editor for expression at (0, 2)
        final BaseExpressionGrid editor = mock(BaseExpressionGrid.class);
        grid.getModel().setCellValue(0, 2, new ExpressionCellValue(Optional.of(editor)));

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 2);

        assertThat(items.size()).isEqualTo(5);
        assertDefaultListItems(items.subList(0, 3), true);

        assertThat(items.get(DIVIDER)).isInstanceOf(HasListSelectorControl.ListSelectorDividerItem.class);
        assertListSelectorItem(items.get(CLEAR_EXPRESSION_TYPE),
                               DMNEditorConstants.ExpressionEditor_Clear,
                               true);

        ((HasListSelectorControl.ListSelectorTextItem) items.get(CLEAR_EXPRESSION_TYPE)).getCommand().execute();
        verify(cellEditorControls).hide();
        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(ClearExpressionTypeCommand.class));
    }

    @Test
    public void testGetItemsWithCellSelectionsCoveringMultipleRows() {
        setupGrid(0);

        addParameterBinding(0);
        grid.getModel().selectCell(0, 0);
        grid.getModel().selectCell(1, 0);

        assertDefaultListItems(grid.getItems(0, 0), false);
    }

    @Test
    public void testOnItemSelectedExpressionColumnDefinedExpressionTypeWithCellSelectionsCoveringMultipleRows() {
        setupGrid(0);

        addParameterBinding(0);
        grid.getModel().selectCell(0, 0);
        grid.getModel().selectCell(1, 0);

        //Set an editor for expression at (0, 2)
        final BaseExpressionGrid editor = mock(BaseExpressionGrid.class);
        grid.getModel().setCellValue(0, 2, new ExpressionCellValue(Optional.of(editor)));

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 2);

        assertThat(items.size()).isEqualTo(5);
        assertDefaultListItems(items.subList(0, 3), false);

        assertThat(items.get(DIVIDER)).isInstanceOf(HasListSelectorControl.ListSelectorDividerItem.class);
        assertListSelectorItem(items.get(CLEAR_EXPRESSION_TYPE),
                               DMNEditorConstants.ExpressionEditor_Clear,
                               false);
    }

    private void assertDefaultListItems(final List<HasListSelectorControl.ListSelectorItem> items,
                                        final boolean enabled) {
        assertThat(items.size()).isEqualTo(3);
        assertListSelectorItem(items.get(INSERT_PARAMETER_ABOVE),
                               DMNEditorConstants.InvocationEditor_InsertParameterAbove,
                               enabled);
        assertListSelectorItem(items.get(INSERT_PARAMETER_BELOW),
                               DMNEditorConstants.InvocationEditor_InsertParameterBelow,
                               enabled);
        assertListSelectorItem(items.get(DELETE_PARAMETER),
                               DMNEditorConstants.InvocationEditor_DeleteParameter,
                               enabled && grid.getModel().getRowCount() > 1);
    }

    private void assertListSelectorItem(final HasListSelectorControl.ListSelectorItem item,
                                        final String text,
                                        final boolean enabled) {
        assertThat(item).isInstanceOf(HasListSelectorControl.ListSelectorTextItem.class);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) item;
        assertThat(ti.getText()).isEqualTo(text);
        assertThat(ti.isEnabled()).isEqualTo(enabled);
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
    public void testOnItemSelectedInsertParameterAbove() {
        setupGrid(0);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_PARAMETER_ABOVE);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addParameterBinding(eq(0));
    }

    @Test
    public void testOnItemSelectedInsertParameterBelow() {
        setupGrid(0);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_PARAMETER_BELOW);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addParameterBinding(eq(1));
    }

    @Test
    public void testOnItemSelectedDeleteParameter() {
        setupGrid(0);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DELETE_PARAMETER);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).deleteParameterBinding(eq(0));
    }

    @Test
    public void testOnItemSelectedDeleteParameterEnabled() {
        setupGrid(0);

        //Grid has one row from Invocation model. It cannot be deleted.
        assertDeleteParameterEnabled(0, false);

        //Grid has two rows. Rows 1 and 2 can be deleted.
        grid.getModel().appendRow(new BaseGridRow());
        assertDeleteParameterEnabled(0, true);
        assertDeleteParameterEnabled(1, true);
    }

    private void assertDeleteParameterEnabled(final int uiRowIndex, final boolean enabled) {
        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(uiRowIndex, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DELETE_PARAMETER);
        assertThat(ti.isEnabled()).isEqualTo(enabled);
    }

    @Test
    public void testAddParameterBinding() {
        setupGrid(0);

        addParameterBinding(0);

        verify(parent).proposeContainingColumnWidth(eq(grid.getWidth() + grid.getPadding() * 2),
                                                    eq(BaseExpressionGrid.RESIZE_EXISTING));

        verify(gridLayer).batch(redrawCommandCaptor.capture());
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();

        final GridLayerRedrawManager.PrioritizedCommand redrawCommand = redrawCommandCaptor.getValue();

        redrawCommand.execute();

        verify(gridLayer).draw();

        verify(headerEditor).bind(any(HasNameAndTypeRef.class),
                                  eq(0),
                                  eq(1));
        verify(cellEditorControls).show(eq(headerEditor),
                                        eq(Optional.of(DMNEditorConstants.InvocationEditor_EditParameter)),
                                        anyInt(),
                                        anyInt());
    }

    private void addParameterBinding(final int index) {
        grid.addParameterBinding(index);

        verify(sessionCommandManager).execute(eq(canvasHandler), addParameterBindingCommandCaptor.capture());

        final AddParameterBindingCommand addParameterBindingCommand = addParameterBindingCommandCaptor.getValue();
        addParameterBindingCommand.execute(canvasHandler);
    }

    @Test
    public void testDeleteParameterBinding() {
        setupGrid(0);

        grid.deleteParameterBinding(0);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              deleteParameterBindingCommandCaptor.capture());

        final DeleteParameterBindingCommand deleteParameterBindingCommand = deleteParameterBindingCommandCaptor.getValue();
        deleteParameterBindingCommand.execute(canvasHandler);

        verify(parent).onResize();
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(gridLayer).batch(redrawCommandCaptor.capture());

        final GridLayerRedrawManager.PrioritizedCommand redrawCommand = redrawCommandCaptor.getValue();
        redrawCommand.execute();

        verify(gridLayer).draw();
    }

    @Test
    public void testClearExpressionType() {
        setupGrid(0);

        grid.clearExpressionType(0);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              clearExpressionTypeCommandCaptor.capture());

        final ClearExpressionTypeCommand clearExpressionTypeCommand = clearExpressionTypeCommandCaptor.getValue();
        clearExpressionTypeCommand.execute(canvasHandler);

        verify(grid).resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
        verify(gridLayer).select(undefinedExpressionEditor);
        verify(undefinedExpressionEditor).selectFirstCell();
        verify(gridLayer).batch(redrawCommandCaptor.capture());
        redrawCommandCaptor.getValue().execute();
        verify(gridLayer).draw();

        //Check undo operation
        reset(grid, gridLayer);
        clearExpressionTypeCommand.undo(canvasHandler);

        //Verify Expression has been restored and UndefinedExpressionEditor resized
        assertThat(grid.getModel().getColumns().get(2).getWidth()).isEqualTo(UndefinedExpressionColumn.DEFAULT_WIDTH);
        verify(grid).resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);

        verify(grid).selectExpressionEditorFirstCell(eq(0), eq(InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX));
        verify(gridLayer).select(undefinedExpressionEditor);
        verify(undefinedExpressionEditor, times(2)).selectFirstCell();

        verify(gridLayer).batch(redrawCommandCaptor.capture());
        assertThat(redrawCommandCaptor.getAllValues()).hasSize(2);
        redrawCommandCaptor.getAllValues().get(1).execute();
        verify(gridLayer).draw();
    }

    @Test
    public void testHeaderFactoryWhenNested() {
        setupGrid(1);

        final TextBoxSingletonDOMElementFactory factory = grid.getHeaderTextBoxFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteHeaderValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetHeaderValueCommand.class);
    }

    @Test
    public void testHeaderFactoryWhenNotNested() {
        setupGrid(0);

        final TextBoxSingletonDOMElementFactory factory = grid.getHeaderTextBoxFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteHeaderValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetHeaderValueCommand.class);
    }

    @Test
    public void testBodyFactoryWhenNested() {
        setupGrid(1);

        final TextAreaSingletonDOMElementFactory factory = grid.getBodyTextAreaFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteCellValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetCellValueCommand.class);
    }

    @Test
    public void testBodyFactoryWhenNotNested() {
        setupGrid(0);

        final TextAreaSingletonDOMElementFactory factory = grid.getBodyTextAreaFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteCellValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetCellValueCommand.class);
    }

    @Test
    public void testGetDisplayName() {
        setupGrid(0);

        assertThat(extractHeaderMetaData().getName().getValue()).isEqualTo(NAME);
    }

    private InvocationColumnHeaderMetaData extractHeaderMetaData() {
        final InvocationParameterColumn column = (InvocationParameterColumn) grid.getModel().getColumns().get(1);
        return (InvocationColumnHeaderMetaData) column.getHeaderMetaData().get(0);
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
    public void testSelectRow() {
        setupGrid(0);

        grid.selectCell(0, InvocationUIModelMapper.ROW_NUMBER_COLUMN_INDEX, false, false);

        assertNOPDomainObjectSelection();
    }

    @Test
    public void testSelectParameterBinding() {
        setupGrid(0);

        grid.selectCell(0, InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX, false, false);

        assertDomainObjectSelection(expression.get().getBinding().get(0).getVariable());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectMultipleParameterBindings() {
        setupGrid(0);

        addParameterBinding(0);

        //Reset DomainObjectSelectionEvent fired when the new row is added.
        reset(domainObjectSelectionEvent);

        grid.selectCell(0, InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX, false, false);

        assertDomainObjectSelection(expression.get().getBinding().get(0).getVariable());

        //Reset DomainObjectSelectionEvent tested above.
        reset(domainObjectSelectionEvent);

        grid.selectCell(1, InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX, false, true);

        assertNOPDomainObjectSelection();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectSingleParameterBindingWithHeaderSelected() {
        setupGrid(0);

        grid.selectHeaderCell(0, InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX, false, false);

        assertDomainObjectSelection(hasExpression);

        //Reset DomainObjectSelectionEvent tested above.
        reset(domainObjectSelectionEvent);

        grid.selectCell(0, InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX, false, true);

        assertDomainObjectSelection(expression.get().getBinding().get(0).getVariable());
    }

    @Test
    public void testSelectHeaderRowColumn() {
        setupGrid(0);

        grid.selectHeaderCell(0, InvocationUIModelMapper.ROW_NUMBER_COLUMN_INDEX, false, false);

        assertNOPDomainObjectSelection();
    }

    @Test
    public void testSelectHeaderNameColumnNameRow() {
        setupGrid(0);

        grid.selectHeaderCell(0, InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX, false, false);

        assertDomainObjectSelection(hasExpression);
    }

    @Test
    public void testSelectHeaderExpressionColumnNameRow() {
        setupGrid(0);

        grid.selectHeaderCell(0, InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX, false, false);

        assertDomainObjectSelection(hasExpression);
    }

    @Test
    public void testSelectHeaderNameColumnParametersRow() {
        setupGrid(0);

        grid.selectHeaderCell(1, InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX, false, false);

        assertNOPDomainObjectSelection();
    }

    @Test
    public void testSelectHeaderExpressionColumnParametersRow() {
        setupGrid(0);

        grid.selectHeaderCell(1, InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX, false, false);

        assertNOPDomainObjectSelection();
    }

    @Test
    public void testSelectHeaderNameColumnParametersRowWhenNested() {
        setupGrid(1);

        grid.selectHeaderCell(0, InvocationUIModelMapper.BINDING_PARAMETER_COLUMN_INDEX, false, false);

        assertNOPDomainObjectSelection();
    }

    @Test
    public void testSelectHeaderExpressionColumnParametersRowWhenNested() {
        setupGrid(1);

        grid.selectHeaderCell(0, InvocationUIModelMapper.BINDING_EXPRESSION_COLUMN_INDEX, false, false);

        assertNOPDomainObjectSelection();
    }

    private void assertDomainObjectSelection(final DomainObject domainObject) {
        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(domainObject);
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
