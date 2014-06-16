/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.uberfire.client.tables;

import com.github.gwtbootstrap.client.ui.SimplePager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * Widget that shows rows of paged data.
 */
public class PagedTable<T>
        extends SimpleTable<T> {

    interface Binder
            extends
            UiBinder<Widget, PagedTable> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    private int pageSize;
    private AsyncDataProvider<T> dataProvider;

    @UiField
    public SimplePager pager;

    public PagedTable( final int pageSize ) {
        super();
        this.pageSize = pageSize;
        this.dataGrid.setPageSize( pageSize );
        this.pager.setDisplay( dataGrid );
        this.pager.setPageSize( pageSize );
    }

    public PagedTable( final int pageSize,
                       final ProvidesKey<T> providesKey ) {
        super( providesKey );
        this.pageSize = pageSize;
        this.dataGrid.setPageSize( pageSize );
        this.pager.setDisplay( dataGrid );
        this.pager.setPageSize( pageSize );
    }

    public PagedTable( final int pageSize,
                       final ProvidesKey<T> providesKey,
                       final ColumnSortEvent.Handler columnSortHandler ) {
        super( providesKey,
               columnSortHandler );
        this.pageSize = pageSize;
        this.dataGrid.setPageSize( pageSize );
        this.pager.setDisplay( dataGrid );
        this.pager.setPageSize( pageSize );
    }

    public PagedTable( final int pageSize,
                       final ColumnSortEvent.Handler columnSortHandler ) {
        super( columnSortHandler );
        this.dataGrid.setPageSize( pageSize );
        this.pageSize = pageSize;
        this.pager.setDisplay( dataGrid );
        this.pager.setPageSize( pageSize );
    }

    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    /**
     * Link a data provider to the table
     * @param dataProvider
     */
    public void setDataProvider( final AsyncDataProvider<T> dataProvider ) {
        this.dataProvider = dataProvider;
        this.dataProvider.addDataDisplay( dataGrid );
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getPageStart() {
        return this.pager.getPageStart();
    }

}
