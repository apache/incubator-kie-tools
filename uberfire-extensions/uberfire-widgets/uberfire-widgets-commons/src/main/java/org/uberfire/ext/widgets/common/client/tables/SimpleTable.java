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

package org.uberfire.ext.widgets.common.client.tables;

import java.util.List;
import javax.inject.Inject;

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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridGlobalPreferences;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.services.shared.preferences.UserPreferencesService;
import org.uberfire.ext.services.shared.preferences.UserPreferencesType;
import org.uberfire.ext.widgets.common.client.resources.CommonResources;
import org.uberfire.client.views.pfly.widgets.DataGrid;

/**
 * A composite Widget that shows rows of data (not-paged) and a "column picker"
 * to allow columns to be hidden from view. Columns can also be sorted.
 */
public class SimpleTable<T>
        extends Composite
        implements HasData<T> {

    interface Binder
            extends
            UiBinder<Widget, SimpleTable> {

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

    protected ColumnPicker<T> columnPicker;

    private GridPreferencesStore gridPreferencesStore;

    @Inject
    private Caller<UserPreferencesService> preferencesService;

    @Inject
    private User identity;

    private ProvidesKey<T> providersKey;

    public SimpleTable() {
        dataGrid = new DataGrid<T>();
        setupGridTable();
    }

    public SimpleTable( final ProvidesKey<T> providesKey,
                        final GridGlobalPreferences gridGlobalPreferences ) {

        dataGrid = new DataGrid<T>( providesKey );
        if ( gridGlobalPreferences != null ) {
            this.gridPreferencesStore = new GridPreferencesStore( gridGlobalPreferences );
        }
        setupGridTable();
    }

    public SimpleTable( final ProvidesKey<T> providesKey ) {

        dataGrid = new DataGrid<T>( providesKey );
        setupGridTable();
    }

    private void setupGridTable() {
        dataGrid.setSkipRowHoverCheck( false );
        dataGrid.setSkipRowHoverStyleUpdate( false );
        dataGrid.setWidth( "100%" );
        dataGrid.setHeight( "300px" );
        dataGrid.addStyleName( CommonResources.INSTANCE.CSS().dataGrid() );
        dataGrid.setRowStyles( new RowStyles<T>() {
            @Override
            public String getStyleNames( T row,
                                         int rowIndex ) {
                return CommonResources.INSTANCE.CSS().dataGridRow();
            }
        } );
        setEmptyTableWidget();

        columnPicker = new ColumnPicker<T>( dataGrid,
                                            gridPreferencesStore );

        columnPicker.addColumnChangedHandler( new ColumnChangedHandler() {

            @Override
            public void beforeColumnChanged() {
            }

            @Override
            public void afterColumnChanged() {
                if ( gridPreferencesStore != null && preferencesService != null ) {
                    List<GridColumnPreference> columnsState = columnPicker.getColumnsState();
                    gridPreferencesStore.resetGridColumnPreferences();
                    for ( GridColumnPreference gcp : columnsState ) {
                        gridPreferencesStore.addGridColumnPreference( gcp );
                    }
                    saveGridPreferences();
                }
            }
        } );

        columnPickerButton = columnPicker.createToggleButton();

        addDataGridStyles( dataGrid.getElement(), CommonResources.INSTANCE.CSS().dataGridHeader(), CommonResources.INSTANCE.CSS().dataGridContent() );

        initWidget( makeWidget() );
    }

    private static native void addDataGridStyles( final JavaScriptObject grid,
                                                  final String header,
                                                  final String content )/*-{
        $wnd.jQuery(grid).find('table:first').addClass(header);
        $wnd.jQuery(grid).find('table:last').addClass(content);
    }-*/;

    protected Widget makeWidget() {
        return uiBinder.createAndBindUi( this );
    }

    public void setEmptyTableCaption( final String emptyTableCaption ) {
        this.emptyTableCaption = emptyTableCaption;
        setEmptyTableWidget();
    }

    private void setEmptyTableWidget() {
        String caption = "-----";
        if ( !( emptyTableCaption == null || emptyTableCaption.trim().isEmpty() ) ) {
            caption = emptyTableCaption;
        }
        dataGrid.setEmptyTableWidget( new Label( caption ) );
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
    public HandlerRegistration addCellPreviewHandler( final Handler<T> handler ) {
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

    /**
     * Link a column sort handler to the table
     * @param handler
     */
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

    public void setPreferencesService( final Caller<UserPreferencesService> preferencesService ) {
        this.preferencesService = preferencesService;
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

    public void addColumns( final List<ColumnMeta<T>> columnMetas ) {
        for ( ColumnMeta columnMeta : columnMetas ) {
            if ( columnMeta.getHeader() == null ) {
                columnMeta.setHeader( getColumnHeader( columnMeta.getCaption(),
                                                       columnMeta.getColumn() ) );
            }
        }
        columnPicker.addColumns( columnMetas );
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
                if ( gridPreferencesStore != null && preferencesService != null ) {
                    List<GridColumnPreference> columnsState = columnPicker.getColumnsState();
                    gridPreferencesStore.resetGridColumnPreferences();
                    for ( GridColumnPreference gcp : columnsState ) {
                        gridPreferencesStore.addGridColumnPreference( gcp );
                    }
                    saveGridPreferences();
                }
            }

            @Override
            public void beforeColumnChanged() {

            }
        } );
        return header;
    }

    public void setColumnWidth( final Column<T, ?> column,
                                final double width,
                                final Style.Unit unit ) {
        dataGrid.setColumnWidth( column,
                                 width,
                                 unit );
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

    public void setGridPreferencesStore( final GridPreferencesStore gridPreferences ) {
        // I need to update my local copy of the preferences 
        //   if I would like to compare with the current state for changes
        this.gridPreferencesStore = gridPreferences;
        columnPicker.setGridPreferencesStore( gridPreferences );
    }

    public GridPreferencesStore getGridPreferencesStore() {
        return this.gridPreferencesStore;
    }

    public void saveGridPreferences() {

        if ( gridPreferencesStore != null && preferencesService != null ) {
            gridPreferencesStore.setPreferenceKey( gridPreferencesStore.getGlobalPreferences().getKey() );
            gridPreferencesStore.setType( UserPreferencesType.GRIDPREFERENCES );
            preferencesService.call( new RemoteCallback<Void>() {
                @Override
                public void callback( Void response ) {
                }
            } ).saveUserPreferences( gridPreferencesStore );
        }
    }

    public void addTableTitle( String tableTitle ) {
        getLeftToolbar().add( new HTML( "<h4>" + tableTitle + "</h4>" ) );
    }

    public void setcolumnPickerButtonVisibe( final boolean show ) {
        columnPickerButton.setVisible( show );
    }

    public void storeColumnToPreferences() {
        List<GridColumnPreference> columnsState = columnPicker.getColumnsState();
        gridPreferencesStore.resetGridColumnPreferences();
        for ( GridColumnPreference gcp : columnsState ) {
            gridPreferencesStore.addGridColumnPreference( gcp );
        }
        saveGridPreferences();
    }

    public void setAlwaysShowScrollBars( boolean alwaysShowScrollBars ) {
        dataGrid.setAlwaysShowScrollBars( alwaysShowScrollBars );
    }

}
