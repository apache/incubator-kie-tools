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

package org.uberfire.client.workbench.widgets.tables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.resources.i18n.WorkbenchConstants;

/**
 * Widget that shows rows of paged data where columns "uuid", "name" and
 * "format" are common. A "checkbox" and "open" button column are added by
 * default. Additional columns can be inserted inbetween these columns by
 * overriding <code>addAncillaryColumns()</code>. A "RSS Feed" button can also
 * be included if required.
 * <p/>
 * Based upon work by Geoffrey de Smet.
 */
public abstract class AbstractPathPagedTable<T extends AbstractPathPageRow>
        extends AbstractPagedTable<T> {

    // UI
    @SuppressWarnings("rawtypes")
    interface AssetPagedTableBinder
            extends
            UiBinder<Widget, AbstractPathPagedTable> {

    }

    private static AssetPagedTableBinder uiBinder = GWT.create( AssetPagedTableBinder.class );

    protected MultiSelectionModel<T> selectionModel;

    public AbstractPathPagedTable( final int pageSize ) {
        super( pageSize );
    }

    /**
     * Return an array of selected Paths. API is maintained for backwards
     * compatibility of legacy code with AssetItemGrid's implementation
     * @return
     */
    public Collection<Path> getSelectedRowPaths() {
        Set<T> selectedRows = selectionModel.getSelectedSet();

        // Compatibility with existing API
        if ( selectedRows.size() == 0 ) {
            return null;
        }

        // Create the array of Paths
        final Collection<Path> paths = new ArrayList<Path>( selectedRows.size() );
        for ( T row : selectedRows ) {
            paths.add( row.getPath() );
        }

        return paths;
    }

    /**
     * Refresh table programmatically
     */
    public void refresh() {
        selectionModel.clear();
        cellTable.setVisibleRangeAndClearData( cellTable.getVisibleRange(), true );
    }

    /**
     * Set up table and common columns. Additional columns can be appended
     * between the "checkbox" and "open" columns by overriding
     * <code>addAncillaryColumns()</code>
     */
    @Override
    protected void doCellTable() {

        final ProvidesKey<T> providesKey = new ProvidesKey<T>() {
            public Object getKey( T row ) {
                return row.getPath();
            }
        };

        cellTable = new CellTable<T>( providesKey );
        selectionModel = new MultiSelectionModel<T>( providesKey );
        cellTable.setSelectionModel( selectionModel );
        SelectionColumn.createAndAddSelectionColumn( cellTable );

        ColumnPicker<T> columnPicker = new ColumnPicker<T>( cellTable );
        SortableHeaderGroup<T> sortableHeaderGroup = new SortableHeaderGroup<T>( cellTable );

        // Add any additional columns
        addAncillaryColumns( columnPicker, sortableHeaderGroup );

        final TextColumn<T> uriColumn = new TextColumn<T>() {
            public String getValue( T row ) {
                return row.getPath().toURI();
            }
        };
        columnPicker.addColumn( uriColumn,
                                new SortableHeader<T, String>(
                                        sortableHeaderGroup,
                                        WorkbenchConstants.INSTANCE.AbstractTableFileURI(),
                                        uriColumn ),
                                false );

        // Add "Open" button column
        Column<T, String> openColumn = new Column<T, String>( new ButtonCell( ButtonSize.SMALL ) ) {
            public String getValue( T row ) {
                return WorkbenchConstants.INSTANCE.AbstractTableOpen();
            }
        };
        openColumn.setFieldUpdater( new FieldUpdater<T, String>() {
            public void update( int index,
                                T row,
                                String value ) {
                getPlaceManager().goTo( row.getPath() );
            }
        } );
        columnPicker.addColumn( openColumn,
                                new TextHeader( WorkbenchConstants.INSTANCE.AbstractTableOpen() ),
                                true );

        cellTable.setWidth( "100%" );
        columnPickerButton = columnPicker.createToggleButton();
    }

    /**
     * Link a data provider to the table
     * @param dataProvider
     */
    public void setDataProvider( final AsyncDataProvider<T> dataProvider ) {
        this.dataProvider = dataProvider;
        this.dataProvider.addDataDisplay( cellTable );
    }

    /**
     * Construct a widget representing the table
     */
    @Override
    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    /**
     * Open selected item(s) to separate tabs
     * @param e
     */
    @UiHandler("openSelectedButton")
    void openSelected( final ClickEvent e ) {
        final Set<T> selectedSet = selectionModel.getSelectedSet();
        for ( T selected : selectedSet ) {
            getPlaceManager().goTo( selected.getPath() );
        }
    }

    /**
     * Refresh table in response to ClickEvent
     * @param e
     */
    @UiHandler("refreshButton")
    void refresh( final ClickEvent e ) {
        refresh();
    }

    private PlaceManager getPlaceManager() {
        return IOC.getBeanManager().lookupBean( PlaceManager.class ).getInstance();
    }
}
