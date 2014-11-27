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

import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.workbench.screens.enums.client.resources.i18n.EnumEditorConstants;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;

public class EnumEditorViewImpl
        extends KieEditorViewImpl
        implements EnumEditorView {

    private final ListDataProvider<EnumRow> dataProvider = new ListDataProvider<EnumRow>();

    @PostConstruct
    public void init() {
        final CellTable<EnumRow> cellTable = new CellTable<EnumRow>( Integer.MAX_VALUE );
        cellTable.setStriped( true );
        cellTable.setCondensed( true );
        cellTable.setBordered( true );
        cellTable.setEmptyTableWidget( new Label( EnumEditorConstants.INSTANCE.noEnumsDefined() ) );
        cellTable.setWidth( "100%" );

        final VerticalPanel panel = new VerticalPanel();
        panel.setWidth( "100%" );

        //Column definitions
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

        //See https://bugzilla.redhat.com/show_bug.cgi?id=1167360
        //Replaced image-based ButtonCell with a button due to IE10 interpreting it as a form-submit button and hence responding to ENTER key presses.
        //See http://stackoverflow.com/questions/12325066/button-click-event-fires-when-pressing-enter-key-in-different-input-no-forms
        final ButtonCell deleteEnumButton = new ButtonCell( ButtonSize.SMALL );
        deleteEnumButton.setType( ButtonType.DANGER );
        deleteEnumButton.setIcon( IconType.MINUS_SIGN );
        final Column<EnumRow, String> deleteEnumColumn = new Column<EnumRow, String>( deleteEnumButton ) {
            @Override
            public String getValue( final EnumRow global ) {
                return EnumEditorConstants.INSTANCE.remove();
            }
        };
        //Write updates back to the model
        deleteEnumColumn.setFieldUpdater( new FieldUpdater<EnumRow, String>() {
            @Override
            public void update( final int index,
                                final EnumRow object,
                                final String value ) {
                dataProvider.getList().remove( index );
            }
        } );
        factNameColumn.setFieldUpdater( new FieldUpdater<EnumRow, String>() {
            @Override
            public void update( final int index,
                                final EnumRow object,
                                final String value ) {
                object.setFactName( value );
            }
        } );
        fieldNameColumn.setFieldUpdater( new FieldUpdater<EnumRow, String>() {
            @Override
            public void update( final int index,
                                final EnumRow object,
                                final String value ) {
                object.setFieldName( value );
            }
        } );
        contextColumn.setFieldUpdater( new FieldUpdater<EnumRow, String>() {
            @Override
            public void update( final int index,
                                final EnumRow object,
                                final String value ) {
                object.setContext( value );
            }
        } );

        cellTable.addColumn( factNameColumn,
                             EnumEditorConstants.INSTANCE.FactColumnHeader() );
        cellTable.addColumn( fieldNameColumn,
                             EnumEditorConstants.INSTANCE.FieldColumnHeader() );
        cellTable.addColumn( contextColumn,
                             EnumEditorConstants.INSTANCE.ContextColumnHeader() );
        cellTable.addColumn( deleteEnumColumn );

        // Connect the table to the data provider.
        dataProvider.addDataDisplay( cellTable );

        final Button addButton = new Button( EnumEditorConstants.INSTANCE.AddEnum(),
                                             new ClickHandler() {
                                                 public void onClick( ClickEvent clickEvent ) {
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
    public boolean confirmClose() {
        return Window.confirm( CommonConstants.INSTANCE.DiscardUnsavedData() );
    }
}