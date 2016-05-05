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
package org.uberfire.ext.widgets.common.client.common.popups.footers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.uberfire.mvp.Command;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * A Modal Footer with OK and Cancel buttons
 */
public class ModalFooterForceSaveReOpenCancelButtons extends ModalFooter {

    private static ModalFooterForceSaveReOpenCancelButtonsBinder uiBinder = GWT.create( ModalFooterForceSaveReOpenCancelButtonsBinder.class );

    private final Command forceSaveCommand;
    private final Command reopenCommand;
    private final Command cancelCommand;

    private final Modal panel;

    interface ModalFooterForceSaveReOpenCancelButtonsBinder
            extends
            UiBinder<Widget, ModalFooterForceSaveReOpenCancelButtons> {

    }

    @UiField
    Button forceSaveButton;

    @UiField
    Button reopenButton;

    @UiField
    Button cancelButton;

    public ModalFooterForceSaveReOpenCancelButtons( final Modal panel,
                                                    final Command forceSaveCommand,
                                                    final Command reopenCommand,
                                                    final Command cancelCommand ) {
        this.forceSaveCommand = checkNotNull( "forceSaveCommand", forceSaveCommand );
        this.reopenCommand = checkNotNull( "reopenCommand", reopenCommand );
        this.cancelCommand = checkNotNull( "cancelCommand", cancelCommand );
        this.panel = checkNotNull( "panel", panel );
        add( uiBinder.createAndBindUi( this ) );
    }

    @UiHandler("forceSaveButton")
    public void onForceSaveButtonClick( final ClickEvent e ) {
        if ( forceSaveCommand != null ) {
            forceSaveCommand.execute();
        }
        panel.hide();
    }

    @UiHandler("reopenButton")
    public void onReOpenButtonClick( final ClickEvent e ) {
        if ( reopenCommand != null ) {
            reopenCommand.execute();
        }
        panel.hide();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClick( final ClickEvent e ) {
        if ( cancelCommand != null ) {
            cancelCommand.execute();
        }
        panel.hide();
    }

}
