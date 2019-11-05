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

package org.uberfire.client.mvp;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.backend.vfs.impl.LockResult;
import org.uberfire.client.resources.i18n.WorkbenchConstants;
import org.uberfire.client.util.UserAgent;
import org.uberfire.client.workbench.VFSLockServiceProxy;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

/**
 * Default implementation of {@link LockManager} using the
 * {@link VFSLockServiceProxy} for lock management.
 */
@Dependent
public class LockManagerImpl implements LockManager {

    @Inject
    private VFSLockServiceProxy lockService;

    @Inject
    private javax.enterprise.event.Event<ChangeTitleWidgetEvent> changeTitleEvent;

    @Inject
    private javax.enterprise.event.Event<UpdatedLockStatusEvent> updatedLockStatusEvent;

    @Inject
    private javax.enterprise.event.Event<NotificationEvent> lockNotification;

    @Inject
    private LockDemandDetector lockDemandDetector;

    @Inject
    private User user;

    private LockTarget lockTarget;

    private LockInfo lockInfo = LockInfo.unlocked();
    private HandlerRegistration closeHandler;

    private boolean lockRequestPending;
    private boolean unlockRequestPending;

    private boolean lockSyncComplete;
    private List<Runnable> syncCompleteRunnables = new ArrayList<Runnable>();

    private Timer reloadTimer;

    @Override
    public void init(final LockTarget lockTarget) {
        this.lockTarget = lockTarget;

        final ParameterizedCommand<LockInfo> command = new ParameterizedCommand<LockInfo>() {

            @Override
            public void execute(final LockInfo lockInfo) {
                if (!lockRequestPending && !unlockRequestPending) {
                    updateLockInfo(lockInfo);
                }
            }
        };
        lockService.retrieveLockInfo(lockTarget.getPath(),
                                     command);
    }

    @Override
    public void onFocus() {
        publishJsApi();
        fireChangeTitleEvent();
        fireUpdatedLockStatusEvent();
    }

    @Override
    public void acquireLockOnDemand() {
        if (lockTarget == null) {
            return;
        }

        final Widget widget = getLockTargetWidget();
        final Element element = widget.getElement();
        acquireLockOnDemand(element);

        widget.addAttachHandler(new AttachEvent.Handler() {

            @Override
            public void onAttachOrDetach(AttachEvent event) {
                // Handle widget reattachment/reparenting
                if (event.isAttached()) {
                    acquireLockOnDemand(element);
                }
            }
        });
    }

    public EventListener acquireLockOnDemand(final Element element) {
        Event.sinkEvents(element,
                         lockDemandDetector.getLockDemandEventTypes());

        EventListener lockDemandListener = new EventListener() {

            @Override
            public void onBrowserEvent(Event event) {
                if (isLockedByCurrentUser()) {
                    return;
                }

                if (lockDemandDetector.isLockRequired(event)) {
                    acquireLock();
                }
            }
        };

        Event.setEventListener(element,
                               lockDemandListener);

        return lockDemandListener;
    }

    @Override
    public void acquireLock() {
        if (lockTarget == null) {
            return;
        }
        if (isLockedByCurrentUser()) {
            fireChangeTitleEvent();
            return;
        }

        if (lockInfo.isLocked()) {
            handleLockFailure(lockInfo);
        } else if (!lockRequestPending) {
            lockRequestPending = true;
            final ParameterizedCommand<LockResult> command = new ParameterizedCommand<LockResult>() {

                @Override
                public void execute(final LockResult result) {
                    if (result.isSuccess()) {
                        updateLockInfo(result.getLockInfo());
                        releaseLockOnClose();
                    } else {
                        handleLockFailure(result.getLockInfo());
                    }
                    lockRequestPending = false;
                }
            };
            lockService.acquireLock(lockTarget.getPath(),
                                    command);
        }
    }

    @Override
    public void releaseLock() {
        final Runnable releaseLock = new Runnable() {

            @Override
            public void run() {
                releaseLockInternal();
            }
        };
        if (lockSyncComplete) {
            releaseLock.run();
        } else {
            syncCompleteRunnables.add(releaseLock);
        }
    }

    private void releaseLockInternal() {
        if (isLockedByCurrentUser() && !unlockRequestPending) {
            unlockRequestPending = true;

            ParameterizedCommand<LockResult> command = new ParameterizedCommand<LockResult>() {

                @Override
                public void execute(final LockResult result) {
                    updateLockInfo(result.getLockInfo());

                    if (result.isSuccess()) {
                        if (closeHandler != null) {
                            closeHandler.removeHandler();
                        }
                    }

                    unlockRequestPending = false;
                }
            };
            lockService.releaseLock(lockTarget.getPath(),
                                    command);
        }
    }

    private void releaseLockOnClose() {
        if (UserAgent.isChrome()) {
            closeHandler = Window.addCloseHandler(event -> requestReleaseLock());
        } else {
            closeHandler = Window.addWindowClosingHandler(event -> releaseLock());
        }
    }

