/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.messageconsole.client.console;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.screens.messageconsole.events.MessageUtils;
import org.kie.workbench.common.screens.messageconsole.events.PublishBatchMessagesEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
@WorkbenchScreen(identifier = "org.kie.workbench.common.screens.messageconsole.MessageConsole")
public class MessageConsoleScreen
        implements MessageConsoleScreenView.Presenter {

    private final PlaceManager placeManager;
    private final MessageConsoleScreenView view;
    private final MessageConsoleService messageConsoleService;

    private final Caller<BuildService> buildService;
    private final Event<PublishBatchMessagesEvent> publishBatchMessagesEvent;

    private Project project;

    private Menus menus;

    @Inject
    public MessageConsoleScreen( final MessageConsoleScreenView view,
            final PlaceManager placeManager,
            final MessageConsoleService messageConsoleService,
            final Caller<BuildService> buildService,
            final Event<PublishBatchMessagesEvent> publishBatchMessagesEvent ) {
        this.view = view;
        this.placeManager = placeManager;
        this.messageConsoleService = messageConsoleService;
        this.buildService = buildService;
        this.publishBatchMessagesEvent = publishBatchMessagesEvent;

        makeMenuBar();

        view.setPresenter( this );
    }

    private void makeMenuBar() {
        menus = MenuFactory
                //TODO use constants
                .newTopLevelMenu( "refresh" )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        view.showBusyIndicator( "ProjectEditorResources.CONSTANTS.Refreshing()" );
                        buildService.call( new RemoteCallback<BuildResults>() {
                            @Override
                            public void callback( final BuildResults results ) {
                                PublishBatchMessagesEvent batchMessages = new PublishBatchMessagesEvent();
                                batchMessages.setCleanExisting( true );
                                batchMessages.setMessageType( MessageUtils.BUILD_SYSTEM_MESSAGE );

                                if ( results.getMessages() != null ) {
                                    for ( BuildMessage buildMessage : results.getMessages() ) {
                                        batchMessages.getMessagesToPublish().add( MessageUtils.convert( buildMessage ) );
                                    }
                                }
                                publishBatchMessagesEvent.fire( batchMessages );
                                view.hideBusyIndicator();
                            }
                        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).build( project );
                    }
                } )
                .endMenu()
                .build();
    }

    public void selectedProjectChanged( @Observes final ProjectContextChangeEvent event ) {
        this.project = event.getProject();
        this.menus.getItems().get( 0 ).setEnabled( project != null );
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return Position.SOUTH;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        //TODO set properly defined constants
        return "Message console";//ProjectEditorResources.CONSTANTS.Problems();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
