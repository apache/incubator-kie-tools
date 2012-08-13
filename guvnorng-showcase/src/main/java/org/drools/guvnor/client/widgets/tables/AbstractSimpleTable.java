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

package org.drools.guvnor.client.widgets.tables;

import java.util.List;

import org.drools.guvnor.client.messages.ConstantsCore;
import org.drools.guvnor.client.rpc.AbstractPageRow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.SelectionModel;

/**
 * A composite Widget that shows rows of data (not-paged) and a "column picker"
 * to allow columns to be hidden from view. Columns can also be sorted.
 */
public abstract class AbstractSimpleTable<T extends AbstractPageRow> extends Composite
    implements
    HasData<T> {

    // Usual suspects
    protected static final ConstantsCore constants = GWT.create( ConstantsCore.class );

    @UiField(provided = true)
    public ToggleButton           columnPickerButton;

    @UiField(provided = true)
    public CellTable<T>           cellTable;

    public AbstractSimpleTable() {
        doCellTable();
        initWidget( makeWidget() );
    }

    /**
     * Refresh table programmatically
     */
    public void refresh() {
        cellTable.setVisibleRangeAndClearData( cellTable.getVisibleRange(),
                                               true );
    }

    /**
     * Override to add additional columns to the table
     * 
     * @param columnPicker
     * @param sortableHeaderGroup
     */
    protected abstract void addAncillaryColumns(ColumnPicker<T> columnPicker,
                                                SortableHeaderGroup<T> sortableHeaderGroup);

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
     * 
     * @return
     */
    protected abstract Widget makeWidget();

    public HandlerRegistration addCellPreviewHandler(Handler<T> handler) {
        return cellTable.addCellPreviewHandler( handler );
    }

    public HandlerRegistration addRangeChangeHandler(RangeChangeEvent.Handler handler) {
        return cellTable.addRangeChangeHandler( handler );
    }

    public HandlerRegistration addRowCountChangeHandler(RowCountChangeEvent.Handler handler) {
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

    public void setRowCount(int count) {
        cellTable.setRowCount( count );
    }

    public void setRowCount(int count,
                            boolean isExact) {
        cellTable.setRowCount( count,
                               isExact );
    }

    public void setVisibleRange(int start,
                                int length) {
        cellTable.setVisibleRange( start,
                                   length );
    }

    public void setVisibleRange(Range range) {
        cellTable.setVisibleRange( range );
    }

    public SelectionModel< ? super T> getSelectionModel() {
        return cellTable.getSelectionModel();
    }

    public T getVisibleItem(int indexOnPage) {
        return cellTable.getVisibleItem( indexOnPage );
    }

    public int getVisibleItemCount() {
        return cellTable.getVisibleItemCount();
    }

    public Iterable<T> getVisibleItems() {
        return cellTable.getVisibleItems();
    }

    public void setRowData(int start,
                           List< ? extends T> values) {
        cellTable.setRowData( start,
                              values );
    }

    public void setSelectionModel(SelectionModel< ? super T> selectionModel) {
        cellTable.setSelectionModel( selectionModel );
    }

    public void setVisibleRangeAndClearData(Range range,
                                            boolean forceRangeChangeEvent) {
        cellTable.setVisibleRangeAndClearData( range,
                                               forceRangeChangeEvent );
    }

    /**
     * Convenience method to allow data to easily set
     * 
     * @param values
     */
    public void setRowData(List< ? extends T> values) {
        setRowCount( values.size() );
        setVisibleRange( 0,
                         values.size() );
        setRowData( 0,
                    values );
    }

}
