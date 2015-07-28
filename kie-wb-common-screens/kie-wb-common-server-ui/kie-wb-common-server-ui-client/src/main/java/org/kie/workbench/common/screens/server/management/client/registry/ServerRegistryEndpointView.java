/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.server.management.client.registry;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

@Dependent
public class ServerRegistryEndpointView extends BaseModal
        implements ServerRegistryEndpointPresenter.View {

    interface Binder
            extends
            UiBinder<Widget, ServerRegistryEndpointView> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    Button connect;

    @UiField
    Button cancel;

    @UiField
    FormGroup versionGroup;

    @UiField
    FormGroup nameGroup;

    @UiField
    FormGroup idGroup;

    @UiField
    TextBox versionTextBox;

    @UiField
    TextBox idTextBox;

    @UiField
    TextBox nameTextBox;

    @UiField
    HelpBlock nameHelpInline;

    @UiField
    HelpBlock idHelpInline;

    @UiField
    HelpBlock versionHelpInline;

    private ServerRegistryEndpointPresenter presenter;

    public ServerRegistryEndpointView() {
        setBody( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final ServerRegistryEndpointPresenter presenter ) {
        this.presenter = presenter;

        idTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                idGroup.setValidationState( ValidationState.NONE );
                idHelpInline.setText( "" );
            }
        } );

        setTitle( presenter.getTitle() );
    }

    @UiHandler( "connect" )
    public void onConnectClick( final ClickEvent e ) {
        if ( idTextBox.getText() == null || idTextBox.getText().trim().isEmpty() ) {
            idGroup.setValidationState( ValidationState.ERROR );
            idHelpInline.setText( "Identifier mandatory" );
            return;
        } else {
            idGroup.setValidationState( ValidationState.NONE );
        }

        if ( nameTextBox.getText() == null || nameTextBox.getText().trim().isEmpty() ) {
            nameGroup.setValidationState( ValidationState.ERROR );
            nameHelpInline.setText( "Name mandatory" );
            return;
        } else {
            nameGroup.setValidationState( ValidationState.NONE );
        }

        presenter.registerServer( idTextBox.getText(), nameTextBox.getText(), versionTextBox.getText() );
    }

    @Override
    public String getBaseURL() {
        return GWT.getHostPageBaseURL().replaceFirst( "/" + GWT.getModuleName() + "/", "" );
    }

    @Override
    public void lockScreen() {
        connect.setEnabled( false );
        cancel.setEnabled( false );
        idTextBox.setEnabled( false );
        nameTextBox.setEnabled( false );
        versionTextBox.setEnabled( false );
    }

    @Override
    public void unlockScreen() {
        connect.setEnabled( true );
        cancel.setEnabled( true );
        idTextBox.setEnabled( true );
        nameTextBox.setEnabled( true );
        versionTextBox.setEnabled( true );
    }

    @UiHandler( "cancel" )
    public void onCancelClick( final ClickEvent e ) {
        presenter.close();
    }

    @Override
    public void show() {
        idTextBox.setText( null );
        nameTextBox.setText( null );
        versionTextBox.setText( null );
        super.show();
    }
}