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

package org.uberfire.client.editors.repository.clone;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryAlreadyExistsException;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.client.UberFirePreferences;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.popups.errors.ErrorPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.util.URIUtil;

@Dependent
public class CloneRepositoryForm
        extends PopupPanel {

    interface CloneRepositoryFormBinder
            extends
            UiBinder<Widget, CloneRepositoryForm> {

    }

    private static CloneRepositoryFormBinder uiBinder = GWT.create( CloneRepositoryFormBinder.class );

    @Inject
    private Caller<RepositoryService> repositoryService;

    @Inject
    private Caller<OrganizationalUnitService> organizationalUnitService;

    @Inject
    private PlaceManager placeManager;

    @UiField
    Button clone;

    @UiField
    Button cancel;

    @UiField
    ControlGroup organizationalUnitGroup;

    @UiField
    ListBox organizationalUnitDropdown;

    @UiField
    HelpInline organizationalUnitHelpInline;

    @UiField
    ControlGroup nameGroup;

    @UiField
    TextBox nameTextBox;

    @UiField
    HelpInline nameHelpInline;

    @UiField
    ControlGroup urlGroup;

    @UiField
    TextBox gitURLTextBox;

    @UiField
    HelpInline urlHelpInline;

    @UiField
    TextBox usernameTextBox;

    @UiField
    PasswordTextBox passwordTextBox;

    @UiField
    Modal popup;

    @UiField
    InlineHTML isOUMandatory;

    private Map<String, OrganizationalUnit> availableOrganizationalUnits = new HashMap<String, OrganizationalUnit>();
    private boolean mandatoryOU = true;

    @PostConstruct
    public void init() {
        mandatoryOU = UberFirePreferences.getProperty( "org.uberfire.client.workbench.clone.ou.mandatory.disable" ) == null;

        setWidget( uiBinder.createAndBindUi( this ) );

        if ( !mandatoryOU ) {
            isOUMandatory.removeFromParent();
        }

        popup.setDynamicSafe( true );
        nameTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                nameGroup.setType( ControlGroupType.NONE );
                nameHelpInline.setText( "" );
            }
        } );
        gitURLTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                urlGroup.setType( ControlGroupType.NONE );
                urlHelpInline.setText( "" );
            }
        } );
        //populate Organizational Units list box
        organizationalUnitService.call( new RemoteCallback<Collection<OrganizationalUnit>>() {
                                            @Override
                                            public void callback( Collection<OrganizationalUnit> organizationalUnits ) {
                                                organizationalUnitDropdown.addItem( "--- Select ---" );
                                                if ( organizationalUnits != null && !organizationalUnits.isEmpty() ) {
                                                    for ( OrganizationalUnit organizationalUnit : organizationalUnits ) {
                                                        organizationalUnitDropdown.addItem( organizationalUnit.getName(),
                                                                                            organizationalUnit.getName() );
                                                        availableOrganizationalUnits.put( organizationalUnit.getName(),
                                                                                          organizationalUnit );
                                                    }
                                                }
                                            }
                                        },
                                        new ErrorCallback<Message>() {
                                            @Override
                                            public boolean error( final Message message,
                                                                  final Throwable throwable ) {
                                                ErrorPopup.showMessage( "Can't load Organizational Units. \n" + throwable.getMessage() );

                                                return false;
                                            }
                                        }
                                      ).getOrganizationalUnits();
    }

    @UiHandler("clone")
    public void onCloneClick( final ClickEvent e ) {

        if ( gitURLTextBox.getText() == null || gitURLTextBox.getText().trim().isEmpty() ) {
            urlGroup.setType( ControlGroupType.ERROR );
            urlHelpInline.setText( "URL is mandatory" );
            return;
        } else if ( !URIUtil.isValid( gitURLTextBox.getText().trim() ) ) {
            urlGroup.setType( ControlGroupType.ERROR );
            urlHelpInline.setText( "Invalid URL format" );
            return;
        } else {
            urlGroup.setType( ControlGroupType.NONE );
        }
        final String organizationalUnit = organizationalUnitDropdown.getValue( organizationalUnitDropdown.getSelectedIndex() );
        if ( mandatoryOU && !availableOrganizationalUnits.containsKey( organizationalUnit ) ) {
            organizationalUnitGroup.setType( ControlGroupType.ERROR );
            organizationalUnitHelpInline.setText( "Organizational Unit is mandatory" );
            return;
        } else {
            organizationalUnitGroup.setType( ControlGroupType.NONE );
        }

        if ( nameTextBox.getText() == null || nameTextBox.getText().trim().isEmpty() ) {
            nameGroup.setType( ControlGroupType.ERROR );
            nameHelpInline.setText( "Repository Name is mandatory" );
            return;
        } else {
            repositoryService.call( new RemoteCallback<String>() {
                @Override
                public void callback( String normalizedName ) {
                    if ( !nameTextBox.getText().equals( normalizedName ) ) {
                        if ( !Window.confirm( "Repository Name contained illegal characters and will be generated as \"" + normalizedName + "\". Do you agree?" ) ) {
                            return;
                        }
                        nameTextBox.setText( normalizedName );
                    }

                    lockScreen();

                    final String scheme = "git";
                    final String alias = nameTextBox.getText().trim();
                    final String origin = gitURLTextBox.getText().trim();
                    final String username = usernameTextBox.getText().trim();
                    final String password = passwordTextBox.getText().trim();
                    final Map<String, Object> env = new HashMap<String, Object>( 3 );
                    env.put( "username", username );
                    env.put( "crypt:password", password );
                    env.put( "origin", origin );

                    repositoryService.call( new RemoteCallback<Repository>() {
                                                @Override
                                                public void callback( Repository o ) {
                                                    BusyPopup.close();
                                                    Window.alert( "The repository is cloned successfully" );
                                                    hide();
                                                    placeManager.goTo( new DefaultPlaceRequest( "RepositoryEditor" ).addParameter( "alias", o.getAlias() ) );
                                                }
                                            },
                                            new ErrorCallback<Message>() {
                                                @Override
                                                public boolean error( final Message message,
                                                                      final Throwable throwable ) {
                                                    try {
                                                        throw throwable;
                                                    } catch ( RepositoryAlreadyExistsException ex ) {
                                                        ErrorPopup.showMessage( "Repository already exists." );
                                                    } catch ( Throwable ex ) {
                                                        ErrorPopup.showMessage( "Can't clone repository. \n" + throwable.getMessage() );
                                                    }
                                                    unlockScreen();
                                                    return true;
                                                }
                                            }
                                          ).createRepository( availableOrganizationalUnits.get( organizationalUnit ), scheme, alias, env );

                }
            } ).normalizeRepositoryName( nameTextBox.getText() );
        }
    }

    private void lockScreen() {
        BusyPopup.showMessage( "Cloning repository..." );
        popup.setCloseVisible( false );
        clone.setEnabled( false );
        cancel.setEnabled( false );
        passwordTextBox.setEnabled( false );
        usernameTextBox.setEnabled( false );
        gitURLTextBox.setEnabled( false );
        organizationalUnitDropdown.setEnabled( false );
        nameTextBox.setEnabled( false );
    }

    private void unlockScreen() {
        BusyPopup.close();
        popup.setCloseVisible( true );
        clone.setEnabled( true );
        cancel.setEnabled( true );
        passwordTextBox.setEnabled( true );
        usernameTextBox.setEnabled( true );
        gitURLTextBox.setEnabled( true );
        organizationalUnitDropdown.setEnabled( true );
        nameTextBox.setEnabled( true );
    }

    @UiHandler("cancel")
    public void onCancelClick( final ClickEvent e ) {
        hide();
    }

    public void hide() {
        BusyPopup.close();
        popup.hide();
        super.hide();
    }

    public void show() {
        popup.show();
    }

}
