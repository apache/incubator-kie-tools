/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.notifications;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.animations.LinearFadeOutAnimation;
import org.uberfire.client.workbench.widgets.notifications.NotificationManager.NotificationPopupHandle;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.client.workbench.widgets.notifications.NotificationManager;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.ActivityResourceType;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.security.shared.api.identity.User;

@Dependent
public class NotificationPopupsManagerView implements NotificationManager.View {

    private final int SPACING = 48;
    private final List<PopupHandle> activeNotifications = new ArrayList<PopupHandle>();
    private final List<PopupHandle> pendingRemovals = new ArrayList<PopupHandle>();
    //When true we are in the process of removing a notification message
    private boolean removing = false;
    private int initialSpacing = SPACING;
    private IsWidget container;
    @Inject private PlaceManager placeManager;
    @Inject private AuthorizationManager authorizationManager;
    @Inject private User user;
    @Inject private ActivityBeansCache activityBeansCache;

    @Override
    public void setContainer(final IsWidget container) {
        this.container = PortablePreconditions.checkNotNull("container",
                                                            container);
    }

    @Override
    public void setInitialSpacing(int spacing) {
        this.initialSpacing = spacing;
    }

    @Override
    public NotificationPopupHandle show(NotificationEvent event,
                                        Command hideCommand) {
        if (container == null) {
            throw new IllegalStateException("The setContainer() method hasn't been called!");
        }

        final NotificationPopupView view = new NotificationPopupView();
        final PopupHandle popupHandle = new PopupHandle(view,
                                                        event);

        activeNotifications.add(popupHandle);
        int size = activeNotifications.size();
        int topMargin = (size == 1) ? initialSpacing : (size * SPACING) - (SPACING - initialSpacing);
        view.setPopupPosition(getLeftPosition(container.asWidget()) + getMargin(),
                              getTopPosition(container.asWidget()) + topMargin);
        view.setNotification(event.getNotification());
        view.setType(event.getType());
        view.setNotificationWidth(getWidth() + "px");

        if (event.hasNavigation()) {
            final String identifier = event.getNavigationPlace().getIdentifier();
            final SyncBeanDef<Activity> syncBeanDefActivity = activityBeansCache.getActivity(identifier);
            final ResourceRef resourceRef = new ResourceRef(identifier, syncBeanDefActivity.getInstance().getResourceType());
            final Boolean isAuthorized = authorizationManager.authorize(resourceRef, user);
            if (isAuthorized) {
                view.addNavigation(
                        event.getNavigationText(),
                        () -> {
                            hideCommand.execute();
                            placeManager.goTo(event.getNavigationPlace());
                        });
            }
        }

        view.show(hideCommand,
                  event.autoHide());

        return popupHandle;
    }

    private int getTopPosition(final Widget widget) {
        int top = widget.getAbsoluteTop();
        // if top is negative (due to scrolling) we try to align with the parent
        // to make sure the notifications are always visible
        if (top < 0 && widget.getParent() != null) {
            top = getTopPosition(widget.getParent());
        }
        return Math.max(top,
                        0);
    }

    private int getLeftPosition(final Widget widget) {
        int left = widget.getAbsoluteLeft();
        // if left is negative (due to scrolling) we try to align with the parent
        // to make sure the notifications are always visible
        if (left < 0 && widget.getParent() != null) {
            left = getLeftPosition(widget.getParent());
        }
        return Math.max(left,
                        0);
    }

    @Override
    public void hide(final NotificationPopupHandle handle) {
        if (container == null) {
            throw new IllegalStateException("The setContainer() method hasn't been called!");
        }

        final int removingIndex = activeNotifications.indexOf(handle);
        if (removingIndex == -1) {
            return;
        }
        if (removing) {
            pendingRemovals.add((PopupHandle) handle);
            return;
        }
        removing = true;
        final NotificationPopupView view = ((PopupHandle) handle).view;
        final LinearFadeOutAnimation fadeOutAnimation = new LinearFadeOutAnimation(view) {
            @Override
            public void onUpdate(double progress) {
                super.onUpdate(progress);
                for (int i = removingIndex; i < activeNotifications.size(); i++) {
                    NotificationPopupView v = activeNotifications.get(i).view;
                    final int left = v.getPopupLeft();
                    final int top = (int) (((i + 1) * SPACING) - (progress * SPACING))
                            - (SPACING - initialSpacing) + getTopPosition(container.asWidget());
                    v.setPopupPosition(left,
                                       top);
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                view.hide();
                activeNotifications.remove(handle);
                removing = false;
                if (pendingRemovals.size() > 0) {
                    PopupHandle popupHandle = pendingRemovals.remove(0);
                    hide(popupHandle);
                }
            }
        };
        fadeOutAnimation.run(500);
    }

    @Override
    public void hideAll() {
        for (NotificationPopupHandle handle : activeNotifications) {
            hide(handle);
        }
    }

    @Override
    public boolean isShowing(NotificationEvent event) {
        for (PopupHandle handle : activeNotifications) {
            if (handle.event.equals(event)) {
                return true;
            }
        }
        return false;
    }

    //80% of container width
    private int getWidth() {
        return (int) (container.asWidget().getElement().getClientWidth() * 0.8);
    }

    //10% of container width
    private int getMargin() {
        return (container.asWidget().getElement().getClientWidth() - getWidth()) / 2;
    }

    private static class PopupHandle implements NotificationPopupHandle {

        final NotificationPopupView view;
        final NotificationEvent event;

        PopupHandle(NotificationPopupView view,
                    NotificationEvent event) {
            this.view = PortablePreconditions.checkNotNull("view",
                                                           view);
            this.event = PortablePreconditions.checkNotNull("event",
                                                            event);
        }
    }
}
