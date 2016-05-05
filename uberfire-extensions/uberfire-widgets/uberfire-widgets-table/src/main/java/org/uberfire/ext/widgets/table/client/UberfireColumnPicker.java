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
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;
import org.uberfire.ext.widgets.table.client.resources.UFTableResources;
import org.uberfire.ext.widgets.table.client.resources.i18n.CommonConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UberfireColumnPicker<T> {

    protected final DataGrid<T> dataGrid;
    protected final List<ColumnMeta<T>> columnMetaList = new ArrayList<>();
    protected final PopupPanel popup = GWT.create( PopupPanel.class );
    protected List<ColumnChangedHandler> columnChangedHandler = new ArrayList<>();

    public UberfireColumnPicker( DataGrid<T> dataGrid ) {
        this.dataGrid = dataGrid;
        setupPopup();
    }

    private void setupPopup() {
        popup.setAutoHideEnabled(true);
        popup.setAutoHideOnHistoryEventsEnabled(true);
    }

    public void addColumnChangedHandler( ColumnChangedHandler handler ) {
        columnChangedHandler.add( handler );
    }

    public Collection<ColumnMeta<T>> getColumnMetaList() {
        return columnMetaList;
    }

    protected String getColumnStoreName(ColumnMeta columnMeta){
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

    public void columnMoved( final int visibleFromIndex,
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

    protected int getVisibleColumnIndex( final ColumnMeta<T> columnMeta ) {
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

    protected void sortAndAddColumns( List<ColumnMeta<T>> columnMetas ) {
        // Sort based on preferences applied
        Collections.sort( columnMetas );
        //Add the columns based on the preferences
        for ( ColumnMeta meta : columnMetas ) {
            addColumn( meta );
        }
    }

    public void adjustColumnWidths() {
        for ( ColumnChangedHandler handler : columnChangedHandler ) {
            handler.afterColumnChanged();
        }
    }

    public void addColumns( List<ColumnMeta<T>> columnMetas ) {
        columnMetaList.addAll( columnMetas );
        sortAndAddColumns( columnMetas );
        adjustColumnWidths();
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

    protected void configureColorPickerPopup( int left, int top, VerticalPanel popupContent ) {
        popup.setWidget( popupContent );
        popup.show();
        int finalLeft = left - popup.getOffsetWidth();
        popup.setPopupPosition( finalLeft, top );
    }

    protected boolean addThisColumnToPopup( ColumnMeta<T> columnMeta ) {
        return true;
    }

    protected void addResetButtom( final int left, final int top, VerticalPanel popupContent ) {
        //there is no reset buttom
    }

    protected void showColumnPickerPopup( final int left,
                                          final int top ) {
        VerticalPanel popupContent = GWT.create( VerticalPanel.class );

        for ( final ColumnMeta<T> columnMeta : columnMetaList ) {
            if ( addThisColumnToPopup( columnMeta ) ) {
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
                        columnMeta.setVisible( visible );
                        adjustColumnWidths();
                    }
                } );

                popupContent.add( checkBox );
            }
        }

        addResetButtom( left, top, popupContent );
        configureColorPickerPopup( left, top, popupContent );
    }

    public Button createToggleButton() {
        final Button button = GWT.create( Button.class );
        button.addStyleName( UFTableResources.INSTANCE.CSS().columnPickerButton() );
        button.setDataToggle( Toggle.BUTTON );
        button.setIcon( IconType.LIST_UL );
        button.setTitle( CommonConstants.INSTANCE.ColumnPickerButtonTooltip() );

        popup.addStyleName( UFTableResources.INSTANCE.CSS().columnPickerPopup() );
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

    protected void resetTableColumns( int left,
                                      int top ) {
        int count = dataGrid.getColumnCount();
        for ( int i = 0; i < count; i++ ) {
            dataGrid.removeColumn( 0 );
        }

        loadGlobalGridPreferences();

        sortAndAddColumns( new ArrayList<ColumnMeta<T>>( columnMetaList ) );

        adjustColumnWidths();

        showColumnPickerPopup( left, top );
    }

    protected void loadGlobalGridPreferences(){

    }


}
