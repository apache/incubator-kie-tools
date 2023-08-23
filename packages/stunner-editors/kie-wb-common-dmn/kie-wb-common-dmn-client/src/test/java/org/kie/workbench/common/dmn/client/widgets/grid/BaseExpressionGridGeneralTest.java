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

package org.kie.workbench.common.dmn.client.widgets.grid;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.commands.factory.canvas.SetComponentWidthCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHasValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHeaderValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHasValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHeaderValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetTypeRefCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionContainerUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.GridFactoryCommandUtils;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionCellValue;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.ListBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.handlers.EditableHeaderGridWidgetEditCellMouseEventHandler;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridCell;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.NodeMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.impl.DefaultGridWidgetCellSelectorMouseEventHandler;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.selections.SelectionExtension;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class BaseExpressionGridGeneralTest extends BaseExpressionGridTest {

    private static final String NODE_UUID = "uuid";

    private static final Name NAME = new Name("name");

    private static final QName TYPE_REF = new QName();

    private static final String DEFINITION = "definition";

    private static final String NAME_ID = "nameId";

    private static final double COLUMN_WIDTH = 100.0;

    private static final double HEADER_HEIGHT = 100.0;

    private GridCellTuple tupleWithoutValue;

    private GridCellValueTuple tupleWithValue;

    @Mock
    private ClientSession session;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private Node node;

    @Mock
    private Index<?, ?> index;

    @Mock
    private Element element;

    @Mock
    private Definition definition;

    @Mock
    private UpdateElementPropertyCommand updateElementPropertyCommand;

    @Mock
    private Group header;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    @Captor
    private ArgumentCaptor<GridLayerRedrawManager.PrioritizedCommand> redrawCommandCaptor;

    @Captor
    private ArgumentCaptor<Command> commandCaptor;

    @Captor
    private ArgumentCaptor<DomainObjectSelectionEvent> domainObjectSelectionEventCaptor;

    @Captor
    private ArgumentCaptor<RefreshFormPropertiesEvent> refreshFormPropertiesEventCaptor;

    @Captor
    private ArgumentCaptor<ExpressionEditorChanged> expressionEditorChangedArgumentCaptor;

    private Decision decision = new Decision();

    private interface MockHasCellEditorControlsHeaderMetaData extends HasCellEditorControls,
                                                                      GridColumn.HeaderMetaData {

    }

    @Override
    public void setup() {
        super.setup();

        tupleWithoutValue = new GridCellTuple(0, 0, grid);
        tupleWithValue = new GridCellValueTuple<>(0, 0, grid, new BaseGridCellValue<>("value"));

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(Collections.singletonList(node));

        when(canvasHandler.getGraphIndex()).thenReturn(index);
        when(element.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(DEFINITION);
        when(definitionUtils.getNameIdentifier(DEFINITION)).thenReturn(NAME_ID);
        when(updateElementPropertyCommand.execute(canvasHandler)).thenReturn(CanvasCommandResultBuilder.SUCCESS);

        when(grid.getHeader()).thenReturn(header);
        when(header.getY()).thenReturn(0.0);
        when(renderer.getHeaderHeight()).thenReturn(HEADER_HEIGHT);
        when(renderer.getHeaderRowHeight()).thenReturn(HEADER_HEIGHT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BaseExpressionGrid getGrid() {
        final Optional<HasName> hasName = Optional.of(decision);

        return new BaseExpressionGrid<Expression, DMNGridData, ExpressionContainerUIModelMapper>(parentCell,
                                                                                                 Optional.empty(),
                                                                                                 HasExpression.NOP,
                                                                                                 hasName,
                                                                                                 gridPanel,
                                                                                                 gridLayer,
                                                                                                 new DMNGridData(),
                                                                                                 renderer,
                                                                                                 definitionUtils,
                                                                                                 sessionManager,
                                                                                                 sessionCommandManager,
                                                                                                 canvasCommandFactory,
                                                                                                 editorSelectedEvent,
                                                                                                 refreshFormPropertiesEvent,
                                                                                                 domainObjectSelectionEvent,
                                                                                                 cellEditorControls,
                                                                                                 listSelector,
                                                                                                 translationService,
                                                                                                 false,
                                                                                                 0,
                                                                                                 readOnlyProvider) {
            @Override
            protected ExpressionContainerUIModelMapper makeUiModelMapper() {
                return mapper;
            }

            @Override
            protected void initialiseUiColumns() {
                //Nothing for this test
            }

            @Override
            public void initialiseUiRows() {
                //Nothing for this test
            }

            @Override
            public void initialiseUiCells() {
                //Nothing for this test
            }
        };
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefaultNodeMouseClickHandlers() {
        final List<NodeMouseEventHandler> handlers = grid.getNodeMouseClickEventHandlers(gridLayer);

        assertThat(handlers).hasSize(2);
        assertThat(handlers.get(0)).isInstanceOf(DefaultGridWidgetCellSelectorMouseEventHandler.class);
        assertThat(handlers.get(1)).isInstanceOf(EditableHeaderGridWidgetEditCellMouseEventHandler.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDefaultNodeMouseDoubleClickHandlers() {
        final List<NodeMouseEventHandler> handlers = grid.getNodeMouseDoubleClickEventHandlers(gridLayer, gridLayer);

        assertThat(handlers).hasSize(1);
        assertThat(handlers.get(0)).isInstanceOf(EditableHeaderGridWidgetEditCellMouseEventHandler.class);
    }

    @Test
    public void testGetMinimumWidthNoColumns() {
        assertMinimumWidth(0.0);

        Assertions.assertThat(grid.getMinimumWidth()).isEqualTo(0);
    }

    @Test
    public void testGetMinimumWidthOneColumn() {
        final double COL_0_MIN = 100.0;

        assertMinimumWidth(COL_0_MIN,
                           new MockColumnData(200.0, COL_0_MIN));
    }

    @Test
    public void testGetMinimumWidthTwoColumns() {
        final double COL_0_ACTUAL = 200.0;
        final double COL_1_MIN = 150.0;

        assertMinimumWidth(COL_0_ACTUAL + COL_1_MIN,
                           new MockColumnData(COL_0_ACTUAL, 100.0),
                           new MockColumnData(225.0, COL_1_MIN));
    }

    @Test
    public void testGetMinimumWidthMultipleColumns() {
        final double COL_0_ACTUAL = 50.0;
        final double COL_1_ACTUAL = 65.0;
        final double COL_2_MIN = 150.0;

        assertMinimumWidth(COL_0_ACTUAL + COL_1_ACTUAL + COL_2_MIN,
                           new MockColumnData(COL_0_ACTUAL, 25.0),
                           new MockColumnData(COL_1_ACTUAL, 35.0),
                           new MockColumnData(225.0, COL_2_MIN));
    }

    @Test
    public void testGetViewportGridAttachedToLayer() {
        doReturn(gridParent).when(grid).getParent();
        doReturn(viewport).when(gridParent).getViewport();

        assertEquals(viewport,
                     grid.getViewport());
    }

    @Test
    public void testGetViewportGridNotAttachedToLayer() {
        assertEquals(viewport,
                     grid.getViewport());
    }

    @Test
    public void testGetLayerGridAttachedToLayer() {
        doReturn(gridParent).when(grid).getParent();
        doReturn(gridLayer).when(gridParent).getLayer();

        assertEquals(gridLayer,
                     grid.getLayer());
    }

    @Test
    public void testGetLayerGridNotAttachedToLayer() {
        assertEquals(gridLayer,
                     grid.getLayer());
    }

    @Test
    public void testSelect() {
        final String expectedUUID = "0000-1111-2222";

        doNothing().when(editorSelectedEvent).fire(any());
        doReturn(Optional.of(expectedUUID)).when(grid).getNodeUUID();

        grid.select();

        verify(grid, never()).selectFirstCell();
        verify(editorSelectedEvent).fire(expressionEditorChangedArgumentCaptor.capture());
        assertEquals(expectedUUID, expressionEditorChangedArgumentCaptor.getValue().getNodeUUID());
    }

    @Test
    public void testDeselect() {
        final String expectedUUID = "0000-1111-2222";

        doReturn(Optional.of(expectedUUID)).when(grid).getNodeUUID();
        grid.getModel().appendRow(new BaseGridRow());
        appendColumns(GridColumn.class);

        //Select a cell so we can check deselection clears selections
        grid.getModel().selectCell(0, 0);
        assertFalse(grid.getModel().getSelectedCells().isEmpty());

        grid.deselect();

        assertTrue(grid.getModel().getSelectedCells().isEmpty());
        verify(grid).clearSelectedDomainObject();
        verify(editorSelectedEvent).fire(expressionEditorChangedArgumentCaptor.capture());
        assertEquals(expectedUUID, expressionEditorChangedArgumentCaptor.getValue().getNodeUUID());
    }

    @Test
    public void testSelectFirstCellWithNoRowsOrColumns() {
        grid.selectFirstCell();

        assertThat(grid.getModel().getSelectedCells()).isEmpty();
    }

    @Test
    public void testSelectFirstCellWithRowAndNonRowNumberColumn() {
        grid.getModel().appendRow(new BaseGridRow());
        appendColumns(GridColumn.class);

        grid.selectFirstCell();

        assertThat(grid.getModel().getSelectedCells()).isNotEmpty();
        assertThat(grid.getModel().getSelectedCells()).contains(new GridData.SelectedCell(0, 0));

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());
        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isInstanceOf(NOPDomainObject.class);
    }

    @Test
    public void testSelectFirstCellWithRowAndRowNumberColumn() {
        grid.getModel().appendRow(new BaseGridRow());
        appendColumns(RowNumberColumn.class);

        grid.selectFirstCell();

        assertThat(grid.getModel().getSelectedCells()).isEmpty();
    }

    @Test
    public void testSelectFirstCellWithRowAndRowNumberColumnAndAnotherColumn() {
        grid.getModel().appendRow(new BaseGridRow());
        appendColumns(RowNumberColumn.class, GridColumn.class);

        grid.selectFirstCell();

        assertThat(grid.getModel().getSelectedCells()).isNotEmpty();
        assertThat(grid.getModel().getSelectedCells()).contains(new GridData.SelectedCell(0, 1));

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());
        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isInstanceOf(NOPDomainObject.class);
    }

    @Test
    public void testSelectCellWithPoint() {
        grid.getModel().appendRow(new BaseGridRow());
        appendColumns(RowNumberColumn.class, GridColumn.class);

        final Point2D point = mock(Point2D.class);
        final double columnOffset = grid.getModel().getColumns().get(0).getWidth();
        final double columnWidth = grid.getModel().getColumns().get(1).getWidth() / 2;
        final double rowOffset = HEADER_HEIGHT + grid.getModel().getRow(0).getHeight() / 2;
        when(point.getX()).thenReturn(columnOffset + columnWidth);
        when(point.getY()).thenReturn(rowOffset);

        grid.selectCell(point, false, true);

        assertThat(grid.getModel().getSelectedCells()).isNotEmpty();
        assertThat(grid.getModel().getSelectedCells()).contains(new GridData.SelectedCell(0, 1));

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());
        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isInstanceOf(NOPDomainObject.class);
    }

    @Test
    public void testSelectExpressionEditorFirstCell() {
        grid.getModel().appendRow(new BaseGridRow());
        appendColumns(GridColumn.class);

        final ExpressionCellValue cellValue = mock(ExpressionCellValue.class);
        final BaseExpressionGrid cellGrid = mock(BaseExpressionGrid.class);
        when(cellValue.getValue()).thenReturn(Optional.of(cellGrid));

        grid.getModel().setCellValue(0, 0, cellValue);

        grid.selectExpressionEditorFirstCell(0, 0);

        verify(gridLayer).select(cellGrid);
        verify(cellGrid).selectFirstCell();
    }

    @Test
    public void testSelectHeaderWithPoint() {
        grid.getModel().appendColumn(new RowNumberColumn());
        grid.getModel().appendColumn(new RowNumberColumn());

        final Point2D point = mock(Point2D.class);
        final double columnOffset = grid.getModel().getColumns().get(0).getWidth();
        final double columnWidth = grid.getModel().getColumns().get(1).getWidth() / 2;
        when(point.getX()).thenReturn(columnOffset + columnWidth);
        when(point.getY()).thenReturn(HEADER_HEIGHT / 2);

        when(grid.getHeader()).thenReturn(header);
        when(header.getY()).thenReturn(0.0);
        when(renderer.getHeaderHeight()).thenReturn(HEADER_HEIGHT);
        when(renderer.getHeaderRowHeight()).thenReturn(HEADER_HEIGHT);

        grid.selectHeaderCell(point, false, false);

        assertHeaderSelection();
        assertDomainObjectEventFiring();
    }

    @Test
    public void testSelectHeaderWithCoordinate() {
        grid.getModel().appendColumn(new RowNumberColumn());
        grid.getModel().appendColumn(new RowNumberColumn());

        grid.selectHeaderCell(0, 1, false, false);

        assertHeaderSelection();
        assertDomainObjectEventFiring();
    }

    @Test
    public void testSelectHeaderCellWithDomainObjectInStunnerGraph() {
        grid.getModel().appendColumn(new RowNumberColumn());
        grid.getModel().appendColumn(new RowNumberColumn());

        //Mock graph to contain decision
        final Definition definition = mock(Definition.class);
        when(node.getUUID()).thenReturn(NODE_UUID);
        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(decision);

        //Mock grid to dispatch header selection as a DomainObject
        doAnswer(i -> {
            grid.fireDomainObjectSelectionEvent(decision);
            return null;
        }).when(grid).doAfterHeaderSelectionChange(anyInt(), anyInt());

        grid.selectHeaderCell(0, 1, false, false);

        assertHeaderSelection();

        verify(refreshFormPropertiesEvent).fire(refreshFormPropertiesEventCaptor.capture());
        final RefreshFormPropertiesEvent refreshFormPropertiesEvent = refreshFormPropertiesEventCaptor.getValue();
        assertThat(refreshFormPropertiesEvent.getUuid()).isEqualTo(NODE_UUID);
        assertThat(refreshFormPropertiesEvent.getSession()).isEqualTo(session);
    }

    @Test
    public void testAdjustSelectionHandling_DataCells() {
        grid.getModel().appendColumn(new RowNumberColumn());
        grid.getModel().appendRow(new BaseGridRow());

        grid.selectHeaderCell(0, 0, false, false);
        reset(grid);
        grid.adjustSelection(SelectionExtension.DOWN, false);

        verify(grid).doAfterSelectionChange(0, 0);
    }

    @Test
    public void testAdjustSelectionHandling_HeaderCells() {
        grid.getModel().appendColumn(new RowNumberColumn());
        grid.getModel().appendRow(new BaseGridRow());

        grid.selectCell(0, 0, false, false);
        reset(grid);
        grid.adjustSelection(SelectionExtension.UP, false);

        verify(grid).doAfterHeaderSelectionChange(0, 0);
    }

    @Test
    public void testAdjustSelectionHandling_MoveUpWhenOnTopAlready() {
        grid.getModel().appendColumn(new RowNumberColumn());
        grid.getModel().appendRow(new BaseGridRow());

        grid.selectHeaderCell(0, 0, false, false);
        reset(grid);
        grid.adjustSelection(SelectionExtension.UP, false);

        verify(grid, never()).doAfterHeaderSelectionChange(anyInt(), anyInt());
    }

    @Test
    public void testAdjustSelectionHandling_MoveDownWhenAtBottomAlready() {
        grid.getModel().appendColumn(new RowNumberColumn());
        grid.getModel().appendRow(new BaseGridRow());

        grid.selectCell(0, 0, false, false);
        reset(grid);
        grid.adjustSelection(SelectionExtension.DOWN, false);

        verify(grid, never()).doAfterSelectionChange(anyInt(), anyInt());
    }

    private void assertHeaderSelection() {
        assertThat(grid.getModel().getSelectedHeaderCells()).isNotEmpty();
        assertThat(grid.getModel().getSelectedHeaderCells()).contains(new GridData.SelectedCell(0, 1));
    }

    private void assertDomainObjectEventFiring() {
        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());
        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isInstanceOf(NOPDomainObject.class);
    }

    @Test
    public void testPaddingWithParent() {
        doReturn(Optional.of(mock(BaseExpressionGrid.class))).when(grid).findParentGrid();

        assertThat(grid.getPadding()).isEqualTo(BaseExpressionGrid.DEFAULT_PADDING);
    }

    @Test
    public void testPaddingWithNoParent() {
        doReturn(Optional.empty()).when(grid).findParentGrid();

        assertThat(grid.getPadding()).isEqualTo(BaseExpressionGrid.DEFAULT_PADDING);
    }

    @Test
    public void testFindParentGrid() throws Exception {
        final GridWidget parentGrid = mock(BaseExpressionGrid.class);
        doReturn(parentGrid).when(parentCell).getGridWidget();

        assertThat(grid.findParentGrid().get()).isEqualTo(parentGrid);
    }

    @Test
    public void testFindParentGridNoParent() throws Exception {
        assertThat(grid.findParentGrid()).isEmpty();
    }

    @Test
    public void testWidthIncreased() throws Exception {
        testUpdateWidthOfPeers(0, 150);
    }

    @Test
    public void testWidthIncreasedMultipleChildColumnsFirstUpdated() throws Exception {
        testUpdateWidthOfPeers(0, 150, 180);
    }

    @Test
    public void testWidthIncreasedMultipleChildColumnsLastUpdated() throws Exception {
        testUpdateWidthOfPeers(1, 150, 180);
    }

    @Test
    public void testWidthDecreased() throws Exception {
        testUpdateWidthOfPeers(0, 80);
    }

    @Test
    public void testWidthDecreasedMultipleChildColumnsFirstUpdated() throws Exception {
        testUpdateWidthOfPeers(0, 35, 45);
    }

    @Test
    public void testWidthDecreasedMultipleChildColumnsLastUpdated() throws Exception {
        testUpdateWidthOfPeers(1, 35, 45);
    }

    @Test
    public void testResizeWhenExpressionEditorChanged() {
        grid.resize(BaseExpressionGrid.RESIZE_EXISTING);

        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(parentCell).onResize();
        verify(gridLayer).batch(redrawCommandCaptor.capture());

        final GridLayerRedrawManager.PrioritizedCommand redrawCommand = redrawCommandCaptor.getValue();
        redrawCommand.execute();

        verify(gridLayer).draw();
    }

    @Test
    public void testResize() {
        grid.resize(BaseExpressionGrid.RESIZE_EXISTING);

        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
        verify(gridPanel).setFocus(true);
        verify(parentCell).onResize();
        verify(gridLayer).batch(redrawCommandCaptor.capture());

        final GridLayerRedrawManager.PrioritizedCommand redrawCommand = redrawCommandCaptor.getValue();
        redrawCommand.execute();

        verify(gridLayer).draw();
        verify(gridLayer, never()).select(any(GridWidget.class));
    }

    @Test
    public void testHeaderTextBoxFactory() {
        appendColumns(GridColumn.class);
        when(grid.getModel().getColumns().get(0).getHeaderMetaData()).thenReturn(Collections.singletonList(mock(EditableHeaderMetaData.class)));
        when(mapper.getUiModel()).thenReturn(() -> grid.getModel());

        final TextBoxSingletonDOMElementFactory factory = grid.getHeaderTextBoxFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteHeaderValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetHeaderValueCommand.class);
    }

    @Test
    public void testHeaderTextAreaFactory() {
        appendColumns(GridColumn.class);
        when(grid.getModel().getColumns().get(0).getHeaderMetaData()).thenReturn(Collections.singletonList(mock(EditableHeaderMetaData.class)));
        when(mapper.getUiModel()).thenReturn(() -> grid.getModel());

        final TextAreaSingletonDOMElementFactory factory = grid.getHeaderTextAreaFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteHeaderValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetHeaderValueCommand.class);
    }

    @Test
    public void testBodyListBoxFactory() {
        appendColumns(GridColumn.class);
        when(grid.getModel().getColumns().get(0).getHeaderMetaData()).thenReturn(Collections.singletonList(mock(EditableHeaderMetaData.class)));
        when(mapper.getUiModel()).thenReturn(() -> grid.getModel());

        final ListBoxSingletonDOMElementFactory factory = grid.getBodyListBoxFactory();
        assertThat(factory.getHasNoValueCommand().apply(tupleWithoutValue)).isInstanceOf(DeleteCellValueCommand.class);
        assertThat(factory.getHasValueCommand().apply(tupleWithValue)).isInstanceOf(SetCellValueCommand.class);
    }

    @Test
    public void testClearValueConsumer() {
        doTestClearValueConsumer(false,
                                 DeleteHasValueCommand.class);

        verify(gridLayer).batch();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClearValueConsumerWhenNotNested() {
        grid.fireDomainObjectSelectionEvent(decision);
        reset(domainObjectSelectionEvent);

        doTestClearValueConsumer(false,
                                 DeleteHasValueCommand.class);

        verify(gridLayer).batch();
        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(decision);
        assertThat(domainObjectSelectionEvent.getCanvasHandler()).isEqualTo(canvasHandler);
    }

    @Test
    public void testClearValueConsumerAndUpdateStunnerTitle() {
        doTestClearValueConsumer(true,
                                 DeleteHasValueCommand.class);

        verify(gridLayer).batch();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClearValueConsumerWhenNotNestedAndUpdateStunnerTitle() {
        grid.fireDomainObjectSelectionEvent(decision);
        reset(domainObjectSelectionEvent);

        final String uuid = UUID.uuid();
        final Name name = new Name();
        doReturn(Optional.of(uuid)).when(grid).getNodeUUID();
        when(index.get(uuid)).thenReturn(element);
        when(canvasCommandFactory.updatePropertyValue(element, NAME_ID, name)).thenReturn(updateElementPropertyCommand);

        doTestClearValueConsumer(true,
                                 DeleteHasValueCommand.class,
                                 UpdateElementPropertyCommand.class);

        verify(gridLayer).batch();
        verify(updateElementPropertyCommand).execute(eq(canvasHandler));
        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(decision);
        assertThat(domainObjectSelectionEvent.getCanvasHandler()).isEqualTo(canvasHandler);
    }

    @SuppressWarnings("unchecked")
    private void doTestClearValueConsumer(final boolean updateStunnerTitle,
                                          final Class... expectedCommandClasses) {
        grid.clearValueConsumer(updateStunnerTitle, new Name()).accept(decision);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              commandCaptor.capture());

        final Command command = commandCaptor.getValue();
        GridFactoryCommandUtils.assertCommands(command,
                                               expectedCommandClasses);

        command.execute(canvasHandler);
    }

    @Test
    public void testSetValueConsumer() {
        doTestSetValueConsumer(false,
                               SetHasValueCommand.class);

        verify(gridLayer).batch();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetValueConsumerWhenNotNested() {
        grid.fireDomainObjectSelectionEvent(decision);
        reset(domainObjectSelectionEvent);

        doTestSetValueConsumer(false,
                               SetHasValueCommand.class);

        verify(gridLayer).batch();
        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(decision);
        assertThat(domainObjectSelectionEvent.getCanvasHandler()).isEqualTo(canvasHandler);
    }

    @Test
    public void testSetValueConsumerAndUpdateStunnerTitle() {
        doTestSetValueConsumer(true,
                               SetHasValueCommand.class);

        verify(gridLayer).batch();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetValueConsumerWhenNotNestedAndUpdateStunnerTitle() {
        grid.fireDomainObjectSelectionEvent(decision);
        reset(domainObjectSelectionEvent);

        final String uuid = UUID.uuid();
        doReturn(Optional.of(uuid)).when(grid).getNodeUUID();
        when(index.get(uuid)).thenReturn(element);
        when(canvasCommandFactory.updatePropertyValue(element, NAME_ID, NAME)).thenReturn(updateElementPropertyCommand);

        doTestSetValueConsumer(true,
                               SetHasValueCommand.class,
                               UpdateElementPropertyCommand.class);

        verify(gridLayer).batch();
        verify(updateElementPropertyCommand).execute(eq(canvasHandler));
        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(decision);
        assertThat(domainObjectSelectionEvent.getCanvasHandler()).isEqualTo(canvasHandler);
    }

    @SuppressWarnings("unchecked")
    private void doTestSetValueConsumer(final boolean updateStunnerTitle,
                                        final Class... expectedCommandClasses) {
        grid.setValueConsumer(updateStunnerTitle).accept(decision, NAME);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              commandCaptor.capture());

        final Command command = commandCaptor.getValue();
        GridFactoryCommandUtils.assertCommands(command,
                                               expectedCommandClasses);

        command.execute(canvasHandler);
    }

    @Test
    public void testSetTypeRefConsumer() {
        doTestSetTypeRefConsumer();

        verify(gridLayer).batch();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetTypeRefConsumerWhenNotNested() {
        grid.fireDomainObjectSelectionEvent(decision);
        reset(domainObjectSelectionEvent);

        doTestSetTypeRefConsumer();

        verify(gridLayer).batch();
        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(decision);
        assertThat(domainObjectSelectionEvent.getCanvasHandler()).isEqualTo(canvasHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowContextMenuHeader() {
        final MockHasCellEditorControlsHeaderMetaData headerMetaData = mock(MockHasCellEditorControlsHeaderMetaData.class);
        final HasCellEditorControls.Editor cellControlsEditorMock = mock(HasCellEditorControls.Editor.class);
        doReturn(Optional.of(cellControlsEditorMock)).when(headerMetaData).getEditor();

        grid.getModel().appendColumn(new BaseGridColumn<Object>(headerMetaData,
                                                                mock(GridColumnRenderer.class),
                                                                COLUMN_WIDTH));

        assertThat(grid.showContextMenuForHeader(0, 0)).isTrue();

        verify(cellControlsEditorMock).bind(eq(headerMetaData),
                                            eq(0),
                                            eq(0));
        verify(cellEditorControls).show(eq(cellControlsEditorMock),
                                        eq((int) COLUMN_WIDTH / 2),
                                        eq((int) HEADER_HEIGHT / 2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowContextMenuHeaderMissingEditor() {
        final MockHasCellEditorControlsHeaderMetaData headerMetaData = mock(MockHasCellEditorControlsHeaderMetaData.class);
        doReturn(Optional.empty()).when(headerMetaData).getEditor();

        grid.getModel().appendColumn(new BaseGridColumn<Object>(mock(GridColumn.HeaderMetaData.class),
                                                                mock(GridColumnRenderer.class),
                                                                COLUMN_WIDTH));

        assertThat(grid.showContextMenuForHeader(0, 0)).isFalse();

        verify(cellEditorControls, never()).show(any(HasCellEditorControls.Editor.class),
                                                 anyInt(),
                                                 anyInt());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowContextMenu() {
        grid.getModel().appendColumn(new BaseGridColumn<Object>(mock(GridColumn.HeaderMetaData.class),
                                                                mock(GridColumnRenderer.class),
                                                                COLUMN_WIDTH));
        final BaseGridRow gridRow = new BaseGridRow();
        grid.getModel().appendRow(gridRow);

        final DMNGridCell<?> dmnGridCellMock = mock(DMNGridCell.class);
        final HasCellEditorControls.Editor cellControlsEditorMock = mock(HasCellEditorControls.Editor.class);
        doReturn(Optional.of(cellControlsEditorMock)).when(dmnGridCellMock).getEditor();

        grid.getModel().setCell(0, 0, () -> dmnGridCellMock);

        assertThat(grid.showContextMenuForCell(0, 0)).isTrue();

        verify(cellControlsEditorMock).bind(eq(grid),
                                            eq(0),
                                            eq(0));
        verify(cellEditorControls).show(eq(cellControlsEditorMock),
                                        eq((int) COLUMN_WIDTH / 2),
                                        eq((int) (gridRow.getHeight() / 2 + HEADER_HEIGHT)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowContextMenuMissingEditor() {
        grid.getModel().appendColumn(new BaseGridColumn<Object>(mock(GridColumn.HeaderMetaData.class),
                                                                mock(GridColumnRenderer.class),
                                                                COLUMN_WIDTH));
        final BaseGridRow gridRow = new BaseGridRow();
        grid.getModel().appendRow(gridRow);

        final DMNGridCell<?> dmnGridCellMock = mock(DMNGridCell.class);
        doReturn(Optional.empty()).when(dmnGridCellMock).getEditor();

        grid.getModel().setCell(0, 0, () -> dmnGridCellMock);

        assertThat(grid.showContextMenuForCell(0, 0)).isFalse();

        verify(cellEditorControls, never()).show(any(HasCellEditorControls.Editor.class),
                                                 anyInt(),
                                                 anyInt());
    }

    @SuppressWarnings("unchecked")
    private void doTestSetTypeRefConsumer() {
        grid.setTypeRefConsumer().accept(decision.getVariable(), TYPE_REF);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              commandCaptor.capture());

        final Command command = commandCaptor.getValue();
        assertThat(command).isInstanceOf(SetTypeRefCommand.class);

        command.execute(canvasHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterColumnResizeCompleted() {
        final DMNGridColumn uiColumn = mockColumn(200.0, grid);

        grid.registerColumnResizeCompleted(uiColumn, 100.0);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              commandCaptor.capture());

        final Command command = commandCaptor.getValue();
        assertThat(command).isInstanceOf(SetComponentWidthCommand.class);
    }

    @Test
    public void testReadOnlyModeActive() {
        when(readOnlyProvider.isReadOnlyDiagram()).thenReturn(true);

        assertThat(grid.isOnlyVisualChangeAllowed()).isTrue();
    }

    @Test
    public void testReadOnlyModeNonActive() {
        when(readOnlyProvider.isReadOnlyDiagram()).thenReturn(false);

        assertThat(grid.isOnlyVisualChangeAllowed()).isFalse();
    }

    /*
     * Test that parent column width is updated to sum of nested columns
     * The update is forced from nested column at position indexOfColumnToUpdate
     * The default width of parent column is 100
     */
    private void testUpdateWidthOfPeers(final int indexOfColumnToUpdate,
                                        final double... widthsOfNestedColumns) {
        // parent column
        final BaseExpressionGrid parentGrid = mock(BaseExpressionGrid.class);
        final GridData parentGridData = mock(GridData.class);
        when(parentGrid.getModel()).thenReturn(parentGridData);
        when(parentGrid.getExpression()).thenReturn(Optional::empty);

        final DMNGridColumn parentColumn = mockColumn(100, parentGrid);
        when(parentCell.getGridWidget()).thenReturn(parentGrid);
        when(parentGridData.getColumns()).thenReturn(Collections.singletonList(parentColumn));
        when(parentGridData.getColumnCount()).thenReturn(widthsOfNestedColumns.length);
        when(gridLayer.getGridWidgets()).thenReturn(Collections.singleton(parentGrid));

        // nested columns
        final List<DMNGridColumn> columns = Arrays.stream(widthsOfNestedColumns)
                .mapToObj(width -> mockColumn(width, grid))
                .collect(Collectors.toList());
        grid.getModel().appendRow(new BaseGridRow());
        columns.stream().forEach(column -> grid.getModel().appendColumn(column));

        // force the peers width update
        columns.get(indexOfColumnToUpdate).updateWidthOfPeers();

        // assert parent width is equal to sum of nested columns widths
        final double padding = BaseExpressionGrid.DEFAULT_PADDING * 2;
        Assertions.assertThat(parentColumn.getWidth()).isEqualTo(Arrays.stream(widthsOfNestedColumns).sum() + padding);
    }

    private void assertMinimumWidth(final double expectedMinimumWidth,
                                    final MockColumnData... columnData) {
        Arrays.asList(columnData).forEach(cd -> {
            final GridColumn uiColumn = mock(GridColumn.class);
            doReturn(cd.width).when(uiColumn).getWidth();
            doReturn(cd.minWidth).when(uiColumn).getMinimumWidth();
            grid.getModel().appendColumn(uiColumn);
        });

        assertEquals(expectedMinimumWidth,
                     grid.getMinimumWidth(),
                     0.0);
    }

    @SafeVarargs
    private final void appendColumns(final Class<? extends GridColumn>... columnClasses) {
        IntStream.range(0, columnClasses.length).forEach(i -> {
            final GridColumn column = mock(columnClasses[i]);
            doReturn(i).when(column).getIndex();
            doReturn(true).when(column).isVisible();
            doReturn(COLUMN_WIDTH).when(column).getWidth();
            grid.getModel().appendColumn(column);
        });
    }

    private static class MockColumnData {

        private double width;
        private double minWidth;

        public MockColumnData(final double width,
                              final double minWidth) {
            this.width = width;
            this.minWidth = minWidth;
        }
    }

    @SuppressWarnings("unchecked")
    private DMNGridColumn mockColumn(final double width,
                                     final BaseGrid gridWidget) {
        final GridColumn.HeaderMetaData headerMetaData = mock(GridColumn.HeaderMetaData.class);
        final GridColumnRenderer columnRenderer = mock(GridColumnRenderer.class);
        return new DMNGridColumn(headerMetaData,
                                 columnRenderer,
                                 width,
                                 gridWidget) {{
            setWidth(width);
        }};
    }
}
