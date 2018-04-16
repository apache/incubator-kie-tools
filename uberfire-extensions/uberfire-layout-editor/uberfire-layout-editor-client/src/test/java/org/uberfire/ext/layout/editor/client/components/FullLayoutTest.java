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

package org.uberfire.ext.layout.editor.client.components;

import org.junit.Test;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.AbstractLayoutEditorTest;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponents;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.components.rows.RowDrop;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.ColumnResizeEvent;
import org.uberfire.ext.layout.editor.client.infra.RowResizeEvent;
import org.uberfire.ext.plugin.type.TagsConverterUtil;

import static org.junit.Assert.*;

public class FullLayoutTest extends AbstractLayoutEditorTest {

    @Test
    public void testFullLayoutFluid() throws Exception {

        container.loadEmptyLayout("A",
                                  LayoutTemplate.Style.FLUID,
                                  "title",
                                  "subtitle");
        container.addProperty(TagsConverterUtil.LAYOUT_PROPERTY,
                              "a|");

        createFirstRow();
        createSecondRow();

        LayoutTemplate layoutTemplate = container.toLayoutTemplate();
        assertEquals(convertLayoutToString(loadLayout(FULL_LAYOUT_FLUID)),
                     convertLayoutToString(layoutTemplate));
    }

    @Test
    public void testFullLayoutPage() throws Exception {

        container.loadEmptyLayout("A",
                                  LayoutTemplate.Style.PAGE,
                                  "title",
                                  "subtitle");
        container.addProperty(TagsConverterUtil.LAYOUT_PROPERTY,
                              "a|");

        createFirstRow();
        createSecondRow();

        resizeSecondRow();
        resizeSecondRow();

        LayoutTemplate layoutTemplate = container.toLayoutTemplate();
        assertEquals(convertLayoutToString(loadLayout(FULL_LAYOUT_PAGE)),
                     convertLayoutToString(layoutTemplate));
    }

    private void resizeSecondRow() {
        Row firstRow = getRowByIndex(0);

        RowResizeEvent resize = new RowResizeEvent(container.hashCode(),
                firstRow.hashCode()).down();
        container.resizeRows(resize);
    }

    private void createSecondRow() {
        Row firstRow = getRowByIndex(0);
        container.createRowDropCommand().execute(new RowDrop(new LayoutComponent(
                "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.ScreenLayoutDragComponent"),
                                                             firstRow.getId(),
                                                             RowDrop.Orientation.AFTER));
        Column column = getColumnByIndex(getRowByIndex(1),
                                         FIRST_COLUMN);
        column.getLayoutComponent().addProperty("Place Name",
                                                "DoraScreen");
    }

    private void createFirstRow() {
        Column firstRowFirstColumn = createFirstRowFirstColumn();
        createFirstRowSecondColumn(firstRowFirstColumn);
    }

    private void dropTwoInnerColumnsInSecondRow() {
        Row row = getRowByIndex(FIRST_ROW);
        Column secondColumn = getColumnByIndex(row,
                                               1);

        row.dropCommand().execute(new ColumnDrop(new LayoutComponent(
                "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.ScreenLayoutDragComponent"),
                                                 secondColumn.getId(),
                                                 ColumnDrop.Orientation.DOWN));

        ColumnWithComponents columnWithComponents = (ColumnWithComponents) getColumnByIndex(row,
                                                                                            SECOND_COLUMN);

        assertNotNull(columnWithComponents.getRow().getParentColumnWithComponents());
        Column newColumn = getColumnByIndex(columnWithComponents.getRow(),
                                            1);
        newColumn.getLayoutComponent().addProperty("c",
                                                   "c");
        newColumn.getLayoutComponent().addProperty("Place Name",
                                                   "AnotherScreen");
    }

    private void resizeColumnsFor_8_4() {
        Row row = getRowByIndex(FIRST_ROW);

        row.resizeColumns(new ColumnResizeEvent(getColumnByIndex(row,
                                                                 0).hashCode(),
                                                row.hashCode()).right());
        row.resizeColumns(new ColumnResizeEvent(getColumnByIndex(row,
                                                                 0).hashCode(),
                                                row.hashCode()).right());
    }

    private void createFirstRowSecondColumn(Column firstRowFirstColumn) {
        Row row = getRowByIndex(FIRST_ROW);

        row.dropCommand().execute(new ColumnDrop(
                new LayoutComponent("org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTML"),
                firstRowFirstColumn.getId(),
                ColumnDrop.Orientation.RIGHT));
        Column htmlColumn = getColumnByIndex(row,
                                             SECOND_COLUMN);
        htmlColumn.getLayoutComponent().addProperty("HTML_CODE",
                                                    "c");

        resizeColumnsFor_8_4();
        dropTwoInnerColumnsInSecondRow();
    }

    private Column createFirstRowFirstColumn() {
        container.createEmptyDropCommand()
                .execute(new RowDrop(new LayoutComponent(
                        "org.uberfire.ext.plugin.client.perspective.editor.layout.editor.ScreenLayoutDragComponent"),
                                     emptyDropRow.getId(),
                                     RowDrop.Orientation.BEFORE));

        Column appHomePresenter = getColumnByIndex(getRowByIndex(FIRST_COLUMN),
                                                   FIRST_COLUMN);
        appHomePresenter.getLayoutComponent().addProperty("Place Name",
                                                          "AppsHomePresenter");
        return appHomePresenter;
    }
}
