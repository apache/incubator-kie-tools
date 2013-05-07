/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.workbench.widgets.tables;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.uberfire.client.resources.WorkbenchResources;

public class ColumnPicker<T> {

    private final Image COLUMN_PICKER_IMAGE = new Image( WorkbenchResources.INSTANCE.images().columnPicker() );

    private final CellTable<T> cellTable;
    private List<ColumnMeta<T>> columnMetaList = new ArrayList<ColumnMeta<T>>();

    public ColumnPicker( CellTable<T> cellTable ) {
        this.cellTable = cellTable;
    }

    public void addColumn( Column<T, ?> column,
                           Header<String> header,
                           boolean visible ) {
        addColumn( new ColumnMeta<T>( column,
                                      header,
                                      visible ) );
    }

    private void addColumn( ColumnMeta<T> columnMeta ) {
        columnMetaList.add( columnMeta );
        if ( columnMeta.isVisible() ) {
            cellTable.addColumn( columnMeta.getColumn(),
                                 columnMeta.getHeader() );
        }
    }

    public ToggleButton createToggleButton() {
        final ToggleButton button = new ToggleButton( COLUMN_PICKER_IMAGE );
        final PopupPanel popup = new PopupPanel( true );
        popup.addAutoHidePartner( button.getElement() );
        popup.addCloseHandler( new CloseHandler<PopupPanel>() {
            public void onClose( CloseEvent<PopupPanel> popupPanelCloseEvent ) {
                button.setDown( false );
            }
        } );
        VerticalPanel popupContent = new VerticalPanel();
        for ( final ColumnMeta<T> columnMeta : columnMetaList ) {
            final CheckBox checkBox = new CheckBox( columnMeta.getHeader().getValue() );
            checkBox.setValue( columnMeta.isVisible() );
            checkBox.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
                public void onValueChange( ValueChangeEvent<Boolean> booleanValueChangeEvent ) {
                    boolean visible = booleanValueChangeEvent.getValue();
                    if ( visible ) {
                        // WORKAROUND because CellTable does not support insertColumn at this time
                        for ( ColumnMeta<T> resettingColumnMeta : columnMetaList ) {
                            if ( resettingColumnMeta.isVisible() ) {
                                cellTable.removeColumn( resettingColumnMeta.getColumn() );
                            }
                        }
                        columnMeta.setVisible( visible );
                        for ( ColumnMeta<T> resettingColumnMeta : columnMetaList ) {
                            if ( resettingColumnMeta.isVisible() ) {
                                cellTable.addColumn( resettingColumnMeta.getColumn(),
                                                     resettingColumnMeta.getHeader() );
                            }
                        }
                    } else {
                        columnMeta.setVisible( visible );
                        cellTable.removeColumn( columnMeta.getColumn() );
                    }
                }
            } );
            popupContent.add( checkBox );
        }
        popup.add( popupContent );
        button.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                if ( button.isDown() ) {
                    popup.setPopupPosition( button.getAbsoluteLeft(),
                                            button.getAbsoluteTop() + button.getOffsetHeight() );
                    popup.show();
                } else {
                    popup.hide( false );
                }
            }
        } );
        return button;
    }

    private static class ColumnMeta<T> {

        private Column<T, ?> column;
        private Header<String> header;
        private boolean visible;

        private ColumnMeta( Column<T, ?> column,
                            Header<String> header,
                            boolean visible ) {
            this.column = column;
            this.header = header;
            this.visible = visible;
        }

        public Column<T, ?> getColumn() {
            return column;
        }

        public Header<String> getHeader() {
            return header;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible( boolean visible ) {
            this.visible = visible;
        }
    }
}
