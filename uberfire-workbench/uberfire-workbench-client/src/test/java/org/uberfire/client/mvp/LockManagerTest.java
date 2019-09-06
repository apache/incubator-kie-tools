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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.fakes.FakeProvider;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.backend.vfs.impl.LockResult;
import org.uberfire.client.mvp.LockTarget.TitleProvider;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.workbench.VFSLockServiceProxy;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@SuppressWarnings("unchecked")
public class LockManagerTest {

    @Spy
    @InjectMocks
    private LockManagerImpl lockManager;

    @Mock
    private LockDemandDetector lockDemandDetector;

    @Mock
    private User user;

    @Mock
    private VFSLockServiceProxy lockService;

    @Mock
    private Path path;

    @Mock
    private EventSourceMock<NotificationEvent> lockNotification;

    @Mock
    private EventSourceMock<ChangeTitleWidgetEvent> changeTitleEvent;

    @Mock
    private EventSourceMock<UpdatedLockStatusEvent> updatedLockStatusEvent;

    @GwtMock
    private TextArea widget;

    @GwtMock
    private Event event;

    private LockTarget target;

    private int reloads = 0;

    @Before
    public void setup() throws Exception {
        mockTimer();

        GwtMockito.useProviderForType(WorkbenchResources.class,
                                      new FakeProvider<WorkbenchResources>() {

                                          @Override
                                          public WorkbenchResources getFake(Class<?> type) {
                                              return null;
                                          }
                                      });

        final Runnable reloadRunnable = new Runnable() {

            @Override
            public void run() {
                reloads++;
            }
        };

        final TitleProvider titleProvider = new TitleProvider() {

            @Override
            public String getTitle() {
                return "";
            }
        };

        target = new LockTarget(path,
                                widget,
                                new DefaultPlaceRequest("mockPlace"),
                                titleProvider,
                                reloadRunnable);

        lockManager.init(target);

        when(path.toURI()).thenReturn("directory/file.drl");
        when(user.getIdentifier()).thenReturn("mockedUser");
        when(lockDemandDetector.isLockRequired(any(Event.class))).thenReturn(true);
    }

    @Test
    public void updateLockInfoOnInit() {
        verify(lockService,
               times(1)).retrieveLockInfo(any(Path.class),
                                          any(ParameterizedCommand.class));
    }

    @Test
    public void acquireLockOnDemand() {
        lockManager.acquireLockOnDemand();

        simulateLockDemand();

        verify(lockService,
               times(1)).acquireLock(any(Path.class),
                                     any(ParameterizedCommand.class));
    }

    @Test
    public void acquireLockDoesNotHitServerIfLocked() {
        lockManager.acquireLockOnDemand();

        simulateLockFailure();
        simulateLockDemand();
        verify(lockService,
               times(1)).acquireLock(any(Path.class),
                                     any(ParameterizedCommand.class));

        simulateLockDemand();
        verify(lockService,
               times(1)).acquireLock(any(Path.class),
                                     any(ParameterizedCommand.class));
    }

    @Test
    public void notifyLockFailure() throws Exception {
        lockManager.acquireLockOnDemand();

        simulateLockFailure();
        simulateLockDemand();

        verify(lockNotification,
               times(1)).fire(any(NotificationEvent.class));
    }

    @Test
    public void notifyLockError() throws Exception {
        lockManager.acquireLockOnDemand();

        simulateLockError();
        simulateLockDemand();

        verify(lockNotification,
               times(1)).fire(any(NotificationEvent.class));
    }

    @Test
    public void reloadOnLockFailure() throws Exception {
        lockManager.acquireLockOnDemand();

        assertEquals(0,
                     reloads);

        simulateLockFailure();
        simulateLockDemand();

        assertEquals(1,
                     reloads);
    }

    @Test
    public void updateTitleOnFocus() {
        verify(changeTitleEvent,
               never()).fire(any(ChangeTitleWidgetEvent.class));
        lockManager.onFocus();
        verify(changeTitleEvent,
               times(1)).fire(any(ChangeTitleWidgetEvent.class));
    }

    @Test
    public void handleWindowReparenting() {
        lockManager.acquireLockOnDemand();
        verify(lockDemandDetector,
               times(1)).getLockDemandEventTypes();

        final ArgumentCaptor<AttachEvent.Handler> handlerCaptor = ArgumentCaptor.forClass(AttachEvent.Handler.class);
        verify(widget,
               times(1)).addAttachHandler(handlerCaptor.capture());

        handlerCaptor.getValue().onAttachOrDetach(new AttachEvent(true) {
        });
        verify(lockDemandDetector,
               times(2)).getLockDemandEventTypes();
    }

