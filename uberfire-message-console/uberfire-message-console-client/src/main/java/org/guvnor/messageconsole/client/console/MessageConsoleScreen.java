/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.messageconsole.client.console;

import java.util.ArrayList;
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
import org.guvnor.messageconsole.client.console.resources.MessageConsoleResources;
import org.guvnor.messageconsole.events.MessageUtils;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
//The identifier has been preserved from kie-wb-common so existing .niogit System repositories are not broken
@WorkbenchScreen(identifier = "org.kie.workbench.common.screens.messageconsole.MessageConsole")
public class MessageConsoleScreen implements RefreshMenuBuilder.SupportsRefresh {

    @Inject
    private Caller<BuildService> buildService;

    @Inject
    private Event<PublishBatchMessagesEvent> publishBatchMessagesEvent;

    @Inject
    private MessageConsoleViewImpl view;

    private Project project;

    private Menus menus;

    public MessageConsoleScreen() {
        makeMenuBar();
    }

    @Override
    public void onRefresh() {
        view.showBusyIndicator(MessageConsoleResources.CONSTANTS.Refreshing());
        buildService.call(new RemoteCallback<BuildResults>() {
                              @Override
                              public void callback(final BuildResults results) {
                                  PublishBatchMessagesEvent batchMessages = new PublishBatchMessagesEvent();
                                  batchMessages.setCleanExisting(true);
                                  batchMessages.setMessageType(MessageUtils.BUILD_SYSTEM_MESSAGE);

                                  if (results.getMessages() != null) {
                                      for (BuildMessage buildMessage : results.getMessages()) {
                                          batchMessages.getMessagesToPublish().add(MessageUtils.convert(buildMessage));
                                      }
                                  }
                                  publishBatchMessagesEvent.fire(batchMessages);
                                  view.hideBusyIndicator();
                              }
                          },
                          new HasBusyIndicatorDefaultErrorCallback(view)).build(project);
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu(MessageConsoleResources.CONSTANTS.ClearMessageConsole())
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        PublishBatchMessagesEvent batchMessages = new PublishBatchMessagesEvent();
                        batchMessages.setCleanExisting(true);
                        batchMessages.setMessagesToPublish(new ArrayList<SystemMessage>());

                        publishBatchMessagesEvent.fire(batchMessages);
                    }
                })
                .endMenu()
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this))
                .endMenu()
                .build();
    }

    public void selectedProjectChanged(@Observes final ProjectContextChangeEvent event) {
        this.project = event.getProject();
        this.menus.getItems().get(0).setEnabled(project != null);
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.SOUTH;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return MessageConsoleResources.CONSTANTS.MessageConsole();
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
