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
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.messageconsole.client.console.resources.MessageConsoleResources;
import org.guvnor.messageconsole.events.FilteredMessagesEvent;
import org.guvnor.messageconsole.events.MessageUtils;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.guvnor.messageconsole.utils.CSVConverter;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.menu.RefreshMenuBuilder;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
//The identifier has been preserved from kie-wb-common so existing .niogit System repositories are not broken
@WorkbenchScreen(identifier = MessageConsoleScreen.ALERTS)
public class MessageConsoleScreen implements RefreshMenuBuilder.SupportsRefresh {

    public static final String ALERTS = "org.kie.workbench.common.screens.messageconsole.MessageConsole";

    private Caller<BuildService> buildService;
    private Event<PublishBatchMessagesEvent> publishBatchMessagesEvent;
    private MessageConsoleViewImpl view;
    private Event<NotificationEvent> workbenchNotification;
    private Module module;
    private Menus menus;
    private List<SystemMessage> messages;

    public MessageConsoleScreen() {

    }

    @Inject
    public MessageConsoleScreen(final Caller<BuildService> buildService,
                                final Event<PublishBatchMessagesEvent> publishBatchMessagesEvent,
                                final MessageConsoleViewImpl view,
                                final Event<NotificationEvent> workbenchNotification) {

        this.buildService = buildService;
        this.publishBatchMessagesEvent = publishBatchMessagesEvent;
        this.view = view;
        this.workbenchNotification = workbenchNotification;

        makeMenuBar();
    }

    public void onFilteredMessagesEvent(final @Observes FilteredMessagesEvent filteredMessagesEvent) {
        messages = filteredMessagesEvent.getMessages();
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
        }, new HasBusyIndicatorDefaultErrorCallback(view)).build(module);
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu(MessageConsoleResources.CONSTANTS.ClearMessageConsole())
                .respondsWith(() -> {
                    PublishBatchMessagesEvent batchMessages = new PublishBatchMessagesEvent();
                    batchMessages.setCleanExisting(true);
                    batchMessages.setMessagesToPublish(new ArrayList<>());
                    publishBatchMessagesEvent.fire(batchMessages);
                })
                .endMenu()
                .newTopLevelMenu(MessageConsoleResources.CONSTANTS.CopyMessageConsole())
                .respondsWith(() -> {
                    copyMessages();
                })
                .endMenu()
                .newTopLevelCustomMenu(new RefreshMenuBuilder(this))
                .endMenu()
                .build();
    }

    public void copyMessages() {
        List<List<String>> table = new ArrayList<>();
        table.add(Arrays.asList("Level", "Text", "File", "Column", "Line"));

        if (messages != null) {
            messages.forEach(m -> {
                String level = m.getLevel() != null ? m.getLevel().name() : "";
                String text = m.getText();
                String file = m.getPath() != null ? m.getPath().getFileName() : "";
                String column = Integer.toString(m.getColumn());
                String line = Integer.toString(m.getLine());
                table.add(Arrays.asList(level, text, file, column, line));
            });
        }

        String csv = CSVConverter.convertTable(table);
    boolean ok = view.copyMessages(csv);

    if (ok) {
        workbenchNotification.fire(
            new NotificationEvent(MessageConsoleResources.CONSTANTS.MessagesCopiedToClipboard(),
                      NotificationEvent.NotificationType.INFO));
    } else {
        workbenchNotification.fire(
           new NotificationEvent(MessageConsoleResources.CONSTANTS.MessagesNotCopiedToClipboard(),
                     NotificationEvent.NotificationType.ERROR));
    }
    }

    public void selectedProjectChanged(@Observes final WorkspaceProjectContextChangeEvent event) {
        this.module = event.getModule();
        this.menus.getItems().get(0).setEnabled(module != null);
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.SOUTH;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return view.getTitle();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(menus);
    }
}