    @Test
    public void releaseLockOnSave() {
        lockManager.acquireLockOnDemand();

        simulateLockSuccess();
        simulateLockDemand();

        lockManager.onSaveInProgress(new SaveInProgressEvent(path));

        verify(lockService,
               times(1)).releaseLock(any(Path.class),
                                     any(ParameterizedCommand.class));
    }

    @Test
    public void releaseLockOnUpdate() {
        lockManager.acquireLockOnDemand();
        simulateLockSuccess();
        simulateLockDemand();

        lockManager.onResourceUpdated(new ResourceUpdatedEvent(path,
                                                               "",
                                                               new SessionInfoImpl(user)));

        verify(lockService,
               times(1)).releaseLock(any(Path.class),
                                     any(ParameterizedCommand.class));
    }

    @Test
    public void reloadEditorOnUpdateFromDifferentUser() {
        lockManager.onResourceUpdated(new ResourceUpdatedEvent(path,
                                                               "",
                                                               new SessionInfoImpl(user)));

        assertEquals(0,
                     reloads);

        lockManager.onResourceUpdated(new ResourceUpdatedEvent(path,
                                                               "",
                                                               new SessionInfoImpl(new UserImpl("differentUser"))));

        assertEquals(0,
                     reloads);
    }

    @Test
    public void releaseOwnedLockOnly() {
        lockManager.acquireLockOnDemand();
        simulateLockFailure();
        simulateLockDemand();

        lockManager.onResourceUpdated(new ResourceUpdatedEvent(path,
                                                               "",
                                                               new SessionInfoImpl(user)));

        verify(lockService,
               never()).releaseLock(any(Path.class),
                                    any(ParameterizedCommand.class));
    }

    @Test
    public void requestAcquireLockOnDemandNoMoreThanOnce() {
        lockManager.acquireLockOnDemand();

        simulateLockNoResponse();
        simulateLockDemand();
        simulateLockDemand();

        verify(lockService,
               times(1)).acquireLock(any(Path.class),
                                     any(ParameterizedCommand.class));
    }

    @Test
    public void acquireLock() {
        lockManager.acquireLock();

        verify(lockService,
               times(1)).acquireLock(any(Path.class),
                                     any(ParameterizedCommand.class));
    }

    @Test
    public void requestAcquireLockNoMoreThanOnce() {
        simulateLockNoResponse();

        lockManager.acquireLock();
        lockManager.acquireLock();

        verify(lockService,
               times(1)).acquireLock(any(Path.class),
                                     any(ParameterizedCommand.class));
    }

    @Test
    public void requestAcquireLockNoMoreThanOnceForSameUser() {
        simulateLockSuccess();

        lockManager.acquireLock();
        lockManager.acquireLock();

        verify(lockService,
               times(1)).acquireLock(any(Path.class),
                                     any(ParameterizedCommand.class));
    }

    @Test
    public void acquireLockFiresChangeTitleEvent() {
        simulateLockSuccess();

        lockManager.acquireLock();

        verify(changeTitleEvent,
               times(1)).fire(any(ChangeTitleWidgetEvent.class));

        lockManager.acquireLock();

        verify(changeTitleEvent,
               times(2)).fire(any(ChangeTitleWidgetEvent.class));
    }

    @Test
    public void testUpdateLockInfoWhenLockInfoURIIsEqualToLockTargetURI() {

        final LockInfo lockInfo = mock(LockInfo.class);
        final Path path = mock(Path.class);
        final Runnable runnable1 = mock(Runnable.class);
        final Runnable runnable2 = mock(Runnable.class);
        final List<Runnable> runnables = spy(new ArrayList<>(Arrays.asList(runnable1, runnable2)));

        doReturn(runnables).when(lockManager).getSyncCompleteRunnables();
        when(lockInfo.getFile()).thenReturn(path);
        when(path.toURI()).thenReturn("directory/file.drl");
        when(this.path.toURI()).thenReturn("directory/file.drl");

        lockManager.updateLockInfo(lockInfo);

        assertEquals(lockInfo, lockManager.getLockInfo());
        assertTrue(lockManager.isLockSyncComplete());
        verify(lockManager).fireChangeTitleEvent();
        verify(lockManager).fireUpdatedLockStatusEvent();
        verify(runnable1).run();
        verify(runnable2).run();
        verify(runnables).clear();
    }

