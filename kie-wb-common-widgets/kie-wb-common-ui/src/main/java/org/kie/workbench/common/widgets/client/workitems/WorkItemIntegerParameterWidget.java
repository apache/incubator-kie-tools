/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.widgets.client.workitems;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.workitems.PortableIntegerParameterDefinition;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.NumericIntegerTextBox;

/**
 * A Widget to display a Work Item Integer parameter
 */
public class WorkItemIntegerParameterWidget extends WorkItemParameterWidget {

    interface WorkItemIntegerParameterWidgetBinder
            extends
            UiBinder<HorizontalPanel, WorkItemIntegerParameterWidget> {

    }

    @UiField
    Label parameterName;

    @UiField
    NumericIntegerTextBox parameterEditor;

    @UiField
    ListBox lstAvailableBindings;

    private static WorkItemIntegerParameterWidgetBinder uiBinder = GWT.create( WorkItemIntegerParameterWidgetBinder.class );

    public WorkItemIntegerParameterWidget( PortableIntegerParameterDefinition ppd,
                                           IBindingProvider bindingProvider,
                                           boolean isReadOnly ) {
        super( ppd,
               bindingProvider );
        this.parameterName.setText( ppd.getName() );
        this.parameterEditor.setEnabled( !isReadOnly );

        //Setup widget to select a literal value
        if ( ppd.getValue() != null ) {
            this.parameterEditor.setText( Integer.toString( ppd.getValue() ) );
        }

        //Setup widget to use bindings
        Set<String> bindings = bindingProvider.getBindings( ppd.getClassName() );
        if ( bindings.size() > 0 ) {
            lstAvailableBindings.clear();
            lstAvailableBindings.addItem( CommonConstants.INSTANCE.Choose() );
            lstAvailableBindings.setEnabled( true && !isReadOnly );
            lstAvailableBindings.setVisible( true );
            int selectedIndex = 0;
            for ( String binding : bindings ) {
                lstAvailableBindings.addItem( binding );
                if ( binding.equals( ppd.getBinding() ) ) {
                    selectedIndex = lstAvailableBindings.getItemCount() - 1;
                }
            }
            lstAvailableBindings.setSelectedIndex( selectedIndex );
            parameterEditor.setEnabled( selectedIndex == 0 && !isReadOnly );
        }

    }

    @Override
    protected Widget getWidget() {
        return uiBinder.createAndBindUi( this );
    }

    @UiHandler("parameterEditor")
    void parameterEditorOnChange( ChangeEvent event ) {
        try {
            ( (PortableIntegerParameterDefinition) ppd ).setValue( Integer.parseInt( parameterEditor.getText() ) );
        } catch ( NumberFormatException nfe ) {
            ( (PortableIntegerParameterDefinition) ppd ).setValue( null );
        }
    }

    @UiHandler("lstAvailableBindings")
    void lstAvailableBindingsOnChange( ChangeEvent event ) {
        int index = lstAvailableBindings.getSelectedIndex();
        parameterEditor.setEnabled( index == 0 );
        if ( index > 0 ) {
            ( (PortableIntegerParameterDefinition) ppd ).setValue( null );
            ( (PortableIntegerParameterDefinition) ppd ).setBinding( lstAvailableBindings.getItemText( index ) );
        } else {
            ( (PortableIntegerParameterDefinition) ppd ).setBinding( "" );
        }
    }

}
