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

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGrid;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseDelegatingExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.handlers.DelegatingGridWidgetEditCellMouseEventHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
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
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridSelectionManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public abstract class BaseLiteralExpressionGridTest<G extends BaseDelegatingExpressionGrid<? extends LiteralExpression, DMNGridData, ? extends BaseUIModelMapper<? extends LiteralExpression>>> {

    private static final int PARENT_ROW_INDEX = 0;

    private static final int PARENT_COLUMN_INDEX = 1;

    protected static final String EXPRESSION_TEXT = "expression";

    protected static final String NODE_UUID = "uuid";

    protected static final String NAME = "name";

    protected GridCellTuple tupleWithoutValue;

    protected GridCellValueTuple tupleWithValue;

    @Mock
    protected DMNGridPanel gridPanel;

    @Mock
    protected DMNGridLayer gridLayer;

    @Mock
    protected GridWidget gridWidget;

    @Mock
    protected DefinitionUtils definitionUtils;

    @Mock
    protected SessionManager sessionManager;

    @Mock
    protected SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    protected DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    protected DMNSession session;

    @Mock
    protected AbstractCanvasHandler canvasHandler;

    @Mock
    protected Diagram diagram;

    @Mock
    protected Graph graph;

    @Mock
    protected Node node;

    @Mock
    protected Index index;

    @Mock
    protected Element element;

    @Mock
    protected CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    protected ListSelectorView.Presenter listSelector;

    @Mock
    protected TranslationService translationService;

    @Mock
    protected GridCellTuple parent;

    @Mock
    protected GridSelectionManager selectionManager;

    @Mock
    protected EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    protected EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    protected EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    protected ValueAndDataTypePopoverView.Presenter headerEditor;

    @Mock
    protected ReadOnlyProvider readOnlyProvider;

    @Captor
    protected ArgumentCaptor<CompositeCommand> compositeCommandCaptor;

    @Captor
    protected ArgumentCaptor<DomainObjectSelectionEvent> domainObjectSelectionEventCaptor;

    protected Decision hasExpression = new Decision();

    protected Optional<? extends LiteralExpression> expression = Optional.empty();

    protected Optional<HasName> hasName = Optional.empty();

    protected BaseEditorDefinition<? extends LiteralExpression, DMNGridData> definition;

    protected GridWidget parentGridWidget;

    protected GridData parentGridUiModel;

    protected G grid;

    @Before
    public void setup() {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getGridPanel()).thenReturn(gridPanel);
        when(session.getGridLayer()).thenReturn(gridLayer);
        when(session.getCellEditorControls()).thenReturn(cellEditorControls);

        tupleWithoutValue = new GridCellTuple(0, 0, gridWidget);
        tupleWithValue = new GridCellValueTuple<>(0, 0, gridWidget, new BaseGridCellValue<>("value"));

        definition = getDefinition();

        final Decision decision = new Decision();
        decision.setName(new Name(NAME));
        hasName = Optional.of(decision);
        expression = definition.getModelClass();
        expression.ifPresent(e -> e.getText().setValue(EXPRESSION_TEXT));

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(gridLayer.getVisibleBounds()).thenReturn(mock(Bounds.class));
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

        parentGridWidget = getParentGridWidget();
        parentGridUiModel = getParentGridWidgetUiModel();

        when(parentGridWidget.getModel()).thenReturn(parentGridUiModel);
        when(parent.getGridWidget()).thenReturn(parentGridWidget);
        when(parent.getRowIndex()).thenReturn(PARENT_ROW_INDEX);
        when(parent.getColumnIndex()).thenReturn(PARENT_COLUMN_INDEX);

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).getTranslation(Mockito.<String>any());
    }

    protected abstract BaseEditorDefinition<? extends LiteralExpression, DMNGridData> getDefinition();

    protected abstract void setupGrid(final int nesting);

    protected GridWidget getParentGridWidget() {
        return mock(GridWidget.class);
    }

    protected GridData getParentGridWidgetUiModel() {
        return mock(GridData.class);
    }

    @Test
    public void testMouseDoubleClickEventHandlers() {
        setupGrid(0);

        final List<NodeMouseEventHandler> handlers = grid.getNodeMouseDoubleClickEventHandlers(selectionManager, gridLayer);
        assertThat(handlers).hasSize(1);
        assertThat(handlers.get(0)).isInstanceOf(DelegatingGridWidgetEditCellMouseEventHandler.class);
    }

    @Test
    public void testSelectFirstCellWhenNested() {
        setupGrid(1);

        grid.selectFirstCell();

        assertThat(grid.getModel().getSelectedCells().size()).isEqualTo(0);
        verify(parentGridUiModel).selectCell(eq(PARENT_ROW_INDEX), eq(PARENT_COLUMN_INDEX));
        verify(gridLayer).select(parentGridWidget);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());
        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(expression.get());
    }

    @Test
    public void testSelectFirstCellWhenNotNested() {
        setupGrid(0);

        grid.selectFirstCell();

        final List<GridData.SelectedCell> selectedCells = grid.getModel().getSelectedCells();
        assertThat(selectedCells.size()).isEqualTo(1);
        assertThat(selectedCells.get(0).getRowIndex()).isEqualTo(0);
        assertThat(selectedCells.get(0).getColumnIndex()).isEqualTo(0);

        verify(gridLayer).select(grid);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());
        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(expression.get());
    }

    @Test
    public void testInitialColumnWidthsFromDefinition() {
        setupGrid(0);

        assertComponentWidths(getDefaultWidth());
    }

    @Test
    public void testInitialColumnWidthsFromExpression() {
        final List<Double> componentWidths = expression.get().getComponentWidths();
        componentWidths.set(0, 200.0);

        setupGrid(0);

        assertComponentWidths(200.0);
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
    public void testPaddingWithParent() {
        setupGrid(0);

        doReturn(Optional.of(mock(BaseExpressionGrid.class))).when(grid).findParentGrid();

        assertThat(grid.getPadding()).isEqualTo(LiteralExpressionGrid.PADDING);
    }

    @Test
    public void testPaddingWithNoParent() {
        setupGrid(0);

        doReturn(Optional.empty()).when(grid).findParentGrid();

        assertThat(grid.getPadding()).isEqualTo(BaseExpressionGrid.DEFAULT_PADDING);
    }

    @Test
    public void testGetItemsWithNoParent() {
        setupGrid(0);

        when(parent.getGridWidget()).thenReturn(mock(GridWidget.class));
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(mock(BaseGridWidget.class)));

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
    public void testOnItemSelected() {
        setupGrid(0);

        final Command command = mock(Command.class);
        final HasListSelectorControl.ListSelectorTextItem listSelectorItem = mock(HasListSelectorControl.ListSelectorTextItem.class);
        when(listSelectorItem.getCommand()).thenReturn(command);

        grid.onItemSelected(listSelectorItem);

        verify(command).execute();
    }

    protected double getDefaultWidth() {
        return DMNGridColumn.DEFAULT_WIDTH;
    }
}

