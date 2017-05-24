/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.layout.editor.client.generator;

import java.util.List;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Row;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.layout.editor.client.infra.ColumnSizeBuilder;
import org.uberfire.ext.layout.editor.client.infra.RowSizeBuilder;

public abstract class AbstractLayoutGenerator implements LayoutGenerator {

    @Override
    public Panel build(LayoutTemplate layoutTemplate) {
        ComplexPanel container = getLayoutContainer();
        List<LayoutRow> rows = layoutTemplate.getRows();
        generateRows(layoutTemplate,
                     rows,
                     container);
        return container;
    }

    private void generateRows(LayoutTemplate layoutTemplate,
                              List<LayoutRow> rows,
                              ComplexPanel parentWidget) {
        for (LayoutRow layoutRow : rows) {
            Row row = new Row();
            if (layoutTemplate.isPageLayout()) {
                row.getElement().addClassName(RowSizeBuilder.buildRowSize(layoutRow.getHeight()));
            }
            for (LayoutColumn layoutColumn : layoutRow.getLayoutColumns()) {
                Column column = new Column(
                        ColumnSizeBuilder.buildColumnSize(new Integer(layoutColumn.getSpan())));
                if (layoutTemplate.isPageLayout() && layoutColumn.getHeight().isEmpty()) {
                    column.getElement().addClassName("uf-perspective-col");
                }
                if (columnHasNestedRows(layoutColumn)) {
                    if (layoutTemplate.isPageLayout() && layoutColumn.getHeight().isEmpty()) {
                        column.asWidget().getElement().addClassName("uf-perspective-col");
                    } else if (!layoutColumn.getHeight().isEmpty()) {
                        column.getElement().addClassName("uf-perspective-row-" + layoutColumn.getHeight());
                    }
                    generateRows(layoutTemplate,
                                 layoutColumn.getRows(),
                                 column);
                } else {
                    generateComponents(layoutTemplate,
                                       layoutColumn,
                                       column);
                }
                column.getElement().addClassName("uf-perspective-rendered-col");
                row.add(column);
            }
            row.getElement().addClassName("uf-perspective-rendered-row");
            parentWidget.add(row);
        }
    }

    private void generateComponents(LayoutTemplate layoutTemplate,
                                    final LayoutColumn layoutColumn,
                                    final Column column) {
        for (final LayoutComponent layoutComponent : layoutColumn.getLayoutComponents()) {
            final LayoutDragComponent dragComponent = getLayoutDragComponent(layoutComponent);
            if (dragComponent != null) {
                RenderingContext componentContext = new RenderingContext(layoutComponent,
                                                                         column);
                IsWidget componentWidget = dragComponent.getShowWidget(componentContext);
                if (layoutTemplate.isPageLayout() && layoutColumn.getHeight().isEmpty()) {
                    componentWidget.asWidget().getElement().addClassName("uf-perspective-col");
                } else if (!layoutColumn.getHeight().isEmpty()) {
                    column.getElement().addClassName("uf-perspective-row-" + layoutColumn.getHeight());
                }
                if (componentWidget != null) {
                    column.add(componentWidget);
                }
            }
        }
    }

    private boolean columnHasNestedRows(LayoutColumn layoutColumn) {
        return layoutColumn.getRows() != null && !layoutColumn.getRows().isEmpty();
    }

    public abstract ComplexPanel getLayoutContainer();

    public abstract LayoutDragComponent getLayoutDragComponent(LayoutComponent layoutComponent);
}
