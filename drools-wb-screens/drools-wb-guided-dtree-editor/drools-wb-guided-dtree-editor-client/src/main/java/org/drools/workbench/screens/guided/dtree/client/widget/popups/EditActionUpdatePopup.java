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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionFieldValue;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionUpdateNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.BoundNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.Node;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionFieldValueImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionUpdateNodeImpl;
import org.drools.workbench.screens.guided.dtree.client.resources.i18n.GuidedDecisionTreeConstants;
import org.drools.workbench.screens.guided.dtree.client.widget.utils.ValueUtilities;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.uberfire.mvp.ParameterizedCommand;

public class EditActionUpdatePopup extends BaseModal {

    interface EditActionUpdateBinder
            extends
            UiBinder<Widget, EditActionUpdatePopup> {

    }

    private static EditActionUpdateBinder uiBinder = GWT.create( EditActionUpdateBinder.class );

    private final ActionUpdateNode node;
    private final ActionUpdateNode clone;
    private final Command callback;

    private final AsyncPackageDataModelOracle oracle;

    private final ParameterizedCommand<ActionFieldValue> onDeleteCallback = new ParameterizedCommand<ActionFieldValue>() {
        @Override
        public void execute( final ActionFieldValue afv ) {
            if ( clone.getFieldValues() == null ) {
                return;
            }
            final int index = clone.getFieldValues().indexOf( afv );
            clone.getFieldValues().remove( index );
            containerFieldValues.remove( index );
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
    ControlGroup bindingGroup;

    @UiField
    ListBox bindingListBox;

    @UiField
    CheckBox modifyCheckBox;

    @UiField
    VerticalPanel containerFieldValues;

    @UiField
    Button addFieldValueButton;

    /**
     * Edit the given ActionModifyNode. A clone is taken whilst editing is in progress to preserve the state
     * of the original node should editing be cancelled by the User.
     * @param node The node to edit
     * @param callback Callback to execute when the User commits changes
     */
    public EditActionUpdatePopup( final ActionUpdateNode node,
                                  final AsyncPackageDataModelOracle oracle,
                                  final Command callback ) {
        setTitle( GuidedDecisionTreeConstants.INSTANCE.popupTitleEditActionUpdate() );
        setWidth( "700px" );

        add( uiBinder.createAndBindUi( this ) );
        add( footer );

        this.node = node;
        this.clone = cloneNode( node );
        this.oracle = oracle;
        this.callback = callback;

        initialiseModify();
        initialiseBoundTypes();
        initialiseFieldValues();
    }

    //Clone node whilst editing to preserve original node should User cancel the edit
    private ActionUpdateNode cloneNode( final ActionUpdateNode node ) {
        final ActionUpdateNode clone = new ActionUpdateNodeImpl( node.getBoundNode() );
        clone.getFieldValues().addAll( clone( node.getFieldValues() ) );
        clone.setParent( node.getParent() );
        clone.setModify( node.isModify() );
        return clone;
    }

    private List<ActionFieldValue> clone( final List<ActionFieldValue> afvs ) {
        final List<ActionFieldValue> clone = new ArrayList<ActionFieldValue>();
        for ( ActionFieldValue afv : afvs ) {
            clone.add( clone( afv ) );
        }
        return clone;
    }

    private ActionFieldValue clone( final ActionFieldValue afv ) {
        final ActionFieldValue clone = new ActionFieldValueImpl( afv.getFieldName(),
                                                                 ValueUtilities.clone( afv.getValue() ) );
        return clone;
    }

    private void initialiseModify() {
        modifyCheckBox.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( final ValueChangeEvent<Boolean> event ) {
                clone.setModify( event.getValue() );
            }
        } );

        this.modifyCheckBox.setValue( clone.isModify() );
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
                clone.getFieldValues().clear();
                initialiseFieldValues();
            }
        } );

        bindingListBox.setSelectedIndex( selectedIndex );
    }

    private void initialiseFieldValues() {
        containerFieldValues.clear();
        addFieldValueButton.setEnabled( true );
        for ( ActionFieldValue afv : clone.getFieldValues() ) {
            final ActionFieldValueEditor afvEditor = new ActionFieldValueEditor( clone.getBoundNode().getClassName(),
                                                                                 afv,
                                                                                 clone.getFieldValues(),
                                                                                 oracle,
                                                                                 onDeleteCallback );
            containerFieldValues.add( afvEditor );
        }
    }

    @UiHandler("addFieldValueButton")
    void onAddFieldValueButtonClick( final ClickEvent event ) {
        final ActionFieldValue afv = new ActionFieldValueImpl();
        final ActionFieldValueEditor afvEditor = new ActionFieldValueEditor( clone.getBoundNode().getClassName(),
                                                                             afv,
                                                                             clone.getFieldValues(),
                                                                             oracle,
                                                                             onDeleteCallback );
        containerFieldValues.add( afvEditor );
        clone.getFieldValues().add( afv );
    }

    private void onOKButtonClick() {
        node.setBoundNode( clone.getBoundNode() );
        node.setModify( clone.isModify() );
        node.getFieldValues().clear();
        node.getFieldValues().addAll( clone.getFieldValues() );

        if ( callback != null ) {
            callback.execute();
        }

        hide();
    }

}
