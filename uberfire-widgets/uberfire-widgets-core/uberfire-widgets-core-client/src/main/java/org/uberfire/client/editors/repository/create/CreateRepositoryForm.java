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

package org.uberfire.client.editors.repository.create;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
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
import org.uberfire.client.common.popups.errors.ErrorPopup;

@Dependent
public class CreateRepositoryForm
        extends Composite
        implements HasCloseHandlers<CreateRepositoryForm> {

    interface CreateRepositoryFormBinder
            extends
            UiBinder<Widget, CreateRepositoryForm> {

    }

    private static CreateRepositoryFormBinder uiBinder = GWT.create( CreateRepositoryFormBinder.class );

    @Inject
    private Caller<RepositoryService> repositoryService;

    @Inject
    private Caller<OrganizationalUnitService> organizationalUnitService;

    @UiField
    ControlGroup organizationalUnitGroup;

    @UiField
    HelpInline organizationalUnitHelpInline;

    @UiField
    ListBox organizationalUnitDropdown;

    @UiField
    ControlGroup nameGroup;

    @UiField
    TextBox nameTextBox;

    @UiField
    HelpInline nameHelpInline;

    @UiField
    Modal popup;

    @UiField
    ControlLabel ouLabel;

    private Map<String, OrganizationalUnit> availableOrganizationalUnits = new HashMap<String, OrganizationalUnit>();

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        popup.setDynamicSafe( true );
        nameTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                nameGroup.setType( ControlGroupType.NONE );
                nameHelpInline.setText( "" );
            }
        } );
        ouLabel.getElement().setInnerText( "Organizational Unit" );
        //populate Organizational Units list box
        organizationalUnitService.call( new RemoteCallback<Collection<OrganizationalUnit>>() {
                                            @Override
                                            public void callback( Collection<OrganizationalUnit> organizationalUnits ) {
                                                organizationalUnitDropdown.addItem( "--- Select ---" );
                                                if ( organizationalUnits != null && !organizationalUnits.isEmpty() ) {
                                                    ouLabel.getElement().setInnerHTML( "<font color=\"red\">*</font> Organizational Unit" );
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
                                                Window.alert( "Can't load Organizational Units. \n" + message.toString() );

                                                return false;
                                            }
                                        }
                                      ).getOrganizationalUnits();
    }

    @Override
    public HandlerRegistration addCloseHandler( final CloseHandler<CreateRepositoryForm> handler ) {
        return addHandler( handler, CloseEvent.getType() );
    }

    @UiHandler("create")
    public void onCreateClick( final ClickEvent e ) {

        boolean hasError = false;
        if ( nameTextBox.getText() == null || nameTextBox.getText().trim().isEmpty() ) {
            nameGroup.setType( ControlGroupType.ERROR );
            nameHelpInline.setText( "Repository Name is mandatory" );
            hasError = true;
        } else {
            nameGroup.setType( ControlGroupType.NONE );
        }

        final String organizationalUnit = organizationalUnitDropdown.getValue( organizationalUnitDropdown.getSelectedIndex() );
        if ( !availableOrganizationalUnits.isEmpty() && !availableOrganizationalUnits.containsKey( organizationalUnit ) ) {
            organizationalUnitGroup.setType( ControlGroupType.ERROR );
            organizationalUnitHelpInline.setText( "Organizational Unit is mandatory" );
            hasError = true;
        } else {
            organizationalUnitGroup.setType( ControlGroupType.NONE );
        }

        if ( hasError ) {
            return;
        }

        final String scheme = "git";
        final String alias = nameTextBox.getText();
        final Map<String, Object> env = new HashMap<String, Object>( 3 );

        repositoryService.call( new RemoteCallback<Repository>() {
                                    @Override
                                    public void callback( Repository o ) {
                                        Window.alert( "The repository is created successfully" );
                                        if ( availableOrganizationalUnits.containsKey( organizationalUnit ) ) {
                                            organizationalUnitService.call( new RemoteCallback<Collection<OrganizationalUnit>>() {
                                                                                @Override
                                                                                public void callback( Collection<OrganizationalUnit> organizationalUnits ) {
                                                                                    hide();
                                                                                }
                                                                            },
                                                                            new ErrorCallback<Message>() {
                                                                                @Override
                                                                                public boolean error( final Message message,
                                                                                                      final Throwable throwable ) {
                                                                                    ErrorPopup.showMessage( "Can't associate repository to an Organizational Unit." );

                                                                                    return true;
                                                                                }
                                                                            }
                                                                          ).addRepository( availableOrganizationalUnits.get( organizationalUnit ), o );

                                        } else {
                                            hide();
                                        }
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
                                            ErrorPopup.showMessage( "Can't create repository. \n" + throwable.getMessage() );
                                        }

                                        return true;
                                    }
                                }
                              ).createRepository( scheme, alias, env );
    }

    @UiHandler("cancel")
    public void onCancelClick( final ClickEvent e ) {
        hide();
    }

    public void hide() {
        popup.hide();
        CloseEvent.fire( this, this );
    }

    public void show() {
        popup.show();
    }

}
