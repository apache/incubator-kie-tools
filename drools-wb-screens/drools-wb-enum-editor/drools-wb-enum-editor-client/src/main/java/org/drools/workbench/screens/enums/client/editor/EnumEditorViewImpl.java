/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.enums.client.editor;

import javax.annotation.PostConstruct;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.workbench.screens.enums.client.resources.i18n.EnumEditorConstants;
import org.drools.workbench.screens.enums.client.widget.DeleteButtonCellWidget;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.common.BusyPopup;

public class EnumEditorViewImpl extends Composite implements EnumEditorView {

    private final ListDataProvider<EnumRow> dataProvider = new ListDataProvider<EnumRow>();

    private boolean isDirty = false;

    @PostConstruct
    public void init() {
        final CellTable<EnumRow> cellTable = new CellTable<EnumRow>( Integer.MAX_VALUE );
        cellTable.setWidth( "100%" );

        final VerticalPanel panel = new VerticalPanel();

        //Column definitions
        final DeleteButtonCellWidget deleteButton = new DeleteButtonCellWidget();
        final Column<EnumRow, String> deleteButtonColumn = new Column<EnumRow, String>( deleteButton ) {
            @Override
            public String getValue( final EnumRow enumRow ) {
                return "";
            }
        };
        final Column<EnumRow, String> factNameColumn = new Column<EnumRow, String>( new EditTextCell() ) {
            @Override
            public String getValue( final EnumRow enumRow ) {
                return enumRow.getFactName();
            }
        };
        final Column<EnumRow, String> fieldNameColumn = new Column<EnumRow, String>( new EditTextCell() ) {
            @Override
            public String getValue( final EnumRow enumRow ) {
                return enumRow.getFieldName();
            }
        };
        final Column<EnumRow, String> contextColumn = new Column<EnumRow, String>( new EditTextCell() ) {
            @Override
            public String getValue( final EnumRow enumRow ) {
                return enumRow.getContext();
            }
        };

        //Write updates back to the model
        deleteButtonColumn.setFieldUpdater( new FieldUpdater<EnumRow, String>() {
            @Override
            public void update( final int index,
                                final EnumRow object,
                                final String value ) {
                isDirty = true;
                dataProvider.getList().remove( index );
            }
        } );
        factNameColumn.setFieldUpdater( new FieldUpdater<EnumRow, String>() {
            @Override
            public void update( final int index,
                                final EnumRow object,
                                final String value ) {
                isDirty = true;
                object.setFactName( value );
            }
        } );
        fieldNameColumn.setFieldUpdater( new FieldUpdater<EnumRow, String>() {
            @Override
            public void update( final int index,
                                final EnumRow object,
                                final String value ) {
                isDirty = true;
                object.setFieldName( value );
            }
        } );
        contextColumn.setFieldUpdater( new FieldUpdater<EnumRow, String>() {
            @Override
            public void update( final int index,
                                final EnumRow object,
                                final String value ) {
                isDirty = true;
                object.setContext( value );
            }
        } );

        cellTable.addColumn( deleteButtonColumn );
        cellTable.addColumn( factNameColumn,
                             EnumEditorConstants.INSTANCE.FactColumnHeader() );
        cellTable.addColumn( fieldNameColumn,
                             EnumEditorConstants.INSTANCE.FieldColumnHeader() );
        cellTable.addColumn( contextColumn,
                             EnumEditorConstants.INSTANCE.ContextColumnHeader() );

        // Connect the table to the data provider.
        dataProvider.addDataDisplay( cellTable );

        final Button addButton = new Button( EnumEditorConstants.INSTANCE.AddEnum(),
                                             new ClickHandler() {
            public void onClick( ClickEvent clickEvent ) {
                isDirty = true;
                final EnumRow enumRow = new EnumRow();
                dataProvider.getList().add( enumRow );
            }
        } );

        panel.add( addButton );
        panel.add( cellTable );

        initWidget( panel );
    }

    @Override
    public void setContent( final String content ) {
        dataProvider.setList( EnumParser.parseEnums( content ) );
    }

    @Override
    public String getContent() {
        if ( dataProvider.getList().isEmpty() ) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for ( final EnumRow enumRow : dataProvider.getList() ) {
            if ( enumRow.isValid() ) {
                sb.append( enumRow.toString() ).append( "\n" );
            }
        }
        return sb.toString();
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public void setNotDirty() {
        this.isDirty = false;
    }

    @Override
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }

    @Override
    public void alertReadOnly() {
        Window.alert( CommonConstants.INSTANCE.CantSaveReadOnly() );
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