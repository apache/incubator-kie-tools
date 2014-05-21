/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.messageconsole.client.console;

import javax.inject.Inject;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import org.kie.workbench.common.screens.messageconsole.client.console.resources.MessageConsoleResources;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.tables.ResizableHeader;

public class MessageConsoleScreenViewImpl
        extends Composite
        implements MessageConsoleScreenView,
                   RequiresResize {

    private static Binder uiBinder = GWT.create( Binder.class );
    private Presenter presenter;
    private final PlaceManager placeManager;

    interface Binder extends UiBinder<Widget, MessageConsoleScreenViewImpl> {

    }

    @UiField(provided = true)
    DataGrid<MessageConsoleServiceRow> dataGrid;

    @UiField
    HorizontalPanel panel;

    public static final ProvidesKey<MessageConsoleServiceRow> KEY_PROVIDER = new ProvidesKey<MessageConsoleServiceRow>() {
        @Override
        public Object getKey( MessageConsoleServiceRow item ) {
            return item != null ? item.getMessageId() : null;
        }
    };

    @Inject
    public MessageConsoleScreenViewImpl( MessageConsoleService messageConsoleService,
            PlaceManager placeManager ) {
        this.placeManager = placeManager;
        dataGrid = new DataGrid<MessageConsoleServiceRow>( KEY_PROVIDER );
        dataGrid.setWidth( "100%" );

        dataGrid.setAutoHeaderRefreshDisabled( true );

        dataGrid.setEmptyTableWidget( new Label( "---" ) );

        setUpColumns();

        messageConsoleService.addDataDisplay( dataGrid );

        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void onResize() {
        dataGrid.setPixelSize( getParent().getOffsetWidth(),
                               getParent().getOffsetHeight() );
        dataGrid.onResize();
    }

    private void setUpColumns() {
        addLevelColumn();
        addTextColumn();
        addFileNameColumn();
        addColumnColumn();
        addLineColumn();
    }

    private void addLineColumn() {
        Column<MessageConsoleServiceRow, ?> lineColumn = new Column<MessageConsoleServiceRow, String>( new TextCell() ) {
            @Override
            public String getValue( MessageConsoleServiceRow row ) {
                return row != null ? Integer.toString( row.getMessageLine() ) : null;
            }
        };
        dataGrid.addColumn( lineColumn,
                            new ResizableHeader( MessageConsoleResources.CONSTANTS.Line(),
                                                 dataGrid,
                                                 lineColumn ) );
        dataGrid.setColumnWidth( lineColumn, 60, Style.Unit.PCT );
    }

    private void addColumnColumn() {
        Column<MessageConsoleServiceRow, ?> column = new Column<MessageConsoleServiceRow, String>( new TextCell() ) {
            @Override
            public String getValue( MessageConsoleServiceRow row ) {
                return Integer.toString( row.getMessageColumn() );
            }
        };
        dataGrid.addColumn( column,
                            new ResizableHeader( MessageConsoleResources.CONSTANTS.Column(),
                                                 dataGrid,
                                                 column ) );
        dataGrid.setColumnWidth( column, 60, Style.Unit.PCT );
    }

    private void addTextColumn() {
        Column<MessageConsoleServiceRow, ?> column = new Column<MessageConsoleServiceRow, String>( new TextCell() ) {
            @Override
            public String getValue( MessageConsoleServiceRow row ) {
                return row.getMessageText();
            }
        };
        dataGrid.addColumn( column,
                            new ResizableHeader( MessageConsoleResources.CONSTANTS.Text(),
                                                 dataGrid,
                                                 column ) );
        dataGrid.setColumnWidth( column, 60, Style.Unit.PCT );
    }

    private void addFileNameColumn() {
        Column<MessageConsoleServiceRow, String> column = new Column<MessageConsoleServiceRow, String>( new ClickableTextCell() ) {
            @Override
            public String getValue( MessageConsoleServiceRow row ) {
                if ( row.getMessagePath() != null ) {
                    return row.getMessagePath().getFileName();
                } else {
                    return "-";
                }
            }
        };
        column.setFieldUpdater( new FieldUpdater<MessageConsoleServiceRow, String>() {
            @Override
            public void update( int index,
                    MessageConsoleServiceRow row,
                                String value ) {
                if ( row.getMessagePath() != null ) {
                    placeManager.goTo( row.getMessagePath() );
                }
            }
        } );
        dataGrid.addColumn( column,
                            new ResizableHeader( MessageConsoleResources.CONSTANTS.FileName(),
                                                 dataGrid,
                                                 column ) );
        dataGrid.setColumnWidth( column, 60, Style.Unit.PCT );
    }

    private void addLevelColumn() {
        Column<MessageConsoleServiceRow, ?> column = new Column<MessageConsoleServiceRow, ImageResource>( new ImageResourceCell() ) {
            @Override
            public ImageResource getValue( MessageConsoleServiceRow row ) {
                switch ( row.getMessageLevel() ) {
                    case ERROR:
                        return MessageConsoleResources.INSTANCE.Error();
                    case WARNING:
                        return MessageConsoleResources.INSTANCE.Warning();
                    case INFO:
                    default:
                        return MessageConsoleResources.INSTANCE.Information();
                }
            }
        };
        dataGrid.addColumn( column,
                            new ResizableHeader( MessageConsoleResources.CONSTANTS.Level(),
                                                 dataGrid,
                                                 column ) );
        dataGrid.setColumnWidth( column, 60, Style.Unit.PCT );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}
