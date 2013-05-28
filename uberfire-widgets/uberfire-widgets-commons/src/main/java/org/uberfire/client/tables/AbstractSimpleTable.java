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

import java.util.List;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import org.uberfire.paging.AbstractPageRow;

/**
 * A composite Widget that shows rows of data (not-paged) and a "column picker"
 * to allow columns to be hidden from view. Columns can also be sorted.
 */
public abstract class AbstractSimpleTable<T extends AbstractPageRow>
        extends Composite
        implements HasData<T> {

    @UiField(provided = true)
    public ToggleButton columnPickerButton;

    @UiField(provided = true)
    public CellTable<T> cellTable;

    public AbstractSimpleTable() {
        doCellTable();
        initWidget( makeWidget() );
    }

    /**
     * Refresh table programmatically
     */
    public void refresh() {
        cellTable.setVisibleRangeAndClearData( cellTable.getVisibleRange(), true );
    }

    /**
     * Override to add additional columns to the table
     * @param columnPicker
     * @param sortableHeaderGroup
     */
    protected abstract void addAncillaryColumns( final ColumnPicker<T> columnPicker,
                                                 final SortableHeaderGroup<T> sortableHeaderGroup );

    /**
     * Set up table with zero columns. Additional columns can be appended by
     * overriding <code>addAncillaryColumns()</code>
     */
    protected void doCellTable() {

        cellTable = new CellTable<T>();

        ColumnPicker<T> columnPicker = new ColumnPicker<T>( cellTable );
        SortableHeaderGroup<T> sortableHeaderGroup = new SortableHeaderGroup<T>( cellTable );

        // Add any additional columns
        addAncillaryColumns( columnPicker,
                             sortableHeaderGroup );

        cellTable.setWidth( "100%" );
        columnPickerButton = columnPicker.createToggleButton();
    }

    /**
     * Instantiate the Widget for this Composite
     * @return
     */
    protected abstract Widget makeWidget();

    public HandlerRegistration addCellPreviewHandler( final Handler<T> handler ) {
        return cellTable.addCellPreviewHandler( handler );
    }

    public HandlerRegistration addRangeChangeHandler( final RangeChangeEvent.Handler handler ) {
        return cellTable.addRangeChangeHandler( handler );
    }

    public HandlerRegistration addRowCountChangeHandler( final RowCountChangeEvent.Handler handler ) {
        return cellTable.addRowCountChangeHandler( handler );
    }

    public int getRowCount() {
        return cellTable.getRowCount();
    }

    public Range getVisibleRange() {
        return cellTable.getVisibleRange();
    }

    public boolean isRowCountExact() {
        return cellTable.isRowCountExact();
    }

    public void setRowCount( final int count ) {
        cellTable.setRowCount( count );
    }

    public void setRowCount( final int count,
                             final boolean isExact ) {
        cellTable.setRowCount( count, isExact );
    }

    public void setVisibleRange( final int start,
                                 final int length ) {
        cellTable.setVisibleRange( start, length );
    }

    public void setVisibleRange( final Range range ) {
        cellTable.setVisibleRange( range );
    }

    public SelectionModel<? super T> getSelectionModel() {
        return cellTable.getSelectionModel();
    }

    public T getVisibleItem( final int indexOnPage ) {
        return cellTable.getVisibleItem( indexOnPage );
    }

    public int getVisibleItemCount() {
        return cellTable.getVisibleItemCount();
    }

    public Iterable<T> getVisibleItems() {
        return cellTable.getVisibleItems();
    }

    public void setRowData( final int start,
                            final List<? extends T> values ) {
        cellTable.setRowData( start, values );
    }

    public void setSelectionModel( final SelectionModel<? super T> selectionModel ) {
        cellTable.setSelectionModel( selectionModel );
    }

    public void setVisibleRangeAndClearData( final Range range,
                                             final boolean forceRangeChangeEvent ) {
        cellTable.setVisibleRangeAndClearData( range, forceRangeChangeEvent );
    }

    /**
     * Convenience method to allow data to easily set
     * @param values
     */
    public void setRowData( final List<? extends T> values ) {
        setRowCount( values.size() );
        setVisibleRange( 0, values.size() );
        setRowData( 0, values );
    }

}