    private native void requestReleaseLock()/*-{
        var pathArray = window.location.pathname.split('/').filter(Boolean);

        var url = "";
        for (var i = 0; i < pathArray.length - 1; i++) {
            url += "/" + pathArray[i];
        }

        url += "/releaseUserLocksServlet";

        var request = new XMLHttpRequest();
        request.open('GET', url, false);
        request.send();
    }-*/;

    private void handleLockFailure(final LockInfo lockInfo) {

        if (lockInfo != null) {
            updateLockInfo(lockInfo);
            lockNotification.fire(new NotificationEvent(WorkbenchConstants.INSTANCE.lockedMessage(lockInfo.lockedBy()),
                                                        NotificationEvent.NotificationType.INFO,
                                                        true,
                                                        lockTarget.getPlace(),
                                                        20));
        } else {
            lockNotification.fire(new NotificationEvent(WorkbenchConstants.INSTANCE.lockError(),
                                                        NotificationEvent.NotificationType.ERROR,
                                                        true,
                                                        lockTarget.getPlace(),
                                                        20));
        }
        // Delay reloading slightly in case we're dealing with a flood of events
        if (reloadTimer == null) {
            reloadTimer = new Timer() {

                public void run() {
                    reload();
                }
            };
        }

        if (!reloadTimer.isRunning()) {
            reloadTimer.schedule(250);
        }
    }

    private void reload() {
        lockTarget.getReloadRunnable().run();
    }

    private boolean isLockedByCurrentUser() {
        return lockInfo.isLocked() && lockInfo.lockedBy().equals(user.getIdentifier());
    }

    void updateLockInfo(final @Observes LockInfo lockInfo) {
        /* Comparing URIs since lockInfo.getFile() can be an ObservablePath or a PathImpl. */
        if (getLockTarget() != null && lockInfo.getFile().toURI().equals(lockTarget.getPath().toURI())) {
            this.lockInfo = lockInfo;
            this.lockSyncComplete = true;

            fireChangeTitleEvent();
            fireUpdatedLockStatusEvent();

            for (Runnable runnable : getSyncCompleteRunnables()) {
                runnable.run();
            }
            getSyncCompleteRunnables().clear();
        }
    }

    public LockTarget getLockTarget() {
        return lockTarget;
    }

    boolean isLockSyncComplete() {
        return lockSyncComplete;
    }

    List<Runnable> getSyncCompleteRunnables() {
        return syncCompleteRunnables;
    }

    void onResourceAdded(@Observes ResourceAddedEvent res) {
        if (lockTarget != null && res.getPath().equals(lockTarget.getPath())) {
            releaseLock();
        }
    }

    void onResourceUpdated(@Observes ResourceUpdatedEvent res) {
        if (lockTarget != null && res.getPath().equals(lockTarget.getPath())) {
            releaseLock();
        }
    }

    void onSaveInProgress(@Observes SaveInProgressEvent evt) {
        if (lockTarget != null && evt.getPath().equals(lockTarget.getPath())) {
            releaseLock();
        }
    }

    void onRenameInProgress(@Observes RenameInProgressEvent event) {
        if (getLockTarget() != null && event.getPath().equals(lockTarget.getPath())) {
            releaseLock();
        }
    }

    void onLockRequired(@Observes LockRequiredEvent event) {
        if (lockTarget != null && isVisible() && !isLockedByCurrentUser()) {
            acquireLock();
        }
    }

    private native void publishJsApi()/*-{
        var lockManager = this;
        $wnd.isLocked = function () {
            return lockManager.@org.uberfire.client.mvp.LockManagerImpl::isLocked()();
        }
        $wnd.isLockedByCurrentUser = function () {
            return lockManager.@org.uberfire.client.mvp.LockManagerImpl::isLockedByCurrentUser()();
        }
        $wnd.acquireLock = function () {
            lockManager.@org.uberfire.client.mvp.LockManagerImpl::acquireLock()();
        }
        $wnd.releaseLock = function () {
            lockManager.@org.uberfire.client.mvp.LockManagerImpl::releaseLock()();
        }
        $wnd.reload = function () {
            return lockManager.@org.uberfire.client.mvp.LockManagerImpl::reload()();
        }
    }-*/;

    private Widget getLockTargetWidget() {
        final IsWidget isWidget = lockTarget.getWidget();
        if (isWidget instanceof Widget) {
            return ((Widget) isWidget);
        }
        return isWidget.asWidget();
    }

    private boolean isLocked() {
        return lockInfo.isLocked();
    }

    protected LockInfo getLockInfo() {
        return lockInfo;
    }

    protected void fireChangeTitleEvent() {
        changeTitleEvent.fire(LockTitleWidgetEvent.create(lockTarget,
                                                          lockInfo,
                                                          user));
    }

    protected void fireUpdatedLockStatusEvent() {
        if (isVisible()) {
            updatedLockStatusEvent.fire(new UpdatedLockStatusEvent(lockInfo.getFile(),
                                                                   lockInfo.isLocked(),
                                                                   isLockedByCurrentUser()));
        }
    }

    private boolean isVisible() {
        final Widget widget = getLockTargetWidget();
        final Element element = widget.getElement();
        boolean visible = UIObject.isVisible(element) &&
                (element.getAbsoluteLeft() != 0) && (element.getAbsoluteTop() != 0);

        return visible;
    }
}