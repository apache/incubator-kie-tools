/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.guided.dtree.client.widget.popups;

import java.util.Date;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.HasValue;
import org.drools.workbench.models.guided.dtree.shared.model.values.Value;
import org.drools.workbench.screens.guided.dtree.client.resources.i18n.GuidedDecisionTreeConstants;
import org.drools.workbench.screens.guided.dtree.client.widget.utils.ValueUtilities;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.widget.EnumDropDownUtilities;
import org.kie.workbench.common.widgets.client.widget.TextBoxFactory;
import org.uberfire.ext.widgets.common.client.common.DatePicker;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

public abstract class ValueEditorFactory {

    private static final String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();

    public Widget getValueEditor( final String className,
                                  final String fieldName,
                                  final HasValue hasValue,
                                  final AsyncPackageDataModelOracle oracle,
                                  final boolean isMultipleSelect ) {
        String dataType = oracle.getFieldType( className,
                                               fieldName );

        //Operators "contained in" and "not contained in" fallback to Strings
        if ( isMultipleSelect ) {
            dataType = DataType.TYPE_STRING;
        }

        //Fields with enumerations fallback to Strings
        DropDownData dd = null;
        if ( oracle.hasEnums( className,
                              fieldName ) ) {
            final Map<String, String> currentValueMap = getCurrentValueMap();
            dd = oracle.getEnums( className,
                                  fieldName,
                                  currentValueMap );
        }

        //Ensure Node has a value if needed
        if ( hasValue.getValue() == null ) {
            final Value value = ValueUtilities.makeEmptyValue( dataType );
            if ( value == null ) {
                ErrorPopup.showMessage( GuidedDecisionTreeConstants.INSTANCE.dataTypeNotSupported0( dataType ) );
                return null;
            } else {
                hasValue.setValue( value );
            }
        }

        //Setup the correct widget corresponding to the data type
        if ( dataType.equals( DataType.TYPE_DATE ) ) {
            final DatePicker valueEditor = new DatePicker( false );

            // Wire-up update handler
            valueEditor.addValueChangeHandler( new ValueChangeHandler<Date>() {
                @Override
                public void onValueChange( final ValueChangeEvent<Date> event ) {
                    hasValue.getValue().setValue( valueEditor.getValue() );
                }
            } );

            //Set Widget's value
            valueEditor.setFormat( DATE_FORMAT );
            valueEditor.setValue( (Date) hasValue.getValue().getValue() );

            return valueEditor;

        } else if ( dataType.equals( DataType.TYPE_BOOLEAN ) ) {
            final ListBox valueEditor = new ListBox();

            valueEditor.addItem( "true" );
            valueEditor.addItem( "false" );

            // Wire-up update handler
            valueEditor.addClickHandler( new ClickHandler() {

                public void onClick( final ClickEvent event ) {
                    final String txtValue = valueEditor.getValue( valueEditor.getSelectedIndex() );
                    final Boolean value = Boolean.valueOf( txtValue );
                    hasValue.getValue().setValue( value );
                }

            } );

            //Set Widget's value
            valueEditor.setSelectedIndex( hasValue.getValue().getValue().equals( Boolean.TRUE ) ? 0 : 1 );
            return valueEditor;

        } else {

            //If we have enumeration data show a ListBox
            if ( dd != null ) {
                final ListBox valueEditor = makeListBox( dd,
                                                         hasValue,
                                                         oracle,
                                                         isMultipleSelect );
                return valueEditor;
            }

            //Otherwise show a TextBox
            final TextBox valueEditor = TextBoxFactory.getTextBox( dataType );

            //Wire-up Handlers before setting value, as invalid values cause the ValueChangeHandler to be invoked
            valueEditor.addValueChangeHandler( new ValueChangeHandler<String>() {
                @Override
                public void onValueChange( final ValueChangeEvent<String> event ) {
                    hasValue.getValue().setValue( event.getValue() );
                }
            } );

            //Set Widget's value
            valueEditor.setText( ValueUtilities.convertNodeValue( hasValue.getValue() ) );
            return valueEditor;
        }
    }

    //Get a Map<String(FieldName), String(FieldValue)> of all Constraints proceeding the one being edited
    //Dependent enumerations span a single Pattern so only walk the tree up to the first non-ConstraintNode
    //as this represents the boundary between Patterns.
    protected abstract Map<String, String> getCurrentValueMap();

    private ListBox makeListBox( final DropDownData dd,
                                 final HasValue hasValue,
                                 final AsyncPackageDataModelOracle oracle,
                                 final boolean isMultipleSelect ) {
        final ListBox lb = new ListBox( isMultipleSelect );

        final EnumDropDownUtilities utilities = new EnumDropDownUtilities() {
            @Override
            protected int addItems( final ListBox listBox ) {
                return 0;
            }

            @Override
            protected void selectItem( final ListBox listBox ) {
                final int itemCount = listBox.getItemCount();
                listBox.setEnabled( itemCount > 0 );
                if ( itemCount > 0 ) {
                    listBox.setSelectedIndex( 0 );
                    hasValue.getValue().setValue( listBox.getValue( 0 ) );
                } else {
                    hasValue.getValue().setValue( "" );
                }
            }
        };

        final String value = hasValue.getValue().getValue().toString();
        utilities.setDropDownData( value,
                                   dd,
                                   isMultipleSelect,
                                   oracle.getResourcePath(),
                                   lb );

        // Wire up update handler
        lb.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                String value = null;
                if ( lb.isMultipleSelect() ) {
                    for ( int i = 0; i < lb.getItemCount(); i++ ) {
                        if ( lb.isItemSelected( i ) ) {
                            if ( value == null ) {
                                value = lb.getValue( i );
                            } else {
                                value = value + "," + lb.getValue( i );
                            }
                        }
                    }
                } else {
                    int index = lb.getSelectedIndex();
                    if ( index > -1 ) {
                        value = lb.getValue( index );
                    }
                }
                hasValue.getValue().setValue( value );
            }
        } );
        return lb;
    }

}
