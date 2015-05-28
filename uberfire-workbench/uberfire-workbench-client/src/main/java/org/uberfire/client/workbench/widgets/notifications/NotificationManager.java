/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.widgets.notifications;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.NotificationEvent.NotificationType;

import com.google.gwt.user.client.Command;

/**
 * Observes all notification events, and coordinates their display and removal.
 */
@ApplicationScoped
public class NotificationManager {

    private final View view;

    public interface NotificationPopupHandle {

    }

    /**
     * The view contract for the UI that shows and hides active notifications.
     */
    public interface View {

        /**
         * Displays a notification with the given severity and contents.
         *
         * @param event
         *            The notification event. Must not be null.
         * @param hideCommand
         *            The command that must be called when the notification is to be closed. When this command is
         *            invoked, the notification manager will change the notification status from active to acknowledged,
         *            and it will invoke the {@link #hide(NotificationPopupHandle)} method with the notification handle
         *            that this method call returned.
         * @return The object to pass to {@link #hide(NotificationPopupHandle)} that will hide this notification. Must
         *         not return null.
         */
        NotificationPopupHandle show( NotificationEvent event,
                                      Command hideCommand );

        /**
         * Hides the active notification identified by the given popup handle. This call is made when a notification
         * changes state from "new" to "acknowledged." Once this call is made, the notification is still in the system,
         * but it should not be displayed as a new notification anymore. As an analogy, if the notification was an
         * email, this call would mark it as read.
         *
         * @param popup
         *            the handle for the active notification that should be hidden.
         */
        void hide( NotificationPopupHandle popup );

        /**
         * Checks whether the given event is currently being shown.
         *
         * @param event
         *            The notification event. Must not be null.
         * @return true if shown
         */
        boolean isShowing( NotificationEvent event );
    }

    @Inject
    public NotificationManager( View view ) {
        this.view = checkNotNull( "view", view );
    }

    private class HideNotificationCommand implements Command {

        private NotificationPopupHandle handle;

        @Override
        public void execute() {
            if ( handle == null ) {
                throw new IllegalStateException( "The show() method hasn't returned a handle yet!" );
            }
            view.hide( handle );
        }

        void setHandle( NotificationPopupHandle handle ) {
            this.handle = handle;
        }
    }

    /**
     * Adds a new notification message to the system, asking the notification presenter to display it, and storing it in
     * the list of existing notifications. This method can be invoked directly, or it can be invoked indirectly by
     * firing a CDI {@link NotificationEvent}.
     *
     * @param event
     *            the notification to display and store in the notification system.
     */
    public void addNotification( @Observes final NotificationEvent event ) {
        if ( !event.isSingleton() || ( event.isSingleton() && !view.isShowing( event ) ) ) {
            HideNotificationCommand hideCommand = new HideNotificationCommand();
            NotificationPopupHandle handle = view.show( event,
                                                        hideCommand );
            hideCommand.setHandle( handle );
        }
    }

}
