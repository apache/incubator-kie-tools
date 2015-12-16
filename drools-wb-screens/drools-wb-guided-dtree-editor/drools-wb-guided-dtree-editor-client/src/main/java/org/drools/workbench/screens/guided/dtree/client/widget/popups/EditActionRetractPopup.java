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

import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionRetractNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.BoundNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionRetractNodeImpl;
import org.drools.workbench.screens.guided.dtree.client.resources.i18n.GuidedDecisionTreeConstants;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class EditActionRetractPopup extends BaseModal {

    interface EditActionRetractBinder
            extends
            UiBinder<Widget, EditActionRetractPopup> {

    }

    private static EditActionRetractBinder uiBinder = GWT.create( EditActionRetractBinder.class );

    private final ActionRetractNode node;
    private final ActionRetractNode clone;
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
    FormGroup bindingGroup;

    @UiField
    ListBox bindingListBox;

    /**
     * Edit the given ActionRetractNode. A clone is taken whilst editing is in progress to preserve the state
     * of the original node should editing be cancelled by the User.
     * @param node The node to edit
     * @param callback Callback to execute when the User commits changes
     */
    public EditActionRetractPopup( final ActionRetractNode node,
                                   final Command callback ) {
        setTitle( GuidedDecisionTreeConstants.INSTANCE.popupTitleEditActionRetract() );

        setBody( uiBinder.createAndBindUi( this ) );
        add( footer );

        this.node = node;
        this.clone = cloneNode( node );
        this.callback = callback;

        initialiseBoundTypes();
    }

    //Clone node whilst editing to preserve original node should User cancel the edit
    private ActionRetractNode cloneNode( final ActionRetractNode node ) {
        final ActionRetractNode clone = new ActionRetractNodeImpl( node.getBoundNode() );
        clone.setParent( node.getParent() );
        return clone;
    }

    private void initialiseBoundTypes() {
        //Extract all bindings available on the path to the root
        final Map<String, TypeNode> bindings = new TreeMap<String, TypeNode>();
        Node parent = clone.getParent();
        while ( parent != null ) {
            if ( parent instanceof TypeNode ) {
                final TypeNode tn = (TypeNode) parent;
                if ( tn.isBound() ) {
                    bindings.put( tn.getBinding(),
                                  tn );
                }
            }
            parent = parent.getParent();
        }

        bindingListBox.setEnabled( !bindings.isEmpty() );
        if ( bindings.isEmpty() ) {
            bindingListBox.addItem( GuidedDecisionTreeConstants.INSTANCE.noBindings() );
            return;
        }

        //Add them to the ListBox
        int selectedIndex = 0;
        final BoundNode boundNode = clone.getBoundNode();
        for ( String binding : bindings.keySet() ) {
            bindingListBox.addItem( binding );
            if ( boundNode != null ) {
                if ( binding.equals( boundNode.getBinding() ) ) {
                    selectedIndex = bindingListBox.getItemCount() - 1;
                }
            }
        }

        //Attach event handler before we set the selected index in case we're selecting the first item
        bindingListBox.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( final ChangeEvent event ) {
                final String binding = bindingListBox.getItemText( bindingListBox.getSelectedIndex() );
                clone.setBoundNode( bindings.get( binding ) );
            }
        } );

        bindingListBox.setSelectedIndex( selectedIndex );
    }

    private void onOKButtonClick() {
        node.setBoundNode( clone.getBoundNode() );

        if ( callback != null ) {
            callback.execute();
        }

        hide();
    }

}
