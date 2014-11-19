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
package org.uberfire.client.views.pfly.splash;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.ParameterizedCommand;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;

/**
 * The footer component for the splash screen popup. Has a "don't show again" toggle and a close button.
 */
@Templated
public class SplashModalFooter extends Composite {

    private ParameterizedCommand<Boolean> closeCommand;

    @Inject @DataField
    private CheckBox dontShowAgain;

    @Inject @DataField
    private Button closeButton;

    @PostConstruct
    private void setup() {
        closeButton.ensureDebugId( "SplashModalFooter-close" );
        dontShowAgain.ensureDebugId( "SplashModalFooter-dontShowAgain" );
    }

    @EventHandler("closeButton")
    public void onOKButtonClick( final ClickEvent e ) {
        if ( closeCommand != null ) {
            closeCommand.execute( !dontShowAgain.getValue() );
        }
    }

    public boolean getShowAgain() {
        return !dontShowAgain.getValue();
    }

    /**
     * Sets the command to be executed when the close button has been clicked. The argument passed to the command is the
     * state of the "don't show again" checkbox.
     */
    public void setCloseCommand( ParameterizedCommand<Boolean> closeCommand ) {
        this.closeCommand = closeCommand;
    }

}
