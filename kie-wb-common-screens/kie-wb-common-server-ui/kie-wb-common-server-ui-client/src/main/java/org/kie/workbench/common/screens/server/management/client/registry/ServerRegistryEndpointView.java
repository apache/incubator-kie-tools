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
    ControlGroup versionGroup;

    @UiField
    ControlGroup nameGroup;

    @UiField
    ControlGroup idGroup;

    @UiField
    TextBox versionTextBox;

    @UiField
    TextBox idTextBox;

    @UiField
    TextBox nameTextBox;

    @UiField
    HelpInline nameHelpInline;

    @UiField
    HelpInline idHelpInline;

    @UiField
    HelpInline versionHelpInline;


    private ServerRegistryEndpointPresenter presenter;

    public ServerRegistryEndpointView() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final ServerRegistryEndpointPresenter presenter ) {
        this.presenter = presenter;

        idTextBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(final KeyPressEvent event) {
                idGroup.setType(ControlGroupType.NONE);
                idHelpInline.setText("");
            }
        });
    }

    @UiHandler("connect")
    public void onConnectClick( final ClickEvent e ) {
        if ( idTextBox.getText() == null || idTextBox.getText().trim().isEmpty() ) {
            idGroup.setType(ControlGroupType.ERROR);
            idHelpInline.setText("Identifier mandatory");
            return;
        }else {
            idGroup.setType(ControlGroupType.NONE);
        }

        if ( nameTextBox.getText() == null || nameTextBox.getText().trim().isEmpty() ) {
            nameGroup.setType( ControlGroupType.ERROR );
            nameHelpInline.setText( "Name mandatory" );
            return;
        } else {
            nameGroup.setType( ControlGroupType.NONE );
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
        idTextBox.setEnabled(false);
        nameTextBox.setEnabled( false );
        versionTextBox.setEnabled( false );
    }

    @Override
    public void unlockScreen() {
        connect.setEnabled( true );
        cancel.setEnabled( true );
        idTextBox.setEnabled(true);
        nameTextBox.setEnabled( true );
        versionTextBox.setEnabled( true );
    }

    @UiHandler("cancel")
    public void onCancelClick( final ClickEvent e ) {
        presenter.close();
    }
}
