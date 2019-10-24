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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

import org.guvnor.messageconsole.events.FilteredMessagesEvent;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.PublishMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.guvnor.messageconsole.events.UnpublishMessagesEvent;
import org.guvnor.messageconsole.whitelist.MessageConsoleWhiteList;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.rpc.SessionInfo;

/**
 * Service for Message Console, the Console is a screen that shows compile time errors.
 * This listens to Messages and if the Console is not open it opens it.
 */
@ApplicationScoped
public class MessageConsoleService {

    private SyncBeanManager iocManager;
    private PlaceManager placeManager;
    private SessionInfo sessionInfo;
    private User identity;
    private Event<FilteredMessagesEvent> filteredMessagesEvent;

    private ListDataProvider<MessageConsoleServiceRow> dataProvider = new ListDataProvider<MessageConsoleServiceRow>();

    //The identifier has been preserved from kie-wb-common so existing .niogit System repositories are not broken
    private static final String MESSAGE_CONSOLE = "org.kie.workbench.common.screens.messageconsole.MessageConsole";

    private String currentPerspective;

    public MessageConsoleService() {
        //CDI proxy
    }

    @Inject
    public MessageConsoleService(final SyncBeanManager iocManager,
                                 final PlaceManager placeManager,
                                 final SessionInfo sessionInfo,
                                 final User identity,
                                 final Event<FilteredMessagesEvent> filteredMessagesEvent) {

        this.iocManager = iocManager;
        this.placeManager = placeManager;
        this.sessionInfo = sessionInfo;
        this.identity = identity;
        this.filteredMessagesEvent = filteredMessagesEvent;
    }

    public void publishMessages(final @Observes PublishMessagesEvent publishEvent) {
        publishMessages(publishEvent.getSessionId(),
                        publishEvent.getUserId(),
                        publishEvent.getPlace(),
                        publishEvent.getMessagesToPublish());
        if (publishEvent.isShowSystemConsole() && checkWhiteList()) {
            placeManager.goTo(MESSAGE_CONSOLE);
        }
        fireFilteredMessagesEvent();
    }

    public void unpublishMessages(final @Observes UnpublishMessagesEvent unpublishEvent) {
        unpublishMessages(unpublishEvent.getSessionId(),
                          unpublishEvent.getUserId(),
                          unpublishEvent.getMessageType(),
                          unpublishEvent.getMessagesToUnpublish());
        if (unpublishEvent.isShowSystemConsole() && checkWhiteList()) {
            placeManager.goTo(MESSAGE_CONSOLE);
        }
        fireFilteredMessagesEvent();
    }

    public void publishBatchMessages(final @Observes PublishBatchMessagesEvent publishBatchEvent) {
        if (publishBatchEvent.isCleanExisting()) {
            unpublishMessages(publishBatchEvent.getSessionId(),
                              publishBatchEvent.getUserId(),
                              publishBatchEvent.getMessageType(),
                              publishBatchEvent.getMessagesToUnpublish());
        } else {
            //only remove provided messages
            removeRowsByMessage(publishBatchEvent.getMessagesToUnpublish());
        }
        publishMessages(publishBatchEvent.getSessionId(),
                        publishBatchEvent.getUserId(),
                        publishBatchEvent.getPlace(),
                        publishBatchEvent.getMessagesToPublish());
        if (publishBatchEvent.isShowSystemConsole() && checkWhiteList()) {
            placeManager.goTo(MESSAGE_CONSOLE);
        }
        fireFilteredMessagesEvent();
    }

    private void fireFilteredMessagesEvent() {
        filteredMessagesEvent.fire(new FilteredMessagesEvent(dataProvider.getList()
                                                                         .stream()
                                                                         .map(m -> m.getMessage())
                                                                         .collect(Collectors.toList())));
    }

    public void addDataDisplay(final HasData<MessageConsoleServiceRow> display) {
        dataProvider.addDataDisplay(display);
        fireFilteredMessagesEvent();
    }

    public void onPerspectiveChange(final @Observes PerspectiveChange perspectiveChange) {
        currentPerspective = perspectiveChange.getIdentifier();
    }

