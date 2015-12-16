/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ConstraintNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ConstraintNodeImpl;
import org.drools.workbench.screens.guided.dtree.client.resources.i18n.GuidedDecisionTreeConstants;
import org.drools.workbench.screens.guided.dtree.client.widget.utils.BindingUtilities;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.HumanReadable;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class EditConstraintPopup extends BaseModal {

    interface EditConstraintBinder
            extends
            UiBinder<Widget, EditConstraintPopup> {

    }

    private static EditConstraintBinder uiBinder = GWT.create( EditConstraintBinder.class );

    private final ConstraintNode node;
    private final ConstraintNode clone;
    private final Command callback;

    private final AsyncPackageDataModelOracle oracle;

    private final ValueEditorFactory valueEditorFactory = new ValueEditorFactory() {
        @Override
        protected Map<String, String> getCurrentValueMap() {
            //Get a Map<String(FieldName), String(FieldValue)> of all Constraints proceeding the one being edited
            //Dependent enumerations span a single Pattern so only walk the tree up to the first non-ConstraintNode
            //as this represents the boundary between Patterns.
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
    };

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
    FormGroup bindingGroup;

    @UiField
    HelpBlock bindingHelpInline;

    @UiField
    BindingTextBox bindingTextBox;

    @UiField
    ListBox operatorListBox;

    @UiField
    FormGroup valueGroup;

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

        setBody( uiBinder.createAndBindUi( this ) );
        add( footer );

        bindingTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                bindingGroup.setValidationState( ValidationState.NONE );
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

        //Get an editor for the class, field and operator
        final Widget editor = valueEditorFactory.getValueEditor( clone.getClassName(),
                                                                 clone.getFieldName(),
                                                                 clone,
                                                                 oracle,
                                                                 OperatorsOracle.operatorRequiresList( operator ) );
        valueHolder.clear();
        if ( editor != null ) {
            valueGroup.setVisible( true );
            valueHolder.setWidget( editor );
        }
    }

    private void onOKButtonClick() {
        boolean hasError = false;
        final String binding = clone.getBinding();
        if ( !( binding == null || binding.isEmpty() ) ) {
            if ( !BindingUtilities.isUniqueInPath( binding,
                                                   clone ) ) {
                bindingGroup.setValidationState( ValidationState.ERROR );
                bindingHelpInline.setText( GuidedDecisionTreeConstants.INSTANCE.bindingIsNotUnique() );
                hasError = true;
            }
        } else {
            bindingGroup.setValidationState( ValidationState.NONE );
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
