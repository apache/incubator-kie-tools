/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.uberfire.client.workbench.widgets.notification;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * Observes all notification events, and coordinates their display and removal.
 */
@ApplicationScoped
public class NotificationManager {

    private final Map<PlaceRequest, View> notificationsContainerViewMap = new HashMap<>();
    private final PlaceRequest rootPlaceRequest = new DefaultPlaceRequest("org.uberfire.client.workbench.widgets.notifications.root");
    private SyncBeanManager iocManager;

    public NotificationManager() {
    }

    @Inject
    public NotificationManager(final SyncBeanManager iocManager) {
        this.iocManager = iocManager;
    }

    /**
     * Adds a new notification message to the system, asking the notification presenter to display it, and storing it in
     * the list of existing notifications. This method can be invoked directly, or it can be invoked indirectly by
     * firing a CDI {@link NotificationEvent}.
     *
     * @param event The notification to display and store in the notification system.
     */
    public void addNotification(@Observes final NotificationEvent event) {
        //If an explicit container has not been specified use the RootPanel
        IsWidget notificationsContainer = RootPanel.get();

        //Lookup, or create, a View specific to the container
        View notificationsContainerView = notificationsContainerViewMap.get(rootPlaceRequest);
        if (notificationsContainerView == null) {
            final SyncBeanDef<View> containerViewBeanDef = iocManager.lookupBean(View.class);
            if (containerViewBeanDef != null) {
                notificationsContainerView = containerViewBeanDef.getInstance();
                notificationsContainerView.setContainer(notificationsContainer);

                if (event.getInitialTopOffset() != null) {
                    notificationsContainerView.setInitialSpacing(event.getInitialTopOffset());
                } else {
                    notificationsContainerView.setInitialSpacing(0);
                }

                notificationsContainerViewMap.put(rootPlaceRequest,
                                                  notificationsContainerView);
            }
        }
        if (notificationsContainerView == null) {
            return;
        }

        //Show notification in the container
        if (!event.isSingleton() || !notificationsContainerView.isShowing(event)) {
            HideNotificationCommand hideCommand = new HideNotificationCommand(notificationsContainerView);
            NotificationPopupHandle handle = notificationsContainerView.show(event,
                                                                             hideCommand);
            hideCommand.setHandle(handle);
        }
    }

    public interface NotificationPopupHandle {

    }

    /**
     * The view contract for the UI that shows and hides active notifications.
     */
    public interface View {

        /**
         * either {@link #show(NotificationEvent, Command)} or {@link #hide(NotificationPopupHandle)}
         * and must be passed a non-null value.
         *
         * @param container The container relative to which Notifications will be shown. Must not be null.
         */
        void setContainer(final IsWidget container);

        /**
         * Configures the initial vertical spacing for the first notifications
         * (see {@link NotificationEvent#getInitialTopOffset()}). A default value is
         * used if this method is never invoked.
         *
         * @param spacing the vertical spacing in number of pixels
         */
        void setInitialSpacing(int spacing);

        /**
         * Displays a notification with the given severity and contents.
         *
         * @param event       The notification event. Must not be null.
         * @param hideCommand The command that must be called when the notification is to be closed. When this command is
         *                    invoked, the notification manager will change the notification status from active to acknowledged,
         *                    and it will invoke the {@link #hide(NotificationPopupHandle)} method with the notification handle
         *                    that this method call returned.
         * @return The object to pass to {@link #hide(NotificationPopupHandle)} that will hide this notification. Must
         * not return null.
         */
        NotificationPopupHandle show(final NotificationEvent event,
                                     final Command hideCommand);

        /**
         * Hides the active notification identified by the given popup handle. This call is made when a notification
         * changes state from "new" to "acknowledged." Once this call is made, the notification is still in the system,
         * but it should not be displayed as a new notification anymore. As an analogy, if the notification was an
         * email, this call would mark it as read.
         *
         * @param popup The handle for the active notification that should be hidden.
         */
        void hide(final NotificationPopupHandle popup);

        /**
         * Hides all active notifications.
         */
        void hideAll();

        /**
         * Checks whether the given event is currently being shown.
         *
         * @param event The notification event. Must not be null.
         * @return true if shown
         */
        boolean isShowing(final NotificationEvent event);
    }

    private class HideNotificationCommand implements Command {

        private final View notificationContainerView;
        private NotificationPopupHandle handle;

        HideNotificationCommand(final View notificationContainerView) {
            this.notificationContainerView =
                    Objects.requireNonNull(notificationContainerView,
                                           "Parameter named 'notificationContainerView' should be not null!");
        }

        @Override
        public void execute() {
            if (handle == null) {
                throw new IllegalStateException("The show() method hasn't returned a handle yet!");
            }
            notificationContainerView.hide(handle);
        }

        void setHandle(NotificationPopupHandle handle) {
            this.handle = handle;
        }
    }
}