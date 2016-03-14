/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.table.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.*;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.uberfire.client.views.pfly.widgets.DataGrid;
import org.uberfire.ext.widgets.table.client.resources.UFTableResources;

import java.util.List;

/**
 * A composite Widget that shows rows of data (not-paged) and a "column picker"
 * to allow columns to be hidden from view. Columns can also be sorted.
 */
public class UberfireSimpleTable<T>
        extends Composite
        implements HasData<T>{


    interface Binder
            extends
            UiBinder<Widget, UberfireSimpleTable> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField(provided = true)
    public Button columnPickerButton;

    @UiField(provided = true)
    public DataGrid<T> dataGrid;

    @UiField
    public HorizontalPanel toolbarContainer;

    @UiField
    public HorizontalPanel rightToolbar;

    @UiField
    public FlowPanel rightActionsToolbar;

    @UiField
    public FlowPanel leftToolbar;

    @UiField
    public FlowPanel centerToolbar;

    private String emptyTableCaption;

    protected UberfireColumnPicker<T> columnPicker;

    public UberfireSimpleTable() {
        setupDataGrid( null );
        setupGridTable();
    }

    public UberfireSimpleTable( final ProvidesKey<T> providesKey ) {
        setupDataGrid( providesKey );
        setupGridTable();
    }

    protected void setupGridTable() {
        setupDataGrid();
        setEmptyTableWidget();

        setupColumnPicker();

        columnPickerButton = columnPicker.createToggleButton();

        initWidget( makeWidget() );
    }

    protected void setupColumnPicker() {
        columnPicker = new UberfireColumnPicker<>( dataGrid );
    }

    protected void setupDataGrid( ProvidesKey<T> providesKey ) {
        if ( providesKey != null ) {
            dataGrid = new DataGrid<T>( providesKey );
        }
        else{
            dataGrid = new DataGrid<T>( );
        }
    }

    public void setEmptyTableCaption( final String emptyTableCaption ) {
        this.emptyTableCaption = emptyTableCaption;
        setEmptyTableWidget();
    }

    protected void setupDataGrid() {
        dataGrid.setSkipRowHoverCheck( false );
        dataGrid.setSkipRowHoverStyleUpdate( false );
        dataGrid.addStyleName( UFTableResources.INSTANCE.CSS().dataGridMain() );
        dataGrid.addStyleName( UFTableResources.INSTANCE.CSS().dataGrid() );
        dataGrid.setRowStyles( ( row, rowIndex ) -> UFTableResources.INSTANCE.CSS().dataGridRow() );
        addDataGridStyles( dataGrid.getElement(), UFTableResources.INSTANCE.CSS().dataGridHeader(),
                           UFTableResources.INSTANCE.CSS().dataGridContent() );
    }

    protected void setEmptyTableWidget() {
        String caption = "-----";
        if ( !emptyCaptionIsDefined() ) {
            caption = emptyTableCaption;
        }
        dataGrid.setEmptyTableWidget( new Label( caption ) );
    }

    private boolean emptyCaptionIsDefined() {
        return emptyTableCaption == null || emptyTableCaption.trim().isEmpty();
    }

    protected static native void addDataGridStyles( final JavaScriptObject grid,
                                                  final String header,
                                                  final String content )/*-{
        $wnd.jQuery(grid).find('table:first').addClass(header);
        $wnd.jQuery(grid).find('table:last').addClass(content);
    }-*/;

    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    public void redraw() {
        dataGrid.redraw();
        dataGrid.flush();
    }

    public void refresh() {
        dataGrid.setVisibleRangeAndClearData( dataGrid.getVisibleRange(),
                                              true );
    }

    @Override
    public HandlerRegistration addCellPreviewHandler( final CellPreviewEvent.Handler<T> handler ) {
        return dataGrid.addCellPreviewHandler( handler );
    }

    @Override
    public HandlerRegistration addRangeChangeHandler( final RangeChangeEvent.Handler handler ) {
        return dataGrid.addRangeChangeHandler( handler );
    }

    @Override
    public HandlerRegistration addRowCountChangeHandler( final RowCountChangeEvent.Handler handler ) {
        return dataGrid.addRowCountChangeHandler( handler );
    }

    public int getColumnIndex( final Column<T, ?> column ) {
        return dataGrid.getColumnIndex( column );
    }

    public HandlerRegistration addColumnSortHandler( final ColumnSortEvent.Handler handler ) {
        return this.dataGrid.addColumnSortHandler( handler );
    }

    @Override
    public int getRowCount() {
        return dataGrid.getRowCount();
    }

    @Override
    public Range getVisibleRange() {
        return dataGrid.getVisibleRange();
    }

    @Override
    public boolean isRowCountExact() {
        return dataGrid.isRowCountExact();
    }

    @Override
    public void setRowCount( final int count ) {
        dataGrid.setRowCount( count );
    }

    @Override
    public void setRowCount( final int count,
                             final boolean isExact ) {
        dataGrid.setRowCount( count,
                              isExact );
    }

    @Override
    public void setVisibleRange( final int start,
                                 final int length ) {
        dataGrid.setVisibleRange( start,
                                  length );
    }

    @Override
    public void setVisibleRange( final Range range ) {
        dataGrid.setVisibleRange( range );
    }

    @Override
    public SelectionModel<? super T> getSelectionModel() {
        return dataGrid.getSelectionModel();
    }

    @Override
    public T getVisibleItem( final int indexOnPage ) {
        return dataGrid.getVisibleItem( indexOnPage );
    }

    @Override
    public int getVisibleItemCount() {
        return dataGrid.getVisibleItemCount();
    }

    @Override
    public Iterable<T> getVisibleItems() {
        return dataGrid.getVisibleItems();
    }

    @Override
    public void setRowData( final int start,
                            final List<? extends T> values ) {
        dataGrid.setRowData( start,
                             values );
        redraw();
    }

    public void setRowData( final List<? extends T> values ) {
        dataGrid.setRowData( values );
        redraw();
    }

    @Override
    public void setSelectionModel( final SelectionModel<? super T> selectionModel ) {
        dataGrid.setSelectionModel( selectionModel );
    }

    public void setSelectionModel( final SelectionModel<? super T> selectionModel,
                                   final CellPreviewEvent.Handler<T> selectionEventManager ) {
        dataGrid.setSelectionModel( selectionModel,
                                    selectionEventManager );
    }

    @Override
    public void setVisibleRangeAndClearData( final Range range,
                                             final boolean forceRangeChangeEvent ) {
        dataGrid.setVisibleRangeAndClearData( range,
                                              forceRangeChangeEvent );
    }


    @Override
    public void setHeight( final String height ) {
        dataGrid.setHeight( height );
    }

    @Override
    public void setPixelSize( final int width,
                              final int height ) {
        dataGrid.setPixelSize( width,
                               height );
    }

    @Override
    public void setSize( final String width,
                         final String height ) {
        dataGrid.setSize( width,
                          height );
    }

    @Override
    public void setWidth( final String width ) {
        dataGrid.setWidth( width );
    }

    public void setColumnWidth( final Column<T, ?> column,
                                final double width,
                                final Style.Unit unit ) {
        dataGrid.setColumnWidth( column,
                                 width,
                                 unit );
    }


    public void setToolBarVisible( final boolean visible ) {
        toolbarContainer.setVisible( visible );
    }

    public ColumnSortList getColumnSortList() {
        return dataGrid.getColumnSortList();
    }

    public HasWidgets getToolbar() {
        return toolbarContainer;
    }

    public HasWidgets getRightToolbar() {
        return rightToolbar;
    }

    public HasWidgets getRightActionsToolbar() {
        return rightActionsToolbar;
    }

    public HasWidgets getLeftToolbar() {
        return leftToolbar;
    }

    public HasWidgets getCenterToolbar() {
        return centerToolbar;
    }

    public void setRowStyles( final RowStyles<T> styles ) {
        dataGrid.setRowStyles( styles );
    }

    public void addTableTitle( String tableTitle ) {
        getLeftToolbar().add( new HTML( "<h4>" + tableTitle + "</h4>" ) );
    }

    public void setAlwaysShowScrollBars( boolean alwaysShowScrollBars ) {
        dataGrid.setAlwaysShowScrollBars( alwaysShowScrollBars );
    }

    public void addColumn( final Column<T, ?> column,
                           final String caption ) {
        addColumn( column,
                   caption,
                   true );
    }

    public void addColumn( final Column<T, ?> column,
                           final String caption,
                           final boolean visible ) {
        ColumnMeta<T> columnMeta = new ColumnMeta<T>( column,
                                                      caption,
                                                      visible );
        addColumn( columnMeta );
    }

    public void addColumns( final List<ColumnMeta<T>> columnMetas ) {
        for ( ColumnMeta columnMeta : columnMetas ) {
            if ( columnMeta.getHeader() == null ) {
                columnMeta.setHeader( getColumnHeader( columnMeta.getCaption(),
                                                       columnMeta.getColumn() ) );
            }
        }
        columnPicker.addColumns( columnMetas );
    }

    protected void addColumn( final ColumnMeta<T> columnMeta ) {
        if ( columnMeta.getHeader() == null ) {
            columnMeta.setHeader( getColumnHeader( columnMeta.getCaption(),
                                                   columnMeta.getColumn() ) );
        }
        columnPicker.addColumn( columnMeta );
    }

    protected ResizableMovableHeader<T> getColumnHeader( final String caption,
                                                         final Column column ) {
        final ResizableMovableHeader header = new ResizableMovableHeader<T>( caption,
                                                                             dataGrid,
                                                                             columnPicker,
                                                                             column ) {
            @Override
            protected int getTableBodyHeight() {
                return dataGrid.getOffsetHeight();
            }
        };
        header.addColumnChangedHandler( new ColumnChangedHandler() {
            @Override
            public void afterColumnChanged() {
                afterColumnChangedHandler();
            }

            @Override
            public void beforeColumnChanged() {

            }
        } );
        return header;
    }

    public void setColumnPickerButtonVisible( final boolean show ) {
        columnPickerButton.setVisible( show );
    }

    protected void afterColumnChangedHandler() {

    }
}
