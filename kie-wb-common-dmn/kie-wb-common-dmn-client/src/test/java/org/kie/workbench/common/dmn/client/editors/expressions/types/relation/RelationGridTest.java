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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.Collections;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.List;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.AddRelationColumnCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.AddRelationRowCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.DeleteRelationColumnCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.DeleteRelationRowCommand;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNClientFullSession;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLayerRedrawManager;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class RelationGridTest {

    private final static int INSERT_COLUMN_BEFORE = 0;

    private final static int INSERT_COLUMN_AFTER = 1;

    private final static int DELETE_COLUMN = 2;

    private final static int DIVIDER = 3;

    private final static int INSERT_ROW_ABOVE = 4;

    private final static int INSERT_ROW_BELOW = 5;

    private final static int DELETE_ROW = 6;

    @Mock
    private GridWidget parentGridWidget;

    @Mock
    private GridData parentGridData;

    @Mock
    private GridColumn parentGridColumn;

    @Mock
    private HasExpression hasExpression;

    private GridCellTuple parent;

    private Relation relation = new Relation();

    private Optional<Relation> expression = Optional.of(relation);

    private Optional<HasName> hasName = Optional.empty();

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private DMNGridLayer gridLayer;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DMNClientFullSession dmnClientFullSession;

    @Mock
    private AbstractCanvasHandler abstractCanvasHandler;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private TranslationService translationService;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Captor
    private ArgumentCaptor<AddRelationColumnCommand> addColumnCommand;

    @Captor
    private ArgumentCaptor<DeleteRelationColumnCommand> deleteColumnCommand;

    @Captor
    private ArgumentCaptor<AddRelationRowCommand> addRowCommand;

    @Captor
    private ArgumentCaptor<DeleteRelationRowCommand> deleteRowCommand;

    private RelationGrid relationGrid;

    @Before
    public void setUp() throws Exception {
        doReturn(abstractCanvasHandler).when(dmnClientFullSession).getCanvasHandler();
        doReturn(dmnClientFullSession).when(sessionManager).getCurrentSession();
        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(anyString());
        parent = spy(new GridCellTuple(0, 0, parentGridWidget));
    }

    private void setupGrid(final int nesting) {
        relationGrid = spy(new RelationGrid(parent,
                                            hasExpression,
                                            expression,
                                            hasName,
                                            gridPanel,
                                            gridLayer,
                                            sessionManager,
                                            sessionCommandManager,
                                            cellEditorControls,
                                            translationService,
                                            listSelector,
                                            nesting));
        doReturn(parentGridData).when(parentGridWidget).getModel();
        doReturn(Collections.singletonList(parentGridColumn)).when(parentGridData).getColumns();
    }

    @Test
    public void testInitialiseUiColumnsEmptyModel() throws Exception {
        setupGrid(0);

        assertEquals(0, relationGrid.getModel().getRowCount());
        assertEquals(1, relationGrid.getModel().getColumns().size());
        assertTrue(relationGrid.getModel().getColumns().get(0) instanceof RowNumberColumn);
    }

    @Test
    public void testInitialiseUiColumns() throws Exception {
        final String columnHeader = "first column";
        relation.getColumn().add(new InformationItem() {{
            getName().setValue(columnHeader);
        }});

        setupGrid(0);

        assertEquals(2, relationGrid.getModel().getColumns().size());
        assertTrue(relationGrid.getModel().getColumns().get(0) instanceof RowNumberColumn);
        assertEquals(columnHeader, relationGrid.getModel().getColumns().get(1).getHeaderMetaData().get(0).getTitle());
    }

    @Test
    public void testInitialiseUiModel() throws Exception {
        relation.getColumn().add(new InformationItem() {{
            getName().setValue("first column header");
        }});
        final String firstRowValue = "first column value 1";
        final String secondRowValue = "first column value 2";
        relation.getRow().add(new List() {{
            getExpression().add(new LiteralExpression() {{
                setText(firstRowValue);
            }});
        }});
        relation.getRow().add(new List() {{
            getExpression().add(new LiteralExpression() {{
                setText(secondRowValue);
            }});
        }});

        setupGrid(0);

        assertEquals(2, relationGrid.getModel().getRowCount());
        assertEquals(firstRowValue, relationGrid.getModel().getRow(0).getCells().get(1).getValue().getValue());
        assertEquals(secondRowValue, relationGrid.getModel().getRow(1).getCells().get(1).getValue().getValue());
    }

    @Test
    public void testHeaderVisibilityWhenNested() {
        setupGrid(1);

        assertFalse(relationGrid.isHeaderHidden());
    }

    @Test
    public void testHeaderVisibilityWhenNotNested() {
        setupGrid(0);

        assertFalse(relationGrid.isHeaderHidden());
    }

    @Test
    public void testGetItems() {
        setupGrid(0);

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = relationGrid.getItems(0, 0);

        assertThat(items.size()).isEqualTo(7);
        assertListSelectorItem(items.get(INSERT_COLUMN_BEFORE),
                               DMNEditorConstants.RelationEditor_InsertColumnBefore);
        assertListSelectorItem(items.get(INSERT_COLUMN_AFTER),
                               DMNEditorConstants.RelationEditor_InsertColumnAfter);
        assertListSelectorItem(items.get(DELETE_COLUMN),
                               DMNEditorConstants.RelationEditor_DeleteColumn);
        assertThat(items.get(DIVIDER)).isInstanceOf(HasListSelectorControl.ListSelectorDividerItem.class);
        assertListSelectorItem(items.get(INSERT_ROW_ABOVE),
                               DMNEditorConstants.RelationEditor_InsertRowAbove);
        assertListSelectorItem(items.get(INSERT_ROW_BELOW),
                               DMNEditorConstants.RelationEditor_InsertRowBelow);
        assertListSelectorItem(items.get(DELETE_ROW),
                               DMNEditorConstants.RelationEditor_DeleteRow);
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

        relationGrid.onItemSelected(listSelectorItem);

        verify(command).execute();
    }

    @Test
    public void testOnItemSelectedInsertColumnBefore() {
        setupGrid(0);

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = relationGrid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_COLUMN_BEFORE);

        relationGrid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(relationGrid).addColumn(eq(0));
    }

    @Test
    public void testOnItemSelectedInsertColumnAfter() {
        setupGrid(0);

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = relationGrid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_COLUMN_AFTER);

        relationGrid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(relationGrid).addColumn(eq(1));
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
        final java.util.List<HasListSelectorControl.ListSelectorItem> items = relationGrid.getItems(0, RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DELETE_COLUMN);

        relationGrid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(relationGrid).deleteColumn(eq(1));
    }

    @Test
    public void testOnItemSelectedDeleteColumnEnabled() {
        setupGrid(0);

        //Grid has one Relation column that cannot be deleted.
        relationGrid.getModel().appendColumn(mock(RelationColumn.class));
        assertListSelectorItemEnabled(0, 0, DELETE_COLUMN, false);
        assertListSelectorItemEnabled(0, 1, DELETE_COLUMN, false);

        //Grid has two Relation columns. Columns 1 and 2 can be deleted.
        relationGrid.getModel().appendColumn(mock(RelationColumn.class));
        assertListSelectorItemEnabled(0, 0, DELETE_COLUMN, false);
        assertListSelectorItemEnabled(0, 1, DELETE_COLUMN, true);
        assertListSelectorItemEnabled(0, 2, DELETE_COLUMN, true);
    }

    @Test
    public void testOnItemSelectedInsertRowAbove() {
        setupGrid(0);

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = relationGrid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_ROW_ABOVE);

        relationGrid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(relationGrid).addRow(eq(0));
    }

    @Test
    public void testOnItemSelectedInsertRowBelow() {
        setupGrid(0);

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = relationGrid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_ROW_BELOW);

        relationGrid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(relationGrid).addRow(eq(1));
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

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = relationGrid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(DELETE_ROW);

        relationGrid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(relationGrid).deleteRow(eq(0));
    }

    @Test
    public void testOnItemSelectedDeleteRowEnabled() {
        setupGrid(0);

        //Grid has one row that cannot be deleted.
        relationGrid.getModel().appendRow(new BaseGridRow());
        assertListSelectorItemEnabled(0, 0, DELETE_ROW, false);

        //Grid has two rows. Rows 1 and 2 can be deleted.
        relationGrid.getModel().appendRow(new BaseGridRow());
        assertListSelectorItemEnabled(0, 0, DELETE_ROW, true);
        assertListSelectorItemEnabled(1, 0, DELETE_ROW, true);
    }

    private void assertListSelectorItemEnabled(final int uiRowIndex,
                                               final int uiColumnIndex,
                                               final int listItemIndex,
                                               final boolean enabled) {
        final java.util.List<HasListSelectorControl.ListSelectorItem> items = relationGrid.getItems(uiRowIndex, uiColumnIndex);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(listItemIndex);
        assertThat(ti.isEnabled()).isEqualTo(enabled);
    }

    @Test
    public void testAddColumn() throws Exception {
        setupGrid(0);

        relationGrid.addColumn(0);

        verify(sessionCommandManager).execute(eq(abstractCanvasHandler), addColumnCommand.capture());

        addColumnCommand.getValue().execute(abstractCanvasHandler);
        verify(parent).proposeContainingColumnWidth(relationGrid.getWidth() + relationGrid.getPadding() * 2);
        verify(parentGridColumn).setWidth(relationGrid.getWidth() + relationGrid.getPadding() * 2);
        verify(gridLayer).batch(any(GridLayerRedrawManager.PrioritizedCommand.class));
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
    }

    @Test
    public void testDeleteColumn() throws Exception {
        relation.getColumn().add(new InformationItem());
        setupGrid(0);

        //Cannot delete column 0 since it is the RowNumber column. The first Relation column is 1.
        relationGrid.deleteColumn(RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        verify(sessionCommandManager).execute(eq(abstractCanvasHandler), deleteColumnCommand.capture());

        deleteColumnCommand.getValue().execute(abstractCanvasHandler);
        verify(parent).proposeContainingColumnWidth(relationGrid.getWidth() + relationGrid.getPadding() * 2);
        verify(parentGridColumn).setWidth(relationGrid.getWidth() + relationGrid.getPadding() * 2);
        verify(gridLayer).batch(any(GridLayerRedrawManager.PrioritizedCommand.class));
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
    }

    @Test
    public void testAddRow() throws Exception {
        setupGrid(0);

        relationGrid.addRow(0);

        verify(sessionCommandManager).execute(eq(abstractCanvasHandler), addRowCommand.capture());

        addRowCommand.getValue().execute(abstractCanvasHandler);
        verify(gridLayer).batch(any(GridLayerRedrawManager.PrioritizedCommand.class));
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
    }

    @Test
    public void testDeleteRow() throws Exception {
        relation.getRow().add(new List());
        setupGrid(0);

        relationGrid.deleteRow(0);

        verify(sessionCommandManager).execute(eq(abstractCanvasHandler), deleteRowCommand.capture());

        deleteRowCommand.getValue().execute(abstractCanvasHandler);
        verify(gridLayer).batch(any(GridLayerRedrawManager.PrioritizedCommand.class));
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
    }
}
