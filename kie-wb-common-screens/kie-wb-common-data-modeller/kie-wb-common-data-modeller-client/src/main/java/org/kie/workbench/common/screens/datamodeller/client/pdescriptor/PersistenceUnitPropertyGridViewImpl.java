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
import com.google.gwt.view.client.ListDataProvider;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
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
    Button addPropertyButton;

    private boolean readOnly = false;

    public PersistenceUnitPropertyGridViewImpl() {

        initWidget( uiBinder.createAndBindUi( this ) );

        dataGrid.setEmptyTableCaption( Constants.INSTANCE.persistence_unit_property_grid_no_properties_message() );
        dataGrid.setToolBarVisible( false );

        addPropertyNameColumn();
        addPropertyValueColumn();
        addRemoveRowColumn();
    }

    private void addPropertyNameColumn() {
        Column<PropertyRow, String> column = new Column<PropertyRow, String>( new EditTextCell() ) {
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
                            Constants.INSTANCE.persistence_unit_property_grid_property_name_column() );
        dataGrid.setColumnWidth( column, 45, Style.Unit.PCT );
    }

    private void addPropertyValueColumn() {
        final Column<PropertyRow, String> column = new Column<PropertyRow, String>( new EditTextCell() ) {

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
                            Constants.INSTANCE.persistence_unit_property_grid_property_value_column() );
        dataGrid.setColumnWidth( column, 45, Style.Unit.PCT );
    }

    private void addRemoveRowColumn() {
        Column<PropertyRow, String> column = new Column<PropertyRow, String>( new ButtonCell( IconType.TRASH, ButtonType.DANGER, ButtonSize.SMALL ) ) {
            @Override
            public String getValue( PropertyRow propertyRow ) {
                return Constants.INSTANCE.persistence_unit_property_grid_action_delete();
            }
        };

        column.setFieldUpdater( new FieldUpdater<PropertyRow, String>() {
            @Override
            public void update( int index,
                                PropertyRow propertyRow,
                                String value ) {

                if ( !readOnly ) {
                    onRemoveProperty( propertyRow );
                }
            }
        } );

        dataGrid.addColumn( column,
                            Constants.INSTANCE.persistence_unit_property_grid_property_action_column() );
        dataGrid.setColumnWidth( column, 10, Style.Unit.PCT );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setReadOnly( boolean readOnly ) {
        this.readOnly = readOnly;
        addPropertyButton.setEnabled( !readOnly );
        newPropertyValueTextBox.setReadOnly( readOnly );
        newPropertyNameTextBox.setReadOnly( readOnly );
    }

    @Override
    public void setDataProvider( ListDataProvider<PropertyRow> dataProvider ) {
        if ( !dataProvider.getDataDisplays().contains( dataGrid ) ) {
            dataProvider.addDataDisplay( dataGrid );
        }
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

    @UiHandler("addPropertyButton")
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
        public void update( final int index,
                            final T object,
                            final C value ) {

            if ( !readOnly ) {
                PropertyRow propertyRow = (PropertyRow) object;
                String sValue = (String) value;
                //TODO add validations
                propertyRow.setName( sValue );
            } else {
                dataGrid.redraw();
            }
        }
    }

    private class PropertyValueFieldUpdater<T, C> implements FieldUpdater<T, C> {

        private EditTextCell cell;

        PropertyValueFieldUpdater( EditTextCell cell ) {
            this.cell = cell;
        }

        @Override
        public void update( final int index,
                            final T object,
                            final C value ) {

            if ( !readOnly ) {
                PropertyRow propertyRow = (PropertyRow) object;
                String sValue = (String) value;
                //TODO add validations
                propertyRow.setValue( sValue );
            } else {
                dataGrid.redraw();
            }
        }

    }

}
