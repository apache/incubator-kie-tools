/*
 * Copyright 2015 JBoss Inc
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
package org.kie.workbench.common.screens.datamodeller.client.pdescriptor;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonCell;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;

public class PersistenceUnitPropertyGridViewImpl
        extends Composite
        implements PersistenceUnitPropertyGridView {

    interface PersistenceUnitPropertyGridViewUiBinder
            extends
            UiBinder<Widget, PersistenceUnitPropertyGridViewImpl> {

    }

    private static PersistenceUnitPropertyGridViewUiBinder uiBinder = GWT.create( PersistenceUnitPropertyGridViewUiBinder.class );

    private Presenter presenter;

    @UiField(provided = true)
    SimpleTable<PropertyRow> dataGrid = new SimpleTable<PropertyRow>();

    @UiField
    TextBox newPropertyNameTextBox;

    @UiField
    TextBox newPropertyValueTextBox;

    @UiField
    HelpInline newPropertyHelpInline;

    @UiField
    Button addPropertyButton;

    public PersistenceUnitPropertyGridViewImpl() {

        dataGrid.setEmptyTableCaption( "No properties" );
        dataGrid.setToolBarVisible( false );

        addPropertyNameColumn();
        addPropertyValueColumn();
        addRemoveRowColumn();

        initWidget( uiBinder.createAndBindUi( this ) );
    }

    private void addPropertyNameColumn() {
        Column<PropertyRow, String> column = new Column<PropertyRow, String>( new EditTextCell( ) ) {
            @Override
            public String getValue( PropertyRow propertyRow ) {
                if ( propertyRow.getName() != null ) {
                    return propertyRow.getName();
                } else {
                    return "";
                }
            }
        };

        column.setFieldUpdater( new PropertyNameFieldUpdater<PropertyRow, String>( (EditTextCell) column.getCell() ) );

        dataGrid.addColumn( column,
                "Property Name" );
        dataGrid.setColumnWidth( column, 45, Style.Unit.PCT );
    }

    private void addPropertyValueColumn() {
        final Column<PropertyRow, String> column = new Column<PropertyRow, String>( new EditTextCell(  ) ) {

            @Override
            public String getValue( PropertyRow propertyRow ) {
                if ( propertyRow.getValue() != null ) {
                    return propertyRow.getValue();
                } else {
                    return "";
                }
            }
        };

        column.setFieldUpdater( new PropertyValueFieldUpdater<PropertyRow, String>( (EditTextCell) column.getCell() ) );

        dataGrid.addColumn( column,
                "Property Value" );
        dataGrid.setColumnWidth( column, 45, Style.Unit.PCT );
    }

    private void addRemoveRowColumn() {
        Column<PropertyRow, String> column = new Column<PropertyRow, String>( new ButtonCell( IconType.TRASH , ButtonType.DANGER, ButtonSize.SMALL) ) {
            @Override
            public String getValue( PropertyRow propertyRow ) {
                return CommonConstants.INSTANCE.Delete();
            }
        };

        column.setFieldUpdater( new FieldUpdater<PropertyRow, String>() {
            @Override
            public void update( int index,
                    PropertyRow propertyRow,
                                String value ) {

                onRemoveProperty( propertyRow );
            }
        } );

        dataGrid.addColumn( column,
                CommonConstants.INSTANCE.Delete() );
        dataGrid.setColumnWidth( column, 10, Style.Unit.PCT );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setReadOnly( boolean readOnly ) {
        //TODO implement this method
    }

    @Override
    public void setList( List<PropertyRow> properties ) {
        dataGrid.setRowData( properties );
    }

    @Override
    public void redraw() {
        dataGrid.redraw();
    }

    @Override
    public String getNewPropertyName() {
        return newPropertyNameTextBox.getText();
    }

    @Override
    public String getNewPropertyValue() {
        return newPropertyValueTextBox.getText();
    }

    @Override
    public void setNewPropertyName( String name ) {
        newPropertyNameTextBox.setText( name );
    }

    @Override
    public void setNewPropertyValue( String value ) {
        newPropertyValueTextBox.setText( value );
    }

    @UiHandler( "addPropertyButton" )
    void onAddProperty( ClickEvent event ) {
        presenter.onAddProperty();
    }

    private void onRemoveProperty( PropertyRow propertyRow ) {
        presenter.onRemoveProperty( propertyRow );
    }

    private class PropertyNameFieldUpdater<T, C> implements FieldUpdater<T, C> {

        private EditTextCell cell;

        PropertyNameFieldUpdater( EditTextCell cell ) {
            this.cell = cell;
        }

        @Override
        public void update( int index,
                T object,
                C value ) {

            PropertyRow propertyRow = ( PropertyRow ) object;
            String sValue = ( String ) value;
            //TODO add validations
            propertyRow.setName( sValue );
        }
    }

    private class PropertyValueFieldUpdater<T, C> implements FieldUpdater<T, C> {

        private EditTextCell cell;

        PropertyValueFieldUpdater( EditTextCell cell ) {
            this.cell = cell;
        }

        @Override
        public void update( int index,
                T object,
                C value ) {

            PropertyRow propertyRow = ( PropertyRow ) object;
            String sValue = ( String ) value;
            //TODO add validations
            propertyRow.setValue( sValue );
        }
    }

}
