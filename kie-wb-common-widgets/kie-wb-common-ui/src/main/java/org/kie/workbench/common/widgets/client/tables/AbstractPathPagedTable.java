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

package org.kie.workbench.common.widgets.client.tables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.tables.PagedTable;
import org.uberfire.paging.AbstractPathPageRow;

/**
 * Widget that shows rows of paged data where columns "uuid", "name" and
 * "format" are common. A "checkbox" and "open" button column are added by
 * default. Additional columns can be inserted inbetween these columns by
 * overriding <code>addAncillaryColumns()</code>. A "RSS Feed" button can also
 * be included if required.
 * <p/>
 * Based upon work by Geoffrey de Smet.
 */
public abstract class AbstractPathPagedTable<T extends AbstractPathPageRow> extends Composite {

    protected MultiSelectionModel<T> selectionModel;

    protected final PagedTable dataGrid;

    private final ProvidesKey<T> providesKey = new ProvidesKey<T>() {
        public Object getKey( T row ) {
            return row.getPath();
        }
    };

    public AbstractPathPagedTable( final int pageSize ) {
        dataGrid = new PagedTable( pageSize,
                                   providesKey );
        selectionModel = new MultiSelectionModel<T>( providesKey );
        dataGrid.setSelectionModel( selectionModel );

        Column<T, Boolean> selectionColumn = new Column<T, Boolean>( new CheckboxCell( true,
                                                                                       true ) ) {
            @Override
            public Boolean getValue( T object ) {
                return dataGrid.getSelectionModel().isSelected( object );
            }
        };
        selectionColumn.setFieldUpdater( new FieldUpdater<T, Boolean>() {
            public void update( int index,
                                T object,
                                Boolean value ) {
                dataGrid.getSelectionModel().setSelected( object,
                                                          value );
            }
        } );
        dataGrid.addColumn( selectionColumn,
                            "" );

        addAncillaryColumns();

        final TextColumn<T> uriColumn = new TextColumn<T>() {
            public String getValue( T row ) {
                return row.getPath().toURI();
            }
        };
        dataGrid.addColumn( uriColumn,
                            CommonConstants.INSTANCE.AbstractTableFileURI(),
                            false );

        // Add "Open" button column
        Column<T, String> openColumn = new Column<T, String>( new ButtonCell( ButtonSize.SMALL ) ) {
            public String getValue( T row ) {
                return CommonConstants.INSTANCE.AbstractTableOpen();
            }
        };
        openColumn.setFieldUpdater( new FieldUpdater<T, String>() {
            public void update( int index,
                                T row,
                                String value ) {
                getPlaceManager().goTo( row.getPath() );
            }
        } );
        dataGrid.addColumn( openColumn,
                            CommonConstants.INSTANCE.AbstractTableOpen() );

        final Button refreshButton = new Button();
        refreshButton.setIcon( IconType.REFRESH );
        refreshButton.setTitle( CommonConstants.INSTANCE.Refresh() );
        refreshButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                dataGrid.refresh();
            }
        } );
        dataGrid.getToolbar().add( refreshButton );

        final Button openSelectedButton = new Button( CommonConstants.INSTANCE.AbstractTableOpenSelected() );
        openSelectedButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                final Set<T> selectedSet = selectionModel.getSelectedSet();
                for ( T selected : selectedSet ) {
                    getPlaceManager().goTo( selected.getPath() );
                }
            }
        } );
        dataGrid.getToolbar().add( openSelectedButton );

        initWidget( dataGrid );
    }

    protected abstract void addAncillaryColumns();

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
        dataGrid.setVisibleRangeAndClearData( dataGrid.getVisibleRange(), true );
    }

    /**
     * Link a data provider to the table
     * @param dataProvider
     */
    public void setDataProvider( final AsyncDataProvider<T> dataProvider ) {
        dataProvider.addDataDisplay( dataGrid );
    }

    private PlaceManager getPlaceManager() {
        return IOC.getBeanManager().lookupBean( PlaceManager.class ).getInstance();
    }
}
