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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
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
import org.kie.workbench.common.dmn.client.commands.expressions.types.undefined.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionCell;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector.UndefinedExpressionSelectorPopoverView;
import org.kie.workbench.common.dmn.client.session.DMNEditorSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCache;
import org.kie.workbench.common.dmn.client.widgets.grid.ExpressionGridCacheImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorDividerItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorTextItem;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.handlers.DelegatingGridWidgetCellSelectorMouseEventHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.handlers.DelegatingGridWidgetEditCellMouseEventHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class UndefinedExpressionGridTest {

    private static final String NODE_UUID = "uuid";

    private static final int PARENT_ROW_INDEX = 0;

    private static final int PARENT_COLUMN_INDEX = 1;

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DMNEditorSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private Node node;

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
    private UndefinedExpressionSelectorPopoverView.Presenter undefinedExpressionSelector;

    @Mock
    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;

    @Mock
    private GridCellTuple parent;

    @Mock
    private GridWidget parentGridWidget;

    @Mock
    private GridData parentGridUiModel;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private ExpressionEditorDefinition literalExpressionEditorDefinition;

    @Mock
    private BaseExpressionGrid literalExpressionEditor;

    @Mock
    private GridSelectionManager selectionManager;

    @Mock
    private NodeMouseClickEvent mouseClickEvent;

    @Mock
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private ManagedSession managedSession;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    @Captor
    private ArgumentCaptor<SetCellValueCommand> setCellValueCommandArgumentCaptor;

    @Captor
    private ArgumentCaptor<RefreshFormPropertiesEvent> refreshFormPropertiesArgumentCaptor;

    @Captor
    private ArgumentCaptor<GridLayerRedrawManager.PrioritizedCommand> redrawCommandArgumentCaptor;

    @Captor
    private ArgumentCaptor<DomainObjectSelectionEvent> domainObjectSelectionArgumentEventCaptor;

    private LiteralExpression literalExpression = new LiteralExpression();

    private Optional<Expression> expression = Optional.empty();

    private Optional<HasName> hasName = Optional.empty();

    private ExpressionGridCache expressionGridCache;

    private UndefinedExpressionEditorDefinition definition;

    private UndefinedExpressionGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getGridPanel()).thenReturn(gridPanel);
        when(session.getGridLayer()).thenReturn(gridLayer);
        when(session.getCellEditorControls()).thenReturn(cellEditorControls);

        expressionGridCache = spy(new ExpressionGridCacheImpl());
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getExpressionGridCache()).thenReturn(expressionGridCache);

        definition = new UndefinedExpressionEditorDefinition(definitionUtils,
                                                             sessionManager,
                                                             sessionCommandManager,
                                                             canvasCommandFactory,
                                                             editorSelectedEvent,
                                                             refreshFormPropertiesEvent,
                                                             domainObjectSelectionEvent,
                                                             listSelector,
                                                             translationService,
                                                             undefinedExpressionSelector,
                                                             expressionEditorDefinitionsSupplier,
                                                             readOnlyProvider);

        expression = definition.getModelClass();

        final ExpressionEditorDefinitions expressionEditorDefinitions = new ExpressionEditorDefinitions();
        expressionEditorDefinitions.add(definition);
        expressionEditorDefinitions.add(literalExpressionEditorDefinition);

        when(expressionEditorDefinitionsSupplier.get()).thenReturn(expressionEditorDefinitions);
        when(literalExpressionEditorDefinition.getType()).thenReturn(ExpressionType.LITERAL_EXPRESSION);
        when(literalExpressionEditorDefinition.getName()).thenReturn(LiteralExpression.class.getSimpleName());
        doCallRealMethod().when(literalExpressionEditor).selectFirstCell();
        when(literalExpressionEditor.getLayer()).thenReturn(gridLayer);

        final GridData literalExpressionUiModel = new BaseGridData();
        literalExpressionUiModel.appendColumn(mock(GridColumn.class));
        literalExpressionUiModel.appendRow(mock(GridRow.class));
        when(literalExpressionEditor.getModel()).thenReturn(literalExpressionUiModel);

        when(literalExpressionEditorDefinition.getModelClass()).thenReturn(Optional.of(literalExpression));
        when(literalExpressionEditorDefinition.getEditor(any(GridCellTuple.class),
                                                         any(Optional.class),
                                                         any(HasExpression.class),
                                                         any(Optional.class),
                                                         anyBoolean(),
                                                         anyInt())).thenReturn(Optional.of(literalExpressionEditor));

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(Collections.singletonList(node));

        when(parentGridWidget.getModel()).thenReturn(parentGridUiModel);
        setupParent();
    }

    private void setupParent() {
        when(parent.getGridWidget()).thenReturn(parentGridWidget);
        when(parent.getRowIndex()).thenReturn(PARENT_ROW_INDEX);
        when(parent.getColumnIndex()).thenReturn(PARENT_COLUMN_INDEX);
    }

    private void setupGrid(final int nesting) {
        this.grid = spy((UndefinedExpressionGrid) definition.getEditor(parent,
                                                                       nesting == 0 ? Optional.of(NODE_UUID) : Optional.empty(),
                                                                       hasExpression,
                                                                       hasName,
                                                                       false,
                                                                       nesting).get());
    }

    @Test
    public void testMouseClickEventHandlers() {
        setupGrid(0);

        final List<NodeMouseEventHandler> handlers = grid.getNodeMouseClickEventHandlers(selectionManager);
        assertThat(handlers).hasSize(2);
        assertThat(handlers.get(0)).isInstanceOf(DelegatingGridWidgetCellSelectorMouseEventHandler.class);
        assertThat(handlers.get(1)).isInstanceOf(DelegatingGridWidgetEditCellMouseEventHandler.class);
    }

    @Test
    public void testMouseDoubleClickEventHandlers() {
        setupGrid(0);

        final List<NodeMouseEventHandler> handlers = grid.getNodeMouseDoubleClickEventHandlers(selectionManager, gridLayer);
        assertThat(handlers).isEmpty();
    }

    @Test
    public void testSelectFirstCellWhenNested() {
        setupGrid(1);

        final Decision decision = mock(Decision.class);
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(decision);

        grid.selectFirstCell();

        assertThat(grid.getModel().getSelectedCells().size()).isEqualTo(0);
        verify(parentGridUiModel).selectCell(eq(PARENT_ROW_INDEX), eq(PARENT_COLUMN_INDEX));
        verify(gridLayer).select(parentGridWidget);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionArgumentEventCaptor.capture());
        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionArgumentEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getCanvasHandler()).isEqualTo(canvasHandler);
        assertThat(domainObjectSelectionEvent.getDomainObject()).isInstanceOf(NOPDomainObject.class);
    }

    @Test
    public void testSelectFirstCellWhenNotNested() {
        setupGrid(0);

        final Decision decision = mock(Decision.class);
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(decision);

        grid.selectFirstCell();

        final List<GridData.SelectedCell> selectedCells = grid.getModel().getSelectedCells();
        assertThat(selectedCells.size()).isEqualTo(1);
        assertThat(selectedCells.get(0).getRowIndex()).isEqualTo(0);
        assertThat(selectedCells.get(0).getColumnIndex()).isEqualTo(0);

        verify(gridLayer).select(grid);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionArgumentEventCaptor.capture());
        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionArgumentEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getCanvasHandler()).isEqualTo(canvasHandler);
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(decision);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResizeBasedOnCellExpressionEditor() {
        setupGrid(0);

        grid.resize(BaseExpressionGrid.RESIZE_EXISTING);

        assertResize(BaseExpressionGrid.RESIZE_EXISTING);
    }

    @Test
    public void testInitialSetupFromDefinition() {
        setupGrid(0);

        final GridData uiModel = grid.getModel();
        assertThat(uiModel).isInstanceOf(DMNGridData.class);

        assertThat(uiModel.getColumnCount()).isEqualTo(1);
        assertThat(uiModel.getColumns().get(0)).isInstanceOf(UndefinedExpressionColumn.class);

        assertThat(uiModel.getRowCount()).isEqualTo(1);

        assertThat(uiModel.getCell(0, 0)).isNotNull();

        assertThat(uiModel.getCell(0, 0)).isInstanceOf(UndefinedExpressionCell.class);
    }

    @Test
    public void testCacheable() {
        setupGrid(0);

        assertFalse(grid.isCacheable());
    }

    @Test
    public void testPaddingWithParent() {
        setupGrid(0);

        doReturn(Optional.of(mock(BaseExpressionGrid.class))).when(grid).findParentGrid();

        assertThat(grid.getPadding()).isEqualTo(UndefinedExpressionGrid.PADDING);
    }

    @Test
    public void testPaddingWithNoParent() {
        setupGrid(0);

        doReturn(Optional.empty()).when(grid).findParentGrid();

        assertThat(grid.getPadding()).isEqualTo(UndefinedExpressionGrid.PADDING);
    }

    @Test
    public void testGetItemsWithParentWithoutCellControls() {
        setupGrid(0);

        final GridData parentGridData = mock(GridData.class);
        final BaseExpressionGrid parentGridWidget = mock(BaseExpressionGrid.class);
        when(parent.getGridWidget()).thenReturn(parentGridWidget);
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(parentGridWidget));
        when(parentGridWidget.getModel()).thenReturn(parentGridData);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);

        assertThat(items).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetItemsWithParentThatDoesSupportCellControls() {
        setupGrid(0);

        final GridData parentGridData = mock(GridData.class);
        final ContextGrid parentGridWidget = mock(ContextGrid.class);
        final HasListSelectorControl.ListSelectorItem listSelectorItem = mock(HasListSelectorControl.ListSelectorItem.class);
        when(parent.getGridWidget()).thenReturn(parentGridWidget);
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(parentGridWidget));
        when(parentGridWidget.getModel()).thenReturn(parentGridData);
        when(parentGridWidget.getItems(anyInt(), anyInt())).thenReturn(Collections.singletonList(listSelectorItem));
        when(parentGridData.getCell(anyInt(), anyInt())).thenReturn(mock(LiteralExpressionCell.class));

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);

        assertThat(items).isNotEmpty();
        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0)).isSameAs(listSelectorItem);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetItemsWithParentThatDoesSupportCellControlsButCellDoesNot() {
        setupGrid(0);

        final GridData parentGridData = mock(GridData.class);
        final ContextGrid parentGridWidget = mock(ContextGrid.class);
        final HasListSelectorControl.ListSelectorItem listSelectorItem = mock(HasListSelectorControl.ListSelectorItem.class);
        when(parent.getGridWidget()).thenReturn(parentGridWidget);
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(parentGridWidget));
        when(parentGridWidget.getModel()).thenReturn(parentGridData);
        when(parentGridWidget.getItems(anyInt(), anyInt())).thenReturn(Collections.singletonList(listSelectorItem));
        when(parentGridData.getCell(anyInt(), anyInt())).thenReturn(mock(BaseGridCell.class));

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);

        assertThat(items).isEmpty();
    }

    @Test
    public void testGetItemsWithParentThatDoesNotSupportCellControls() {
        setupGrid(0);

        final GridData parentGridData = mock(GridData.class);
        final BaseExpressionGrid parentGridWidget = mock(BaseExpressionGrid.class);
        when(parent.getGridWidget()).thenReturn(parentGridWidget);
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(parentGridWidget));
        when(parentGridWidget.getModel()).thenReturn(parentGridData);

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);

        assertThat(items).isEmpty();
    }

    @Test
    public void testGetItemsEmpty() {
        setupGrid(0);

        reset(expressionEditorDefinitionsSupplier);
        doReturn(new ExpressionEditorDefinitions()).when(expressionEditorDefinitionsSupplier).get();

        final List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);

        assertThat(items.size()).isEqualTo(0);
    }

    @Test
    public void testOnItemSelectedDivider() {
        setupGrid(0);

        final ListSelectorDividerItem dItem = mock(ListSelectorDividerItem.class);

        grid.onItemSelected(dItem);

        verify(cellEditorControls, never()).hide();
        verify(grid, never()).onExpressionTypeChanged(any(ExpressionType.class));
    }

    @Test
    public void testOnItemSelected() {
        setupGrid(0);

        final Command command = mock(Command.class);
        final ListSelectorTextItem listSelectorItem = mock(ListSelectorTextItem.class);
        when(listSelectorItem.getCommand()).thenReturn(command);

        grid.onItemSelected(listSelectorItem);

        verify(command).execute();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnExpressionTypeChangedWhenNested() {
        assertOnExpressionTypeChanged(1);

        verify(expressionGridCache, never()).getExpressionGrid(Mockito.<String>any());

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionArgumentEventCaptor.capture());
        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionArgumentEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isInstanceOf(NOPDomainObject.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnExpressionTypeChangedWhenNotNested() {
        final Decision decision = mock(Decision.class);
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(decision);

        assertOnExpressionTypeChanged(0);

        verify(expressionGridCache).getExpressionGrid(eq(NODE_UUID));

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionArgumentEventCaptor.capture());
        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionArgumentEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getCanvasHandler()).isEqualTo(canvasHandler);
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(decision);
    }

    private void assertResize(final Function<BaseExpressionGrid, Double> expectedResizer) {
        verify(parent).proposeContainingColumnWidth(eq(grid.getWidth()), eq(expectedResizer));
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(parent).onResize();

        verify(gridLayer).batch(redrawCommandArgumentCaptor.capture());

        redrawCommandArgumentCaptor.getValue().execute();

        verify(gridLayer).draw();
    }

    @SuppressWarnings("unchecked")
    private void assertOnExpressionTypeChanged(final int nesting) {
        setupGrid(nesting);

        grid.onExpressionTypeChanged(ExpressionType.LITERAL_EXPRESSION);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              setCellValueCommandArgumentCaptor.capture());

        final SetCellValueCommand setCellValueCommand = setCellValueCommandArgumentCaptor.getValue();
        setCellValueCommand.execute(canvasHandler);

        verify(literalExpressionEditorDefinition).getEditor(eq(parent),
                                                            eq(nesting == 0 ? Optional.of(NODE_UUID) : Optional.empty()),
                                                            eq(hasExpression),
                                                            eq(hasName),
                                                            eq(false),
                                                            eq(nesting));

        assertResize(BaseExpressionGrid.RESIZE_EXISTING);
        verify(literalExpressionEditor).selectCell(eq(0),
                                                   eq(0),
                                                   eq(false),
                                                   eq(false));
        verify(literalExpressionEditor).selectFirstCell();
        verify(gridPanel).setFocus(true);

        reset(gridPanel, gridLayer, parent);
        setupParent();

        setCellValueCommand.undo(canvasHandler);

        assertResize(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
        verify(gridLayer).select(grid);
        verify(grid).selectCell(eq(0),
                                eq(0),
                                eq(false),
                                eq(false));
    }
}
