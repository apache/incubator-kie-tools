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

package org.uberfire.ext.layout.editor.client.components.container;

import java.util.function.Supplier;

import org.junit.Test;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.AbstractLayoutEditorTest;
import org.uberfire.ext.layout.editor.client.api.ComponentDropEvent;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent;
import org.uberfire.ext.layout.editor.client.components.rows.RowDrop;
import org.uberfire.ext.layout.editor.client.infra.RowResizeEvent;
import org.uberfire.mvp.ParameterizedCommand;

import static org.jgroups.util.Util.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ContainerTest extends AbstractLayoutEditorTest {

    @Test
    public void assertEmptyContainerHasEmptyDropRow() {
        container.loadEmptyLayout("layout",
                                  LayoutTemplate.Style.FLUID,
                                  "",
                                  "");
        assertTrue(container.getRows().isEmpty());
        assertNotNull(container.getEmptyDropRow());
        verify(view).addEmptyRow(emptyDropRow.getView());
    }

    @Test
    public void createFirstRowFluid() {
        container.loadEmptyLayout("layout",
                                  LayoutTemplate.Style.FLUID,
                                  "",
                                  "");
        assertEquals(0,
                     getRowsSizeFromContainer());
        assertNotNull(container.getEmptyDropRow());
        verify(view).addEmptyRow(emptyDropRow.getView());

        container.createEmptyDropCommand()
                .execute(new RowDrop(new LayoutComponent("dragType"),
                                     emptyDropRow.getId(),
                                     RowDrop.Orientation.BEFORE));
        assertEquals(1,
                     getRowsSizeFromContainer());
        verify(componentDropEventMock,
               times(1)).fire(any(ComponentDropEvent.class));
    }

    @Test
    public void createRowsPage() {
        container.loadEmptyLayout("layout",
                                  LayoutTemplate.Style.PAGE,
                                  "",
                                  "");
        assertEquals(0,
                     getRowsSizeFromContainer());
        assertNotNull(container.getEmptyDropRow());
        verify(view).addEmptyRow(emptyDropRow.getView());

        container.createEmptyDropCommand()
                .execute(new RowDrop(new LayoutComponent("dragType"),
                                     emptyDropRow.getId(),
                                     RowDrop.Orientation.BEFORE));
        assertEquals(1,
                     getRowsSizeFromContainer());
        assertEquals(Row.ROW_DEFAULT_HEIGHT,
                     getRowByIndex(0).getHeight());
        verify(componentDropEventMock,
               times(1)).fire(any(ComponentDropEvent.class));

        Row row0 = getRowByIndex(0);

        row0.drop("dragType",
                  RowDrop.Orientation.AFTER);

        assertEquals(Row.ROW_DEFAULT_HEIGHT / 2,
                     getRowByIndex(0).getHeight());
        assertEquals(Row.ROW_DEFAULT_HEIGHT / 2,
                     getRowByIndex(1).getHeight());

        container.resizeRows(new RowResizeEvent(container.hashCode(),
                row0.hashCode()).down());

        assertEquals(Row.ROW_DEFAULT_HEIGHT / 2 + 1,
                     getRowByIndex(0).getHeight());
        assertEquals(Row.ROW_DEFAULT_HEIGHT / 2 - 1,
                     getRowByIndex(1).getHeight());

        ComponentColumn col = (ComponentColumn) getColumnByIndex(getRowByIndex(1),
                                                                 0);
        col.remove();

        assertEquals(Row.ROW_DEFAULT_HEIGHT,
                     getRowByIndex(0).getHeight());
    }

    @Test
    public void loadEmptyTemplateClearEmptyRow() throws Exception {
        reset(container.getView());
        LayoutTemplate layoutTemplate = new LayoutTemplate("layout");
        container.load(layoutTemplate, "title", "subtitle");
        assertEquals(container.getLayoutName(), "layout");
        assertEquals(container.getRows().size(), 0);
        assertNotNull(container.getEmptyDropRow());
        verify(container.getView()).clear();
        verify(container.getView()).addEmptyRow(container.getEmptyDropRow().getView());

        reset(container.getView());
        container.load(layoutTemplate, "", "");
        assertEquals(container.getRows().size(), 0);
        assertNotNull(container.getEmptyDropRow());
        verify(container.getView()).clear();
        verify(container.getView()).addEmptyRow(container.getEmptyDropRow().getView());
    }

    @Test
    public void loadEmptyLayoutClearEmptyRow() throws Exception {
        reset(container.getView());
        container.loadEmptyLayout("layout", LayoutTemplate.Style.FLUID, "title", "subtitle");
        assertEquals(container.getLayoutName(), "layout");
        assertEquals(container.getRows().size(), 0);
        assertNotNull(container.getEmptyDropRow());
        verify(container.getView()).clear();
        verify(container.getView()).addEmptyRow(container.getEmptyDropRow().getView());

        reset(container.getView());
        container.loadEmptyLayout("layout", LayoutTemplate.Style.FLUID, "title", "subtitle");
        assertEquals(container.getRows().size(), 0);
        assertNotNull(container.getEmptyDropRow());
        verify(container.getView()).clear();
        verify(container.getView()).addEmptyRow(container.getEmptyDropRow().getView());
    }

    @Test
    public void loadNonEmptyTemplateClearExistingRows() throws Exception {
        LayoutTemplate layoutTemplate = getLayoutFromFileTemplate(SAMPLE_FULL_FLUID_LAYOUT);
        container.load(layoutTemplate, "title", "subtitle");
        assertEquals(container.getRows().size(), 4);

        container.load(layoutTemplate, "", "");
        assertEquals(container.getRows().size(), 4);
    }

    @Test
    public void loadAndExportFluidLayout() throws Exception {

        LayoutTemplate expected = loadLayout(SAMPLE_FULL_FLUID_LAYOUT);

        LayoutTemplate actual = container.toLayoutTemplate();
        assertEquals(expected,
                     actual);
        assertEquals(convertLayoutToString(expected),
                     convertLayoutToString(actual));
    }

    @Test
    public void loadAndExportPageLayout() throws Exception {

        LayoutTemplate expected = loadLayout(SAMPLE_FULL_PAGE_LAYOUT);

        LayoutTemplate actual = container.toLayoutTemplate();

        assertEquals(expected,
                     actual);
        assertEquals(convertLayoutToString(expected),
                     convertLayoutToString(actual));
    }

    @Test
    public void dropBeforeComponentShouldCreateANewRow() throws Exception {

        loadLayout(SINGLE_ROW_COMPONENT_LAYOUT);

        Row dropRow = getRowByIndex(FIRST_ROW);

        RowDrop dropNewComponentOnFirstRow = new RowDrop(new LayoutComponent("dragType"),
                                                         dropRow.getId(),
                                                         RowDrop.Orientation.BEFORE);
        dropNewComponentOnFirstRow.newComponent();

        container.createRowDropCommand().execute(dropNewComponentOnFirstRow);

        assertEquals(2,
                     getRowsSizeFromContainer());

        Column droppedColumn = getColumnByIndex(getRowByIndex(FIRST_ROW),
                                                FIRST_COLUMN);
        assertEquals("dragType",
                     droppedColumn.getLayoutComponent()
                             .getDragTypeName());

        assertEquals(dropRow,
                     getRowByIndex(SECOND_ROW));

        verify(componentDropEventMock,
               times(1)).fire(any(ComponentDropEvent.class));
    }

    @Test
    public void moveComponentShouldRemoveComponentFromCurrentRow() throws Exception {
        loadLayout(SINGLE_ROW_TWO_COMPONENTS_LAYOUT);

        Row dropRow = getRowByIndex(FIRST_ROW);

        RowDrop moveComponentAndDropInFirstRow = new RowDrop(new LayoutComponent("dragType"),
                                                             dropRow.getId(),
                                                             RowDrop.Orientation.BEFORE);
        moveComponentAndDropInFirstRow.fromMove(dropRow.getId(),
                                                getColumns(dropRow).get(0));

        container.createRowDropCommand().execute(moveComponentAndDropInFirstRow);

        assertEquals(2,
                     getRowsSizeFromContainer());
        assertEquals(1,
                     getColumns(getRowByIndex(FIRST_ROW)).size());
        assertEquals(1,
                     getColumns(getRowByIndex(SECOND_ROW)).size());

        verify(componentDropEventMock,
               times(1)).fire(any(ComponentDropEvent.class));
        verify(componentRemoveEventMock,
               times(1)).fire(any(ComponentRemovedEvent.class));
    }

    @Test
    public void swapRows() throws Exception {
        loadLayout(SAMPLE_FULL_FLUID_LAYOUT);

        Row row1 = getRowByIndex(FIRST_ROW);
        Row row2 = getRowByIndex(SECOND_ROW);

        container.swapRows(new RowDnDEvent(row1.getId(),
                                           row2.getId(),
                                           RowDrop.Orientation.AFTER));

        assertEquals(row2,
                     getRowByIndex(FIRST_ROW));
        assertEquals(row1,
                     getRowByIndex(SECOND_ROW));
    }

    @Test
    public void dropAfterComponentShouldCreateANewRow() throws Exception {
        loadLayout(SINGLE_ROW_COMPONENT_LAYOUT);

        Row dropRow = getRowByIndex(0);

        ParameterizedCommand<RowDrop> rowDropCommand = container.createRowDropCommand();
        RowDrop drop = new RowDrop(new LayoutComponent("dragType"),
                                   dropRow.getId(),
                                   RowDrop.Orientation.AFTER);
        drop.newComponent();
        rowDropCommand.execute(drop);

        assertEquals(2,
                     getRowsSizeFromContainer());
        assertEquals(dropRow,
                     getRowByIndex(FIRST_ROW));
        verify(lockRequiredEventMock, times(1)).fire(any(LockRequiredEvent.class));
        verify(componentDropEventMock,
               times(1)).fire(any(ComponentDropEvent.class));
    }

    @Test
    public void removeSingleComponentFromRowShouldRemoveRow() throws Exception {

        loadLayout(SINGLE_ROW_COMPONENT_LAYOUT);

        assertFalse(container.getRows().isEmpty());

        Row row = getRowByIndex(FIRST_ROW);
        ComponentColumn column = (ComponentColumn) getColumns(row).get(0);

        column.remove();

        assertTrue(container.getRows().isEmpty());
    }

    @Test
    public void addGetPropertyTest() throws Exception {
        assertNull(container.getProperty("key"));
        container.addProperty("key",
                              "value");
        assertNotNull(container.getProperty("key"));
        assertTrue(container.getProperties().containsKey("key"));
    }

    @Test
    public void createCurrentLayoutTemplateSupplierTest() throws Exception {
        LayoutTemplate expected = loadLayout(SAMPLE_FULL_FLUID_LAYOUT);

        LayoutTemplate toLayoutTemplate = container.toLayoutTemplate();
        Supplier<LayoutTemplate> currentLayoutTemplateSupplier = container.createCurrentLayoutTemplateSupplier();

        assertEquals(expected,
                     toLayoutTemplate);
        assertEquals(expected,
                     currentLayoutTemplateSupplier.get());
    }

    @Test
    public void testLockSupplier() {
        container.setLockSupplier(() -> false);
        assertEquals(Boolean.FALSE, container.getLockSupplier().get());
    }
}