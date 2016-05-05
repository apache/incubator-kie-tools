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
package org.uberfire.ext.widgets.core.client.wizards;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.uberfire.commons.validation.PortablePreconditions;

/**
 * A Modal Footer used by the Wizard
 */
public class WizardPopupFooter extends ModalFooter {

    private static WizardPopupFooterBinder uiBinder = GWT.create( WizardPopupFooterBinder.class );

    private final Command cmdPreviousButton;
    private final Command cmdNextButton;
    private final Command cmdCancelButton;
    private final Command cmdFinishButton;

    interface WizardPopupFooterBinder
            extends
            UiBinder<Widget, WizardPopupFooter> {

    }

    @UiField
    Button btnPrevious;

    @UiField
    Button btnNext;

    @UiField
    Button btnCancel;

    @UiField
    Button btnFinish;

    public WizardPopupFooter( final Command cmdPreviousButton,
                              final Command cmdNextButton,
                              final Command cmdCancelButton,
                              final Command cmdFinishButton ) {
        this.cmdPreviousButton = PortablePreconditions.checkNotNull( "cmdPreviousButton",
                                                                     cmdPreviousButton );
        this.cmdNextButton = PortablePreconditions.checkNotNull( "cmdNextButton",
                                                                 cmdNextButton );
        this.cmdCancelButton = PortablePreconditions.checkNotNull( "cmdCancelButton",
                                                                   cmdCancelButton );
        this.cmdFinishButton = PortablePreconditions.checkNotNull( "cmdFinishButton",
                                                                   cmdFinishButton );
        add( uiBinder.createAndBindUi( this ) );
    }

    public void enablePreviousButton( final boolean enabled ) {
        btnPrevious.setEnabled( enabled );
    }

    public void enableNextButton( final boolean enabled ) {
        btnNext.setEnabled( enabled );
    }

    public void enableFinishButton( final boolean enabled ) {
        btnFinish.setEnabled( enabled );
        if ( enabled ) {
            btnFinish.setType( ButtonType.PRIMARY );
        } else {
            btnFinish.setType( ButtonType.DEFAULT );
        }
    }

    public void setPreviousButtonFocus( final boolean focused ) {
        btnPrevious.setFocus( focused );
    }

    public void setNextButtonFocus( final boolean focused ) {
        btnNext.setFocus( focused );
    }

    @UiHandler("btnPrevious")
    public void onPreviousButtonClick( final ClickEvent e ) {
        cmdPreviousButton.execute();
    }

    @UiHandler("btnNext")
    public void onNextButtonClick( final ClickEvent e ) {
        cmdNextButton.execute();
    }

    @UiHandler("btnCancel")
    public void onCancelButtonClick( final ClickEvent e ) {
        cmdCancelButton.execute();
    }

    @UiHandler("btnFinish")
    public void onFinishButtonClick( final ClickEvent e ) {
        cmdFinishButton.execute();
    }

}
