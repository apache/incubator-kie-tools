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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutInstance;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.layout.editor.client.infra.RowSizeBuilder;

public abstract class AbstractLayoutGenerator implements LayoutGenerator {

    @Override
    public LayoutInstance build(LayoutTemplate layoutTemplate) {
        HTMLElement container = createContainer(layoutTemplate);
        container.getClassList().add("uf-perspective-container");
        container.getClassList().add("uf-perspective-rendered-container");

        LayoutInstance layoutInstance = new LayoutInstance(container);
        List<LayoutRow> rows = layoutTemplate.getRows();
        generateRows(layoutTemplate, layoutInstance, rows, container);
        return layoutInstance;
    }

    private void generateRows(LayoutTemplate layoutTemplate,
                              LayoutInstance layoutInstance,
                              List<LayoutRow> rows,
                              HTMLElement parentWidget) {
        for (LayoutRow layoutRow : rows) {
            HTMLElement row = createRow(layoutRow);

            if (layoutTemplate.isPageStyle()) {
                row.getClassList().add(RowSizeBuilder.buildRowSize(layoutRow.getHeight()));
                row.getClassList().add("uf-le-overflow");
            }
            for (LayoutColumn layoutColumn : layoutRow.getLayoutColumns()) {
                HTMLElement column = createColumn(layoutColumn);

                if (layoutTemplate.isPageStyle() && layoutColumn.getHeight().isEmpty()) {
                    column.getClassList().add("uf-perspective-col");
                }
                if (columnHasNestedRows(layoutColumn)) {
                    if (layoutTemplate.isPageStyle() && layoutColumn.getHeight().isEmpty()) {
                        column.getClassList().add("uf-perspective-col");
                    } else if (!layoutColumn.getHeight().isEmpty()) {
                        column.getClassList().add("uf-perspective-row-" + layoutColumn.getHeight());
                    }
                    generateRows(layoutTemplate,
                                 layoutInstance,
                                 layoutColumn.getRows(),
                                 column);
                } else {
                    generateComponents(layoutTemplate,
                                       layoutInstance,
                                       layoutColumn,
                                       column);
                }
                column.getClassList().add("uf-perspective-rendered-col");
                row.appendChild(column);
            }
            row.getClassList().add("uf-perspective-rendered-row");
            parentWidget.appendChild(row);
        }
    }

    private void generateComponents(LayoutTemplate layoutTemplate,
                                    final LayoutInstance layoutInstance,
                                    final LayoutColumn layoutColumn,
                                    final HTMLElement column) {
        for (final LayoutComponent layoutComponent : layoutColumn.getLayoutComponents()) {
            final LayoutDragComponent dragComponent = lookupLayoutDragComponent(layoutComponent);
            if (dragComponent != null) {
                Widget columnWidget = ElementWrapperWidget.getWidget(column);
                RenderingContext componentContext = new RenderingContext(layoutComponent, columnWidget);
                IsWidget componentWidget = dragComponent.getShowWidget(componentContext);

                if (layoutTemplate.isPageStyle() && layoutColumn.getHeight().isEmpty()) {
                    componentWidget.asWidget().getElement().addClassName("uf-perspective-col");
                }
                else if (!layoutColumn.getHeight().isEmpty()) {
                    column.getClassList().add("uf-perspective-row-" + layoutColumn.getHeight());
                }
                if (componentWidget != null) {
                    DOMUtil.appendWidgetToElement(column, componentWidget);
                }
            }
        }
    }

    private boolean columnHasNestedRows(LayoutColumn layoutColumn) {
        return layoutColumn.getRows() != null && !layoutColumn.getRows().isEmpty();
    }

    // Abstract methods

    protected abstract LayoutDragComponent lookupLayoutDragComponent(LayoutComponent layoutComponent);

    protected abstract HTMLElement createContainer(LayoutTemplate layoutTemplate);

    protected abstract HTMLElement createRow(LayoutRow layoutRow);

    protected abstract HTMLElement createColumn(LayoutColumn layoutColumn);
}
