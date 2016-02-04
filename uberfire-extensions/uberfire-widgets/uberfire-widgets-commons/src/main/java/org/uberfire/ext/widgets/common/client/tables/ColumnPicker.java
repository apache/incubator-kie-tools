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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.uberfire.ext.services.shared.preferences.GridColumnPreference;
import org.uberfire.ext.services.shared.preferences.GridPreferencesStore;
import org.uberfire.ext.widgets.common.client.resources.CommonResources;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;

public class ColumnPicker<T> {

    private final DataGrid<T> dataGrid;
    private final List<ColumnMeta<T>> columnMetaList = new ArrayList<ColumnMeta<T>>();
    private final PopupPanel popup = GWT.create( PopupPanel.class );

    private GridPreferencesStore gridPreferences;
    private List<ColumnChangedHandler> columnChangedHandler = new ArrayList<ColumnChangedHandler>();

    public ColumnPicker( DataGrid<T> dataGrid,
                         GridPreferencesStore gridPreferences ) {
        this.dataGrid = dataGrid;
        this.gridPreferences = gridPreferences;
        popup.setAutoHideEnabled(true);
        popup.setAutoHideOnHistoryEventsEnabled(true);
    }

    public ColumnPicker( DataGrid<T> dataGrid ) {
        this( dataGrid, null );
    }

    public void addColumnChangedHandler( ColumnChangedHandler handler ) {
        columnChangedHandler.add( handler );
    }

    public void addColumns( List<ColumnMeta<T>> columnMetas ) {
        columnMetaList.addAll( columnMetas );
        sortAndAddColumns( columnMetas );
        adjustColumnWidths();
    }

    public Collection<ColumnMeta<T>> getColumnMetaList() {
        return columnMetaList;
    }

    public void removeColumn( ColumnMeta<T> columnMeta ) {
        columnMetaList.remove( columnMeta );
        int count = dataGrid.getColumnCount();
        for ( int i = 0; i < count; i++ ) {
            dataGrid.removeColumn( 0 );
        }

        sortAndAddColumns( columnMetaList );
        adjustColumnWidths();
    }

    protected void sortAndAddColumns( List<ColumnMeta<T>> columnMetas ) {
        // Check for column preferences and orders
        for ( ColumnMeta meta : columnMetas ) {
            checkColumnMeta( meta );
        }
        // Sort based on preferences applied
        Collections.sort( columnMetas );
        //Add the columns based on the preferences
        for ( ColumnMeta meta : columnMetas ) {
            addColumn( meta );
        }
    }

    protected void checkColumnMeta( ColumnMeta<T> columnMeta ) {
        if ( gridPreferences != null ) {
            List<GridColumnPreference> columnPreferences = gridPreferences.getColumnPreferences();
            if ( !columnPreferences.isEmpty() ) {
                boolean found = false;
                for ( int i = 0; i < gridPreferences.getColumnPreferences().size() && !found; i++ ) {
                    GridColumnPreference gcp = gridPreferences.getColumnPreferences().get( i );
                    if ( gcp.getName().equals( getColumnStoreName(columnMeta) ) ) {
                        columnMeta.setVisible( true );
                        if ( gcp.getWidth() != null ) {
                            dataGrid.setColumnWidth( columnMeta.getColumn(), gcp.getWidth() );
                        } else {
                            dataGrid.setColumnWidth( columnMeta.getColumn(), 100, Style.Unit.PCT );
                        }
                        columnMeta.setPosition( gcp.getPosition() );
                        found = true;
                    }
                }
                if ( !found ) {
                    columnMeta.setPosition( -1 );
                    columnMeta.setVisible( false );
                }
            } else if ( gridPreferences.getGlobalPreferences() != null ) {
                int position = gridPreferences.getGlobalPreferences().getInitialColumns().indexOf( getColumnStoreName( columnMeta ) );
                if ( position != -1 ) {
                    columnMeta.setVisible( true );
                    columnMeta.setPosition( position );
                } else {
                    columnMeta.setPosition( -1 );
                    columnMeta.setVisible( false );
                }
            }
        }
    }

    public void addColumn( ColumnMeta<T> columnMeta ) {
        if ( columnMeta == null ) {
            return;
        }
        if ( !columnMetaList.contains( columnMeta ) ) {
            columnMetaList.add( columnMeta );
        }
        if ( columnMeta.isVisible() ) {
            dataGrid.addColumn( columnMeta.getColumn(), columnMeta.getHeader() );
        }
    }

