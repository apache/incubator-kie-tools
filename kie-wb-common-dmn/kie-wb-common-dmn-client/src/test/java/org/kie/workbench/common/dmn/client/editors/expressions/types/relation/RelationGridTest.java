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
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.List;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.AddRelationColumnCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.AddRelationRowCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.DeleteRelationColumnCommand;
import org.kie.workbench.common.dmn.client.commands.expressions.types.relation.DeleteRelationRowCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.DeleteHeaderValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetHeaderValueCommand;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.session.DMNClientFullSession;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
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

    private static final String NODE_UUID = "uuid";

    private GridCellTuple tupleWithoutValue;

    private GridCellValueTuple tupleWithValue;

    @Mock
    private GridWidget parentGridWidget;

    @Mock
    private GridData parentGridData;

    @Mock
    private GridColumn parentGridColumn;

    @Mock
    private HasExpression hasExpression;

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
    private DMNClientFullSession dmnClientFullSession;

    @Mock
    private AbstractCanvasHandler canvasHandler;

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

    @Captor
    private ArgumentCaptor<AddRelationColumnCommand> addColumnCommand;

    @Captor
    private ArgumentCaptor<DeleteRelationColumnCommand> deleteColumnCommand;

    @Captor
    private ArgumentCaptor<AddRelationRowCommand> addRowCommand;

    @Captor
    private ArgumentCaptor<DeleteRelationRowCommand> deleteRowCommand;

    private GridCellTuple parent;

    private Relation relation = new Relation();

    private Optional<Relation> expression = Optional.of(relation);

    private Optional<HasName> hasName = Optional.empty();

    private RelationEditorDefinition definition;

    private RelationGrid grid;

    @Before
    public void setUp() throws Exception {
        tupleWithoutValue = new GridCellTuple(0, 1, gridWidget);
        tupleWithValue = new GridCellValueTuple<>(0, 1, gridWidget, new BaseGridCellValue<>("value"));

        definition = new RelationEditorDefinition(gridPanel,
                                                  gridLayer,
                                                  definitionUtils,
                                                  sessionManager,
                                                  sessionCommandManager,
                                                  canvasCommandFactory,
                                                  cellEditorControls,
                                                  listSelector,
                                                  translationService);

        final Decision decision = new Decision();
        decision.setName(new Name("name"));
        hasName = Optional.of(decision);
        expression = definition.getModelClass();

        doReturn(canvasHandler).when(dmnClientFullSession).getCanvasHandler();
        doReturn(dmnClientFullSession).when(sessionManager).getCurrentSession();
        doReturn(parentGridData).when(parentGridWidget).getModel();
        doReturn(Collections.singletonList(parentGridColumn)).when(parentGridData).getColumns();

        parent = spy(new GridCellTuple(0, 0, parentGridWidget));

        when(gridWidget.getModel()).thenReturn(new BaseGridData(false));

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(anyString());
    }

    private void setupGrid(final int nesting) {
        this.grid = spy((RelationGrid) definition.getEditor(parent,
                                                            nesting == 0 ? Optional.of(NODE_UUID) : Optional.empty(),
                                                            hasExpression,
                                                            expression,
                                                            hasName,
                                                            nesting).get());
    }

    @Test
    public void testInitialiseUiColumnsEmptyModel() throws Exception {
        expression = Optional.of(new Relation());

        setupGrid(0);

        assertEquals(0,
                     grid.getModel().getRowCount());
        assertEquals(1,
                     grid.getModel().getColumns().size());
        assertTrue(grid.getModel().getColumns().get(0) instanceof RowNumberColumn);
    }

    @Test
    public void testInitialiseUiColumns() throws Exception {
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
    public void testGetItems() {
        setupGrid(0);

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);

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

        grid.onItemSelected(listSelectorItem);

        verify(command).execute();
    }

    @Test
    public void testOnItemSelectedInsertColumnBefore() {
        setupGrid(0);

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_COLUMN_BEFORE);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addColumn(eq(0));
    }

    @Test
    public void testOnItemSelectedInsertColumnAfter() {
        setupGrid(0);

        final java.util.List<HasListSelectorControl.ListSelectorItem> items = grid.getItems(0, 0);
        final HasListSelectorControl.ListSelectorTextItem ti = (HasListSelectorControl.ListSelectorTextItem) items.get(INSERT_COLUMN_AFTER);

        grid.onItemSelected(ti);

        verify(cellEditorControls).hide();
        verify(grid).addColumn(eq(1));
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

        grid.addColumn(0);

        verify(sessionCommandManager).execute(eq(canvasHandler), addColumnCommand.capture());

        addColumnCommand.getValue().execute(canvasHandler);
        verify(parent).proposeContainingColumnWidth(grid.getWidth() + grid.getPadding() * 2);
        verify(parentGridColumn).setWidth(grid.getWidth() + grid.getPadding() * 2);
        verify(gridLayer).batch(any(GridLayerRedrawManager.PrioritizedCommand.class));
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
    }

    @Test
    public void testDeleteColumn() throws Exception {
        relation.getColumn().add(new InformationItem());
        setupGrid(0);

        //Cannot delete column 0 since it is the RowNumber column. The first Relation column is 1.
        grid.deleteColumn(RelationUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        verify(sessionCommandManager).execute(eq(canvasHandler), deleteColumnCommand.capture());

        deleteColumnCommand.getValue().execute(canvasHandler);
        verify(parent).proposeContainingColumnWidth(grid.getWidth() + grid.getPadding() * 2);
        verify(parentGridColumn).setWidth(grid.getWidth() + grid.getPadding() * 2);
        verify(gridLayer).batch(any(GridLayerRedrawManager.PrioritizedCommand.class));
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
    }

    @Test
    public void testAddRow() throws Exception {
        setupGrid(0);

        grid.addRow(0);

        verify(sessionCommandManager).execute(eq(canvasHandler), addRowCommand.capture());

        addRowCommand.getValue().execute(canvasHandler);
        verify(gridLayer).batch(any(GridLayerRedrawManager.PrioritizedCommand.class));
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
    }

    @Test
    public void testDeleteRow() throws Exception {
        relation.getRow().add(new List());
        setupGrid(0);

        grid.deleteRow(0);

        verify(sessionCommandManager).execute(eq(canvasHandler), deleteRowCommand.capture());

        deleteRowCommand.getValue().execute(canvasHandler);
        verify(gridLayer).batch(any(GridLayerRedrawManager.PrioritizedCommand.class));
        verify(gridPanel).refreshScrollPosition();
        verify(gridPanel).updatePanelSize();
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
}
