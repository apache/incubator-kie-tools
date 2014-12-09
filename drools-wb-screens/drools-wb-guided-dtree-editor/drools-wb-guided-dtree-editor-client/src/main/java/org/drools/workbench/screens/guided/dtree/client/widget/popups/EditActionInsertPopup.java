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
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionInsertNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionFieldValueImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ActionInsertNodeImpl;
import org.drools.workbench.screens.guided.dtree.client.resources.i18n.GuidedDecisionTreeConstants;
import org.drools.workbench.screens.guided.dtree.client.widget.utils.ValueUtilities;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;
import org.uberfire.mvp.ParameterizedCommand;

public class EditActionInsertPopup extends BaseModal {

    interface EditActionInsertBinder
            extends
            UiBinder<Widget, EditActionInsertPopup> {

    }

    private static EditActionInsertBinder uiBinder = GWT.create( EditActionInsertBinder.class );

    private final ActionInsertNode node;
    private final ActionInsertNode clone;
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
    ControlGroup classNameGroup;

    @UiField
    ListBox classNameListBox;

    @UiField
    CheckBox insertLogicalCheckBox;

    @UiField
    VerticalPanel containerFieldValues;

    @UiField
    Button addFieldValueButton;

    /**
     * Edit the given ActionInsertNode. A clone is taken whilst editing is in progress to preserve the state
     * of the original node should editing be cancelled by the User.
     * @param node The node to edit
     * @param callback Callback to execute when the User commits changes
     */
    public EditActionInsertPopup( final ActionInsertNode node,
                                  final AsyncPackageDataModelOracle oracle,
                                  final Command callback ) {
        setTitle( GuidedDecisionTreeConstants.INSTANCE.popupTitleEditActionInsert() );
        setWidth( "700px" );

        add( uiBinder.createAndBindUi( this ) );
        add( footer );

        this.node = node;
        this.clone = cloneNode( node );
        this.oracle = oracle;
        this.callback = callback;

        initialiseInsertLogicalCheckBox();
        initialiseClassNames();
        initialiseFieldValues();
    }

    //Clone node whilst editing to preserve original node should User cancel the edit
    private ActionInsertNode cloneNode( final ActionInsertNode node ) {
        final ActionInsertNode clone = new ActionInsertNodeImpl( node.getClassName() );
        clone.getFieldValues().addAll( clone( node.getFieldValues() ) );
        clone.setLogicalInsertion( node.isLogicalInsertion() );
        clone.setParent( node.getParent() );
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

    private void initialiseInsertLogicalCheckBox() {
        insertLogicalCheckBox.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( final ValueChangeEvent<Boolean> event ) {
                clone.setLogicalInsertion( event.getValue() );
            }
        } );

        this.insertLogicalCheckBox.setValue( clone.isLogicalInsertion() );
    }

    private void initialiseClassNames() {
        //Extract all class names available
        final String[] classNames = oracle.getFactTypes();
        classNameListBox.setEnabled( !( classNames == null || classNames.length == 0 ) );
        if ( classNames == null || classNames.length == 0 ) {
            classNameListBox.addItem( GuidedDecisionTreeConstants.INSTANCE.noBindings() );
            return;
        }

        //Add them to the ListBox
        int selectedIndex = 0;
        for ( String className : classNames ) {
            classNameListBox.addItem( className );
            if ( className.equals( clone.getClassName() ) ) {
                selectedIndex = classNameListBox.getItemCount() - 1;
            }
        }

        //Attach event handler before we set the selected index in case we're selecting the first item
        classNameListBox.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( final ChangeEvent event ) {
                final String className = classNameListBox.getItemText( classNameListBox.getSelectedIndex() );
                clone.setClassName( className );
                clone.getFieldValues().clear();
                initialiseFieldValues();
            }
        } );

        classNameListBox.setSelectedIndex( selectedIndex );
    }

    private void initialiseFieldValues() {
        containerFieldValues.clear();
        addFieldValueButton.setEnabled( true );
        for ( ActionFieldValue afv : clone.getFieldValues() ) {
            final ActionFieldValueEditor afvEditor = new ActionFieldValueEditor( clone.getClassName(),
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
        final ActionFieldValueEditor afvEditor = new ActionFieldValueEditor( clone.getClassName(),
                                                                             afv,
                                                                             clone.getFieldValues(),
                                                                             oracle,
                                                                             onDeleteCallback );
        containerFieldValues.add( afvEditor );
        clone.getFieldValues().add( afv );
    }

    private void onOKButtonClick() {
        node.setClassName( clone.getClassName() );
        node.setLogicalInsertion( clone.isLogicalInsertion() );
        node.getFieldValues().clear();
        node.getFieldValues().addAll( clone.getFieldValues() );

        if ( callback != null ) {
            callback.execute();
        }

        hide();
    }

}
