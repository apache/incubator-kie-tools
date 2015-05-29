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

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.PasswordTextBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.util.URIUtil;

@Dependent
public class ServerRegistryEndpointView extends Composite
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
    ControlGroup urlGroup;

    @UiField
    ControlGroup nameGroup;

    @UiField
    TextBox nameTextBox;

    @UiField
    TextBox endpointTextBox;

    @UiField
    HelpInline nameHelpInline;

    @UiField
    HelpInline urlHelpInline;

    @UiField
    TextBox usernameTextBox;

    @UiField
    PasswordTextBox passwordTextBox;

    private ServerRegistryEndpointPresenter presenter;

    public ServerRegistryEndpointView() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final ServerRegistryEndpointPresenter presenter ) {
        this.presenter = presenter;

        endpointTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                urlGroup.setType( ControlGroupType.NONE );
                urlHelpInline.setText( "" );
            }
        } );
    }

    @UiHandler("connect")
    public void onConnectClick( final ClickEvent e ) {
        if ( endpointTextBox.getText() == null || endpointTextBox.getText().trim().isEmpty() ) {
            urlGroup.setType( ControlGroupType.ERROR );
            urlHelpInline.setText( "Endpoint mandatory" );
            return;
        } else if ( !URIUtil.isValid( endpointTextBox.getText().trim() ) ) {
            urlGroup.setType( ControlGroupType.ERROR );
            urlHelpInline.setText( "Invalid Endpoint format." );
            return;
        } else {
            urlGroup.setType( ControlGroupType.NONE );
        }

        if ( nameTextBox.getText() == null || nameTextBox.getText().trim().isEmpty() ) {
            nameGroup.setType( ControlGroupType.ERROR );
            nameHelpInline.setText( "Name mandatory" );
            return;
        } else {
            nameGroup.setType( ControlGroupType.NONE );
        }

        presenter.registerServer( endpointTextBox.getText(), nameTextBox.getText(), usernameTextBox.getText(), passwordTextBox.getText() );
    }

    @Override
    public String getBaseURL() {
        return GWT.getHostPageBaseURL().replaceFirst( "/" + GWT.getModuleName() + "/", "" );
    }

    @Override
    public void lockScreen() {
        connect.setEnabled( false );
        cancel.setEnabled( false );
        passwordTextBox.setEnabled( false );
        usernameTextBox.setEnabled( false );
        endpointTextBox.setEnabled( false );
        nameTextBox.setEnabled( false );
    }

    @Override
    public void unlockScreen() {
        connect.setEnabled( true );
        cancel.setEnabled( true );
        passwordTextBox.setEnabled( true );
        usernameTextBox.setEnabled( true );
        endpointTextBox.setEnabled( true );
        nameTextBox.setEnabled( true );
    }

    @UiHandler("cancel")
    public void onCancelClick( final ClickEvent e ) {
        presenter.close();
    }
}
