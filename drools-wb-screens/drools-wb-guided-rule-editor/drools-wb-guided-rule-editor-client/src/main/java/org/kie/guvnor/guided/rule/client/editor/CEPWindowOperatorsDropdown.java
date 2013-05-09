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
package org.kie.guvnor.guided.rule.client.editor;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.models.commons.backend.rule.SharedConstants;
import org.drools.guvnor.models.commons.shared.rule.HasCEPWindow;
import org.kie.guvnor.commons.ui.client.resources.i18n.HumanReadableConstants;
import org.kie.guvnor.datamodel.oracle.CEPOracle;
import org.kie.guvnor.guided.rule.client.resources.GuidedRuleEditorResources;
import org.kie.guvnor.commons.ui.client.resources.HumanReadable;
import org.uberfire.client.common.AbstractRestrictedEntryTextBox;

import java.util.List;

/**
 * Drop-down Widget for Operators including supplementary controls for CEP
 * operator parameters
 */
public class CEPWindowOperatorsDropdown extends Composite
        implements
        HasValueChangeHandlers<OperatorSelection> {

    private List<String> operators = CEPOracle.getCEPWindowOperators();
    private ListBox box;
    private HorizontalPanel parametersContainer = new HorizontalPanel();
    private HorizontalPanel windowContainer = new HorizontalPanel();

    private boolean isReadOnly = false;

    protected HasCEPWindow hcw;

    //Parameter value defining the server-side class used to generate DRL for CEP operator parameters
    private static final String CEP_OPERATOR_PARAMETER_GENERATOR = "org.kie.guvnor.guided.rule.backend.server.util.CEPWindowOperatorParameterDRLBuilder";

    public CEPWindowOperatorsDropdown() {
        windowContainer.setStylePrimaryName( GuidedRuleEditorResources.INSTANCE.css().container() );
        initWidget( windowContainer );
    }

    public CEPWindowOperatorsDropdown( HasCEPWindow hcw,
                                       boolean isReadOnly ) {
        this();
        this.isReadOnly = isReadOnly;
        setCEPWindow( hcw );
    }

    public void setCEPWindow( HasCEPWindow hcw ) {
        this.hcw = hcw;
        windowContainer.clear();
        windowContainer.add( getDropDown() );
        windowContainer.add( getOperatorExtension() );
    }

    /**
     * Gets the index of the currently-selected item.
     * @return
     */
    public int getSelectedIndex() {
        return box.getSelectedIndex();
    }

    /**
     * Gets the value associated with the item at a given index.
     * @param index
     * @return
     */
    public String getValue( int index ) {
        return box.getValue( index );
    }

    //Additional widget for CEP Window operator parameter
    private Widget getOperatorExtension() {
        parametersContainer.setStylePrimaryName( GuidedRuleEditorResources.INSTANCE.css().container() );
        return parametersContainer;
    }

    //Hide\display the additional CEP widget is appropriate
    private void operatorChanged( OperatorSelection selection ) {
        parametersContainer.clear();
        String operator = selection.getValue();

        if ( CEPOracle.isCEPWindowOperatorTime( operator ) ) {
            AbstractRestrictedEntryTextBox txt = new CEPTimeParameterTextBox( hcw.getWindow(),
                                                                              1 );
            initialiseTextBox( txt );
        } else if ( CEPOracle.isCEPWindowOperatorLength( operator ) ) {
            AbstractRestrictedEntryTextBox txt = new CEPLengthParameterTextBox( hcw.getWindow(),
                                                                                1 );
            initialiseTextBox( txt );
        } else {
            parametersContainer.setVisible( false );
            hcw.getWindow().clearParameters();
        }
    }

    private void initialiseTextBox( AbstractRestrictedEntryTextBox txt ) {
        String key = String.valueOf( 1 );
        String value = hcw.getWindow().getParameter( key );
        if ( value == null ) {
            value = "";
            hcw.getWindow().setParameter( key,
                                          value );
        }
        if ( !txt.isValidValue( value,
                                false ) ) {
            value = "";
            hcw.getWindow().setParameter( key,
                                          value );
        }
        txt.setText( value );
        txt.setEnabled( !isReadOnly );
        parametersContainer.add( txt );
        parametersContainer.setVisible( true );
        hcw.getWindow().setParameter( SharedConstants.OPERATOR_PARAMETER_GENERATOR,
                                      CEP_OPERATOR_PARAMETER_GENERATOR );

    }

    //Actual drop-down
    private Widget getDropDown() {

        String selected = "";
        String selectedText = "";

        box = new ListBox();
        box.setEnabled( !isReadOnly );
        box.addItem( HumanReadableConstants.INSTANCE.noCEPWindow(),
                     "" );

        for ( int i = 0; i < operators.size(); i++ ) {
            String op = operators.get( i );
            box.addItem( HumanReadable.getOperatorDisplayName( op ),
                         op );
            if ( op.equals( hcw.getWindow().getOperator() ) ) {
                selected = op;
                selectedText = HumanReadable.getOperatorDisplayName( op );
                box.setSelectedIndex( i + 1 );
            }
        }
        selectItem( hcw.getWindow().getOperator() );

        //Fire event to ensure parent Widgets correct their state depending on selection
        final HasValueChangeHandlers<OperatorSelection> source = this;
        final OperatorSelection selection = new OperatorSelection( selected,
                                                                   selectedText );
        Scheduler.get().scheduleFinally( new Command() {

            public void execute() {
                operatorChanged( selection );
                ValueChangeEvent.fire( source,
                                       selection );
            }

        } );

        //Signal parent Widget whenever a change happens
        box.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                String selected = box.getValue( box.getSelectedIndex() );
                String selectedText = box.getItemText( box.getSelectedIndex() );
                OperatorSelection selection = new OperatorSelection( selected,
                                                                     selectedText );
                operatorChanged( selection );
                ValueChangeEvent.fire( source,
                                       selection );
            }
        } );

        return box;
    }

    /**
     * Allow parent Widgets to register for events when the operator changes
     */
    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<OperatorSelection> handler ) {
        return addHandler( handler,
                           ValueChangeEvent.getType() );
    }

    /**
     * Select a given item in the drop-down
     * @param operator The DRL operator, not the HumanReadable form
     */
    public void selectItem( String operator ) {
        String currentOperator = box.getValue( box.getSelectedIndex() );
        if ( currentOperator.equals( operator ) ) {
            return;
        }
        for ( int i = 0; i < box.getItemCount(); i++ ) {
            String op = box.getValue( i );
            if ( op.equals( operator ) ) {
                box.setSelectedIndex( i );
                break;
            }
        }
        String selected = box.getValue( box.getSelectedIndex() );
        String selectedText = box.getItemText( box.getSelectedIndex() );
        OperatorSelection selection = new OperatorSelection( selected,
                                                             selectedText );
        operatorChanged( selection );
    }

}
