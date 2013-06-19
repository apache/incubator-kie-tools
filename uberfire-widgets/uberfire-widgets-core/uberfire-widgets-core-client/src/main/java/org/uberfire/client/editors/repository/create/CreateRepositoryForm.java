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
import com.github.gwtbootstrap.client.ui.Dropdown;
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
import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.group.GroupService;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;

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
    private Caller<GroupService> groupService;

    @UiField
    ControlGroup groupGroup;

    @UiField
    HelpInline groupHelpInline;

    @UiField
    ControlGroup nameGroup;

    @UiField
    ListBox groupDropdown;

    @UiField
    TextBox nameTextBox;

    @UiField
    HelpInline nameHelpInline;

    @UiField
    TextBox usernameTextBox;

    @UiField
    PasswordTextBox passwordTextBox;

    @UiField
    Modal popup;

    private Map<String, Group> availableGroups = new HashMap<String, Group>();

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
        //populate group list box
        groupService.call(new RemoteCallback<Collection<Group>>() {
                              @Override
                              public void callback( Collection<Group> groups ) {
                                  groupDropdown.addItem("-----select group-----");
                                  if (groups != null && !groups.isEmpty()) {
                                      for (Group group : groups) {
                                          groupDropdown.addItem(group.getName(), group.getName());
                                          availableGroups.put(group.getName(), group);
                                      }
                                  }
                              }
                          },
                new ErrorCallback() {
                    @Override
                    public boolean error( final Message message,
                            final Throwable throwable ) {
                        Window.alert( "Can't load groups. \n" + message.toString() );

                        return false;
                    }
                }).getGroups();
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

        final String group = groupDropdown.getValue(groupDropdown.getSelectedIndex());
        if ( !availableGroups.isEmpty() && !availableGroups.containsKey(group)) {
            groupGroup.setType( ControlGroupType.ERROR );
            groupHelpInline.setText( "Group is mandatory" );
            hasError = true;
        } else {
            groupGroup.setType( ControlGroupType.NONE );
        }

        if ( hasError ) {
            return;
        }

        final String scheme = "git";
        final String alias = nameTextBox.getText();
        final String username = usernameTextBox.getText();
        final String password = passwordTextBox.getText();
        final Map<String, Object> env = new HashMap<String, Object>( 3 );
        env.put( "username", username );
        env.put( "crypt:password", password );
        env.put( "init", true );


        repositoryService.call( new RemoteCallback<Repository>() {
                                    @Override
                                    public void callback( Repository o ) {
                                        Window.alert( "The repository is created successfully" );
                                        if (availableGroups.containsKey(group)) {
                                            groupService.call(new RemoteCallback<Collection<Group>>() {
                                                              @Override
                                                              public void callback( Collection<Group> groups ) {
                                                                  hide();
                                                              }
                                                          },
                                                new ErrorCallback() {
                                                    @Override
                                                    public boolean error( final Message message,
                                                            final Throwable throwable ) {
                                                        Window.alert( "Can't add repository to a group. \n" + message.toString() );

                                                        return false;
                                                    }
                                                }).addRepository(availableGroups.get(group), o);

                                            }  else {
                                                hide();
                                            }
                                    }
                                },
                                new ErrorCallback() {
                                    @Override
                                    public boolean error( final Message message,
                                                          final Throwable throwable ) {
                                        Window.alert( "Can't create repository, please check error message. \n" + message.toString() );

                                        return false;
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
