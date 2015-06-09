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

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * Observes all notification events, and coordinates their display and removal.
 */
@ApplicationScoped
public class NotificationManager {

    private final SyncBeanManager iocManager;
    private final Map<PlaceRequest, View> notificationsContainerViewMap = new HashMap<PlaceRequest, View>();

    private final PlaceManager placeManager;
    private final PlaceRequest rootPlaceRequest = new DefaultPlaceRequest( "org.uberfire.client.workbench.widgets.notifications.root" );

    public interface NotificationPopupHandle {

    }

    /**
     * The view contract for the UI that shows and hides active notifications.
     */
    public interface View {

        /**
         * Set the container relative to which Notifications are to be shown. This should be called before
         * either {@link #show(NotificationEvent, Command)} or {@link #hide(NotificationPopupHandle)}
         * and must be passed a non-null value.
         *
         * @param container
         *          The container relative to which Notifications will be shown. Must not be null.
         */
        void setContainer( final IsWidget container );
        
        /**
         * Configures the initial vertical spacing for the first notifications
         * (see {@link NotificationEvent#getInitialTopOffset()}). A default value is
         * used if this method is never invoked.
         * 
         * @param spacing
         *            the vertical spacing in number of pixels
         */
        void setInitialSpacing (int spacing);

        /**
         * Displays a notification with the given severity and contents.
         *
         * @param event
         *          The notification event. Must not be null.
         * @param hideCommand
         *          The command that must be called when the notification is to be closed. When this command is
         *          invoked, the notification manager will change the notification status from active to acknowledged,
         *          and it will invoke the {@link #hide(NotificationPopupHandle)} method with the notification handle
         *          that this method call returned.
         * @return The object to pass to {@link #hide(NotificationPopupHandle)} that will hide this notification. Must
         * not return null.
         */
        NotificationPopupHandle show( final NotificationEvent event,
                                      final Command hideCommand );

        /**
         * Hides the active notification identified by the given popup handle. This call is made when a notification
         * changes state from "new" to "acknowledged." Once this call is made, the notification is still in the system,
         * but it should not be displayed as a new notification anymore. As an analogy, if the notification was an
         * email, this call would mark it as read.
         *
         * @param popup
         *          The handle for the active notification that should be hidden.
         */
        void hide( final NotificationPopupHandle popup );
        
        /**
         * Hides all active notifications.
         */
        void hideAll();

        /**
         * Checks whether the given event is currently being shown.
         *
         * @param event
         *          The notification event. Must not be null.
         * @return true if shown
         */
        boolean isShowing( final NotificationEvent event );
    }

    @Inject
    public NotificationManager( final SyncBeanManager iocManager,
                                final PlaceManager placeManager ) {
        this.iocManager = PortablePreconditions.checkNotNull( "iocManager",
                                                              iocManager );
        this.placeManager = PortablePreconditions.checkNotNull( "placeManager",
                                                                placeManager );
    }

    private class HideNotificationCommand implements Command {

        private NotificationPopupHandle handle;
        private final View notificationContainerView;

        HideNotificationCommand( final View notificationContainerView ) {
            this.notificationContainerView = PortablePreconditions.checkNotNull( "notificationContainerView", notificationContainerView );
        }

        @Override
        public void execute() {
            if ( handle == null ) {
                throw new IllegalStateException( "The show() method hasn't returned a handle yet!" );
            }
            notificationContainerView.hide( handle );
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
     *          The notification to display and store in the notification system.
     */
    public void addNotification( @Observes final NotificationEvent event ) {
        //If an explicit container has not been specified use the RootPanel
        PlaceRequest placeRequest = event.getPlaceRequest();
        IsWidget notificationsContainer = RootPanel.get();
        if ( placeRequest == null ) {
            placeRequest = rootPlaceRequest;
        } else {
            final Activity activity = placeManager.getActivity( placeRequest );
            if ( activity instanceof WorkbenchActivity ) {
                notificationsContainer = ( (WorkbenchActivity) activity ).getWidget();
            }
        }

        //Lookup, or create, a View specific to the container
        View notificationsContainerView = notificationsContainerViewMap.get( placeRequest );
        if ( notificationsContainerView == null ) {
            final IOCBeanDef<View> containerViewBeanDef = iocManager.lookupBean( View.class );
            if ( containerViewBeanDef != null ) {
                notificationsContainerView = containerViewBeanDef.getInstance();
                notificationsContainerView.setContainer( notificationsContainer );
                if (event.getInitialTopOffset() != null) {
                    notificationsContainerView.setInitialSpacing( event.getInitialTopOffset() );
                }
                notificationsContainerViewMap.put( placeRequest,
                                                   notificationsContainerView );
            }
        }
        if ( notificationsContainerView == null ) {
            return;
        }

        //Show notification in the container
        if ( !event.isSingleton() || !notificationsContainerView.isShowing( event ) ) {
            HideNotificationCommand hideCommand = new HideNotificationCommand( notificationsContainerView );
            NotificationPopupHandle handle = notificationsContainerView.show( event,
                                                                              hideCommand );
            hideCommand.setHandle( handle );
        }
    }

    //Clean-up container map when an Activity closes; in the absence of a WeakHashMap in JavaScript
    public void onClosePlaceEvent( @Observes final ClosePlaceEvent event ) {
        final PlaceRequest placeRequest = event.getPlace();
        if ( placeRequest == null ) {
            return;
        }
        
        final View view = notificationsContainerViewMap.remove( placeRequest );
        if ( view != null ) {
            view.hideAll();
        }
    }
    
    public void onPlaceLostFocus( @Observes final PlaceLostFocusEvent event ) {
        final View view = notificationsContainerViewMap.get( event.getPlace() );
        if ( view != null ) {
            view.hideAll();
        }
    }

}