    public void setGridPreferencesStore( GridPreferencesStore gridPreferences ) {
        this.gridPreferences = gridPreferences;
    }

    public Button createToggleButton() {
        final Button button = GWT.create( Button.class );
        button.addStyleName( CommonResources.INSTANCE.CSS().columnPickerButton() );
        button.setDataToggle( Toggle.BUTTON );
        button.setIcon( IconType.LIST_UL );
        button.setTitle( CommonConstants.INSTANCE.ColumnPickerButtonTooltip() );

        popup.addStyleName( CommonResources.INSTANCE.CSS().columnPickerPopup() );
        popup.addAutoHidePartner( button.getElement() );
        popup.addCloseHandler( new CloseHandler<PopupPanel>() {
            public void onClose( CloseEvent<PopupPanel> popupPanelCloseEvent ) {
                if ( popupPanelCloseEvent.isAutoClosed() ) {
                    button.setActive( false );
                }
            }
        } );

        button.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                if ( !button.isActive() ) {
                    showColumnPickerPopup( button.getAbsoluteLeft() + button.getOffsetWidth(),
                                           button.getAbsoluteTop() + button.getOffsetHeight() );
                } else {
                    popup.hide( false );
                }
            }
        } );
        return button;
    }

    private void showColumnPickerPopup( final int left,
                                        final int top ) {
        VerticalPanel popupContent = GWT.create( VerticalPanel.class );

        for ( final ColumnMeta<T> columnMeta : columnMetaList ) {
            if ( gridPreferences == null || !gridPreferences.getGlobalPreferences()
                    .getBannedColumns().contains( getColumnStoreName(columnMeta) ) ) {
                final CheckBox checkBox = GWT.create( CheckBox.class );
                checkBox.setText( columnMeta.getHeader().getValue() );
                checkBox.setValue( columnMeta.isVisible() );
                checkBox.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
                    public void onValueChange( ValueChangeEvent<Boolean> booleanValueChangeEvent ) {
                        boolean visible = booleanValueChangeEvent.getValue();
                        if ( visible ) {
                            dataGrid.insertColumn( getVisibleColumnIndex( columnMeta ),
                                                   columnMeta.getColumn(),
                                                   columnMeta.getHeader() );
                        } else {
                            dataGrid.removeColumn( columnMeta.getColumn() );
                        }
                        columnMeta.setVisible(visible);
                        adjustColumnWidths();
                    }
                } );

                popupContent.add( checkBox );
            }
        }

        if ( gridPreferences != null ) {
            Button resetButton = GWT.create( Button.class );
            resetButton.setText( CommonConstants.INSTANCE.Reset() );
            resetButton.setSize( ButtonSize.EXTRA_SMALL );
            resetButton.addClickHandler( new ClickHandler() {

                @Override
                public void onClick( ClickEvent event ) {
                    resetTableColumns( left, top );
                }
            } );

            popupContent.add( resetButton );
        }
        popup.setWidget( popupContent );
        popup.show();
        int finalLeft = left - popup.getOffsetWidth();
        popup.setPopupPosition( finalLeft, top );
    }

    protected void resetTableColumns( int left,
                                      int top ) {
        gridPreferences.resetGridColumnPreferences();
        int count = dataGrid.getColumnCount();
        for ( int i = 0; i < count; i++ ) {
            dataGrid.removeColumn( 0 );
        }

        for ( final ColumnMeta<T> columnMeta : columnMetaList ) {
            int position = gridPreferences.getGlobalPreferences().getInitialColumns().indexOf( getColumnStoreName( columnMeta ) );
            columnMeta.setPosition( position );
            columnMeta.setVisible( position > -1 );
        }

        sortAndAddColumns( new ArrayList<ColumnMeta<T>>( columnMetaList ) );

        adjustColumnWidths();

        showColumnPickerPopup( left, top );
    }

    public List<GridColumnPreference> getColumnsState() {
        List<GridColumnPreference> state = new ArrayList<GridColumnPreference>();
        for ( final ColumnMeta<T> cm : columnMetaList ) {
            if ( cm.isVisible() ) {
                state.add( new GridColumnPreference( getColumnStoreName( cm ),
                                                     dataGrid.getColumnIndex( cm.getColumn() ),
                                                     dataGrid.getColumnWidth( cm.getColumn() ) ) );
            }
        }
        return state;
    }

    private int getVisibleColumnIndex( final ColumnMeta<T> columnMeta ) {
        int index = 0;
        for ( final ColumnMeta<T> cm : columnMetaList ) {
            if ( cm.equals( columnMeta ) ) {
                return index;
            }
            if ( cm.isVisible() ) {
                index++;
            }
        }
        return index;
    }

    public void adjustColumnWidths() {
        for ( ColumnChangedHandler handler : columnChangedHandler ) {
            handler.afterColumnChanged();
        }

        List<GridColumnPreference> preferences = getColumnsState();

        if ( preferences.isEmpty() ) {
            return;
        }
        if ( preferences.size() == 1 ) {
            dataGrid.setColumnWidth( dataGrid.getColumn( 0 ),
                                     100,
                                     Style.Unit.PCT );
            return;
        }

        int fixedColumnsWidth = 0;
        Map<String, String> fixedWidths = new HashMap<String, String>();
        List<String> columnsToCalculate = new ArrayList<String>();

        for ( GridColumnPreference preference : preferences ) {
            if ( preference.getWidth() != null && preference.getWidth().endsWith( Style.Unit.PX.getType() ) ) {
                fixedWidths.put( preference.getName(), preference.getWidth() );
                fixedColumnsWidth += Integer.decode( preference.getWidth().substring( 0, preference.getWidth().indexOf( Style.Unit.PX.getType() ) ) );
            } else {
                columnsToCalculate.add( preference.getName() );
            }
        }

        if ( columnsToCalculate.size() > 0 ) {

            double columnPCT = 100 / columnsToCalculate.size();

            if ( dataGrid.getOffsetWidth() != 0 ) {
                int availabelColumnSpace = dataGrid.getOffsetWidth() - fixedColumnsWidth;
                double availablePCT = availabelColumnSpace * 100 / dataGrid.getOffsetWidth();
                columnPCT = columnPCT * availablePCT / 100;
            }

            for ( ColumnMeta<T> cm : columnMetaList ) {
                if ( cm.isVisible() ) {
                    if ( columnsToCalculate.contains( getColumnStoreName( cm ) ) ) {
                        dataGrid.setColumnWidth( cm.getColumn(),
                                                 columnPCT,
                                                 Style.Unit.PCT);
                    } else {
                        dataGrid.setColumnWidth( cm.getColumn(), fixedWidths.get( getColumnStoreName( cm ) ) );
                    }
                }
            }
        }
    }

    protected void columnMoved( final int visibleFromIndex,
                                final int visibleBeforeIndex ) {
        int visibleColumnFromIndex = 0;
        ColumnMeta<T> columnMetaToMove = null;
        for ( int i = 0; i < columnMetaList.size(); i++ ) {
            final ColumnMeta<T> columnMeta = columnMetaList.get( i );
            if ( columnMeta.isVisible() ) {
                if ( visibleFromIndex == visibleColumnFromIndex ) {
                    columnMetaToMove = columnMeta;
                    break;
                }
                visibleColumnFromIndex++;
            }
        }
        if ( columnMetaToMove == null ) {
            return;
        }

        columnMetaList.remove( columnMetaToMove );

        boolean columnInserted = false;
        int visibleColumnBeforeIndex = 0;
        for ( int i = 0; i < columnMetaList.size(); i++ ) {
            final ColumnMeta<T> columnMeta = columnMetaList.get( i );
            if ( columnMeta.isVisible() ) {
                if ( visibleBeforeIndex == visibleColumnBeforeIndex ) {
                    columnMetaList.add( i,
                                        columnMetaToMove );
                    columnInserted = true;
                    break;
                }
                visibleColumnBeforeIndex++;
            }
        }
        if ( !columnInserted ) {
            columnMetaList.add( columnMetaToMove );
        }
    }

    private String getColumnStoreName(ColumnMeta columnMeta){
        if(columnMeta!=null){
            if(columnMeta.getColumn()!=null) {
                String colStoreName = columnMeta.getColumn().getDataStoreName();
                if (colStoreName!=null && !colStoreName.isEmpty()) {
                    return colStoreName;
                }
            }
            return columnMeta.getCaption();
        }
        return "";
    }
}