    private void publishMessages(final String sessionId,
                                 final String userId,
                                 final PublishMessagesEvent.Place place,
                                 final List<SystemMessage> messages) {
        List<MessageConsoleServiceRow> list = dataProvider.getList();
        List<SystemMessage> newMessages = filterMessages(sessionId,
                                                         userId,
                                                         null,
                                                         messages);
        List<MessageConsoleServiceRow> newRows = new ArrayList<MessageConsoleServiceRow>();

        int index = (place != null && place == PublishMessagesEvent.Place.TOP) ? 0 : (list != null && list.size() > 0 ? list.size() : 0);

        for (SystemMessage systemMessage : newMessages) {
            newRows.add(new MessageConsoleServiceRow(sessionId,
                                                     userId,
                                                     systemMessage));
        }

        list.addAll(index,
                    newRows);
        list.sort(MessageConsoleServiceRow.DESC_ORDER);
    }

    private void unpublishMessages(final String sessionId,
                                   final String userId,
                                   final String messageType,
                                   final List<SystemMessage> messages) {

        String currentSessionId = sessionInfo != null ? sessionInfo.getId() : null;
        String currentUserId = identity != null ? identity.getIdentifier() : null;

        List<MessageConsoleServiceRow> rowsToDelete = new ArrayList<MessageConsoleServiceRow>();
        for (MessageConsoleServiceRow row : dataProvider.getList()) {
            if (sessionId == null && userId == null) {
                //delete messages for all users and sessions
                if (messageType == null || messageType.equals(row.getMessageType())) {
                    rowsToDelete.add(row);
                }
            } else if (sessionId != null) {
                //messages for a given session, no matter what the user have, sessions are unique.
                if (sessionId.equals(currentSessionId) && (messageType == null || messageType.equals(row.getMessageType()))) {
                    rowsToDelete.add(row);
                }
            } else {
                //messages for a user.
                if (userId.equals(currentUserId) && (messageType == null || messageType.equals(row.getMessageType()))) {
                    rowsToDelete.add(row);
                }
            }
        }

        dataProvider.getList().removeAll(rowsToDelete);
        removeRowsByMessage(messages);
    }

    private void removeRowsByMessage(final List<SystemMessage> messages) {
        List<MessageConsoleServiceRow> rowsToDelete = new ArrayList<MessageConsoleServiceRow>();
        if (messages != null) {
            for (MessageConsoleServiceRow row : dataProvider.getList()) {
                if (messages.contains(row.getMessage())) {
                    rowsToDelete.add(row);
                }
            }
            dataProvider.getList().removeAll(rowsToDelete);
        }
    }

    private List<SystemMessage> filterMessages(final String sessionId,
                                               final String userId,
                                               final String messageType,
                                               final List<SystemMessage> messages) {
        List<SystemMessage> result = new ArrayList<SystemMessage>();

        String currentSessionId = sessionInfo != null ? sessionInfo.getId() : null;
        String currentUserId = identity != null ? identity.getIdentifier() : null;

        if (messages != null) {
            for (SystemMessage message : messages) {
                if (sessionId == null && userId == null) {
                    //messages for all users, all sessions.
                    if (messageType == null || messageType.equals(message.getMessageType())) {
                        result.add(message);
                    }
                } else if (sessionId != null) {
                    //messages for a given session, no matter what the user have, sessions are unique.
                    if (sessionId.equals(currentSessionId) && (messageType == null || messageType.equals(message.getMessageType()))) {
                        result.add(message);
                    }
                } else {
                    //messages for a user.
                    if (userId.equals(currentUserId) && (messageType == null || messageType.equals(message.getMessageType()))) {
                        result.add(message);
                    }
                }
            }
        }
        return result;
    }

    private boolean checkWhiteList() {

        // I herd you like lists so I put a list into your list
        Collection<SyncBeanDef<MessageConsoleWhiteList>> whiteListList = getAvailableWhiteLists();

        if (whiteListList.isEmpty()) {
            return true;
        } else {
            return reLookupBean(whiteListList.iterator().next()).getInstance().contains(currentPerspective);
        }
    }

    private SyncBeanDef<MessageConsoleWhiteList> reLookupBean(SyncBeanDef<MessageConsoleWhiteList> baseBean) {
        return (SyncBeanDef<MessageConsoleWhiteList>) iocManager.lookupBean(baseBean.getBeanClass());
    }

    private Collection<SyncBeanDef<MessageConsoleWhiteList>> getAvailableWhiteLists() {
        return iocManager.lookupBeans(MessageConsoleWhiteList.class);
    }

    //This is required for Unit Testing
    ListDataProvider<MessageConsoleServiceRow> getDataProvider() {
        return dataProvider;
    }
}
