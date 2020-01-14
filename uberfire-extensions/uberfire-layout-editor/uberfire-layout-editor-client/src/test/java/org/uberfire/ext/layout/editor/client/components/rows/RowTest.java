/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.components.rows;

import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.AbstractLayoutEditorTest;
import org.uberfire.ext.layout.editor.client.api.ComponentDropEvent;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponents;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.ColumnResizeEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jgroups.util.Util.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RowTest extends AbstractLayoutEditorTest {

    @Test
    public void dropOnLeftColumn() throws Exception {

        loadLayout(SINGLE_ROW_TWO_COMPONENTS_LAYOUT);

        Row row = getRowByIndex(FIRST_ROW);
        Column dropColumn = getColumnByIndex(row,
                                             FIRST_COLUMN);

        assertEquals(2,
                     row.getColumns().size());

        row.dropCommand().execute(new ColumnDrop(new LayoutComponent("dragType"),
                                                 dropColumn.getId(),
                                                 ColumnDrop.Orientation.LEFT));

        Column newColumn = getColumnByIndex(row,
                                            FIRST_COLUMN);

        assertEquals(3,
                     row.getColumns().size());
        assertEquals("dragType",
                     newColumn.getLayoutComponent().getDragTypeName());
        assertEquals(dropColumn,
                     getColumnByIndex(row,
                                      SECOND_COLUMN));
    }

    @Test
    public void dropOnRightColumn() throws Exception {

        loadLayout(SINGLE_ROW_TWO_COMPONENTS_LAYOUT);

        Row row = getRowByIndex(FIRST_ROW);
        Column dropColumn = getColumnByIndex(row,
                                             FIRST_COLUMN);

        assertEquals(2,
                     row.getColumns().size());

        row.dropCommand().execute(new ColumnDrop(new LayoutComponent("dragType"),
                                                 dropColumn.getId(),
                                                 ColumnDrop.Orientation.RIGHT));

        Column newColumn = getColumnByIndex(row,
                                            SECOND_COLUMN);

        assertEquals(3,
                     row.getColumns().size());
        assertEquals("dragType",
                     newColumn.getLayoutComponent().getDragTypeName());
        assertEquals(dropColumn,
                     getColumnByIndex(row,
                                      FIRST_COLUMN));
    }

    @Test
    public void dropAboveColumnShouldCreateColumnWithComponents() throws Exception {

        loadLayout(SINGLE_ROW_TWO_COMPONENTS_LAYOUT);

        Row row = getRowByIndex(FIRST_ROW);
        Column dropColumn = getColumnByIndex(row,
                                             FIRST_COLUMN);
        LayoutComponent originalColumnLayoutComponent = dropColumn.getLayoutComponent();

        assertEquals(2,
                     row.getColumns().size());

        row.dropCommand().execute(new ColumnDrop(new LayoutComponent("dragType"),
                                                 dropColumn.getId(),
                                                 ColumnDrop.Orientation.DOWN));

        assertEquals(2,
                     row.getColumns().size());

        ColumnWithComponents columnWithComponents = (ColumnWithComponents) getColumnByIndex(row,
                                                                                            FIRST_COLUMN);

        List<Column> childs = extractColumnsFrom(columnWithComponents);

        assertEquals(2,
                     childs.size());
        assertEquals(originalColumnLayoutComponent,
                     childs.get(FIRST_COLUMN).getLayoutComponent());
        LayoutComponent newColumnLayoutComponent = childs.get(SECOND_COLUMN).getLayoutComponent();
        assertEquals("dragType",
                     newColumnLayoutComponent.getDragTypeName());
    }

    @Test
    public void dropUpperColumnShouldCreateColumnWithComponents() throws Exception {

        loadLayout(SINGLE_ROW_TWO_COMPONENTS_LAYOUT);

        Row row = getRowByIndex(FIRST_ROW);
        Column dropColumn = getColumnByIndex(row,
                                             SECOND_COLUMN);
        LayoutComponent originalColumnLayoutComponent = dropColumn.getLayoutComponent();

        assertEquals(2,
                     row.getColumns().size());

        row.dropCommand().execute(new ColumnDrop(new LayoutComponent("dragType"),
                                                 dropColumn.getId(),
                                                 ColumnDrop.Orientation.UP));

        assertEquals(2,
                     row.getColumns().size());

        ColumnWithComponents columnWithComponents = (ColumnWithComponents) getColumnByIndex(row,
                                                                                            SECOND_COLUMN);

        List<Column> childs = extractColumnsFrom(columnWithComponents);

        assertEquals(2,
                     childs.size());
        LayoutComponent newColumnLayoutComponent = childs.get(FIRST_COLUMN).getLayoutComponent();
        assertEquals("dragType",
                     newColumnLayoutComponent.getDragTypeName());
        assertEquals(originalColumnLayoutComponent,
                     childs.get(SECOND_COLUMN).getLayoutComponent());
    }

    @Test
    public void resizeEventTest() throws Exception {

        loadLayout(SINGLE_ROW_TWO_COMPONENTS_LAYOUT);

        Row row = getRowByIndex(FIRST_ROW);
        Column first = getColumnByIndex(row,
                                        FIRST_COLUMN);
        Column second = getColumnByIndex(getRowByIndex(FIRST_ROW),
                                         SECOND_COLUMN);

        Integer originalFirstSize = first.getColumnWidth();
        Integer originalSecondSize = second.getColumnWidth();

        row.resizeColumns(new ColumnResizeEvent(second.hashCode(),
                                                row.hashCode()).left());

        assertEquals(originalFirstSize - 1,
                     first.getColumnWidth());
        assertEquals(originalSecondSize + 1,
                     second.getColumnWidth());

        row.resizeColumns(new ColumnResizeEvent(second.hashCode(),
                                                row.hashCode()).left());

        assertEquals(originalFirstSize - 2,
                     first.getColumnWidth());
        assertEquals(originalSecondSize + 2,
                     second.getColumnWidth());

        row.resizeColumns(new ColumnResizeEvent(first.hashCode(),
                                                row.hashCode()).right());

        assertEquals(originalFirstSize - 1,
                     first.getColumnWidth());
        assertEquals(originalSecondSize + 1,
                     second.getColumnWidth());
    }

    @Test
    public void testRemoveElementInColumnWithComponents() throws Exception {

        ArgumentCaptor<ComponentRemovedEvent> removeEventCaptor = ArgumentCaptor.forClass(ComponentRemovedEvent.class);

        loadLayout(SAMPLE_COLUMN_WITH_COMPONENTS_LAYOUT);

        Row row = getRowByIndex(FIRST_ROW);
        assertThat(row.getColumns()).hasSize(1);

        Column rowColumn = row.getColumns().get(0);
        assertThat(rowColumn).isNotNull().isInstanceOf(ColumnWithComponents.class);

        ColumnWithComponents columnWithComponents = (ColumnWithComponents) rowColumn;
        assertThat(columnWithComponents.getRow().getColumns()).hasSize(3);

        Column firstColumn = columnWithComponents.getRow().getColumns().get(0);
        assertThat(firstColumn).isNotNull().isInstanceOf(ComponentColumn.class);

        Column secondColumn = columnWithComponents.getRow().getColumns().get(1);
        assertThat(secondColumn).isNotNull().isInstanceOf(ComponentColumn.class);

        Column thirdColumn = columnWithComponents.getRow().getColumns().get(2);
        assertThat(thirdColumn).isNotNull().isInstanceOf(ComponentColumn.class);

        // Remove thirdColumn -> the expected result is that rowColumn will be a ColumnWithComponents with two columns (firstColumn & secondColumn)
        row.removeColumn(thirdColumn);
        rowColumn = row.getColumns().get(0);
        assertThat(rowColumn).isNotNull().isInstanceOf(ColumnWithComponents.class);

        verify(componentRemoveEventMock,
               times(1)).fire(removeEventCaptor.capture());
        assertFalse(removeEventCaptor.getValue().getFromMove());

        columnWithComponents = (ColumnWithComponents) rowColumn;
        assertThat(columnWithComponents.getRow().getColumns()).hasSize(2).contains(firstColumn,
                                                                                   secondColumn);

        // Remove firstColumn -> since rowColumn will have onlye one ComponentColumn the expected result is that
        // rowColumn will be a ComponentColumn copy of secondColumn
        row.removeColumn(firstColumn);

        verify(componentRemoveEventMock,
               times(2)).fire(removeEventCaptor.capture());
        assertFalse(removeEventCaptor.getValue().getFromMove());

        assertThat(row.getColumns()).hasSize(1);

        rowColumn = row.getColumns().get(0);

        assertThat(rowColumn).isNotNull().isInstanceOf(ComponentColumn.class);

        assertThat(rowColumn).isEqualToComparingOnlyGivenFields(secondColumn,
                                                                "columnWidth",
                                                                "columnHeight",
                                                                "layoutComponent");
    }

    @Test
    public void testIsDropInSameColumnWithComponent() throws Exception {

        loadLayout(SAMPLE_COLUMN_WITH_COMPONENTS_LAYOUT);

        Row row = getRowByIndex(FIRST_ROW);

        assertThat(row.getColumns()).hasSize(1);

        Column rowColumn = row.getColumns().get(0);
        ColumnWithComponents columnWithComponents = (ColumnWithComponents) rowColumn;
        Column firstColumn = columnWithComponents.getRow().getColumns().get(0);

        // when drop is not in the same column
        ColumnDrop columnDrop = mock(ColumnDrop.class);
        when(columnDrop.getOldColumn()).thenReturn(firstColumn);
        when(columnDrop.getEndId()).thenReturn("container: | row:1");
        assertFalse(row.isDropInSameColumnWithComponent(columnDrop));

        // when drop is in the same column
        when(columnDrop.getOldColumn()).thenReturn(firstColumn);
        when(columnDrop.getEndId()).thenReturn(firstColumn.getId());
        assertTrue(row.isDropInSameColumnWithComponent(columnDrop));
    }

    @Test
    public void moveElementInRow() throws Exception {

        loadLayout(SAMPLE_COLUMN_WITH_COMPONENTS_LAYOUT);

        Row row = getRowByIndex(FIRST_ROW);
        assertThat(row.getColumns()).hasSize(1);

        Column rowColumn = row.getColumns().get(0);
        assertThat(rowColumn).isNotNull().isInstanceOf(ColumnWithComponents.class);

        ColumnWithComponents columnWithComponents = (ColumnWithComponents) rowColumn;
        assertThat(columnWithComponents.getRow().getColumns()).hasSize(3);

        Column column = columnWithComponents.getRow().getColumns().get(0);
        assertThat(column).isNotNull().isInstanceOf(ComponentColumn.class);

        // Dragging thirdColumn
        dnDManager.dragComponent(column.getLayoutComponent(),
                                 columnWithComponents.getRow().getId(),
                                 column);
        row.removeColumn(column);
        rowColumn = row.getColumns().get(0);
        assertThat(rowColumn).isNotNull().isInstanceOf(ColumnWithComponents.class);

        ArgumentCaptor<ComponentRemovedEvent> removeEventCaptor = ArgumentCaptor.forClass(ComponentRemovedEvent.class);
        verify(componentRemoveEventMock,
               times(1)).fire(removeEventCaptor.capture());

        assertTrue(removeEventCaptor.getValue().getFromMove());
        assertTrue(dnDManager.isOnComponentMove());

        // Dropping (we don't need any dropData for this test)
        row.drop("", RowDrop.Orientation.BEFORE);
        ArgumentCaptor<ComponentDropEvent> dropEventCaptor = ArgumentCaptor.forClass(ComponentDropEvent.class);

        verify(componentDropEventMock).fire(dropEventCaptor.capture());
        assertTrue(dropEventCaptor.getValue().getFromMove());
        // after drop dnDManager is no longer on move state
        assertFalse(dnDManager.isOnComponentMove());
    }

    @Test
    public void testMoveElement() throws Exception {
        loadLayout(SAMPLE_FULL_FLUID_LAYOUT);

        Row row = getRowByIndex(FIRST_ROW);
        assertThat(row.getColumns()).hasSize(4);

        Column rowColumn = row.getColumns().get(0);
        assertThat(rowColumn).isNotNull().isInstanceOf(ComponentColumn.class);

        ComponentColumn column = (ComponentColumn) rowColumn;

        dnDManager.dragComponent(column.getLayoutComponent(),
                                 row.getId(),
                                 column);
        row.removeColumn(column);

        assertThat(row.getColumns()).hasSize(3);

        ArgumentCaptor<ComponentRemovedEvent> removeEventCaptor = ArgumentCaptor.forClass(ComponentRemovedEvent.class);
        verify(componentRemoveEventMock,
               times(1)).fire(removeEventCaptor.capture());

        assertTrue(removeEventCaptor.getValue().getFromMove());
        assertTrue(dnDManager.isOnComponentMove());

        // Dropping (we don't need any dropData for this test)
        row.drop("", RowDrop.Orientation.BEFORE);
        ArgumentCaptor<ComponentDropEvent> dropEventCaptor = ArgumentCaptor.forClass(ComponentDropEvent.class);

        verify(componentDropEventMock).fire(dropEventCaptor.capture());
        assertTrue(dropEventCaptor.getValue().getFromMove());
        // after drop dnDManager is no longer on move state
        assertFalse(dnDManager.isOnComponentMove());
    }

    @Test
    public void moveLastElementInRow() throws Exception {
        loadLayout(FULL_LAYOUT_PAGE);

        Row firstRow = getRowByIndex(FIRST_ROW);

        Row secondRow = getRowByIndex(SECOND_ROW);

        assertThat(container.getRows())
                .hasSize(2)
                .containsOnly(firstRow, secondRow);

        assertThat(secondRow.getColumns())
                .hasSize(1);

        Column droppedColumn = secondRow.getColumns().get(0);
        assertThat(droppedColumn)
                .isNotNull();

        dnDManager.dragComponent(droppedColumn.getLayoutComponent(),
                                 droppedColumn.getId(),
                                 droppedColumn);

        // Dropping secondRow BEFORE firstRow
        firstRow.drop("", RowDrop.Orientation.BEFORE);

        assertThat(container.getRows())
                .hasSize(2);

        // after the drop firstRowAfterMove must be a new row containing droppedColumn
        Row firstRowAfterMove = getRowByIndex(FIRST_ROW);

        // after the drop secondRowAfterMove must be firstRow
        Row secondRowAfterMove = getRowByIndex(SECOND_ROW);

        assertNotEquals(firstRow, firstRowAfterMove);

        assertNotEquals(secondRow, secondRowAfterMove);

        assertThat(firstRowAfterMove.getColumns())
                .hasSize(1);

        assertThat(firstRowAfterMove.getColumns().get(0).getLayoutComponent())
                .isNotNull()
                .isEqualTo(droppedColumn.getLayoutComponent());

        assertEquals(firstRow, secondRowAfterMove);

    }
}