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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.AddRelationColumnCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.AddRelationRowCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.DeleteRelationColumnCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.DeleteRelationRowCommand;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHasValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHasValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetTypeRefCommand;
import org.kie.workbench.common.dmn.client.editors.expressions.types.GridFactoryCommandUtils;
import org.kie.workbench.common.dmn.client.editors.types.HasValueAndTypeRef;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseBounds;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class RelationGridTest {

    private final static int HEADER_COLUMNS = 0;

    private final static int INSERT_COLUMN_BEFORE = 1;

    private final static int INSERT_COLUMN_AFTER = 2;

    private final static int DELETE_COLUMN = 3;

    private final static int HEADER_ROWS = 4;

    private final static int INSERT_ROW_ABOVE = 5;

    private final static int INSERT_ROW_BELOW = 6;

    private final static int DELETE_ROW = 7;

    private static final String NODE_UUID = "uuid";

    private static final String NAME = "column-1";

    private static final String NAME_NEW = "name-new";

    private GridCellTuple tupleWithoutValue;

    private GridCellValueTuple tupleWithValue;

    @Mock
    private GridWidget parentGridWidget;

    @Mock
    private GridData parentGridData;

    @Mock
    private GridColumn parentGridColumn;

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
    private GraphCommandExecutionContext graphContext;

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
    private EventSourceMock<ExpressionEditorChanged> editorSelectedEvent;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private EventSourceMock<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Mock
    private ManagedInstance<ValueAndDataTypePopoverView.Presenter> headerEditors;

    @Mock
    private ValueAndDataTypePopoverView.Presenter headerEditor;

    @Mock
    private GridBodyCellEditContext gridBodyCellEditContext;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    @Captor
    private ArgumentCaptor<AddRelationColumnCommand> addColumnCommand;

    @Captor
    private ArgumentCaptor<DeleteRelationColumnCommand> deleteColumnCommand;

    @Captor
    private ArgumentCaptor<AddRelationRowCommand> addRowCommand;

    @Captor
    private ArgumentCaptor<DeleteRelationRowCommand> deleteRowCommand;

    @Captor
    private ArgumentCaptor<GridLayerRedrawManager.PrioritizedCommand> redrawCommandCaptor;

    @Captor
    private ArgumentCaptor<CompositeCommand> compositeCommandCaptor;

    @Captor
    private ArgumentCaptor<DomainObjectSelectionEvent> domainObjectSelectionEventCaptor;

    private GridCellTuple parent;

    private Relation relation = new Relation();

    private Decision hasExpression = new Decision();

    private Optional<Relation> expression = Optional.of(relation);

    private Optional<HasName> hasName = Optional.empty();

    private RelationEditorDefinition definition;

    private RelationGrid grid;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getGridPanel()).thenReturn(gridPanel);
        when(session.getGridLayer()).thenReturn(gridLayer);
        when(session.getCellEditorControls()).thenReturn(cellEditorControls);

        tupleWithoutValue = new GridCellTuple(0, 1, gridWidget);
        tupleWithValue = new GridCellValueTuple<>(0, 1, gridWidget, new BaseGridCellValue<>("value"));

        definition = new RelationEditorDefinition(definitionUtils,
                                                  sessionManager,
                                                  sessionCommandManager,
                                                  canvasCommandFactory,
                                                  editorSelectedEvent,
                                                  refreshFormPropertiesEvent,
                                                  domainObjectSelectionEvent,
                                                  listSelector,
                                                  translationService,
                                                  headerEditors,
                                                  readOnlyProvider);

        final Decision decision = new Decision();
        decision.setName(new Name("name"));
        hasName = Optional.of(decision);
        expression = definition.getModelClass();
        definition.enrich(Optional.empty(), hasExpression, expression);

        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(parentGridWidget.getModel()).thenReturn(parentGridData);
        when(parentGridData.getColumns()).thenReturn(Collections.singletonList(parentGridColumn));

        parent = spy(new GridCellTuple(0, 0, parentGridWidget));

        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getGraphExecutionContext()).thenReturn(graphContext);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(Collections.singletonList(node));

        when(gridWidget.getModel()).thenReturn(new BaseGridData(false));
        when(gridLayer.getDomElementContainer()).thenReturn(gridLayerDomElementContainer);
        when(gridLayerDomElementContainer.iterator()).thenReturn(mock(Iterator.class));
        when(gridLayer.getVisibleBounds()).thenReturn(new BaseBounds(0, 0, 200, 200));
        when(gridLayer.getViewport()).thenReturn(viewport);
        when(viewport.getTransform()).thenReturn(transform);

        when(headerEditors.get()).thenReturn(headerEditor);
        when(gridBodyCellEditContext.getRelativeLocation()).thenReturn(Optional.empty());

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(Mockito.<String>any());
        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).getTranslation(Mockito.<String>any());
    }

    private void setupGrid(final int nesting) {
        this.hasExpression.setExpression(expression.get());
        this.grid = spy((RelationGrid) definition.getEditor(parent,
                                                            nesting == 0 ? Optional.of(NODE_UUID) : Optional.empty(),
                                                            hasExpression,
                                                            hasName,
                                                            false,
                                                            nesting).get());
    }

    @Test
    public void testInitialiseUiColumnsEmptyModel() {
        expression = Optional.of(new Relation());

        setupGrid(0);

        assertEquals(0,
                     grid.getModel().getRowCount());
        assertEquals(1,
                     grid.getModel().getColumns().size());
        assertTrue(grid.getModel().getColumns().get(0) instanceof RowNumberColumn);
    }

    @Test
    public void testInitialiseUiColumns() {
        final String columnHeader = "first column";
        relation.getColumn().add(new InformationItem() {{
            getName().setValue(columnHeader);
        }});

        expression = Optional.of(relation);

        setupGrid(0);

        assertEquals(2,
                     grid.getModel().getColumns().size());
        assertTrue(grid.getModel().getColumns().get(0) instanceof RowNumberColumn);
        assertEquals(columnHeader,
                     grid.getModel().getColumns().get(1).getHeaderMetaData().get(0).getTitle());
    }

    @Test
    public void testInitialiseUiModel() {
        relation.getColumn().add(new InformationItem() {{
            getName().setValue("first column header");
        }});
        final String firstRowValue = "first column value 1";
        final String secondRowValue = "first column value 2";
        final List rowList1 = new List();
        rowList1.getExpression().add(HasExpression.wrap(rowList1,
                                                        new LiteralExpression() {{
                                                            getText().setValue(firstRowValue);
                                                        }}));
        final List rowList2 = new List();
        rowList2.getExpression().add(HasExpression.wrap(rowList2,
                                                        new LiteralExpression() {{
                                                            getText().setValue(secondRowValue);
                                                        }}));

        relation.getRow().add(rowList1);
        relation.getRow().add(rowList2);

        expression = Optional.of(relation);

        setupGrid(0);

        assertEquals(2,
                     grid.getModel().getRowCount());
        assertEquals(firstRowValue,
                     grid.getModel().getRow(0).getCells().get(1).getValue().getValue());
        assertEquals(secondRowValue,
                     grid.getModel().getRow(1).getCells().get(1).getValue().getValue());
    }

    @Test
    public void testInitialSetupFromDefinition() {
        setupGrid(0);

        final GridData uiModel = grid.getModel();
        assertThat(uiModel).isInstanceOf(RelationGridData.class);

        assertThat(uiModel.getColumnCount()).isEqualTo(2);
        assertThat(uiModel.getColumns().get(0)).isInstanceOf(RowNumberColumn.class);
        assertThat(uiModel.getColumns().get(1)).isInstanceOf(RelationColumn.class);

        assertThat(uiModel.getRowCount()).isEqualTo(1);

        assertThat(uiModel.getCell(0, 0).getValue().getValue()).isEqualTo(1);
        assertThat(uiModel.getCell(0, 1).getValue().getValue()).isEqualTo("");
    }

    @Test
    public void testInitialColumnWidthsFromDefinition() {
        setupGrid(0);

        assertComponentWidths(50.0,
                              DMNGridColumn.DEFAULT_WIDTH);
    }

    @Test
    public void testInitialColumnWidthsFromExpression() {
        final java.util.List<Double> componentWidths = expression.get().getComponentWidths();
        componentWidths.set(0, 200.0);
        componentWidths.set(1, 300.0);

        setupGrid(0);

        assertComponentWidths(200.0,
                              300.0);
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
    public void testGetItems() {
        setupGrid(0);

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);

        assertThat(items.size()).isEqualTo(8);
        assertListSelectorHeaderItem(items.get(HEADER_COLUMNS),
                                     DMNEditorConstants.RelationEditor_HeaderColumns);
        assertListSelectorTextItem(items.get(INSERT_COLUMN_BEFORE),
                                   DMNEditorConstants.RelationEditor_InsertColumnLeft);
        assertListSelectorTextItem(items.get(INSERT_COLUMN_AFTER),
                                   DMNEditorConstants.RelationEditor_InsertColumnRight);
        assertListSelectorTextItem(items.get(DELETE_COLUMN),
                                   DMNEditorConstants.RelationEditor_DeleteColumn);
        assertListSelectorHeaderItem(items.get(HEADER_ROWS),
                                     DMNEditorConstants.RelationEditor_HeaderRows);
        assertListSelectorTextItem(items.get(INSERT_ROW_ABOVE),
                                   DMNEditorConstants.RelationEditor_InsertRowAbove);
        assertListSelectorTextItem(items.get(INSERT_ROW_BELOW),
                                   DMNEditorConstants.RelationEditor_InsertRowBelow);
        assertListSelectorTextItem(items.get(DELETE_ROW),
                                   DMNEditorConstants.RelationEditor_DeleteRow);
    }

    @Test
    public void testGetHeaderItems() {
        setupGrid(0);

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getHeaderItems(0, 0);

        assertThat(items.size()).isEqualTo(4);
        assertListSelectorHeaderItem(items.get(HEADER_COLUMNS),
                                     DMNEditorConstants.RelationEditor_HeaderColumns);
        assertListSelectorTextItem(items.get(INSERT_COLUMN_BEFORE),
                                   DMNEditorConstants.RelationEditor_InsertColumnLeft);
        assertListSelectorTextItem(items.get(INSERT_COLUMN_AFTER),
                                   DMNEditorConstants.RelationEditor_InsertColumnRight);
        assertListSelectorTextItem(items.get(DELETE_COLUMN),
                                   DMNEditorConstants.RelationEditor_DeleteColumn);
    }

    private void assertListSelectorHeaderItem(final HasListSelectorControl.ListSelectorItem item,
                                              final String text) {
        assertThat(item).isInstanceOf(HasListSelectorControl.ListSelectorHeaderItem.class);
        final HasListSelectorControl.ListSelectorHeaderItem hi = (HasListSelectorControl.ListSelectorHeaderItem) item;
        assertThat(hi.getText()).isEqualTo(text);
    }

    private void assertListSelectorTextItem(final HasListSelectorControl.ListSelectorItem item,
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
    public void testOnItemSelectedInsertColumnBefore() {
        setupGrid(0);
        mockInsertColumnCommandExecution();

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 1);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_COLUMN_BEFORE);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addColumn(eq(1));
    }

    @Test
    public void testOnItemSelectedInsertColumnAfter() {
        setupGrid(0);
        mockInsertColumnCommandExecution();

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 1);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_COLUMN_AFTER);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addColumn(eq(2));
    }

    @Test
    public void testOnItemSelectedInsertColumnEnabled() {
        setupGrid(0);

        assertListSelectorItemEnabled(0, 0, INSERT_COLUMN_BEFORE, false);
        assertListSelectorItemEnabled(0, 1, INSERT_COLUMN_BEFORE, true);

        assertListSelectorItemEnabled(0, 0, INSERT_COLUMN_AFTER, false);
        assertListSelectorItemEnabled(0, 1, INSERT_COLUMN_AFTER, true);
    }

    @Test
    public void testOnItemSelectedDeleteColumn() {
        relation.getColumn().add(new InformationItem());
        setupGrid(0);

        //Cannot delete column 0 since it is the RowNumber column. The first Relation column is 1.
        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DELETE_COLUMN);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).deleteColumn(eq(1));
    }

    @Test
    public void testOnItemSelectedDeleteColumnEnabled() {
        setupGrid(0);

        //Grid has one Relation column that cannot be deleted.
        assertListSelectorItemEnabled(0, 0, DELETE_COLUMN, false);
        assertListSelectorItemEnabled(0, 1, DELETE_COLUMN, false);

        //Grid has two Relation columns. Columns 1 and 2 can be deleted.
        grid.getModel().appendColumn(mock(RelationColumn.class));
        assertListSelectorItemEnabled(0, 0, DELETE_COLUMN, false);
        assertListSelectorItemEnabled(0, 1, DELETE_COLUMN, true);
        assertListSelectorItemEnabled(0, 2, DELETE_COLUMN, true);
    }

    @Test
    public void testOnItemSelectedInsertRowAbove() {
        setupGrid(0);

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_ROW_ABOVE);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addRow(eq(0));
    }

    @Test
    public void testOnItemSelectedInsertRowBelow() {
        setupGrid(0);

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_ROW_BELOW);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addRow(eq(1));
    }

    @Test
    public void testOnItemSelectedInsertRowEnabled() {
        setupGrid(0);

        assertListSelectorItemEnabled(0, 0, INSERT_ROW_ABOVE, true);
        assertListSelectorItemEnabled(1, 0, INSERT_ROW_ABOVE, true);

        assertListSelectorItemEnabled(0, 0, INSERT_ROW_BELOW, true);
        assertListSelectorItemEnabled(1, 0, INSERT_ROW_BELOW, true);
    }

    @Test
    public void testOnItemSelectedDeleteRow() {
        relation.getRow().add(new List());
        setupGrid(0);

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DELETE_ROW);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).deleteRow(eq(0));
    }

    @Test
    public void testOnItemSelectedDeleteRowEnabled() {
        setupGrid(0);

        //Grid has one row that cannot be deleted.
        assertListSelectorItemEnabled(0, 0, DELETE_ROW, false);

        //Grid has two rows. Rows 1 and 2 can be deleted.
        grid.getModel().appendRow(new BaseGridRow());
        assertListSelectorItemEnabled(0, 0, DELETE_ROW, true);
        assertListSelectorItemEnabled(1, 0, DELETE_ROW, true);
    }

    @Test
    public void testOnHeaderItemSelectedInsertColumnBefore() {
        setupGrid(0);
        mockInsertColumnCommandExecution();

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getHeaderItems(0, RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_COLUMN_BEFORE);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addColumn(eq(1));
    }

    @Test
    public void testOnHeaderItemSelectedInsertColumnAfter() {
        setupGrid(0);
        mockInsertColumnCommandExecution();

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getHeaderItems(0, RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_COLUMN_AFTER);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addColumn(eq(2));
    }

    @Test
    public void testOnHeaderItemSelectedDeleteColumn() {
        relation.getColumn().add(new InformationItem());
        setupGrid(0);

        //Cannot delete column 0 since it is the RowNumber column. The first Relation column is 1.
        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getHeaderItems(0, RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DELETE_COLUMN);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).deleteColumn(eq(1));
    }

    @Test
    public void testGetItemsWithCellSelectionsCoveringMultipleRows() {
        setupGrid(0);

        addRow(0);
        grid.getModel().selectCell(0, 0);
        grid.getModel().selectCell(1, 0);

        assertListSelectorItemEnabled(0, 0, INSERT_ROW_ABOVE, false);
        assertListSelectorItemEnabled(0, 0, INSERT_ROW_BELOW, false);
        assertListSelectorItemEnabled(0, 0, DELETE_ROW, false);
    }

    @Test
    public void testGetItemsWithCellSelectionsCoveringMultipleColumns() {
        setupGrid(0);

        addColumn(1);
        grid.getModel().selectCell(0, 0);
        grid.getModel().selectCell(0, 1);

        assertListSelectorItemEnabled(0, 0, INSERT_COLUMN_BEFORE, false);
        assertListSelectorItemEnabled(0, 0, INSERT_COLUMN_AFTER, false);
        assertListSelectorItemEnabled(0, 0, DELETE_COLUMN, false);
    }

    private void assertListSelectorItemEnabled(final int uiRowIndex,
                                               final int uiColumnIndex,
                                               final int listItemIndex,
                                               final boolean enabled) {
        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(uiRowIndex, uiColumnIndex);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(listItemIndex);
        assertThat(ti.isEnabled()).isEqualTo(enabled);
    }

    @Test
    public void testAddColumn() throws Exception {
        setupGrid(0);

        addColumn(1);

        verifyCommandExecuteOperation(BaseExpressionGrid.RESIZE_EXISTING);
        verify(grid).selectHeaderCell(eq(0),
                                      eq(1),
                                      eq(false),
                                      eq(false));

        verify(headerEditor).bind(any(RelationColumnHeaderMetaData.class),
                                  eq(0),
                                  eq(1));
        verify(cellEditorControls).show(eq(headerEditor),
                                        anyInt(),
                                        anyInt());

        //Check undo operation
        reset(gridPanel, gridLayer, grid, parentGridColumn);
        verify(sessionCommandManager).execute(eq(canvasHandler), addColumnCommand.capture());
        addColumnCommand.getValue().undo(canvasHandler);

        verifyCommandUndoOperation(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);
    }

    private void addColumn(final int index) {
        mockInsertColumnCommandExecution();

        grid.addColumn(index);
    }

    private void mockInsertColumnCommandExecution() {
        when(sessionCommandManager.execute(eq(canvasHandler),
                                           any(AbstractCanvasGraphCommand.class))).thenAnswer((i) -> {
            final AbstractCanvasHandler handler = (AbstractCanvasHandler) i.getArguments()[0];
            final AbstractCanvasGraphCommand command = (AbstractCanvasGraphCommand) i.getArguments()[1];
            return command.execute(handler);
        });
    }

    private void verifyCommandExecuteOperation(final Function<BaseExpressionGrid, Double> resizeFunction) {
        verify(parent).proposeContainingColumnWidth(eq(grid.getWidth() + grid.getPadding() * 2), eq(resizeFunction));
        verify(parentGridColumn).setWidth(grid.getWidth() + grid.getPadding() * 2);
        verify(gridLayer).batch(redrawCommandCaptor.capture());
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();

        final GridLayerRedrawManager.PrioritizedCommand redrawCommand = redrawCommandCaptor.getValue();
        redrawCommand.execute();
        verify(gridLayer).draw();
    }

    private void verifyCommandUndoOperation(final Function<BaseExpressionGrid, Double> resizeFunction) {
        verify(parent).proposeContainingColumnWidth(eq(grid.getWidth() + grid.getPadding() * 2), eq(resizeFunction));
        verify(parentGridColumn).setWidth(grid.getWidth() + grid.getPadding() * 2);
        verify(gridLayer).batch(redrawCommandCaptor.capture());
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();

        assertThat(redrawCommandCaptor.getAllValues()).hasSize(2);
        redrawCommandCaptor.getAllValues().get(1).execute();
        verify(gridLayer).draw();
    }

    @Test
    public void testDeleteColumn() {
        relation.getColumn().add(new InformationItem());
        setupGrid(0);

        //Cannot delete column 0 since it is the RowNumber column. The first Relation column is 1.
        grid.deleteColumn(RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        verify(sessionCommandManager).execute(eq(canvasHandler), deleteColumnCommand.capture());
        deleteColumnCommand.getValue().execute(canvasHandler);

        verifyCommandExecuteOperation(BaseExpressionGrid.RESIZE_EXISTING_MINIMUM);

        //Check undo operation
        reset(gridPanel, gridLayer, grid, parentGridColumn);
        verify(sessionCommandManager).execute(eq(canvasHandler), deleteColumnCommand.capture());
        deleteColumnCommand.getValue().undo(canvasHandler);

        verifyCommandUndoOperation(BaseExpressionGrid.RESIZE_EXISTING);
    }

    @Test
    public void testAddRow() {
        setupGrid(0);

        addRow(0);

        verifyCommandExecuteOperation(BaseExpressionGrid.RESIZE_EXISTING);
    }

    private void addRow(final int index) {
        grid.addRow(index);

        verify(sessionCommandManager).execute(eq(canvasHandler), addRowCommand.capture());

        addRowCommand.getValue().execute(canvasHandler);
    }

    @Test
    public void testDeleteRow() {
        relation.getRow().add(new List());
        setupGrid(0);

        grid.deleteRow(0);

        verify(sessionCommandManager).execute(eq(canvasHandler), deleteRowCommand.capture());

        deleteRowCommand.getValue().execute(canvasHandler);

        verifyCommandExecuteOperation(BaseExpressionGrid.RESIZE_EXISTING);
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

        assertThat(extractHeaderMetaData().getValue().getValue()).isEqualTo(NAME);
    }

    private RelationColumnHeaderMetaData extractHeaderMetaData() {
        final RelationColumn column = (RelationColumn) grid.getModel().getColumns().get(1);
        return (RelationColumnHeaderMetaData) column.getHeaderMetaData().get(0);
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
                                               DeleteHasValueCommand.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameWithNullValue() {
        setupGrid(0);

        extractHeaderMetaData().setValue(null);

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              compositeCommandCaptor.capture());

        GridFactoryCommandUtils.assertCommands(compositeCommandCaptor.getValue(),
                                               DeleteHasValueCommand.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetDisplayNameWithNonEmptyValue() {
        setupGrid(0);

        extractHeaderMetaData().setValue(new Name(NAME_NEW));

        verify(sessionCommandManager).execute(eq(canvasHandler),
                                              compositeCommandCaptor.capture());

        GridFactoryCommandUtils.assertCommands(compositeCommandCaptor.getValue(),
                                               SetHasValueCommand.class);
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

        grid.selectCell(0, 0, false, false);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isInstanceOf(NOPDomainObject.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectMultipleCells() {
        setupGrid(0);
        addRow(0);
        addColumn(1);

        //DomainObject selected when row added, so reset for this test
        reset(domainObjectSelectionEvent);

        grid.selectCell(0, 1, false, false);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent event1 = domainObjectSelectionEventCaptor.getValue();
        assertThat(event1.getDomainObject()).isEqualTo(expression.get().getRow().get(0).getExpression().get(0).getExpression());

        //Reset DomainObjectSelectionEvent tested above.
        reset(domainObjectSelectionEvent);

        grid.selectCell(0, 2, false, true);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent event2 = domainObjectSelectionEventCaptor.getValue();
        assertThat(event2.getDomainObject()).isInstanceOf(NOPDomainObject.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectSingleCellWithHeaderSelected() {
        setupGrid(0);

        grid.selectHeaderCell(0, RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT, false, false);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent event1 = domainObjectSelectionEventCaptor.getValue();
        assertThat(event1.getDomainObject()).isEqualTo(expression.get().getColumn().get(0));

        //Reset DomainObjectSelectionEvent tested above.
        reset(domainObjectSelectionEvent);

        grid.selectCell(0, RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT, false, true);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent event2 = domainObjectSelectionEventCaptor.getValue();
        assertThat(event2.getDomainObject()).isEqualTo(expression.get().getRow().get(0).getExpression().get(0).getExpression());
    }

    @Test
    public void testSelectExpression() {
        setupGrid(0);

        grid.selectCell(0, 1, false, false);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(expression.get().getRow().get(0).getExpression().get(0).getExpression());
    }

    @Test
    public void testSelectHeaderExpression() {
        setupGrid(0);

        grid.selectHeaderCell(0, RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT, false, false);

        verify(domainObjectSelectionEvent).fire(domainObjectSelectionEventCaptor.capture());

        final DomainObjectSelectionEvent domainObjectSelectionEvent = domainObjectSelectionEventCaptor.getValue();
        assertThat(domainObjectSelectionEvent.getDomainObject()).isEqualTo(expression.get().getColumn().get(0));
    }

    @Test
    public void testAsDMNModelInstrumentedBase() {
        setupGrid(0);

        final DMNModelInstrumentedBase actual = extractHeaderMetaData().asDMNModelInstrumentedBase();

        assertThat(actual).isInstanceOf(InformationItem.class);
    }

    @Test
    public void testSelectFirstCell() {
        setupGrid(0);

        grid.selectFirstCell();

        assertThat(grid.getModel().getSelectedCells()).containsOnly(new GridData.SelectedCell(0, 1));
    }

    @Test
    public void testMultipleColumnHeaderEditorInstances() {
        final ValueAndDataTypePopoverView.Presenter headerEditor2 = mock(ValueAndDataTypePopoverView.Presenter.class);

        when(headerEditors.get()).thenReturn(headerEditor, headerEditor2);

        setupGrid(0);

        addColumn(2);

        //Adding the column above shows the header editor by default, so reset it for the purpose of this test.
        reset(headerEditor2);

        final GridColumn uiColumn1 = grid.getModel().getColumns().get(1);
        assertColumnHeaderEditor(uiColumn1, headerEditor);

        final GridColumn uiColumn2 = grid.getModel().getColumns().get(2);
        assertColumnHeaderEditor(uiColumn2, headerEditor2);
    }

    private void assertColumnHeaderEditor(final GridColumn uiColumn,
                                          final ValueAndDataTypePopoverView.Presenter headerEditor) {
        assertThat(uiColumn).isInstanceOf(RelationColumn.class);
        final RelationColumn relationColumn = (RelationColumn) uiColumn;
        final GridColumn.HeaderMetaData relationColumnHeaderMetaData = relationColumn.getHeaderMetaData().get(0);
        assertThat(relationColumnHeaderMetaData).isInstanceOf(RelationColumnHeaderMetaData.class);
        final RelationColumnHeaderMetaData relationColumnHeaderMetaData1 = (RelationColumnHeaderMetaData) relationColumnHeaderMetaData;
        assertThat(relationColumnHeaderMetaData1.getEditor()).isPresent();
        //The only way to assert that editor was bound to the column is to try editing the header
        relationColumnHeaderMetaData1.edit(gridBodyCellEditContext);
        verify(headerEditor).bind(any(HasValueAndTypeRef.class), anyInt(), anyInt());
    }
}
