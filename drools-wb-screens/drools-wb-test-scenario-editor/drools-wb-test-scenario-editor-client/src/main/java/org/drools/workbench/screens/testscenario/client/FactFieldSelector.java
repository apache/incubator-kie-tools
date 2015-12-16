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

package org.drools.workbench.screens.testscenario.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.ListBox;

public class FactFieldSelector
        extends Composite
        implements HasSelectionHandlers<String> {

    @UiField
    ListBox fieldsListBox;

    interface FactFieldSelectorUiBinder
            extends
            UiBinder<Widget, FactFieldSelector> {

    }

    private static FactFieldSelectorUiBinder uiBinder = GWT.create( FactFieldSelectorUiBinder.class );

    public FactFieldSelector() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void addField( String field ) {
        fieldsListBox.addItem( field );
    }

    public String getSelectedText() {
        return fieldsListBox.getItemText( fieldsListBox.getSelectedIndex() );
    }

    @Override
    public HandlerRegistration addSelectionHandler( final SelectionHandler<String> selectionHandler ) {
        return addHandler( selectionHandler, SelectionEvent.getType() );
    }
}