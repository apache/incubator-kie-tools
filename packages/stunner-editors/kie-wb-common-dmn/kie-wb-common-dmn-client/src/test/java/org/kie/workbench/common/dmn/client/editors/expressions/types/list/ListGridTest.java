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
package org.kie.workbench.common.dmn.client.editors.expressions.types.list;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.list.AddListRowCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.list.ClearExpressionTypeCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.list.DeleteListRowCommand;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGridRowNumberColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
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
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListUIModelMapperHelper.EXPRESSION_COLUMN_INDEX;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListUIModelMapperHelper.ROW_COLUMN_INDEX;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ListGridTest {

    private final static int HEADER = 0;

    private final static int INSERT_ROW_ABOVE = 1;

    private final static int INSERT_ROW_BELOW = 2;

    private final static int DELETE_ROW = 3;

    private final static int DIVIDER = 4;

    private final static int CLEAR_EXPRESSION_TYPE = 5;

    private static final String NODE_UUID = "uuid";

    private static final String NAME = "name";

    private static final String NAME_NEW = "name-new";

    @Mock
    private Viewport viewport;

    @Mock
    private Transform transform;

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
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private TranslationService translationService;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private ValueAndDataTypePopoverView.Presenter headerEditor;

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
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    @Captor
    private ArgumentCaptor<CompositeCommand> compositeCommandCaptor;

    @Captor
    private ArgumentCaptor<AddListRowCommand> addListRowCommandCaptor;

    @Captor
    private ArgumentCaptor<DeleteListRowCommand> deleteListRowCommandCaptor;

    @Captor
    private ArgumentCaptor<ClearExpressionTypeCommand> clearExpressionTypeCommandCaptor;

    @Captor
    private ArgumentCaptor<GridLayerRedrawManager.PrioritizedCommand> redrawCommandCaptor;

    @Captor
    private ArgumentCaptor<DomainObjectSelectionEvent> domainObjectSelectionEventCaptor;

    private LiteralExpression literalExpression = new LiteralExpression();

    private Decision hasExpression = new Decision();

    private Optional<org.kie.workbench.common.dmn.api.definition.model.List> expression = Optional.empty();

    private Optional<HasName> hasName = Optional.empty();

    private ListEditorDefinition definition;

    private ListGrid grid;

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

        definition = new ListEditorDefinition(definitionUtils,
                                              sessionManager,
                                              sessionCommandManager,
                                              canvasCommandFactory,
                                              editorSelectedEvent,
                                              refreshFormPropertiesEvent,
                                              domainObjectSelectionEvent,
                                              listSelector,
                                              translationService,
                                              expressionEditorDefinitionsSupplier,
                                              headerEditor,
                                              readOnlyProvider);

        final Decision decision = new Decision();
        decision.setName(new Name(NAME));
        hasName = Optional.of(decision);
        expression = definition.getModelClass();
        definition.enrich(Optional.empty(), hasExpression, expression);

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add((ExpressionEditorDefinition) definition);
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);
        expressionEditorDefinitions.add(undefinedExpressionEditorDefinition);

        when(expressionEditorDefinitionsSupplier.get()).thenReturn(expressionEditorDefinitions);
        when(literalExpressionEditor.getParentInformation()).thenReturn(parent);
        when(literalExpressionEditorDefinition.getModelClass()).thenReturn(Optional.of(literalExpression));
        when(literalExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                         any(Optional.class),
                                                         any(HasExpression.class),
                                                         any(Optional.class),
                                                         anyBoolean(),
                                                         anyInt())).thenReturn(Optional.of(literalExpressionEditor));

        when(undefinedExpressionEditor.getParentInformation()).thenReturn(parent);
        when(undefinedExpressionEditorDefinition.getModelClass()).thenReturn(Optional.empty());
        when(undefinedExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                           any(Optional.class),
                                                           any(HasExpression.class),
                                                           any(Optional.class),
                                                           anyBoolean(),
                                                           anyInt())).thenReturn(Optional.of(undefinedExpressionEditor));

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getGraphExecutionContext()).thenReturn(graphCommandExecutionContext);

        when(gridWidget.getModel()).thenReturn(new BaseGridData(false));
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, 100, 200));
        when(gridLayer.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(transform);

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

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(Mockito.<String>any());
        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).getTranslation(Mockito.<String>any());
    }

    private void setupGrid() {
        this.hasExpression.setExpression(expression.get());
        this.grid = spy((ListGrid) definition.getEditor(parent,
                                                        Optional.of(NODE_UUID),
                                                        hasExpression,
                                                        hasName,
                                                        false,
                                                        0).get());

        when(parent.getGridWidget()).thenReturn(gridWidget);
        when(parent.getRowIndex()).thenReturn(0);
        when(parent.getColumnIndex()).thenReturn(EXPRESSION_COLUMN_INDEX);
    }

    @Test
    public void testInitialSetupFromDefinition() {
        setupGrid();

        final GridData uiModel = grid.getModel();
        assertTrue(uiModel instanceof ListGridData);

        assertEquals(2,
                     uiModel.getColumnCount());
        assertTrue(uiModel.getColumns().get(ROW_COLUMN_INDEX) instanceof ContextGridRowNumberColumn);
        assertTrue(uiModel.getColumns().get(EXPRESSION_COLUMN_INDEX) instanceof ExpressionEditorColumn);

        assertEquals(1,
                     uiModel.getRowCount());

        assertEquals(1,
                     uiModel.getCell(0, ROW_COLUMN_INDEX).getValue().getValue());
        assertTrue(uiModel.getCell(0, EXPRESSION_COLUMN_INDEX).getValue() instanceof ExpressionCellValue);
        final ExpressionCellValue dcv0 = (ExpressionCellValue) uiModel.getCell(0, EXPRESSION_COLUMN_INDEX).getValue();
        assertEquals(literalExpressionEditor,
                     dcv0.getValue().get());
    }

    @Test
    public void testInitialColumnWidthsFromDefinition() {
        setupGrid();

        assertComponentWidths(50.0,
                              ListGrid.LIST_DEFAULT_WIDTH);
    }

    @Test
    public void testInitialColumnWidthsFromExpression() {
        final List<Double> componentWidths = expression.get().getComponentWidths();
        componentWidths.set(0, 100.0);
        componentWidths.set(1, 200.0);

        setupGrid();

        assertComponentWidths(100.0,
                              200.0);
    }

    private void assertComponentWidths(final double... widths) {
        final GridData uiModel = grid.getModel();
        IntStream.range(0, widths.length).forEach(i -> assertEquals(widths[i], uiModel.getColumns().get(i).getWidth(), 0.0));
    }

    @Test
    public void testCacheable() {
        setupGrid();

        assertTrue(grid.isCacheable());
    }

    @Test
    public void testGetItemsRowNumberColumn() {
        setupGrid();

        assertDefaultListItems(grid.getItems(0, ROW_COLUMN_INDEX), true);
    }

    @Test
    public void testOnItemSelectedExpressionColumnUndefinedExpressionType() {
        setupGrid();

        //Clear expression at (0, EXPRESSION_COLUMN_INDEX)
        grid.getModel().setCellValue(0, EXPRESSION_COLUMN_INDEX, new ExpressionCellValue(Optional.empty()));

        assertDefaultListItems(grid.getItems(0, EXPRESSION_COLUMN_INDEX), true);
    }

    @Test
    public void testOnItemSelectedExpressionColumnDefinedExpressionType() {
        setupGrid();

        //The default model from ListEditorDefinition has a LiteralExpression at (0, EXPRESSION_COLUMN_INDEX)
        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, EXPRESSION_COLUMN_INDEX);

        assertThat(items.size()).isEqualTo(6);
        assertDefaultListItems(items.subList(0, 4), true);

        assertThat(items.get(DIVIDER)).isInstanceOf(HasListSelectorControl.ListSelectorDividerItem.class);
        assertListSelectorTextItem(items.get(CLEAR_EXPRESSION_TYPE),
                                   DMNEditorConstants.ExpressionEditor_Clear,
                                   true);

        ((HasListSelectorControl.ListSelectorTextItem) items.get(CLEAR_EXPRESSION_TYPE)).getCommand().execute();
        verify(cellEditorControls).hide();
        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              any(ClearExpressionTypeCommand.class));
    }

    @Test
    public void testGetItemsWithCellSelectionsCoveringMultipleRows() {
        setupGrid();

        addListRow(0);
        grid.getModel().selectCell(0, ROW_COLUMN_INDEX);
        grid.getModel().selectCell(1, ROW_COLUMN_INDEX);

        assertDefaultListItems(grid.getItems(0, ROW_COLUMN_INDEX), false);
    }

    @Test
    public void testGetItemsWithCellSelectionsCoveringMultipleColumns() {
        setupGrid();

        grid.getModel().selectCell(0, ROW_COLUMN_INDEX);
        grid.getModel().selectCell(0, EXPRESSION_COLUMN_INDEX);

        assertDefaultListItems(grid.getItems(0, ROW_COLUMN_INDEX), true);
    }

    @Test
    public void testOnItemSelectedExpressionColumnDefinedExpressionTypeWithCellSelectionsCoveringMultipleRows() {
        setupGrid();

        addListRow(0);
        grid.getModel().selectCell(0, ROW_COLUMN_INDEX);
        grid.getModel().selectCell(1, ROW_COLUMN_INDEX);

        //Set an editor for expression at (0, EXPRESSION_COLUMN_INDEX)
        final BaseExpressionGrid editor = mock(BaseExpressionGrid.class);
        grid.getModel().setCellValue(0, EXPRESSION_COLUMN_INDEX, new ExpressionCellValue(Optional.of(editor)));

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, EXPRESSION_COLUMN_INDEX);

        assertThat(items.size()).isEqualTo(6);
        assertDefaultListItems(items.subList(0, 4), false);

        assertThat(items.get(DIVIDER)).isInstanceOf(HasListSelectorControl.ListSelectorDividerItem.class);
        assertListSelectorTextItem(items.get(CLEAR_EXPRESSION_TYPE),
                                   DMNEditorConstants.ExpressionEditor_Clear,
                                   false);
    }

    private void assertDefaultListItems(final List<HasListSelectorControl.ListSelectorItem> items,
                                        final boolean enabled) {
        assertThat(items.size()).isEqualTo(4);
        assertListSelectorHeaderItem(items.get(HEADER),
                                     DMNEditorConstants.ListEditor_HeaderRows);
        assertListSelectorTextItem(items.get(INSERT_ROW_ABOVE),
                                   DMNEditorConstants.ListEditor_InsertRowAbove,
                                   enabled);
        assertListSelectorTextItem(items.get(INSERT_ROW_BELOW),
                                   DMNEditorConstants.ListEditor_InsertRowBelow,
                                   enabled);
        assertListSelectorTextItem(items.get(DELETE_ROW),
                                   DMNEditorConstants.ListEditor_DeleteRow,
                                   enabled && grid.getModel().getRowCount() > 1);
    }

    private void assertListSelectorHeaderItem(final HasListSelectorControl.ListSelectorItem item,
                                              final String text) {
        assertThat(item).isInstanceOf(HasListSelectorControl.ListSelectorHeaderItem.class);
        final HasListSelectorControl.ListSelectorHeaderItem hi = (HasListSelectorControl.ListSelectorHeaderItem) item;
        assertThat(hi.getText()).isEqualTo(text);
    }

    private void assertListSelectorTextItem(final HasListSelectorControl.ListSelectorItem item,
                                            final String text,
                                            final boolean enabled) {
        assertThat(item).isInstanceOf(HasListSelectorControl.ListSelectorTextItem.class);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) item;
        assertThat(ti.getText()).isEqualTo(text);
        assertThat(ti.isEnabled()).isEqualTo(enabled);
    }

    @Test
    public void testOnItemSelected() {
        setupGrid();

        final Command command = mock(Command.class);
        final HasListSelectorControl.ListSelectorTextItem listSelectorItem = mock(HasListSelectorControl.ListSelectorTextItem.class);
        when(listSelectorItem.getCommand()).thenReturn(command);

        grid.onItemSelected(listSelectorItem);

        verify(command).execute();
    }

    @Test
    public void testOnItemSelectedInsertRowAbove() {
        setupGrid();

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_ROW_ABOVE);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addRow(eq(0));
    }

    @Test
    public void testOnItemSelectedInsertRowBelow() {
        setupGrid();

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_ROW_BELOW);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addRow(eq(1));
    }

    @Test
    public void testOnItemSelectedDeleteRow() {
        setupGrid();

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DELETE_ROW);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).deleteRow(eq(0));
    }

    @Test
    public void testOnItemSelectedDeleteRowEnabled() {
        setupGrid();

        //Grid has one row from List model. It cannot be deleted.
        assertDeleteRowEnabled(0, false);

        //Grid has two rows. Rows 1 or 2 can be deleted.
        grid.getModel().appendRow(new BaseGridRow());
        assertDeleteRowEnabled(0, true);
        assertDeleteRowEnabled(1, true);
    }

    private void assertDeleteRowEnabled(final int uiRowIndex, final boolean enabled) {
        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(uiRowIndex, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DELETE_ROW);
        assertThat(ti.isEnabled()).isEqualTo(enabled);
    }

    @Test
    public void testAddListRow() {
        setupGrid();

        addListRow(0);

        verify(parent).proposeContainingColumnWidth(eq(grid.getWidth() + grid.getPadding() * 2), eq(BaseExpressionGrid.RESIZE_EXISTING));

        verify(gridLayer).batch(redrawCommandCaptor.capture());
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();

        final GridLayerRedrawManager.PrioritizedCommand redrawCommand = redrawCommandCaptor.getValue();

        redrawCommand.execute();

        verify(gridLayer).draw();
    }

    private void addListRow(final int index) {
        grid.addRow(index);

        verify(sessionCommandManager).execute(eq(canvasHandler), addListRowCommandCaptor.capture());

        final AddListRowCommand addContextEntryCommand = addListRowCommandCaptor.getValue();
        addContextEntryCommand.execute(canvasHandler);
    }

    @Test
    public void testDeleteListRow() {
        setupGrid();

        grid.deleteRow(0);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              deleteListRowCommandCaptor.capture());

        final DeleteListRowCommand deleteContextEntryCommand = deleteListRowCommandCaptor.getValue();
        deleteContextEntryCommand.execute(canvasHandler);

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
        setupGrid();

        final ClearExpressionTypeCommand clearExpressionTypeCommand = clearRowExpression(0);

        verify(grid).resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
        verify(gridLayer).select(undefinedExpressionEditor);
        verify(undefinedExpressionEditor).selectFirstCell();
        verify(gridLayer).batch(redrawCommandCaptor.capture());
        redrawCommandCaptor.getValue().execute();
        verify(gridLayer).draw();

        //Check undo operation
        reset(grid, gridLayer);
        clearExpressionTypeCommand.undo(canvasHandler);

        //Verify Expression has been restored and LiteralExpressionEditor resized
        assertThat(grid.getModel().getColumns().get(EXPRESSION_COLUMN_INDEX).getWidth()).isEqualTo(ListGrid.LIST_DEFAULT_WIDTH);
        verify(grid).resize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);

        verify(grid).selectExpressionEditorFirstCell(eq(0), eq(ListUIModelMapperHelper.EXPRESSION_COLUMN_INDEX));
        verify(gridLayer).select(literalExpressionEditor);
        verify(literalExpressionEditor).selectFirstCell();

        verify(gridLayer).batch(redrawCommandCaptor.capture());
        assertThat(redrawCommandCaptor.getAllValues()).hasSize(2);
        redrawCommandCaptor.getAllValues().get(1).execute();
        verify(gridLayer).draw();
    }

    private ClearExpressionTypeCommand clearRowExpression(final int index) {
        grid.clearExpressionType(index);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              clearExpressionTypeCommandCaptor.capture());

        final ClearExpressionTypeCommand clearExpressionTypeCommand = clearExpressionTypeCommandCaptor.getValue();
        clearExpressionTypeCommand.execute(canvasHandler);
        return clearExpressionTypeCommand;
    }

    @Test
    public void testSelectRow() {
        setupGrid();

        grid.selectCell(0, ListUIModelMapperHelper.ROW_COLUMN_INDEX, false, false);

        assertNOPDomainObjectSelection();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectMultipleRows() {
        setupGrid();
        addListRow(0);

        grid.selectCell(0, ListUIModelMapperHelper.ROW_COLUMN_INDEX, false, false);

        assertNOPDomainObjectSelection();

        //Reset DomainObjectSelectionEvent tested above.
        reset(domainObjectSelectionEvent);

        grid.selectCell(1, ListUIModelMapperHelper.ROW_COLUMN_INDEX, false, true);

        assertNOPDomainObjectSelection();
    }

    @Test
    public void testSelectDefinedExpression() {
        setupGrid();

        grid.selectCell(0, ListUIModelMapperHelper.EXPRESSION_COLUMN_INDEX, false, false);

        assertDomainObjectSelection(expression.get().getExpression().get(0).getExpression());
    }

    @Test
    public void testSelectUndefinedExpression() {
        setupGrid();

        clearRowExpression(0);

        grid.selectCell(0, ListUIModelMapperHelper.EXPRESSION_COLUMN_INDEX, false, false);

        assertNOPDomainObjectSelection();
    }

    private void assertDomainObjectSelection(final Expression domainObject) {
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
    public void testSelectFirstCell() {
        setupGrid();

        grid.selectFirstCell();

        assertThat(grid.getModel().getSelectedCells()).containsOnly(new GridData.SelectedCell(0, 1));
    }
}
