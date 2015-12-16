/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A Modal Footer with OK and Cancel buttons plus a button to create a new Pattern
 */
public class ModalFooterChangePattern extends ModalFooter {

    private static ModalFooterOKCancelButtonsBinder uiBinder = GWT.create( ModalFooterOKCancelButtonsBinder.class );

    private final Command okCommand;
    private final Command newPatternCommand;
    private final Command cancelCommand;

    interface ModalFooterOKCancelButtonsBinder
            extends
            UiBinder<Widget, ModalFooterChangePattern> {

    }

    @UiField
    Button okButton;

    @UiField
    Button newPatternButton;

    @UiField
    Button cancelButton;

    public ModalFooterChangePattern( final Command okCommand,
                                     final Command newPatternCommand,
                                     final Command cancelCommand ) {
        this.okCommand = PortablePreconditions.checkNotNull( "okCommand",
                                                             okCommand );
        this.newPatternCommand = PortablePreconditions.checkNotNull( "newPatternCommand",
                                                                     newPatternCommand );
        this.cancelCommand = PortablePreconditions.checkNotNull( "cancelCommand",
                                                                 cancelCommand );
        add( uiBinder.createAndBindUi( this ) );
    }

    public void enableOkButton( final boolean enabled ) {
        okButton.setEnabled( enabled );
    }

    public void enableCancelButton( final boolean enabled ) {
        cancelButton.setEnabled( enabled );
    }

    @UiHandler("okButton")
    public void onOKButtonClick( final ClickEvent e ) {
        okCommand.execute();
    }

    @UiHandler("newPatternButton")
    public void onNewPatternButtonClick( final ClickEvent e ) {
        newPatternCommand.execute();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClick( final ClickEvent e ) {
        cancelCommand.execute();
    }

}
