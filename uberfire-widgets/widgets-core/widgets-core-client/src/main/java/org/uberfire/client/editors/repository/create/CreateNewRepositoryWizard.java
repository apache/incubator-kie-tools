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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.backend.Root;
import org.uberfire.backend.vfs.FileSystem;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.PathImpl;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.resources.CoreImages;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@Dependent
public class CreateNewRepositoryWizard extends FormStylePopup {

    @Inject
    private Caller<VFSService>              vfsService;

    @Inject
    private Caller<FileExplorerRootService> rootService;

    @Inject
    private Event<Root>                     event;

    private static CoreImages               images              = GWT.create( CoreImages.class );

    private TextBox                         nameTextBox         = new TextBox();
    private TextBox                         descriptionTextArea = new TextBox();
    private TextBox                         usernameTextBox     = new TextBox();
    private PasswordTextBox                 passwordTextBox     = new PasswordTextBox();

    public CreateNewRepositoryWizard() {
        super( images.backupLarge(),
               "New Repository" );

        nameTextBox.setTitle( "New Repository Name" );
        nameTextBox.setWidth( "200px" );
        addAttribute( "New Repository Name:",
                      nameTextBox );

        //descriptionTextArea.setSize("200px", "120px");
        descriptionTextArea.setWidth( "200px" );
        descriptionTextArea.setTitle( "Description" );
        addAttribute( "Description:",
                      descriptionTextArea );

        usernameTextBox.setWidth( "200px" );
        usernameTextBox.setTitle( "User Name" );
        addAttribute( "User Name:",
                      usernameTextBox );

        passwordTextBox.setWidth( "200px" );
        passwordTextBox.setTitle( "Password" );
        addAttribute( "Password:",
                      passwordTextBox );

        HorizontalPanel hp = new HorizontalPanel();
        Button create = new Button( "Create" );
        create.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent arg0) {

                final Map<String, Object> env = new HashMap<String, Object>( 2 );
                env.put( "username", passwordTextBox.getText() );
                env.put( "password", usernameTextBox.getText() );
                env.put( "init", true );
                final String uri = "git://" + nameTextBox.getText();

                vfsService.call( new RemoteCallback<FileSystem>() {
                    @Override
                    public void callback(final FileSystem v) {
                        Window.alert( "The repository is created successfully" );
                        hide();
                        final PlaceRequest repositoryEditor = new DefaultPlaceRequest( "RepositoryEditor" )
                                .addParameter( "path:uri", uri )
                                .addParameter( "path:name", nameTextBox.getText() );

                        final Root newRoot = new Root(new PathImpl(nameTextBox.getText(), uri), repositoryEditor);

                        rootService.call( new RemoteCallback<Root>() {
                            @Override
                            public void callback(Root response) {
                                event.fire( newRoot );
                            }
                        } ).addRoot( newRoot );
                    }
                } ).newFileSystem( uri, env );
            }
        } );
        hp.add( create );

        Button cancel = new Button( "Cancel" );
        cancel.addClickHandler( new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                hide();
            }
        } );
        hp.add( new HTML( "&nbsp" ) );
        hp.add( cancel );
        addAttribute( "",
                      hp );
    }

}
