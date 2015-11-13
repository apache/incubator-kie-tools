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
package org.uberfire.ext.wires.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;
import org.uberfire.paging.AbstractPageRow;

@Dependent
@WorkbenchScreen( identifier = "PagedTableScreen" )
public class PagedTableScreen extends Composite implements RequiresResize {

    private static final int PADDING = 30;

    protected final PagedTable<Row> dataGrid = new PagedTable<Row>( 10, null, null, true, true, true );
    protected final FlowPanel panel = new FlowPanel();
    protected final Button addButton = new Button();
    protected final List<Row> data = new ArrayList<Row>();
    protected final AsyncDataProvider<Row> dataProvider = new AsyncDataProvider<Row>() {
        @Override
        protected void onRangeChanged( final HasData<Row> display ) {
            final ColumnSortList columnSortList = dataGrid.getColumnSortList();
            Collections.sort( data, new Comparator<Row>() {
                @Override
                public int compare( final Row o1, final Row o2 ) {
                    if ( columnSortList == null || columnSortList.size() == 0 || columnSortList.get( 0 ).isAscending() == false ) {
                        return o1.getName().compareTo( o2.getName() );
                    } else {
                        return o2.getName().compareTo( o1.getName() );
                    }
                }
            } );
            Scheduler.get().scheduleFixedDelay( new Scheduler.RepeatingCommand() {
                @Override
                public boolean execute() {
                    updateRowCount( data.size(), true );
                    updateRowData( 0, data );
                    return false;
                }
            }, 1000 );

        }
    };

    @PostConstruct
    public void init() {
        dataGrid.setEmptyTableCaption( "No data" );

        final Column<Row, String> nameColumn = new Column<Row, String>( new TextCell() ) {
            @Override
            public String getValue( Row row ) {
                return row.getName();
            }
        };
        final Column<Row, String> descColumn = new Column<Row, String>( new TextCell() ) {
            @Override
            public String getValue( Row row ) {
                return row.getDescription();
            }
        };
        nameColumn.setSortable( true );
        dataGrid.addColumn( nameColumn, "Name" );
        dataGrid.addColumn( descColumn, "Description" );

        addButton.setText( "New Row" );
        addButton.setIcon( IconType.PLUS );
        addButton.getElement().getStyle().setMarginLeft( 10, Style.Unit.PX );
        addButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                data.add( new Row( data.size() ) );
                dataGrid.refresh();
            }
        } );
        dataProvider.addDataDisplay( dataGrid );

        dataGrid.addColumnSortHandler( new ColumnSortEvent.AsyncHandler( dataGrid ) );

        panel.add( dataGrid );
        panel.add( addButton );
        initWidget( panel );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Paged Table Screen";
    }

    class Row extends AbstractPageRow {

        private String name;

        public Row( int index ) {
            this.name = "Row " + index;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return "Description for " + getName();
        }

        @Override
        public int compareTo( AbstractPageRow o ) {
            return getName().compareTo( ( (Row) o ).getName() );
        }
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight() - PADDING;
        int width = getParent().getOffsetWidth() - PADDING;
        setPixelSize( width, height );
    }

}
