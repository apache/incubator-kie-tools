/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.editor.group.workflow;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.Group;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsMessages;
import org.uberfire.ext.security.management.client.widgets.management.editor.group.GroupViewer;
import org.uberfire.ext.security.management.client.widgets.management.editor.workflow.EntityWorkflowView;
import org.uberfire.ext.security.management.client.widgets.management.events.ContextualEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.DeleteGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnDeleteEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.INFO;

/**
 * <p>Main entry point for viewing a group instance.</p>
 * 
 * @since 0.8.0
 */
@Dependent
public class GroupViewerWorkflow implements IsWidget {

    ClientUserSystemManager userSystemManager;
    Event<OnErrorEvent> errorEvent;
    ConfirmBox confirmBox;
    Event<NotificationEvent> workbenchNotification;
    Event<DeleteGroupEvent> deleteGroupEvent;
    GroupViewer groupViewer;
    public EntityWorkflowView view;

    Group group;
    
    @Inject
    public GroupViewerWorkflow(final ClientUserSystemManager userSystemManager, 
                               final Event<OnErrorEvent> errorEvent,
                               final ConfirmBox confirmBox,
                               final Event<NotificationEvent> workbenchNotification,
                               final Event<DeleteGroupEvent> deleteGroupEvent,
                               final GroupViewer groupViewer,
                               final EntityWorkflowView view) {
        this.userSystemManager = userSystemManager;
        this.errorEvent = errorEvent;
        this.confirmBox = confirmBox;
        this.workbenchNotification = workbenchNotification;
        this.deleteGroupEvent = deleteGroupEvent;
        this.groupViewer = groupViewer;
        this.view = view;
    }
    
    @PostConstruct
    public void setup() {
        
    }

    public void show(final String name) {
        assert name != null;
        clear();

        // Configure the workflow view.
        view.setWidget(groupViewer.asWidget())
                .setSaveButtonVisible(false)
                .setSaveButtonEnabled(false)
                .setCancelButtonVisible(false)
                .setCallback(null);
        
        // Perform load and show logic.
        load(name, new Command() {
            @Override
            public void execute() {
                groupViewer.show(group);
            }
        });
    }
    
    public void clear() {
        groupViewer.clear();
        view.clearNotification();
        group = null;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    void load(final String name, final Command callback) {
        // Call backend service.
        userSystemManager.groups(new RemoteCallback<Group>() {
            @Override
            public void callback(final Group group) {
                GroupViewerWorkflow.this.group = group;
                callback.execute();
            }
        }, errorCallback).get(name);
    }

    void delete() {
        final String name = group.getName();
        userSystemManager.groups(new RemoteCallback<Void>() {
            @Override
            public void callback(final Void o) {
                deleteGroupEvent.fire(new DeleteGroupEvent(name));
                workbenchNotification.fire(new NotificationEvent(UsersManagementWidgetsMessages.INSTANCE.groupRemoved(name), INFO));
                clear();
            }
        }, new ErrorCallback<Message>() {
            @Override
            public boolean error(final Message o, final Throwable throwable) {
                showError(throwable);
                return false;
            }
        }).delete(name);
    }

    void onDeleteUserEvent(@Observes final OnDeleteEvent onDeleteEvent) {
        if (checkEventContext(onDeleteEvent, groupViewer)) {
            confirmBox.show(UsersManagementWidgetsConstants.INSTANCE.confirmAction(), UsersManagementWidgetsConstants.INSTANCE.ensureRemoveGroup(),
                    new Command() {
                        @Override
                        public void execute() {
                            delete();
                        }
                    });
        }
    }

    protected boolean checkEventContext(final ContextualEvent contextualEvent, final Object context) {
        return contextualEvent != null && contextualEvent.getContext() != null && contextualEvent.getContext().equals(context);
    }

    protected final ErrorCallback<Message> errorCallback = new ErrorCallback<Message>() {
        @Override
        public boolean error(final Message message, final Throwable throwable) {
            showError(throwable);
            return false;
        }
    };

    protected void showError(final Throwable throwable) {
        final String msg = throwable != null ? throwable.getMessage() : UsersManagementWidgetsConstants.INSTANCE.genericError();
        errorEvent.fire(new OnErrorEvent(GroupViewerWorkflow.this, msg));
    }

}
