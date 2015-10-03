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

package org.uberfire.ext.layout.editor.client.generator;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Row;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.RenderingContext;
import org.uberfire.ext.layout.editor.client.row.RowView;
import org.uberfire.ext.layout.editor.client.util.DragTypeBeanResolver;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * A bootstrap based layout generator
 */
@ApplicationScoped
public class BootstrapLayoutGenerator implements LayoutGenerator {


    @Inject
    private DragTypeBeanResolver dragTypeBeanResolver;

    @Override
    public Container build(LayoutTemplate layoutTemplate) {
        Container mainPanel = new Container();
        mainPanel.getElement().setId( "mainContainer" );
        List<LayoutRow> rows = layoutTemplate.getRows();
        generateRows(rows, mainPanel);
        return mainPanel;
    }

    private void generateRows(List<LayoutRow> rows, ComplexPanel parentWidget) {
        for ( LayoutRow layoutRow : rows ) {
            Row row = new Row();
            for ( LayoutColumn layoutColumn : layoutRow.getLayoutColumns() ) {
                Column column = new Column( RowView.buildColumnSize(new Integer( layoutColumn.getSpan() )) );
                if ( columnHasNestedRows(layoutColumn) ) {
                    generateRows(layoutColumn.getRows(), column);
                } else {
                    generateComponents(layoutColumn, column );
                }
                row.add( column );
            }
            parentWidget.add( row );
        }
    }

    private void generateComponents(final LayoutColumn layoutColumn, final Column column) {
        for (final LayoutComponent layoutComponent : layoutColumn.getLayoutComponents() ) {

            final LayoutDragComponent dragComponent = dragTypeBeanResolver.lookupDragTypeBean(layoutComponent.getDragTypeName());
            if (dragComponent != null) {
                RenderingContext componentContext = new RenderingContext(layoutComponent, column);
                IsWidget componentWidget = dragComponent.getShowWidget(componentContext);
                if (componentWidget != null) {
                    column.add(componentWidget);
                }
            }
        }
    }

    private boolean columnHasNestedRows( LayoutColumn layoutColumn) {
        return layoutColumn.getRows() != null && !layoutColumn.getRows().isEmpty();
    }
}
