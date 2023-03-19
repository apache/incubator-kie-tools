/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.infra;

import java.util.List;

import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponents;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.components.rows.Row;

public class LayoutTemplateAdapter {

    public static LayoutTemplate convert(Container container) {
        return convertToLayoutEditor(container);
    }

    private static LayoutTemplate convertToLayoutEditor(Container container) {
        LayoutTemplate layoutTemplate = new LayoutTemplate(container.getLayoutName(),
                                                           container.getProperties(),
                                                           container.getPageStyle());
        extractRows(container.getRows(),
                    layoutTemplate);
        return layoutTemplate;
    }

    private static void extractRows(List<Row> rows,
                                    LayoutTemplate layoutTemplate) {
        for (Row row : rows) {
            LayoutRow layoutRow = new LayoutRow(row.getHeight().toString(), row.getProperties());
            extractColumns(row.getColumns(),
                           layoutRow);
            layoutTemplate.addRow(layoutRow);
        }
    }

    private static void extractColumns(List<Column> columns,
                                       LayoutRow layoutRow) {
        for (Column col : columns) {
            LayoutColumn layoutColumn = new LayoutColumn(col.getColumnWidth().toString(),
                                                         col.getColumnHeight().toString(),
                                                         col.getProperties());
            if (col.hasInnerRows()) {
                extractColumnWithComponents(col,
                                            layoutColumn);
            } else {
                extractComponents(col,
                                  layoutColumn);
            }
            layoutRow.add(layoutColumn);
        }
    }

    private static void extractColumnWithComponents(Column col,
                                                    LayoutColumn layoutColumn) {
        if (col instanceof ColumnWithComponents) {
            ColumnWithComponents columnWithComponents = (ColumnWithComponents) col;
            Row row = columnWithComponents.getRow();
            LayoutRow layoutRow = new LayoutRow(Row.ROW_DEFAULT_HEIGHT.toString(), row.getProperties());
            extractColumns(columnWithComponents.getRow().getColumns(),
                           layoutRow);
            layoutColumn.addRow(layoutRow);
        }
    }

    private static void extractComponents(Column col,
                                          LayoutColumn layoutColumn) {
        extractLayoutEditorComponent(col,
                                     layoutColumn);
    }

    private static void extractLayoutEditorComponent(Column col,
                                                     LayoutColumn layoutColumn) {
        layoutColumn.add(col.getLayoutComponent());
    }
}
