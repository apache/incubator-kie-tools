/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client.workbench.widgets.splash;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import org.uberfire.mvp.ParameterizedCommand;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ModalFooter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Modal Footer with a single OK button
 */
public class SplashModalFooter extends ModalFooter {

    private static ModalFooterOKButtonBinder uiBinder = GWT.create( ModalFooterOKButtonBinder.class );

    private final ParameterizedCommand<Boolean> closeCommand;

    interface ModalFooterOKButtonBinder
            extends
            UiBinder<Widget, SplashModalFooter> {

    }

    @UiField
    CheckBox dontShowAgain;

    @UiField
    Button closeButton;

    public SplashModalFooter( final ParameterizedCommand<Boolean> closeCommand ) {
        this.closeCommand = checkNotNull( "okCommand", closeCommand );
        add( uiBinder.createAndBindUi( this ) );
        closeButton.ensureDebugId( "SplashModalFooter-close" );
        dontShowAgain.ensureDebugId( "SplashModalFooter-dontShowAgain" );
    }

    @UiHandler("closeButton")
    public void onOKButtonClick( final ClickEvent e ) {
        closeCommand.execute( !dontShowAgain.getValue() );
    }

    public boolean getShowAgain() {
        return !dontShowAgain.getValue();
    }

}