    @Test
    public void testUpdateLockInfoWhenLockInfoURIIsNotEqualToLockTargetURI() {

        final LockInfo lockInfo = mock(LockInfo.class);
        final Path path = mock(Path.class);
        final Runnable runnable1 = mock(Runnable.class);
        final Runnable runnable2 = mock(Runnable.class);
        final List<Runnable> runnables = spy(new ArrayList<>(Arrays.asList(runnable1, runnable2)));

        doReturn(runnables).when(lockManager).getSyncCompleteRunnables();
        when(lockInfo.getFile()).thenReturn(path);
        when(path.toURI()).thenReturn("directory/file1.drl");
        when(this.path.toURI()).thenReturn("directory/file2.drl");

        lockManager.updateLockInfo(lockInfo);

        assertNotEquals(lockInfo, lockManager.getLockInfo());
        assertFalse(lockManager.isLockSyncComplete());
        verify(lockManager, never()).fireChangeTitleEvent();
        verify(lockManager, never()).fireUpdatedLockStatusEvent();
        verify(runnable1, never()).run();
        verify(runnable2, never()).run();
        verify(runnables, never()).clear();
    }

    @Test
    public void testUpdateLockInfoWhenLockTargetIsNull() {

        final LockInfo lockInfo = mock(LockInfo.class);
        final Runnable runnable1 = mock(Runnable.class);
        final Runnable runnable2 = mock(Runnable.class);
        final List<Runnable> runnables = spy(new ArrayList<>(Arrays.asList(runnable1, runnable2)));

        doReturn(runnables).when(lockManager).getSyncCompleteRunnables();
        doReturn(null).when(lockManager).getLockTarget();

        lockManager.updateLockInfo(lockInfo);

        assertNotEquals(lockInfo, lockManager.getLockInfo());
        assertFalse(lockManager.isLockSyncComplete());
        verify(lockManager, never()).fireChangeTitleEvent();
        verify(lockManager, never()).fireUpdatedLockStatusEvent();
        verify(runnable1, never()).run();
        verify(runnable2, never()).run();
        verify(runnables, never()).clear();
    }

    @Test
    public void testOnRenameInProgressWhenLockInfoPathIsEqualToLockTargetPath() {

        final RenameInProgressEvent renameInProgressEvent = mock(RenameInProgressEvent.class);

        when(renameInProgressEvent.getPath()).thenReturn(path);

        lockManager.onRenameInProgress(renameInProgressEvent);

        verify(lockManager).releaseLock();
    }

    @Test
    public void testOnRenameInProgressWhenLockInfoPathIsNotEqualToLockTargetPath() {

        final RenameInProgressEvent renameInProgressEvent = mock(RenameInProgressEvent.class);
        final Path path = mock(Path.class);

        when(renameInProgressEvent.getPath()).thenReturn(path);

        lockManager.onRenameInProgress(renameInProgressEvent);

        verify(lockManager, never()).releaseLock();
    }

    @Test
    public void testOnRenameInProgressWhenLockTargetIsNull() {

        final RenameInProgressEvent renameInProgressEvent = mock(RenameInProgressEvent.class);

        doReturn(null).when(lockManager).getLockTarget();

        lockManager.onRenameInProgress(renameInProgressEvent);

        verify(lockManager, never()).releaseLock();
    }

    private void simulateLockDemand() {
        EventListener listener = lockManager.acquireLockOnDemand(widget.getElement());
        listener.onBrowserEvent(event);
    }

    private void simulateLockFailure() {
        doAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Object[] args = invocation.getArguments();
                LockInfo lockInfo = new LockInfo(true,
                                                 "somebody",
                                                 path);
                final LockResult failed = LockResult.failed(lockInfo);
                ((ParameterizedCommand<LockResult>) args[1]).execute(failed);
                return null;
            }
        }).when(lockService).acquireLock(any(Path.class),
                                         any(ParameterizedCommand.class));
    }

    private void simulateLockSuccess() {
        doAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Object[] args = invocation.getArguments();
                final LockResult acquired = LockResult.acquired(path,
                                                                user.getIdentifier());
                ((ParameterizedCommand<LockResult>) args[1]).execute(acquired);
                return null;
            }
        }).when(lockService).acquireLock(any(Path.class),
                                         any(ParameterizedCommand.class));
    }

    private void simulateLockError() {
        doAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Object[] args = invocation.getArguments();
                final LockResult acquired = LockResult.error();
                ((ParameterizedCommand<LockResult>) args[1]).execute(acquired);
                return null;
            }
        }).when(lockService).acquireLock(any(Path.class),
                                         any(ParameterizedCommand.class));
    }

    private void simulateLockNoResponse() {
        doAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        }).when(lockService).acquireLock(any(Path.class),
                                         any(ParameterizedCommand.class));
    }

    private void mockTimer() throws Exception {
        final Timer mockTimer = new Timer() {

            @Override
            public void run() {
                target.getReloadRunnable().run();
            }

            @Override
            public void schedule(int delayMillis) {
                run();
            }
        };
        final Field reloadTimer = LockManagerImpl.class.getDeclaredField("reloadTimer");
        reloadTimer.setAccessible(true);
        reloadTimer.set(lockManager,
                        mockTimer);
    }
}