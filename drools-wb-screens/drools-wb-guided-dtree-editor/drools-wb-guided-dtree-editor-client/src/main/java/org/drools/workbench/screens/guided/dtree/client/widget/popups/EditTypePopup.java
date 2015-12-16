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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionRetractNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionUpdateNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.TypeNodeImpl;
import org.drools.workbench.screens.guided.dtree.client.resources.i18n.GuidedDecisionTreeConstants;
import org.drools.workbench.screens.guided.dtree.client.widget.utils.BindingUtilities;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class EditTypePopup extends BaseModal {

    interface EditTypeBinder
            extends
            UiBinder<Widget, EditTypePopup> {

    }

    private static EditTypeBinder uiBinder = GWT.create( EditTypeBinder.class );

    private final TypeNode node;
    private final TypeNode clone;
    private final Command callback;

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
    FormGroup bindingGroup;

    @UiField
    HelpBlock bindingHelpInline;

    @UiField
    BindingTextBox bindingTextBox;

    /**
     * Edit the given TypeNode. A clone is taken whilst editing is in progress to preserve the state
     * of the original node should editing be cancelled by the User. Bindings are checked to be unique
     * in the path from this node being edited to the tree's root. When the User commits the changes
     * the provided callback is executed and this popup closed.
     * @param node The node to edit
     * @param callback Callback to execute when the User commits changes
     */
    public EditTypePopup( final TypeNode node,
                          final Command callback ) {
        setTitle( GuidedDecisionTreeConstants.INSTANCE.popupTitleEditType() );

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
        this.callback = callback;

        this.classNameLabel.setText( clone.getClassName() );
        this.bindingTextBox.setText( clone.getBinding() );
    }

    //Clone node whilst editing to preserve original node should User cancel the edit
    private TypeNode cloneNode( final TypeNode node ) {
        final TypeNode clone = new TypeNodeImpl( node.getClassName() );
        clone.setParent( node.getParent() );
        clone.setBinding( node.getBinding() );
        return clone;
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
        } else if ( hasBoundChildren( node ) ) {
            bindingGroup.setValidationState( ValidationState.ERROR );
            bindingHelpInline.setText( GuidedDecisionTreeConstants.INSTANCE.bindingIsUsed() );
            hasError = true;

        } else {
            bindingGroup.setValidationState( ValidationState.NONE );
        }

        if ( hasError ) {
            return;
        }

        //Copy changes into the original node
        node.setBinding( clone.getBinding() );

        if ( callback != null ) {
            callback.execute();
        }

        hide();
    }

    private boolean hasBoundChildren( final Node node ) {
        for ( Node n : node.getChildren() ) {
            if ( n instanceof ActionRetractNode ) {
                final ActionRetractNode arn = (ActionRetractNode) n;
                if ( arn.getBoundNode().equals( node ) ) {
                    return true;
                }
            } else if ( n instanceof ActionUpdateNode ) {
                final ActionUpdateNode aun = (ActionUpdateNode) n;
                if ( aun.getBoundNode().equals( node ) ) {
                    return true;
                }
            }

            if ( hasBoundChildren( n ) ) {
                return true;
            }
        }
        return false;
    }

}
