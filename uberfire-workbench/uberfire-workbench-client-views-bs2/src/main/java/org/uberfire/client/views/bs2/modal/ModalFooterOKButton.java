/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.client.views.bs2.modal;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ModalFooter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;

/**
 * A Modal Footer with a single OK button
 */
public class ModalFooterOKButton extends ModalFooter {

    private static ModalFooterOKButtonBinder uiBinder = GWT.create( ModalFooterOKButtonBinder.class );

    private final Command okCommand;

    interface ModalFooterOKButtonBinder
            extends
            UiBinder<Widget, ModalFooterOKButton> {

    }

    @UiField
    Button okButton;

    public ModalFooterOKButton( final Command okCommand ) {
        this.okCommand = PortablePreconditions.checkNotNull( "okCommand",
                                                             okCommand );
        add( uiBinder.createAndBindUi( this ) );
    }

    public void enableOkButton( final boolean enabled ) {
        okButton.setEnabled( enabled );
    }

    @UiHandler("okButton")
    public void onOKButtonClick( final ClickEvent e ) {
        okCommand.execute();
    }
}
