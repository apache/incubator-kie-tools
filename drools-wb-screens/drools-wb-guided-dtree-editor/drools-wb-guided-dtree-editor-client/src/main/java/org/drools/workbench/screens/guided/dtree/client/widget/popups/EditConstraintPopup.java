/*
 * Copyright 2014 JBoss Inc
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

package org.drools.workbench.screens.guided.dtree.client.widget.popups;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ConstraintNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ConstraintNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.values.Value;
import org.drools.workbench.screens.guided.dtree.client.resources.i18n.GuidedDecisionTreeConstants;
import org.drools.workbench.screens.guided.dtree.client.widget.utils.BindingUtilities;
import org.drools.workbench.screens.guided.dtree.client.widget.utils.ValueUtilities;
import org.kie.uberfire.client.common.popups.KieBaseModal;
import org.kie.uberfire.client.common.popups.errors.ErrorPopup;
import org.kie.uberfire.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.HumanReadable;
import org.kie.workbench.common.widgets.client.widget.EnumDropDownUtilities;
import org.kie.workbench.common.widgets.client.widget.PopupDatePicker;
import org.kie.workbench.common.widgets.client.widget.TextBoxFactory;
import org.uberfire.client.callbacks.Callback;

public class EditConstraintPopup extends KieBaseModal {

    interface EditConstraintBinder
            extends
            UiBinder<Widget, EditConstraintPopup> {

    }

    private static EditConstraintBinder uiBinder = GWT.create( EditConstraintBinder.class );

    private final ConstraintNode node;
    private final ConstraintNode clone;
    private final Command callback;

    private final AsyncPackageDataModelOracle oracle;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            onOKButtonClick();
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( okCommand,
                                                                                      cancelCommand );

    @UiField
    Label classNameLabel;

    @UiField
    Label fieldNameLabel;

    @UiField
    ControlGroup bindingGroup;

    @UiField
    HelpInline bindingHelpInline;

    @UiField
    BindingTextBox bindingTextBox;

    @UiField
    ListBox operatorListBox;

    @UiField
    ControlGroup valueGroup;

    @UiField
    SimplePanel valueHolder;

    /**
     * Edit the given ConstraintNode. A clone is taken whilst editing is in progress to preserve the state
     * of the original node should editing be cancelled by the User. Bindings are checked to be unique
     * in the path from this node being edited to the tree's root. When the User commits the changes
     * the provided callback is executed and this popup closed.
     * @param node The node to edit
     * @param oracle DataModelOracle to drive population of popup
     * @param callback Callback to execute when the User commits changes
     */
    public EditConstraintPopup( final ConstraintNode node,
                                final AsyncPackageDataModelOracle oracle,
                                final Command callback ) {
        setTitle( GuidedDecisionTreeConstants.INSTANCE.popupTitleEditConstraint() );

        add( uiBinder.createAndBindUi( this ) );
        add( footer );

        bindingTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                bindingGroup.setType( ControlGroupType.NONE );
                bindingHelpInline.setText( "" );
            }
        } );

        bindingTextBox.addBlurHandler( new BlurHandler() {
            @Override
            public void onBlur( final BlurEvent event ) {
                clone.setBinding( bindingTextBox.getText() );
            }
        } );

        this.node = node;
        this.clone = cloneNode( node );
        this.oracle = oracle;
        this.callback = callback;

        this.classNameLabel.setText( clone.getClassName() );
        this.fieldNameLabel.setText( clone.getFieldName() );
        this.bindingTextBox.setText( clone.getBinding() );

        initialiseOperators();
    }

    //Clone node whilst editing to preserve original node should User cancel the edit
    private ConstraintNode cloneNode( final ConstraintNode node ) {
        final ConstraintNode clone = new ConstraintNodeImpl( node.getClassName(),
                                                             node.getFieldName() );
        if ( node.getOperator() != null ) {
            clone.setOperator( node.getOperator() );
        }
        if ( node.getValue() != null ) {
            clone.setValue( node.getValue() );
        }
        clone.setParent( node.getParent() );
        clone.setBinding( node.getBinding() );
        return clone;
    }

    private void initialiseOperators() {
        oracle.getOperatorCompletions( clone.getClassName(),
                                       clone.getFieldName(),
                                       new Callback<String[]>() {
                                           @Override
                                           public void callback( final String[] operators ) {
                                               operatorListBox.clear();
                                               operatorListBox.setEnabled( false );
                                               if ( operators == null ) {
                                                   return;
                                               }

                                               int selectedIndex = 0;
                                               operatorListBox.setEnabled( true );
                                               operatorListBox.addItem( GuidedDecisionTreeConstants.INSTANCE.noOperator() );
                                               for ( int index = 0; index < operators.length; index++ ) {
                                                   final String operator = operators[ index ];
                                                   if ( operator.equals( clone.getOperator() ) ) {
                                                       selectedIndex = index + 1;
                                                   }
                                                   operatorListBox.addItem( HumanReadable.getOperatorDisplayName( operator ),
                                                                            operator );
                                               }
                                               operatorListBox.setSelectedIndex( selectedIndex );
                                               initialiseValue();
                                           }
                                       } );

        operatorListBox.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( final ChangeEvent event ) {
                final int selectedIndex = operatorListBox.getSelectedIndex();
                if ( selectedIndex == 0 ) {
                    clone.setOperator( null );
                } else {
                    clone.setOperator( operatorListBox.getValue( selectedIndex ) );
                }
                initialiseValue();
            }
        } );
    }

    private void initialiseValue() {
        String dataType = oracle.getFieldType( clone.getClassName(),
                                               clone.getFieldName() );

        //Don't show a ValueEditor if no operator has been selected
        if ( operatorListBox.getSelectedIndex() == 0 ) {
            clone.setValue( null );
            valueGroup.setVisible( false );
            valueHolder.clear();
            return;
        }

        //Don't show a ValueEditor if the operator does not require a Value
        final String operator = operatorListBox.getValue( operatorListBox.getSelectedIndex() );
        if ( !OperatorsOracle.isValueRequired( operator ) ) {
            clone.setValue( null );
            valueGroup.setVisible( false );
            valueHolder.clear();
            return;
        }

        //Don't show a ValueEditor if the field is "this"
        if ( dataType.equals( DataType.TYPE_THIS ) ) {
            clone.setValue( null );
            valueGroup.setVisible( false );
            valueHolder.clear();
            return;
        }

        //Operators "contained in" and "not contained in" fallback to Strings
        if ( OperatorsOracle.operatorRequiresList( operator ) ) {
            dataType = DataType.TYPE_STRING;
        }

        //Fields with enumerations fallback to Strings
        DropDownData dd = null;
        if ( oracle.hasEnums( clone.getClassName(),
                              clone.getFieldName() ) ) {
            final Map<String, String> currentValueMap = getCurrentValueMap();
            dd = oracle.getEnums( clone.getClassName(),
                                  clone.getFieldName(),
                                  currentValueMap );
            if ( dd != null ) {
                dataType = DataType.TYPE_STRING;
            }
        }

        //Ensure Node has a value if needed
        if ( clone.getValue() == null ) {
            final Value value = ValueUtilities.makeEmptyValue( dataType );
            if ( value == null ) {
                ErrorPopup.showMessage( GuidedDecisionTreeConstants.INSTANCE.dataTypeNotSupported0( dataType ) );
                return;
            } else {
                clone.setValue( value );
            }
        }

        valueGroup.setVisible( true );

        //Setup the correct widget corresponding to the data type
        if ( dataType.equals( DataType.TYPE_DATE ) ) {
            final PopupDatePicker valueEditor = new PopupDatePicker( false );
            valueHolder.setWidget( valueEditor );

            // Wire-up update handler
            valueEditor.addValueChangeHandler( new ValueChangeHandler<Date>() {

                public void onValueChange( final ValueChangeEvent<Date> event ) {
                    clone.getValue().setValue( event.getValue() );
                }

            } );

            //Set Widget's value
            valueEditor.setValue( (Date) clone.getValue().getValue() );

        } else if ( dataType.equals( DataType.TYPE_BOOLEAN ) ) {
            final ListBox valueEditor = new ListBox();
            valueHolder.setWidget( valueEditor );

            valueEditor.addItem( "true" );
            valueEditor.addItem( "false" );

            // Wire-up update handler
            valueEditor.addClickHandler( new ClickHandler() {

                public void onClick( final ClickEvent event ) {
                    final String txtValue = valueEditor.getValue( valueEditor.getSelectedIndex() );
                    final Boolean value = Boolean.valueOf( txtValue );
                    clone.getValue().setValue( value );
                }

            } );

            //Set Widget's value
            valueEditor.setSelectedIndex( clone.getValue().getValue().equals( Boolean.TRUE ) ? 0 : 1 );

        } else {

            //If we have enumeration data show a ListBox
            if ( dd != null ) {
                final ListBox valueEditor = makeListBox( dd );
                valueHolder.setWidget( valueEditor );
                return;
            }

            //Otherwise show a TextBox
            final TextBox valueEditor = TextBoxFactory.getTextBox( dataType );
            valueHolder.setWidget( valueEditor );

            //Wire-up Handlers before setting value, as invalid values cause the ValueChangeHandler to be invoked
            valueEditor.addValueChangeHandler( new ValueChangeHandler<String>() {
                @Override
                public void onValueChange( final ValueChangeEvent<String> event ) {
                    clone.getValue().setValue( event.getValue() );
                }
            } );

            //Set Widget's value
            valueEditor.setText( ValueUtilities.convertNodeValue( clone.getValue() ) );
        }
    }

    //Get a Map<String(FieldName), String(FieldValue)> of all Constraints proceeding the one being edited
    //Dependent enumerations span a single Pattern so only walk the tree up to the first non-ConstraintNode
    //as this represents the boundary between Patterns.
    private Map<String, String> getCurrentValueMap() {
        final Map<String, String> currentValueMap = new HashMap<String, String>();
        Node parent = node.getParent();
        while ( parent != null ) {
            if ( parent instanceof ConstraintNode ) {
                final ConstraintNode cn = (ConstraintNode) parent;
                currentValueMap.put( cn.getFieldName(),
                                     cn.getValue().toString() );
                parent = parent.getParent();
            } else {
                parent = null;
            }
        }
        return currentValueMap;
    }

    private ListBox makeListBox( final DropDownData dd ) {
        final boolean isMultipleSelect = OperatorsOracle.operatorRequiresList( clone.getOperator() );
        final ListBox lb = makeListBox( dd,
                                        isMultipleSelect );

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
                clone.getValue().setValue( value );
            }
        } );
        return lb;
    }

    private ListBox makeListBox( final DropDownData dd,
                                 final boolean isMultipleSelect ) {
        final ListBox lb = new ListBox( isMultipleSelect );

        final EnumDropDownUtilities utilities = new EnumDropDownUtilities() {
            @Override
            protected int addItems( final com.google.gwt.user.client.ui.ListBox listBox ) {
                return 0;
            }

            @Override
            protected void selectItem( final com.google.gwt.user.client.ui.ListBox listBox ) {
                final int itemCount = listBox.getItemCount();
                listBox.setEnabled( itemCount > 0 );
                if ( itemCount > 0 ) {
                    listBox.setSelectedIndex( 0 );
                    clone.getValue().setValue( listBox.getValue( 0 ) );
                } else {
                    clone.getValue().setValue( "" );
                }
            }
        };

        final String value = clone.getValue().getValue().toString();
        utilities.setDropDownData( value,
                                   dd,
                                   isMultipleSelect,
                                   oracle.getResourcePath(),
                                   lb );

        return lb;
    }

    private void onOKButtonClick() {
        boolean hasError = false;
        final String binding = clone.getBinding();
        if ( !( binding == null || binding.isEmpty() ) ) {
            if ( !BindingUtilities.isUniqueInPath( binding,
                                                   clone ) ) {
                bindingGroup.setType( ControlGroupType.ERROR );
                bindingHelpInline.setText( GuidedDecisionTreeConstants.INSTANCE.bindingIsNotUnique() );
                hasError = true;
            }
        } else {
            bindingGroup.setType( ControlGroupType.NONE );
        }

        if ( hasError ) {
            return;
        }

        //Copy changes into the original node
        node.setBinding( clone.getBinding() );
        node.setOperator( clone.getOperator() );
        node.setValue( clone.getValue() );

        if ( callback != null ) {
            callback.execute();
        }

        hide();
    }

}
